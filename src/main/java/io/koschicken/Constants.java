package io.koschicken;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.koschicken.bean.GroupPower;
import io.koschicken.bean.HorseEvent;
import io.koschicken.bean.PrincessConfig;
import io.koschicken.utils.SafeProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static io.koschicken.PCRConstants.*;
import static io.koschicken.listener.PrincessIntercept.On;

public class Constants {

    public static final String HELP_MSG = "1.其他功能帮助\n" +
            "2.会战帮助\n" +
            "3.公会帮助\n" +
            "4.机器人设置\n" +
            "5.bilibili相关帮助\n" +
            "请选择命令提示菜单";
    public static final String BILIBILI_MSG = "以下全部为私聊\n" +
            "1.获取视频封面图片 [视频封面 av/bv号]\nav与bv需统一大小写\n例: 视频封面 av1145124 \n" +
            "2.av/bv转换 \n直接输入av/bv号即可 \n例: av11458\n" +
            "3.当前up直播状态 [直播 upuid]\n仅仅是b站直播，其他站的在计划中.jpg\n";
    public static final String BILIBILI_MSG_P2 =
            "4.设置开播提示 [设置开播提示 up的uid]\n" +
                    "5.查看开播提示槽位使用情况 [查看开播提示]\n" +
                    "6.清除一个开播槽位上的记录 [清除开播提示 槽位]\n示例 清除开播提示 1\n" +
                    "直播信息均一分钟刷新一次";
    public static final String CONFIG_MES = "机器人设置尚未开放";
    public static final String GROUP_HELP_MSG = "公会功能暂不开放";
    public static final String FIGHT_HELP_MSG = "会战功能暂不开放";
    public static final String OTHER_HELP_MSG = "杂项指令：\n" +
            "\t抽卡：[#井、#up井、#十连、#up十连、#抽卡 数量、#up抽卡 数量]\n" +
            "\t\t带up就是up池，不带up就是白金池\n" +
            "\t加密切噜语：[切噜 文本]\n" +
            "\t解密切噜语：[翻译切噜 文本]\n" +
            "\t赛马：[#赛马@机器人] 此时会开启赛马\n" +
            "\t\t押马[马的编号]#[马币数量] 下注\n" +
            "\t\t[#开始赛马@机器人] 此时会开始赛马\n";
    public static final String OTHER_HELP_MSG_P2 =
            "\t签到 [#给xcw上供、#签到、#上供] 每天只能签到一次，可以获取5000币\n" +
                    "\t私聊或者群里发送 [我有多少钱] 可以获取余额\n" +
                    "\t查看群内设置 [#查看本群设置]\n" +
                    "\t祖安模式 [#一键嘴臭]\n" +
                    "\t关键字：叫车、车来 可以让机器人发送一张图片，[关键字]+[空格]+[tag名]，可获取指定tag的图片，如 [叫车 东方]\n" +
                    "\t还有很多乱七八糟的功能群里问问开发机器人的家伙";
    public static final String CONFIG_DIR = "./config";
    public static final String CQ_AT = "[CQ:at,qq=";
    public static final String[] QIELU = {"切噜", "切哩", "切吉", "噜拉", "啪噜", "切璐", "扣", "啦哩", "啦嘟", "切泼", "啪噼", ",", "嚕嚕", "啰哩", "切拉", "切噼"};
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
    public static String ROBOT_QQ = "0";//机器人qq
    public static HashMap<String, Integer> RE_QIELU = new HashMap<>();
    public static boolean CAN_SEND_IMAGE = false;//这个机器人能不能发送图片的标记
    public static String IP;
    public static PrincessConfig PRINCESS_CONFIG;
    public static String[] EMOJI_LIST =
            new String[]{"🦄", "🐴", "🐺", "🐂", "🐄", "🐎", "🐇", "🐓", "🦏", "🐩", "🐮",
                    "🐵", "🐙", "💀", "🐤", "🐨", "🐮", "🐔", "🐸", "👻", "🐛", "🐠", "🐶",
                    "🐯", "🚽", "👨🏾‍", "🕊", "👴🏾", "🚗", "🦽", "🏎", "🚲", "🏍", "✈", "🔞", "  "};
    public static HorseEvent HORSE_EVENT;

