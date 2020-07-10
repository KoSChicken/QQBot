package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Ignore;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.messages.types.PowerType;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simplerobot.modules.utils.KQCodeUtils;
import io.koschicken.Constants;
import io.koschicken.bean.Gacha;
import io.koschicken.bean.GroupPower;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import static io.koschicken.Constants.*;
import static io.koschicken.listener.PrincessIntercept.On;
import static io.koschicken.utils.ImageUtil.composeImg;
import static io.koschicken.utils.StringTool.*;

// TODO 增加抽中三星时出货gif动画
@Service
public class OtherListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtherListener.class);
    private static final String ZUICHOU = "https://nmsl.shadiao.app/api.php";
    private static final String RAINBOW_FART = "https://chp.shadiao.app/api.php";
    private static final String TEMP = "./temp/";
    private static HashMap<String, LocalDateTime> coolDown; //抽卡冷却时间
    @Autowired
    ScoresService ScoresServiceImpl;

    public static void AllCoolDown() {
        coolDown = new HashMap<>();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#帮助", "#help", "帮助"}, at = true, keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "请输入【#序号】获取帮助详情");
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), helpMsg);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#其他功能帮助", "#1"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void otherHelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), OTHER_HELP_MSG);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#会战帮助", "#2"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void fightHelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), FIGHT_HELP_MSG);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#工会帮助", "#公会帮助", "#3"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void groupHelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), GROUP_HELP_MSG);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#机器人设置", "#4"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void configListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), CONFIG_MES);
    }

    @Listen(value = MsgGetTypes.groupMsg)
    @Filter(value = {"#bilibili相关帮助", "#5"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void bilibiliListen(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), BilibiliMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"帮助", "#帮助", "#help"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void configListen1(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "请输入【#序号】获取帮助详情");
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), helpMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"其他功能帮助", "#其它功能帮助", "#1"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void otherHelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), OTHER_HELP_MSG);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"会战帮助", "#会战帮助", "#2"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void fightHelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), FIGHT_HELP_MSG);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"工会帮助", "#工会帮助", "公会帮助", "#公会帮助", "#3"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void groupHelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), GROUP_HELP_MSG);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"机器人设置", "#机器人设置", "#4"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void testListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), CONFIG_MES);
    }

    @Listen(value = MsgGetTypes.privateMsg)
    @Filter(value = {"bilibili相关帮助", "#bilibili相关帮助", "#5"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void bilibiliListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), BilibiliMsg);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#提醒买药", "#买药", "#小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void maiyao(GroupMsg msg, MsgSender sender) {
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "image/" + princessConfig.getTixingmaiyao());
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
        int i;
        for (String s : set1) {
            int j = map1.get(s);
            for (i = 0; i < j; i++) {
                list.add(s);
            }
        }

        //人物图片
        if (canSendImage) {
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

//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "老婆", at = true, keywordMatchType = KeywordMatchType.CONTAINS)
//    public void kimo(GroupMsg msg, MsgSender sender) {
//        Random random = new Random();
//        random.setSeed(System.currentTimeMillis());
//        String send;
//        if (canSendImage && kimo_Definde_image != null) {
//            int i = kimo_Definde.length + kimo_Definde_image.size();
//            int j = random.nextInt(i);
//            if (j > kimo_Definde.length - 1) {
//                send = kimo_Definde_image.get(j - kimo_Definde.length);
//            } else {
//                send = kimo_Definde[random.nextInt(kimo_Definde.length)];
//            }
//        } else {
//            send = kimo_Definde[random.nextInt(kimo_Definde.length)];
//        }
//        sender.SENDER.sendGroupMsg(msg.getGroupCode(), send);
//    }
//
//    @Listen(MsgGetTypes.privateMsg)
//    @Filter(value = "挂载绅士图片", keywordMatchType = KeywordMatchType.EQUALS)
//    public void kimo(PrivateMsg msg, MsgSender sender) {
//        File file = new File("./hentai");
//
//        if (file.exists()) {
//            File[] files = file.listFiles();
//            if (files != null && files.length > 0) {
//                kimo_Definde_image = new ArrayList<>();
//                CQCodeUtil build = CQCodeUtil.build();
//                for (File f : files) {
//                    CQCode cqCode_image = build.getCQCode_Image(f.getAbsolutePath());
//                    kimo_Definde_image.add(cqCode_image.toString());
//                }
//                sender.SENDER.sendPrivateMsg(msg.getQQCode(), "读取图片成功");
//            } else {
//                sender.SENDER.sendPrivateMsg(msg.getQQCode(), "文件夹里还没有图片哦");
//            }
//        } else {
//            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "没有检测到有名字叫 hentai 的文件夹，图片请放到那里");
//        }
//    }

    @Listen(value = {MsgGetTypes.groupMsg, MsgGetTypes.privateMsg})
    @Filter(value = "切噜.*")
    public void qielu(MsgGet msg, MsgSender sender) {
        String needTran = msg.getMsg().replaceAll(" +", "");
        if (needTran.length() > 2) {
            needTran = needTran.substring(2);
            StringBuilder tranled = new StringBuilder();

            byte[] bytes;
            bytes = needTran.getBytes(StandardCharsets.UTF_8);
            for (byte aByte : bytes) {
                int[] cache = spiltByte(aByte < 0 ? -aByte + 127 : aByte);
                tranled.append(QieLU[cache[0]]);
                tranled.append(QieLU[cache[1]]);
            }
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg).getGroupCode(), tranled.toString());
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg).getQQCode(), tranled.toString());
            }
        } else {
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg).getGroupCode(), "没有要翻译的语句");
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg).getQQCode(), "没有要翻译的语句");
            }
        }
    }

    @Listen(value = {MsgGetTypes.groupMsg, MsgGetTypes.privateMsg})
    @Filter(value = "翻译切噜.*")
    public void reQielu(MsgGet msg, MsgSender sender) {
        String needTran = msg.getMsg().replaceAll(" +", "");
        needTran = needTran.replaceAll(",", "%%");
        needTran = needTran.replaceAll("扣", "扣扣");
        needTran = needTran.substring(4);

        if (needTran.length() > 0) {
            ArrayList<Byte> bytes = new ArrayList<>();
            //防止前面和最后出现"，"这个不和谐因素
            char[] cache = new char[2];
            int q, w;
            for (int i = 0; i < needTran.length(); i += 4) {
                cache[0] = needTran.charAt(i + 2);
                cache[1] = needTran.charAt(i + 3);
                q = reQieLU.get(String.valueOf(cache));
                cache[0] = needTran.charAt(i);
                cache[1] = needTran.charAt(i + 1);
                w = reQieLU.get(String.valueOf(cache));
                bytes.add(reSpiltByte(q, w));
            }

            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg), String.valueOf(getChars(bytes.toArray(new Byte[0]))));
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg), String.valueOf(getChars(bytes.toArray(new Byte[0]))));
            }
        } else {
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg), "没有要翻译的语句哎");
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg), "没有要翻译的语句哎");
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭扭蛋"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openEgg(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || princessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setGachaSwitch(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
                setJson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            GroupPower groupPower = new GroupPower();
            groupPower.setGachaSwitch(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
            setJson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启扭蛋"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutEgg(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || princessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setGachaSwitch(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
                setJson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            GroupPower groupPower = new GroupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
            setJson();
        }
    }

