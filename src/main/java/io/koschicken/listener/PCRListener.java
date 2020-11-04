package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import io.koschicken.bean.Gacha;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.koschicken.Constants.*;
import static io.koschicken.PCRConstants.*;
import static io.koschicken.utils.ImageUtil.composeImg;

@Service
public class PCRListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PCRListener.class);

    private static final Random RANDOM = new Random();

    private static final String COOLDOWN_MSG = "急毛，等会再抽";
    private static final String VOICE_URL = "https://redive.estertion.win/sound/vo_ci/";
    private static final String TEMP = "./temp/voice/";
    private static HashMap<String, LocalDateTime> COOLDOWN; //抽卡冷却时间

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#提醒买药", "#买药", "#小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void maiyao(GroupMsg msg, MsgSender sender) {
        try {
            File file = new File("./image/" + PRINCESS_CONFIG.getTixingmaiyao());
            if (file.exists()) {
                CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(file.getAbsolutePath());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n" + cqCodeImage.toString());
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "快他妈上号买药cnmd");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#十连", "#十連"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void gashapon10(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = doGashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#up十连", "#up十連"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void gashapon10Up(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = doUpGashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void gashapon300(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = doGashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void gashapon300Up(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = doUpGashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void gashaponSimple(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(3));//获取抽多少次
                //抽卡次数不能为1以下
                if (q < 1) {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽尼🐴负数呢？");
                    return;
                }
                //抽卡次数不能超过设置的最高值and冷却时间到没到
                if (q <= PRINCESS_CONFIG.getGachaLimit() && isCool(msg.getQQCode())) {
                    Gacha gacha = doGashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),
                            CQ_AT + msg.getQQCode() + "]" + gacha.getData());
                    //抽卡太欧需要被禁言
                    ban(msg, sender, gacha);
                    refreshCooldown(msg.getQQCode());
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + "做个人吧，抽这么多，家里有矿？");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void gashaponUpSimple(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(5));
                //抽卡次数不能为1以下
                if (q < 1) {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽尼🐴负数呢？");
                    return;
                }
                if (q <= PRINCESS_CONFIG.getGachaLimit() && isCool(msg.getQQCode())) {
                    Gacha gacha = doUpGashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + gacha.getData());
                    ban(msg, sender, gacha);
                    refreshCooldown(msg.getQQCode());
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + "做个人吧，抽这么多，家里有矿？");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" + COOLDOWN_MSG);
        }
    }

    private void ban(GroupMsg msg, MsgSender sender, Gacha gacha) {
        if (gacha.isBan()) {
            Integer ssrCount = gacha.getSsrCount();
            try {
                long pow = (long) Math.pow(10, ssrCount - 1D);
                LOGGER.info("禁言时间： {}", pow);
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), pow * 60);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "小伙子你很欧啊，奖励禁言大礼包");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("权限不足");
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "好他妈气啊，ban不掉这个欧洲狗管理。");
            }
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重置扭蛋cd"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void clearCD(GroupMsg msg, MsgSender sender) {
        if (PRINCESS_CONFIG.getMasterQQ().equals(msg.getQQCode())) {
            COOLDOWN.clear();
        }
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "已清除cd信息");
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#猜语音"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void guessCygames(GroupMsg msg, MsgSender sender) throws IOException {
        String voice = getVoice();
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        CQCode cqCode_record = cqCodeUtil.getCQCode_Record(voice);
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), cqCode_record.toString());
    }

    /**
     * 普通池的概率
     */
    private Gacha doGashapon(int num) {
        RANDOM.setSeed(System.currentTimeMillis());
        int r = 0, sr = 0, ssr = 0;//抽出来的三星二星有几个
        for (int i = 0; i < num - num / 10; i++) {
            int j = RANDOM.nextInt(1000);
            if (j > 1000 - PLATINUM_SSR_CHANCE) {
                ssr++;
            } else if (j > PLATINUM_R_CHANCE) {
                sr++;
            } else {
                r++;
            }
        }
        for (int i = 0; i < num / 10; i++) {
            int j = RANDOM.nextInt(1000);
            if (j > 1000 - PLATINUM_SSR_CHANCE) {
                ssr++;
            } else {
                sr++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();
        for (int i = 0; i < ssr; i++) {
            int j = RANDOM.nextInt(SSR.length);
            map1.merge(SSR[j], 1, Integer::sum);
        }
        for (int i = 0; i < sr; i++) {
            int j = RANDOM.nextInt(SR.length);
            map2.merge(SR[j], 1, Integer::sum);
        }
        for (int i = 0; i < r; i++) {
            int j = RANDOM.nextInt(R.length);
            map3.merge(R[j], 1, Integer::sum);
        }
        Gacha g = new Gacha();
        g.setSsrCount(ssr);
        g.setData(getGashaponString(r, sr, ssr, map1, map2, map3));
        try {
            g.setBan(num / ssr < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * up池的概率
     */
    private Gacha doUpGashapon(int num) {
        RANDOM.setSeed(System.currentTimeMillis());
        int r = 0, sr = 0, ssr = 0;//抽出来的三星二星有几个
        //无保底
        for (int i = 0; i < num - num / 10; i++) {
            int j = RANDOM.nextInt(1000);
            if (j > 1000 - SSR_CHANCE) {
                ssr++;
            } else if (j > 1000 - SSR_CHANCE - SR_CHANCE) {
                sr++;
            } else {
                r++;
            }
        }
        //有保底
        for (int i = 0; i < num / 10; i++) {
            int j = RANDOM.nextInt(1000);
            if (j > 1000 - SSR_CHANCE) {
                ssr++;
            } else {
                sr++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();
        for (int i = 0; i < ssr; i++) {
            int q = RANDOM.nextInt(SSR_CHANCE);
            if (q < UP_SSR_CHANCE) {
                //抽出来up角色
                map1.merge(SSR_UP[q % SSR_UP.length], 1, Integer::sum);
            } else {
                int j = RANDOM.nextInt(NO_UP_SSR.length);
                map1.merge(NO_UP_SSR[j], 1, Integer::sum);
            }
        }
        for (int i = 0; i < sr; i++) {
            int q = RANDOM.nextInt(SR_CHANCE);
            if (q < UP_SR_CHANCE) {
                //抽出来up角色
                map2.merge(SR_UP[q % SR_UP.length], 1, Integer::sum);
            } else {
                int j = RANDOM.nextInt(NO_UP_SR.length);
                map2.merge(NO_UP_SR[j], 1, Integer::sum);
            }
        }
        for (int i = 0; i < r; i++) {
            int q = RANDOM.nextInt(R_CHANCE);
            if (q < UP_R_CHANCE) {
                //抽出来up角色
                try {
                    map3.merge(R_UP[q % R_UP.length], 1, Integer::sum);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                int j = RANDOM.nextInt(NO_UP_R.length);
                map3.merge(NO_UP_R[j], 1, Integer::sum);
            }
        }
        Gacha g = new Gacha();
        g.setSsrCount(ssr);
        g.setData(getGashaponString(r, sr, ssr, map1, map2, map3));
        try {
            g.setBan(num / ssr < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * 组织抽卡结果
     */
    private String getGashaponString(int r, int sr, int ssr,
                                     HashMap<String, Integer> map1,
                                     HashMap<String, Integer> map2,
                                     HashMap<String, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        if (ssr != 0) {
            stringBuilder.append("★★★×").append(ssr);
        }
        if (sr != 0) {
            stringBuilder.append("★★×").append(sr);
        }
        if (r != 0) {
            stringBuilder.append("★×").append(r);
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        ArrayList<String> list = new ArrayList<>();
        for (String s : set1) {
            int j = map1.get(s);
            for (int i = 0; i < j; i++) {
                list.add(s);
            }
        }
        //人物图片
        if (CAN_SEND_IMAGE) {
            int total = r + sr + ssr;
            if (total == 1 || total == 10) { // 仅在单抽或十连的情况下才显示动画
                for (String s : list) {
                    File file = new File("./config/gif/" + s + ".gif");
                    CQCode cqCode_image = CQCodeUtil.build().getCQCode_Image(file.getAbsolutePath());
                    stringBuilder.append(cqCode_image.toString());
                }
            } else {
                try {
                    String s = composeImg(list);
                    if (null != s) {
                        File file = new File(s);
                        CQCode cqCode_image = CQCodeUtil.build().getCQCode_Image(file.getAbsolutePath());
                        stringBuilder.append(cqCode_image.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ssr != 0) {
            stringBuilder.append("\n三星：");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (sr != 0) {
            stringBuilder.append("\n二星：");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }
        if (r != 0) {
            stringBuilder.append("\n一星：");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 刷新抽卡冷却时间
     */
    private void refreshCooldown(String QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (COOLDOWN == null) {
            COOLDOWN = new HashMap<>();
        }
        COOLDOWN.put(QQ, localDateTime.plusSeconds(PRINCESS_CONFIG.getGachaCooldown()));
    }

    /**
     * 获取冷却时间是不是到了
     */
    private boolean isCool(String QQ) {
        if (COOLDOWN == null) {
            COOLDOWN = new HashMap<>();
            return true;
        } else {
            if (COOLDOWN.get(QQ) != null) {
                return COOLDOWN.get(QQ).isBefore(LocalDateTime.now());
            } else {
                return true;
            }
        }
    }

    /**
     * 获取一个
     */
    private String getVoice() throws IOException {
        Document document = Jsoup.connect(VOICE_URL).get();
        Elements elements = document.getElementsByTag("a");
        List<String> codes = elements.stream().map(element -> element.attr("href")).collect(Collectors.toList());
        codes.remove(0);
        int index = RandomUtils.nextInt(1, codes.size());
        String code = codes.get(index);
        Document voiceDocument = Jsoup.connect(VOICE_URL + code).get();
        Elements voiceElements = voiceDocument.getElementsByTag("a");
        List<String> voiceList = voiceElements.stream().map(element -> element.attr("href")).collect(Collectors.toList());
        voiceList.remove(0);
        String url = VOICE_URL + code + voiceList.get(0);
        File voice = new File(TEMP + voiceList.get(0));
        InputStream content = Request.Get(url)
                .setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3")
                .execute().returnResponse().getEntity().getContent();
        if (!voice.exists()) {
            FileUtils.copyInputStreamToFile(content, voice);
        }
        return voice.getAbsolutePath();
    }
}