    static {
        RE_QIELU.put("切噜", 0);
        RE_QIELU.put("切哩", 1);
        RE_QIELU.put("切吉", 2);
        RE_QIELU.put("噜拉", 3);
        RE_QIELU.put("啪噜", 4);
        RE_QIELU.put("切璐", 5);
        RE_QIELU.put("扣扣", 6);
        RE_QIELU.put("啦哩", 7);
        RE_QIELU.put("啦嘟", 8);
        RE_QIELU.put("切泼", 9);
        RE_QIELU.put("啪噼", 10);
        RE_QIELU.put("%%", 11);
        RE_QIELU.put("嚕嚕", 12);
        RE_QIELU.put("啰哩", 13);
        RE_QIELU.put("切拉", 14);
        RE_QIELU.put("切噼", 15);
    }

    private Constants() {
    }

    //加载配置文件
    public static void getFile() {
        File file = new File(CONFIG_DIR + "/config.txt");
        //群组设定
        if (!file.exists() || !file.isFile()) {
            //没有读取到配置文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //读取到了
            String jsonString;
            try {
                jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                Set<String> keySet = jsonObject.keySet();
                for (String s : keySet) {
                    String string = jsonObject.getJSONObject(s).toJSONString();
                    GroupPower keyValues = JSON.parseObject(string, GroupPower.class);
                    On.put(s, keyValues);
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    //加载通用配置
    public static void getConfig() {
        //通用设定
        File file = new File(CONFIG_DIR + "/通用配置.txt");
        if (!file.exists() || !file.isFile()) {
            try {
                freshConfig(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PRINCESS_CONFIG = new PrincessConfig("买药.png", 1000, 5,
                    true, true, true, true, "",
                    5000, 1000, "", true, "");
        }
        try {
            PRINCESS_CONFIG = loadConfig(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void freshConfig(File file) throws IOException {
        OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        SafeProperties pro = new SafeProperties();
        pro.addComment("这个图片应和jar在同一文件夹下");
        pro.setProperty("提醒买药小助手图片名", "买药.png");
        pro.setProperty("抽卡上限", "1000");
        pro.addComment("每次抽卡中间所需的冷却时间，单位为秒");
        pro.setProperty("抽卡冷却", "5");
        pro.addComment("以下四个为机器人开关的默认设置 true为开，false为关");
        pro.setProperty("总开关默认开启", "true");
        pro.setProperty("抽卡默认开启", "true");
        pro.setProperty("买药提醒默认开启", "true");
        pro.setProperty("赛马默认开启", "true");
        pro.addComment("主人qq相当于在所有群里对这个机器人有管理员权限");
        pro.setProperty("主人qq", "");
        pro.addComment("签到增加币数目，设置为负数则有可能会越签越少");
        pro.setProperty("签到一次金币", "5000");
        pro.addComment("发一次色图所要花费的币数量，设置为负数可能会越花越多");
        pro.setProperty("发一次色图花费", "1000");
        pro.addComment("loliconApi的APIKEY");
        pro.setProperty("LOLICON_API_KEY", "");
        pro.addComment("r18私聊开关");
        pro.setProperty("r18私聊", "true");
        pro.addComment("Bilibili的Cookie");
        pro.setProperty("bilibiliCookie", "");
        pro.store(outputStream, "通用配置");
        outputStream.close();
    }

    public static PrincessConfig loadConfig(File file) throws IOException {
        SafeProperties pro = new SafeProperties();
        InputStreamReader in;
        in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        pro.load(in);
        PrincessConfig princessConfig;
        princessConfig = new PrincessConfig(pro.getProperty("提醒买药小助手图片名"),
                Integer.parseInt(pro.getProperty("抽卡上限")),
                Integer.parseInt(pro.getProperty("抽卡冷却")),
                Boolean.parseBoolean(pro.getProperty("总开关默认开启")),
                Boolean.parseBoolean(pro.getProperty("抽卡默认开启")),
                Boolean.parseBoolean(pro.getProperty("买药提醒默认开启")),
                Boolean.parseBoolean(pro.getProperty("赛马默认开启")),
                pro.getProperty("主人qq"),
                Integer.parseInt(pro.getProperty("签到一次金币")),
                Integer.parseInt(pro.getProperty("发一次色图花费")),
                pro.getProperty("LOLICON_API_KEY"),
                Boolean.parseBoolean(pro.getProperty("r18私聊")),
                pro.getProperty("bilibiliCookie")
        );
        in.close();
        return princessConfig;
    }

    //刷新写入配置文件
    public static synchronized void setJson() {
        String jsonObject = JSON.toJSONString(On);
        try {
            File file = new File(CONFIG_DIR + "/config.txt");
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }
            FileUtils.write(file, jsonObject, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读好坏事
    @SuppressWarnings("unchecked")
    public static synchronized void getEvent() {
        HORSE_EVENT = new HorseEvent();
        String jsonObject = JSON.toJSONString(HORSE_EVENT);
        try {
            File file = new File(CONFIG_DIR + "/事件.txt");
            if (!file.exists() || !file.isFile()) {
                if (file.createNewFile()) {
                    FileUtils.write(file, jsonObject, "utf-8");
                }
            } else {
                JSONObject jsonObject1 = JSON.parseObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                List<String> bedHorseEvent = JSON.parseObject(jsonObject1.get("bedHorseEvent").toString(), List.class);
                List<String> goodHorseEvent = JSON.parseObject(jsonObject1.get("goodHorseEvent").toString(), List.class);
                HORSE_EVENT.getGoodHorseEvent().addAll(goodHorseEvent);
                HORSE_EVENT.getBedHorseEvent().addAll(bedHorseEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读扭蛋信息
    public static synchronized void getGachaConfig() {
        try {
            File file = new File(CONFIG_DIR + "/扭蛋.txt");
            if (!file.exists() || !file.isFile()) {
                if (file.createNewFile()) {
                    //准备内置的转蛋信息写入内存
                    //up池
                    JSONObject upGacha = new JSONObject();
                    upGacha.put("三星总概率", SSR_CHANCE);
                    upGacha.put("二星总概率", SR_CHANCE);
                    upGacha.put("一星总概率", R_CHANCE);
                    upGacha.put("三星人物池（除去up角）", NO_UP_SSR);
                    upGacha.put("二星人物池（除去up角）", NO_UP_SR);
                    upGacha.put("一星人物池（除去up角）", NO_UP_R);
                    upGacha.put("三星人物池（up角）", SSR_UP);
                    upGacha.put("二星人物池（up角）", SR_UP);
                    upGacha.put("一星人物池（up角）", R_UP);
                    upGacha.put("三星up总概率", UP_SSR_CHANCE);
                    upGacha.put("二星up总概率", UP_SR_CHANCE);
                    upGacha.put("一星up总概率", UP_R_CHANCE);
                    //白金池
                    JSONObject gacha = new JSONObject();
                    gacha.put("三星总概率", PLATINUM_SSR_CHANCE);
                    gacha.put("二星总概率", PLATINUM_SR_CHANCE);
                    gacha.put("一星总概率", PLATINUM_R_CHANCE);
                    gacha.put("三星人物池", SSR);
                    gacha.put("二星人物池", SR);
                    gacha.put("一星人物池", R);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("up池信息", upGacha);
                    jsonObject.put("白金池信息", gacha);
                    FileUtils.write(file, jsonObject.toJSONString(), "utf-8");
                }
            } else {
                JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                JSONObject gacha = jsonObject.getJSONObject("白金池信息");
                JSONObject upGacha = jsonObject.getJSONObject("up池信息");
                PLATINUM_R_CHANCE = (int) gacha.get("一星总概率");
                PLATINUM_SR_CHANCE = (int) gacha.get("二星总概率");
                PLATINUM_SSR_CHANCE = (int) gacha.get("三星总概率");
                R = Arrays.stream(gacha.getJSONArray("一星人物池").toArray()).map(Object::toString).toArray(String[]::new);
                SR = Arrays.stream(gacha.getJSONArray("二星人物池").toArray()).map(Object::toString).toArray(String[]::new);
                SSR = Arrays.stream(gacha.getJSONArray("三星人物池").toArray()).map(Object::toString).toArray(String[]::new);

                R_CHANCE = upGacha.getInteger("一星总概率");
                SR_CHANCE = upGacha.getInteger("二星总概率");
                SSR_CHANCE = upGacha.getInteger("三星总概率");
                NO_UP_R = Arrays.stream(upGacha.getJSONArray("一星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                NO_UP_SR = Arrays.stream(upGacha.getJSONArray("二星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                NO_UP_SSR = Arrays.stream(upGacha.getJSONArray("三星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                R_UP = Arrays.stream(upGacha.getJSONArray("一星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);
                SR_UP = Arrays.stream(upGacha.getJSONArray("二星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);
                SSR_UP = Arrays.stream(upGacha.getJSONArray("三星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);

                UP_R_CHANCE = upGacha.getInteger("一星up总概率");
                UP_SR_CHANCE = upGacha.getInteger("二星up总概率");
                UP_SSR_CHANCE = upGacha.getInteger("三星up总概率");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LOGGER.error("扭蛋配置文件错误，是否删除了一项？");
        }
    }
}
