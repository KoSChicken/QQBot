package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.sender.MsgSender;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.koschicken.Constants.CQ_AT;
import static io.koschicken.listener.PrincessIntercept.On;

@Service
public class DiceListener {

    // 群号->映射群员->映射押注内容 押注金额
    private static final HashMap<String, Map<Long, List<String>>> diceMap = new HashMap<>();
    private static final List<String> typeList;
    private static final HashMap<String, Boolean> progressMap = new HashMap<>(); // 骰子游戏状态
    private static final int RATE_N = 2;
    private static final int RATE_B = 34;

    static {
        typeList = new ArrayList<>();
        typeList.add("大");
        typeList.add("小");
        typeList.add("豹子");
    }

    @Autowired
    ScoresService scoresServiceImpl;

    @Autowired
    private BotManager botManager;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"骰子说明"})
    public void startHorse(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "#骰子@机器人 创建游戏\n押骰子[大|小|豹子]#[金额] 下注\n#投掷骰子@机器人 开始游戏\n大小的倍率为"
                + RATE_N + "，豹子倍率为" + RATE_B
        );
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#骰子.*"}, at = true)
    public void startDice(GroupMsg msg, MsgSender sender) {
        //必须开启才可以开始
        if (On.get(msg.getGroupCode()).isHorseSwitch()) {
            //只能同时开启一次
            if (diceMap.get(msg.getGroupCode()) != null) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已经有骰子游戏在进行了");
            } else {
                diceMap.put(msg.getGroupCode(), new HashMap<>());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "骰子游戏开始");
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"押骰子.*#[0-9]*"})
    public void bet(GroupMsg msg, MsgSender sender) {
        if (diceMap.get(msg.getGroupCode()) == null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "当前没有骰子游戏，不能下注");
            return;
        }
        Boolean running = progressMap.get(msg.getGroupCode());
        if (running != null && running) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "骰子游戏开始，不再接受下注");
            return;
        }
        String re = "^押骰子(.*)#([0-9]+)$";
        String str = msg.getMsg();
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(str);
        String no = "";
        int coin = 0;
        while (m.find()) {
            no = m.group(1);
            coin = Integer.parseInt(m.group(2));
        }
        if (!typeList.contains(no)) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "下注只能为 大、小或豹子");
            return;
        }
        if (coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "反向下注不可取");
            return;
        }
        Scores scores = scoresServiceImpl.getById(msg.getCodeNumber());
        if (scores == null || scores.getScore() - coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没那么多可以下注的币");
            return;
        }

        if (diceMap.get(msg.getGroupCode()).get(msg.getCodeNumber()) != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "你已经下过注了");
        } else {
            List<String> list = new ArrayList<>();
            list.add(no);
            list.add(String.valueOf(coin));
            diceMap.get(msg.getGroupCode()).put(msg.getCodeNumber(), list);
            scores.setScore(scores.getScore() - coin);
            scoresServiceImpl.updateById(scores);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "下注完成");
        }
        int size = diceMap.get(msg.getGroupCode()).size();
        if (size > 4 && (!progressMap.get(msg.getGroupCode()) || Objects.isNull(progressMap.get(msg.getGroupCode())))) {
            start(msg, sender);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#投掷骰子.*", at = true)
    public void start(GroupMsg msg, MsgSender sender) {
        if (diceMap.get(msg.getGroupCode()) != null) {
            Boolean running = progressMap.get(msg.getGroupCode());
            if (running == null || !running) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "骰子游戏开始");
                Dice dice = new Dice(msg.getGroupCode());
                dice.start();
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "骰子游戏正在进行中");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "请先#骰子@机器人");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#豹？")
    public void bao(GroupMsg msg, MsgSender sender) {
        List<String> diceResult = new ArrayList<>();
        boolean allSame = true; // 豹子flag
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            int roll = roll();
            sum += roll;
            diceResult.add(String.valueOf(roll));
            if (i != 0 && roll != Integer.parseInt(diceResult.get(i - 1))) {
                allSame = false;
            }
        }
        String result = result(allSame, sum);
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "豹子".equals(result) ? "豹了" : "没豹");
    }

    public void allClear(String groupQQ, String result) {
        Map<Long, List<String>> group = diceMap.get(groupQQ);
        Iterator<Long> iterator = group.keySet().iterator();
        List<Scores> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Long entry = iterator.next();
            if (group.get(entry).get(0).equals(result)) {
                Scores byId = scoresServiceImpl.getById(entry);
                double rate;
                if ("豹子".equals(result)) {
                    rate = RATE_B;
                } else {
                    rate = RATE_N;
                }
                byId.setScore((int) (byId.getScore() + Integer.parseInt(group.get(entry).get(1)) * rate));
                list.add(byId);
            }
        }
        scoresServiceImpl.updateBatchById(list);
        diceMap.remove(groupQQ);
        progressMap.remove(groupQQ);
    }

    private int roll() {
        return RandomUtils.nextInt(1, 7);
    }

    private String result(boolean allSame, int sum) {
        if (allSame) {
            return "豹子";
        } else {
            if (sum >= 4 && sum <= 10) {
                return "小";
            } else if (sum >= 11 && sum <= 17) {
                return "大";
            }
            return "";
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#roll(.*)[-dD](.*)"})
    public void roll(GroupMsg msg, MsgSender sender) {
        if (msg.getMsg().contains("w")) {
            return;
        }
        try {
            String regex = "#roll(.*)[-dD](.*)";
            String message = msg.getMsg();
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(message);
            int count = 1;
            int limit = 4;
            while (m.find()) {
                count = Math.max(Integer.parseInt(m.group(1).trim()), 1);
                limit = Math.max(Integer.parseInt(m.group(2).trim()), 4);
            }
            Scores scores = scoresServiceImpl.getById(msg.getQQ());
            if (count == 10 && limit == 10 && scores.getScore() >= 10) {
                // 10d10，则进行金币翻倍判断
                gameRoll10d10(msg, sender, count, limit, scores);
                return;
            }
            if (count > 20) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "你正常点，没那么多骰子给你扔。");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[CQ:at,qq=").append(msg.getQQ()).append("]roll出了");
            for (int i = 0; i < count; i++) {
                int singleDice = RandomUtils.nextInt(1, limit + 1);
                sb.append("[").append(singleDice).append("]");
                if (i != count - 1) {
                    sb.append(", ");
                }
            }
            sb.append("点，本次使用了").append(count).append("个").append(limit).append("面骰。");
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), sb.toString());
        } catch (NumberFormatException e) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "格式错误");
        }
    }

    private void gameRoll10d10(GroupMsg msg, MsgSender sender, int count, int limit, Scores scores) {
        int[] gameRoll = gameRoll();
        /*StringBuilder sb = new StringBuilder();
        sb.append(CQ_AT).append(msg.getQQ()).append("]roll出了");
        for(int i = 0; i < gameRoll.length; i++) {
            sb.append("[").append(gameRoll[i]).append("]");
            if (i != count - 1) {
                sb.append(", ");
            }
        }
        sb.append("点，本次使用了").append(count).append("个").append(limit).append("面骰。")
                .append("10d10将会扣除你10%的金币，如果roll出7个大于7的数字，余额将翻倍。");
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), sb.toString());*/
        boolean check = check(gameRoll);
        if (check) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 恭喜你，达成了7个大于7的条件，财富已翻倍。");
            int max = (Integer.MAX_VALUE - 1) / 2;
            int newScores = scores.getScore() >= max ? Integer.MAX_VALUE : scores.getScore() * 2;
            scores.setScore(newScores);
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 没中。");
            int newScores = scores.getScore() - scores.getScore() / 25;
            scores.setScore(newScores);
        }
        scoresServiceImpl.updateById(scores);
    }

    private int[] gameRoll() {
        int[] result = new int[10];
        for (int i = 0; i < 10; i++) {
            result[i] = RandomUtils.nextInt(1, 11);
        }
        return result;
    }

    private boolean check(int[] arr) {
        int valid = 0;
        for (int j : arr) {
            if (j >= 7) {
                valid++;
            }
        }
        return valid >= 7;
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#roll10d10w"})
    public void roll10D10W(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "10d10w已被sbbot禁用");
//        try {
//            // 10d10，则进行金币翻倍判断
//            Scores scores = scoresServiceImpl.getById(msg.getQQ());
//            gameRoll10d10W(msg, sender, scores);
//        } catch (NumberFormatException e) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "格式错误");
//        }
    }

    private void gameRoll10d10W(GroupMsg msg, MsgSender sender, Scores scores) {
        int i = 1;
        boolean check = false;
        if (scores.getScore() < 10) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 余额不足。");
            return;
        }
        while(!check && scores.getScore() >= 10) {
            i++;
            int newScores = scores.getScore() - scores.getScore() / 25;
            scores.setScore(newScores);
            scoresServiceImpl.updateById(scores);
            check = check(gameRoll());
            if (check) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 恭喜你，roll了" + i + "次，中了，现在余额：" + scores.getScore());
                int max = (Integer.MAX_VALUE - 1) / 2;
                newScores = scores.getScore() >= max ? Integer.MAX_VALUE : scores.getScore() * 2;
                scores.setScore(newScores);
                scoresServiceImpl.updateById(scores);
                break;
            }
        }
    }

    public class Dice extends Thread {
        private final String groupQQ;

        public Dice(String groupQQ) {
            this.groupQQ = groupQQ;
        }

        @Override
        public void run() {
            final BotSender sender = botManager.defaultBot().getSender();
            List<String> diceResult = new ArrayList<>();
            progressMap.put(groupQQ, true);
            boolean allSame = true; // 豹子flag
            int sum = 0;

            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(RandomUtils.nextInt(1, 1000) + 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                int roll = roll();
                sum += roll;
                diceResult.add(String.valueOf(roll));
                if (i != 0 && roll != Integer.parseInt(diceResult.get(i - 1))) {
                    allSame = false;
                }
                sender.SENDER.sendGroupMsg(groupQQ, String.valueOf(roll));
                try {
                    Thread.sleep(RandomUtils.nextInt(1, 1000) + 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            String result = result(allSame, sum);
            sender.SENDER.sendGroupMsg(groupQQ, "骰子结果为：" + result);
            StringBuilder sb = getWinners(result);
            sender.SENDER.sendGroupMsg(groupQQ, sb.toString());
            allClear(groupQQ, result); //收钱
        }

        private StringBuilder getWinners(String result) {
            Map<Long, List<String>> map = diceMap.get(groupQQ);
            List<Long> winner = new ArrayList<>();
            map.forEach((qq, value) -> {
                List<String> list = map.get(qq);
                String str = list.get(0);
                if (str.equals(result)) {
                    winner.add(qq);
                }
            });
            StringBuilder sb = new StringBuilder();
            if (winner.isEmpty()) {
                sb.append("本次骰子游戏无人押中，很遗憾");
            } else {
                sb.append("恭喜");
                for (Long qq : winner) {
                    sb.append(" [CQ:at,qq=").append(qq).append("] ");
                }
                sb.append("押中🎲，赢得了奖金");
            }
            return sb;
        }
    }
}
