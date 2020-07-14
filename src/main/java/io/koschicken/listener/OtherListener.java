package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
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
import io.koschicken.bean.GroupPower;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static io.koschicken.Constants.*;
import static io.koschicken.listener.PrincessIntercept.On;
import static io.koschicken.utils.StringTool.*;

@Service
public class OtherListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtherListener.class);
    private static final String ZUICHOU = "https://nmsl.shadiao.app/api.php";
    private static final String RAINBOW_FART = "https://chp.shadiao.app/api.php";
    private static final String TEMP = "./temp/";

    @Autowired
    ScoresService ScoresServiceImpl;

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
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), OTHER_HELP_MSG_P2);
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
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), BilibiliMsg_P2);
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
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), OTHER_HELP_MSG_P2);
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
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), BilibiliMsg_P2);
    }

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

            Byte[] bytesArr = new Byte[bytes.size()];
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg), String.valueOf(getChars(bytes.toArray(bytesArr))));
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg), String.valueOf(getChars(bytes.toArray(bytesArr))));
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
}