//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"#关闭PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void shut(GroupMsg msg, MsgSender sender) {
//        try {
//            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
//            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
//                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(false));
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
//                setjson();
//            }
//        } catch (NullPointerException e) {
//            //没这个群的自动都是同意
//            GroupPower groupPower = new GroupPower();
//            groupPower.setOn(false);
//            On.put(msg.getGroupCode(), groupPower);
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
//            setjson();
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"#开启PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void open(GroupMsg msg, MsgSender sender) {
//        try {
//            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
//            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
//                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(true));
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
//                setjson();
//            }
//        } catch (NullPointerException e) {
//            //没这个群的自动都是同意
//            GroupPower groupPower = new GroupPower();
//            On.put(msg.getGroupCode(), groupPower);
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
//            setjson();
//        }
//    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutBuy(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || princessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setMaiyaoSwitch(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
                setJson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            GroupPower groupPower = new GroupPower();
            groupPower.setMaiyaoSwitch(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
            setJson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openBuy(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || princessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setMaiyaoSwitch(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
                setJson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            GroupPower groupPower = new GroupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
            setJson();
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重载设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void reloadConfig(PrivateMsg msg, MsgSender sender) {
        getConfig();
        getGachaConfig();
        getEvent();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "扭蛋池，马事件已更新,现在设置为：\n" +
                "提醒买药小助手图片名:" + princessConfig.getTixingmaiyao() +
                "\n抽卡上限" + princessConfig.getGachaLimit() +
                "\n抽卡冷却:" + princessConfig.getGachaCooldown() +
                "\n总开关默认：" + princessConfig.isGlobalSwitch() +
                "\n好像没啥用的开关默认：" + princessConfig.isMaiyaoSwitch() +
                "\n扭蛋开关默认：" + princessConfig.isGachaSwitch() +
                "\n赛马开关默认：" + princessConfig.isHorseSwitch() +
                "\nmasterQQ：" + princessConfig.getMasterQQ());
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"通用设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void config(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "提醒买药小助手图片名:" + princessConfig.getTixingmaiyao() +
                "\n抽卡上限" + princessConfig.getGachaLimit() +
                "\n抽卡冷却:" + princessConfig.getGachaCooldown() +
                "\n总开关默认：" + princessConfig.isGlobalSwitch() +
                "\n好像没啥用的开关默认：" + princessConfig.isMaiyaoSwitch() +
                "\n扭蛋开关默认：" + princessConfig.isGachaSwitch() +
                "\n赛马开关默认：" + princessConfig.isHorseSwitch() +
                "\nmasterQQ：" + princessConfig.getMasterQQ());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#查看本群设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void groupConfig(GroupMsg msg, MsgSender sender) {
        GroupPower groupPower = On.get(msg.getGroupCode());
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "扭蛋开关:" + groupPower.isGachaSwitch() +
                "\n买药小助手开关" + groupPower.isMaiyaoSwitch() +
                "\n买马开关：" + groupPower.isHorseSwitch());
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"刷新全部签到"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void refreshSign(PrivateMsg msg, MsgSender sender) {
        if (msg.getQQCode().equals(princessConfig.getMasterQQ())) {
            ScoresServiceImpl.clearSign();
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "已刷新全部签到");
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "权限不足");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#讲几句难听的"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void zuichou(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        int i = random.nextInt(100);
        LOGGER.info("nmsl? {}", i);
        if (i >= 80) {
            try {
                String rf = Request.Get(ZUICHOU).execute().returnContent().asString();
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), rf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "我是个有素质的bot");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#讲几句好听的"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void rainbowFart(GroupMsg msg, MsgSender sender) {
        try {
            String rf = Request.Get(RAINBOW_FART).execute().returnContent().asString();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), rf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#头像.*"})
    public void touxiang(GroupMsg msg, MsgSender sender) {
        String[] split = msg.getMsg().split(" +");
        String QQ = "";
        if (split.length > 1) {
            QQ = split[1];
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有要获取头像的QQ号");
        }
        String AVATAR_API = "http://thirdqq.qlogo.cn/g?b=qq&nk=";
        String api = AVATAR_API + QQ + "&s=640";
        try {
            InputStream imageStream = Request.Get(api).execute().returnResponse().getEntity().getContent();
            File pic = new File(TEMP + System.currentTimeMillis() + ".jpg");
            FileUtils.copyInputStreamToFile(imageStream, pic);
            CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(pic.getAbsolutePath());
            LOGGER.info(pic.getAbsolutePath());
            String message = cqCodeImage.toString();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), message);
            boolean delete = pic.delete();
            LOGGER.info("文件删除成功了吗？{}", delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
