package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Ignore;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simplerobot.modules.utils.KQCodeUtils;
import io.koschicken.Constants;
import io.koschicken.bean.Gacha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.ImageUtil.composeImg;

@Service
public class PCRListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PCRListener.class);

    private static HashMap<String, LocalDateTime> coolDown; //抽卡冷却时间

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#提醒买药", "#买药", "#小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void maiyao(GroupMsg msg, MsgSender sender) {
        try {
            File file = ResourceUtils.getFile("/image/" + princessConfig.getTixingmaiyao());
            String str;
            if (file.exists()) {
                KQCodeUtils kqCodeUtils = KQCodeUtils.getInstance();
                str = kqCodeUtils.toCq("image", "file" + "=" + file.getAbsolutePath());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n" + str);
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "快他妈上号买药cnmd");
            }
        } catch (NullPointerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#十连", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon10(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = dp_Gashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up十连", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon10Up(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = dp_UpGashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon300(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = dp_Gashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon300Up(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gacha gacha = dp_UpGashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gacha.getData());
            ban(msg, sender, gacha);
            refreshCooldown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void GashaponSimple(GroupMsg msg, MsgSender sender) {
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
                if (q <= princessConfig.getGachaLimit() && isCool(msg.getQQCode())) {
                    Gacha gacha = dp_Gashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),
                            coolQAt + msg.getQQCode() + "]" + gacha.getData());
                    //抽卡太欧需要被禁言
                    ban(msg, sender, gacha);
                    refreshCooldown(msg.getQQCode());
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "做个人吧，抽这么多，家里有矿？");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void GashaponUpSimple(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(5));
                //抽卡次数不能为1以下
                if (q < 1) {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽尼🐴负数呢？");
                    return;
                }
                if (q <= princessConfig.getGachaLimit() && isCool(msg.getQQCode())) {
                    Gacha gacha = dp_UpGashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gacha.getData());
                    ban(msg, sender, gacha);
                    refreshCooldown(msg.getQQCode());
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "做个人吧，抽这么多，家里有矿？");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + "急毛，等会再抽");
        }
    }

    private void ban(GroupMsg msg, MsgSender sender, Gacha gacha) {
        if (gacha.isBan()) {
            Integer ssrCount = gacha.getSsrCount();
            try {
                long pow = (long) Math.pow(10, ssrCount - 1);
                LOGGER.info("禁言时间： {}", pow);
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), pow * 60);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "小伙子你很欧啊，奖励禁言大礼包");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                LOGGER.error("权限不足");
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "好他妈气啊，ban不掉这个欧洲狗管理。");
            }
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重置扭蛋cd"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void clearCD(GroupMsg msg, MsgSender sender) {
        if (princessConfig.getMasterQQ().equals(msg.getQQCode())) {
            coolDown.clear();
        }
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "已清除cd信息");
    }

    /**
     * 普通池的概率
     *
     * @param num
     */
    @Ignore
    private Gacha dp_Gashapon(int num) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int r = 0, sr = 0, ssr = 0;//抽出来的三星二星有几个
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - platinumSSRChance) {
                ssr++;
            } else if (j > platinumRChance) {
                sr++;
            } else {
                r++;
            }
        }
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - platinumSSRChance) {
                ssr++;
            } else {
                sr++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();
        for (int i = 0; i < ssr; i++) {
            int j = random.nextInt(SSR.length);
            map1.merge(SSR[j], 1, Integer::sum);
        }
        for (int i = 0; i < sr; i++) {
            int j = random.nextInt(Constants.SR.length);
            map2.merge(Constants.SR[j], 1, Integer::sum);
        }
        for (int i = 0; i < r; i++) {
            int j = random.nextInt(Constants.R.length);
            map3.merge(Constants.R[j], 1, Integer::sum);
        }
        Gacha g = new Gacha();
        g.setSsrCount(ssr);
        g.setData(get_GashaponString(r, sr, ssr, map1, map2, map3));
        try {
            g.setBan(num / ssr < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * up池的概率
     *
     * @param num
     */
    private Gacha dp_UpGashapon(int num) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int r = 0, sr = 0, ssr = 0;//抽出来的三星二星有几个

        //无保底
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - SSRChance) {
                ssr++;
            } else if (j > 1000 - SSRChance - SRChance) {
                sr++;
            } else {
                r++;
            }
        }
        //有保底
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - SSRChance) {
                ssr++;
            } else {
                sr++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();

        for (int i = 0; i < ssr; i++) {
            int q = random.nextInt(SSRChance);
            if (q < upSSRChance) {
                //抽出来up角色
                map1.merge(SSRUp[q % SSRUp.length], 1, Integer::sum);
            } else {
                int j = random.nextInt(noUpSSR.length);
                map1.merge(noUpSSR[j], 1, Integer::sum);
            }
        }
        for (int i = 0; i < sr; i++) {
            int q = random.nextInt(SRChance);
            if (q < upSRChance) {
                //抽出来up角色
                map2.merge(SRUp[q % SRUp.length], 1, Integer::sum);
            } else {
                int j = random.nextInt(noUpSR.length);
                map2.merge(noUpSR[j], 1, Integer::sum);
            }
        }
        for (int i = 0; i < r; i++) {
            int q = random.nextInt(RChance);
            if (q < upRChance) {
                //抽出来up角色
                try {
                    map3.merge(RUp[q % RUp.length], 1, Integer::sum);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                int j = random.nextInt(noUpR.length);
                map3.merge(noUpR[j], 1, Integer::sum);
            }
        }

        Gacha g = new Gacha();
        g.setSsrCount(ssr);
        g.setData(get_GashaponString(r, sr, ssr, map1, map2, map3));
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
    @Ignore
    private String get_GashaponString(int r, int sr, int ssr,
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
        if (canSendImage) {
            int total = r + sr + ssr;
            if (total == 1 || total == 10) { // 仅在单抽或十连的情况下才显示动画
                for (int i = 0; i < list.size(); i++) {
                    File file = new File("./config/gif/" + list.get(i) + ".gif");
                    CQCode cqCode_image = CQCodeUtil.build().getCQCode_Image(file.getAbsolutePath());
                    stringBuilder.append(cqCode_image.toString());
//                    if (i > 2) {
//                        break; // 最多只发三张动态图
//                    }
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
        if (coolDown == null) {
            coolDown = new HashMap<>();
        }
        coolDown.put(QQ, localDateTime.plusSeconds(princessConfig.getGachaCooldown()));
    }

    /**
     * 获取冷却时间是不是到了
     *
     * @param QQ
     */
    private boolean isCool(String QQ) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            if (coolDown.get(QQ) != null) {
                return coolDown.get(QQ).isBefore(LocalDateTime.now());
            } else {
                return true;
            }
        }
    }
}
