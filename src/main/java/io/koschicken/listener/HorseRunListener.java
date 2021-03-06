package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.result.GroupMemberInfo;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.messages.types.PowerType;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.sender.MsgSender;
import io.koschicken.bean.GroupPower;
import io.koschicken.bean.Horse;
import io.koschicken.database.bean.Lucky;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.LuckyService;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.koschicken.Constants.*;
import static io.koschicken.listener.PrincessIntercept.On;

@Service
public class HorseRunListener {

    public static final int SIGN_SCORE = 5000;
    //赛马  群号->映射群员->映射押注对象号码 押注金额
    private static final HashMap<String, Map<Long, int[]>> maList = new HashMap<>();
    private static final HashMap<String, Integer> progressList = new HashMap<>(); // 赛马进度
    private static final Logger LOGGER = LoggerFactory.getLogger(HorseRunListener.class);
    @Autowired
    ScoresService scoresService;
    @Autowired
    LuckyService luckyService;
    /**
     * bot管理器
     */
    @Autowired
    private BotManager botManager;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "退款(.*)")
    public void refundWu(GroupMsg msg, MsgSender sender) {
        String message = msg.getMsg();
        String regex = "退款(.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(message);
        int refund = 0;
        while (m.find()) {
            try {
                refund = Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                LOGGER.info("数量不是数字，默认为0");
            }
        }
        if (refund != 0) {
            if (refund < 0) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "退款数值不能为负数");
                return;
            }
            scoresService.refundWu(Long.parseLong(msg.getQQCode()), refund);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "恭喜你，成功退款，快查查余额吧 :)");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#天降福利")
    public void allRich(GroupMsg msg, MsgSender sender) {
        if (PRINCESS_CONFIG.getMasterQQ().equals(msg.getQQ())) {
            scoresService.allRich(10000);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "所有人的钱包都增加了一万块钱");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#金融危机.*")
    public void financialCrisis(GroupMsg msg, MsgSender sender) {
        if (PRINCESS_CONFIG.getMasterQQ().equals(msg.getQQ())) {
            String target;
            String[] split = msg.getMsg().split(" +");
            if (split.length > 1) {
                target = split[1];
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有金融危机袭击的目标");
                return;
            }
            scoresService.financialCrisis(Long.parseLong(target));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + target + "] 遭遇金融危机，财产减半。");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#赛马.*", "#赛.*"}, at = true)
    public void startHorse(GroupMsg msg, MsgSender sender) {
        //必须开启才可以开始比赛
        if (On.get(msg.getGroupCode()).isHorseSwitch()) {
            //比赛只能同时开启一次
            if (maList.get(msg.getGroupCode()) != null) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已经有比赛在进行了");
            } else {
                maList.put(msg.getGroupCode(), new HashMap<>());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "比赛开盘");
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#给xcw上供", "上供", "#上供", "签到", "#签到", "簽到", "#簽到"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void sign(GroupMsg msg, MsgSender sender) {
        int rank = RandomUtils.nextInt(1, 101);
        int score = getScore(rank);
        if (On.get(msg.getGroupCode()).isHorseSwitch()) {
            Scores scores = scoresService.getById(msg.getCodeNumber());
            if (scores != null) {
                if (Boolean.TRUE.equals(scores.getiSign())) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 每天只能签到一次");
                    return;
                }
                scores.setScore(scores.getScore() + score);
                scores.setiSign(true);
                String groupCode = scores.getGroupCode();
                if (groupCode != null && !groupCode.contains(msg.getGroupCode())) {
                    scores.setGroupCode(groupCode + ", " + msg.getGroupCode());
                } else {
                    scores.setGroupCode(msg.getGroupCode());
                }
                scoresService.updateById(scores);
                if (score > 15) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 签到成功，币+" + score + "，现在币:" + scores.getScore());
                } else {
                    Lucky entity = new Lucky(msg.getCodeNumber(), new Date(), score);
                    luckyService.save(entity);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 天选之人！币+" + score + "，现在币:" + scores.getScore());
                }
            } else {
                scores = new Scores();
                scores.setQQ(msg.getCodeNumber());
                scores.setiSign(true);
                scores.setScore(SIGN_SCORE); // 第一次签到的仍然是5000
                scores.setGroupCode(msg.getGroupCode());
                scoresService.save(scores);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQ() + "] 签到成功，币+" + score);
            }
        }
    }

    private int getScore(int rank) {
        int score;
        if (rank >= 99) {
            score = RandomUtils.nextInt(25000, 50001);
        } else if (rank >= 95) {
            score = RandomUtils.nextInt(10000, 25001);
        } else if (rank >= 50) {
            score = RandomUtils.nextInt(5000, 10001);
        } else if (rank >= 15) {
            score = RandomUtils.nextInt(2500, 5001);
        } else {
            score = rank;
        }
        return score;
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#天选")
    public void luckyList(GroupMsg msg, MsgSender sender) {
        String groupCode = msg.getGroupCode();
        StringBuilder sb = new StringBuilder();
        sb.append("天选之人榜\n");
        List<Lucky> list = luckyService.listByGroupCode(groupCode);
        for (int i = 0; i < 10; i++) {
            Lucky lucky = list.get(i);
            GroupMemberInfo info = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), String.valueOf(lucky.getQq()));
            sb.append(i + 1).append(". ").append(dealCard(info.getCard())).append(" 天选次数：").append(lucky.getCount()).append("\n");
        }
        sb.append("等").append(list.size()).append("位群友");
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), sb.toString().trim());
    }

    private String dealCard(String card) {
        return card.replace("怪物猎人辱华", "屏蔽字");
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#开始赛.*", at = true)
    public void start(GroupMsg msg, MsgSender sender) {
        if (maList.get(msg.getGroupCode()) != null) {
            Integer progress = progressList.get(msg.getGroupCode());
            if (progress != null && progress >= 0) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "赛马已经开始过了");
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "赛马开始，走过路过不要错过");
                Horse horse = new Horse();
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), drawHorse(horse));
                HorseFight horseFight = new HorseFight(msg.getGroupCode(), horse);
                horseFight.start();
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "请先#赛马@机器人");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"押马[1-5]#[0-9]*"})
    public void buyHorse(GroupMsg msg, MsgSender sender) {
        if (maList.get(msg.getGroupCode()) == null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "当前没有比赛，不能下注");
            return;
        }
        Integer progress = progressList.get(msg.getGroupCode());
        if (progress != null && progress > 2) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "赛程已经过半，不能再下注了");
            return;
        }
        String re = "^押马([1-5])#([0-9]+)$";
        String str = msg.getMsg();
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(str);
        int no = 0, coin = 0;
        while (m.find()) {
            no = Integer.parseInt(m.group(1));
            coin = Integer.parseInt(m.group(2));
        }
        if (no > 5 || no < 1) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有这个编号的选手");
            return;
        }
        if (coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "反向下注不可取");
            return;
        }
        Scores byId = scoresService.getById(msg.getCodeNumber());
        if (byId == null || byId.getScore() - coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没那么多可以下注的币");
            return;
        }

        if (maList.get(msg.getGroupCode()).get(msg.getCodeNumber()) != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "你已经下过注了");
        } else {
            int[] integers = new int[2];
            integers[0] = no - 1;
            integers[1] = coin;
            maList.get(msg.getGroupCode()).put(msg.getCodeNumber(), integers);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "下注完成 加油啊" + no + "号");
        }
        int size = maList.get(msg.getGroupCode()).size();
        if (size > 4) {
            start(msg, sender);
        }
    }

    @Listen(value = {MsgGetTypes.groupMsg, MsgGetTypes.privateMsg})
    @Filter(value = {"我有多少钱", "余额"},
            keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void myCoin(MsgGet msg, MsgSender sender) {
        Scores scores;
        GroupMsg groupMsg;
        PrivateMsg privateMsg;
        if (msg instanceof GroupMsg) {
            groupMsg = (GroupMsg) msg;
            scores = scoresService.getById(groupMsg.getCodeNumber());
            if (scores != null) {
                String isSign = Boolean.TRUE.equals(scores.getiSign()) ? "" : "，还没有签到";
                if (scores.getScore() > 0) {
                    sender.SENDER.sendGroupMsg(groupMsg.getGroupCode(), CQ_AT + groupMsg.getQQ() + "] 有"
                            + scores.getScore() + "块钱" + isSign);
                } else {
                    sender.SENDER.sendGroupMsg(groupMsg.getGroupCode(), CQ_AT + groupMsg.getQQ() + "] 你没钱，穷仔" + isSign);
                }
            } else {
                sender.SENDER.sendGroupMsg(groupMsg.getGroupCode(),
                        CQ_AT + groupMsg.getQQCode() + "] 没有记录");
            }
        } else {
            privateMsg = (PrivateMsg) msg;
            scores = scoresService.getById(privateMsg.getCodeNumber());

            if (scores != null) {
                String isSign = Boolean.TRUE.equals(scores.getiSign()) ? "" : "，还没有签到哦";
                sender.SENDER.sendPrivateMsg(privateMsg.getQQCode(), "有" + scores.getScore() + "块钱" + isSign);
            } else {
                sender.SENDER.sendPrivateMsg(privateMsg.getQQCode(), "没有记录");
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#财富榜")
    public void rank(GroupMsg msg, MsgSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append("群友财富榜\n");
        List<Scores> list = scoresService.rank("%" + msg.getGroupCode() + "%");
        for (int i = 0; i < list.size(); i++) {
            GroupMemberInfo info = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), String.valueOf(list.get(i).getQQ()));
            sb.append(i + 1).append(". ").append(dealCard(info.getCard())).append(" 余额：").append(list.get(i).getScore()).append("\n");
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), sb.toString().trim());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启赛马"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void horseBegin(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQ()).getPowerType();
            if (powerType.isOwner() || powerType.isAdmin()) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setHorseSwitch(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启赛马");
                setJson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            GroupPower groupPower = new GroupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启赛马");
            setJson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭赛马"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void horseDisable(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQ()).getPowerType();
            if (powerType.isOwner() || powerType.isAdmin()) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setHorseSwitch(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭赛马");
                setJson();
            }
        } catch (NullPointerException e) {
            GroupPower groupPower = new GroupPower();
            groupPower.setHorseSwitch(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭赛马");
            setJson();
        }
    }

    /**
     * 根据传入的马赛场实况类，制作出马赛场图
     */
    public String drawHorse(Horse horse) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < horse.getPosition().size(); i++) {
            stringBuilder.append(i + 1);
            for (int j = 0; j < 9 - horse.getPosition().get(i); j++) {
                stringBuilder.append("Ξ"); //
            }
            stringBuilder.append(EMOJI_LIST[horse.getType().get(i)]);//画马
            for (int j = 0; j < horse.getPosition().get(i) - 1; j++) {
                stringBuilder.append("Ξ");//
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void allClear(String groupQQ, int winner) {
        Map<Long, int[]> group = maList.get(groupQQ);
        Iterator<Long> iterator = group.keySet().iterator();
        List<Scores> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Long entry = iterator.next();
            if (group.get(entry)[0] == winner) {
                Scores byId = scoresService.getById(entry);
                byId.setScore((int) (byId.getScore() + group.get(entry)[1] * 1.5));
                list.add(byId);
            } else {
                Scores byId = scoresService.getById(entry);
                byId.setScore(byId.getScore() - group.get(entry)[1]);
                list.add(byId);
            }
        }
        scoresService.updateBatchById(list);
        maList.remove(groupQQ);
        progressList.remove(groupQQ);
    }

    public class HorseFight extends Thread {
        private final String groupQQ;
        private final Horse horse;
        private final List<Integer> horseList;
        private boolean fighting = true;
        private int winnerHorse;

        public HorseFight(String groupQQ, Horse horse) {
            this.groupQQ = groupQQ;
            this.horse = horse;
            horseList = horse.getPosition();
        }

        @Override
        public void run() {
            final BotSender sender = botManager.defaultBot().getSender();
            int progress = 0;
            while (fighting) {
                progressList.put(groupQQ, progress++);
                try {
                    Thread.sleep(RandomUtils.nextInt(1, 1000) + 2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                String s = event();
                sender.SENDER.sendGroupMsg(groupQQ, s);//事件发生器
                add();//所有马向前跑一格
                sender.SENDER.sendGroupMsg(groupQQ, drawHorse(horse));
                try {
                    Thread.sleep(RandomUtils.nextInt(1, 1000) + 3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            sender.SENDER.sendGroupMsg(groupQQ, drawHorse(horse));//最后再画一次马图
            sender.SENDER.sendGroupMsg(groupQQ, winnerHorse + 1 + "最终赢得了胜利，让我们为他鼓掌");
            StringBuilder sb = getWinners();
            sender.SENDER.sendGroupMsg(groupQQ, sb.toString());
            allClear(groupQQ, winnerHorse);//收钱
        }

        private StringBuilder getWinners() {
            Map<Long, int[]> map = maList.get(groupQQ); // int[0]->马的编号 int[1]->钱
            List<Long> winner = new ArrayList<>();
            map.forEach((qq, value) -> {
                int[] intArray = map.get(qq);
                if (Arrays.binarySearch(intArray, winnerHorse) >= 0) {
                    winner.add(qq);
                }
            });
            StringBuilder sb = new StringBuilder();
            if (winner.isEmpty()) {
                sb.append("本次赛马无人押中，很遗憾");
            } else {
                sb.append("恭喜");
                for (Long qq : winner) {
                    sb.append(" [CQ:at,qq=").append(qq).append("] ");
                }
                sb.append("赢得了奖金");
            }
            return sb;
        }

        public void add() {
            List<Integer> winners = new ArrayList<>();
            List<Integer> list = horse.getPosition();
            for (int i = 0; i < horse.getPosition().size(); i++) {
                int j = list.get(i) + 1;
                list.set(i, j);
                //一个马跑完全程停止
                if (j > 9) {
                    winners.add(i);
                    fighting = false;
                }
            }
            if (!fighting) {
                // 随机获取一个赢家
                int size = winners.size();
                LOGGER.info("完成比赛的马数量为{}", size);
                if (size > 1) {
                    int i = RandomUtils.nextInt(0, size);
                    LOGGER.info("赢家下标为{}", i);
                    winnerHorse = winners.get(i);
                } else {
                    winnerHorse = winners.get(0);
                }
            }
        }

        public String event() {
            //计算这次发生的是好事还是坏事
            if (RandomUtils.nextInt(1, 77) > 32) {
                //好事
                int i = RandomUtils.nextInt(1, horse.getPosition().size());//作用于哪只马
                horseList.set(i, horseList.get(i) + 1);
                return HORSE_EVENT.getGoodHorseEvent().get(
                        RandomUtils.nextInt(1, HORSE_EVENT.getGoodHorseEvent().size())).replace("?", String.valueOf(i + 1));
            } else {
                //坏事
                int i = RandomUtils.nextInt(1, horse.getPosition().size());
                horseList.set(i, horseList.get(i) - 1);
                return HORSE_EVENT.getBedHorseEvent().get(
                        RandomUtils.nextInt(1, HORSE_EVENT.getBedHorseEvent().size())).replace("?", String.valueOf(i + 1));
            }
        }
    }
}
