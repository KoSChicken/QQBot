package io.koschicken;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.koschicken.bean.GroupPower;
import io.koschicken.bean.HorseEvent;
import io.koschicken.bean.PrincessConfig;
import io.koschicken.utils.SafeProperties;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static io.koschicken.listener.PrincessIntercept.On;

public class Constants {
    public static final String helpMsg = "1.其他功能帮助\n" +
            "2.会战帮助\n" +
            "3.公会帮助\n" +
            "4.机器人设置\n" +
            "5.bilibili相关帮助\n" +
            "请选择命令提示菜单";

    public static final String BilibiliMsg = "以下全部为私聊\n" +
            "1.获取视频封面图片 [视频封面 av/bv号]\nav与bv需统一大小写\n例: 视频封面 av1145124 \n" +
            "2.av/bv转换 \n直接输入av/bv号即可 \n例: av11458\n" +
            "3.当前up直播状态 [直播 upuid]\n仅仅是b站直播，其他站的在计划中.jpg\n" +
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
            "\t\t[#开始赛马@机器人] 此时会开始赛马\n" +
            "\t签到 [#给xcw上供、#签到、#上供] 每天只能签到一次，可以获取5000币\n" +
            "\t私聊或者群里发送 [我有多少钱] 可以获取余额\n" +
            "\t查看群内设置 [#查看本群设置]\n" +
            "\t祖安模式 [#一键嘴臭]\n" +
            "\t关键字：叫车、车来 可以让机器人发送一张图片，[关键字]+[空格]+[tag名]，可获取指定tag的图片，如 [叫车 东方]\n" +
            "\t还有很多乱七八糟的功能群里问问开发机器人的家伙";

    public static final String configDir = "./src/main/resources/config";
    public static final String coolQAt = "[CQ:at,qq=";
    public static final String[] QieLU = {"切噜", "切哩", "切吉", "噜拉", "啪噜", "切璐", "扣", "啦哩", "啦嘟", "切泼", "啪噼", ",", "嚕嚕", "啰哩", "切拉", "切噼"};
    public static String robotQQ = "0";//机器人qq
    public static String[] R = {"日和莉", "怜", "未奏希", "胡桃", "依里", "由加莉", "铃莓", "碧", "美咲", "莉玛"};
    public static String[] SR = {"茜里", "宫子", "雪", "铃奈", "香织", "美美", "惠理子", "忍", "真阳", "栞", "千歌", "空花", "珠希", "美冬", "深月", "铃", "绫音", "美里"};
    public static String[] SSR = {"杏奈", "真步", "璃乃", "初音", "依绪", "咲恋", "望", "妮胧", "秋乃", "真琴", "静流", "莫妮卡", "姬塔", "纯", "亚里莎", "冰川镜华"};
    public static String[] noUpSSR = {"杏奈", "真步", "璃乃", "依绪", "望", "妮胧", "秋乃", "真琴", "静流", "莫妮卡", "姬塔", "纯", "咲恋", "冰川镜华"};
    public static String[] noUpSR = {"紡希", "绫音", "茜里", "宫子", "雪", "铃奈", "香织", "美美", "惠理子", "忍", "真阳", "栞", "千歌", "空花", "珠希", "美冬", "深月", "铃"};
    public static String[] noUpR = {"胡桃", "铃莓", "日和莉", "怜", "未奏希", "依里", "由加莉", "美咲", "莉玛", "茉莉"};
    public static String[] SSRUp = {"初音"};
    public static String[] SRUp = {"美里"};
    public static String[] RUp = {"碧"};
    public static int SSRChance = 25;
    public static int SRChance = 180;
    public static int RChance = 795;
    public static int platinumSSRChance = 25;
    public static int platinumSRChance = 180;
    public static int platinumRChance = 795;
    public static int upSSRChance = 7;
    public static int upSRChance = 30; //30;
    public static int upRChance = 100; //200;
    public static HashMap<String, Integer> reQieLU = new HashMap<>();
    public static boolean canSendImage = false;//这个机器人能不能发送图片的标记
    public static String ip;
    public static PrincessConfig princessConfig;
    public static String[] emojis = new String[]{"\uD83E\uDD84", "\uD83D\uDC34", "\uD83D\uDC3A", "\uD83D\uDC02", "\uD83D\uDC04", "\uD83D\uDC0E", "\uD83D\uDC07", "\uD83D\uDC13", "\uD83E\uDD8F", "\uD83D\uDC29", "\uD83D\uDC2E", "\uD83D\uDC35", "\uD83D\uDC19", "\uD83D\uDC80", "\uD83D\uDC24", "\uD83D\uDC28", "\uD83D\uDC2E", "\uD83D\uDC14", "\uD83D\uDC38", "\uD83D\uDC7B", "\uD83D\uDC1B", "\uD83D\uDC20", "\uD83D\uDC36", "\uD83D\uDC2F", "  ", "\uD83D\uDEBD"};
    public static HorseEvent horseEvent;

    static {
        reQieLU.put("切噜", 0);
        reQieLU.put("切哩", 1);
        reQieLU.put("切吉", 2);
        reQieLU.put("噜拉", 3);
        reQieLU.put("啪噜", 4);
        reQieLU.put("切璐", 5);
        reQieLU.put("扣扣", 6);
        reQieLU.put("啦哩", 7);
        reQieLU.put("啦嘟", 8);
        reQieLU.put("切泼", 9);
        reQieLU.put("啪噼", 10);
        reQieLU.put("%%", 11);
        reQieLU.put("嚕嚕", 12);
        reQieLU.put("啰哩", 13);
        reQieLU.put("切拉", 14);
        reQieLU.put("切噼", 15);
    }

    //加载配置文件
    public static void getFile() {
        File file = new File(configDir + "/config.txt");
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
                jsonString = FileUtils.readFileToString(file, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                Set<String> keySet = jsonObject.keySet();
                for (String s : keySet) {
                    String string = jsonObject.getJSONObject(s).toJSONString();
                    GroupPower keyValues = JSONArray.parseObject(string, GroupPower.class);
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
        File file = new File(configDir + "/通用配置.txt");
        if (!file.exists() || !file.isFile()) {
            try {
                freshConfig(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            princessConfig = new PrincessConfig("买药.png", 1000, 5,
                    true, true, true, true, "",
                    5000, 1000, "");
        }
        try {
            princessConfig = loadConfig(file);
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
                pro.getProperty("LOLICON_API_KEY")
        );
        in.close();
        return princessConfig;
    }

    //刷新写入配置文件
    public synchronized static void setJson() {
        String jsonObject = JSONObject.toJSONString(On);
        try {
            File file = new File(configDir + "/config.txt");
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }
            FileUtils.write(file, jsonObject, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读好坏事
    public synchronized static void getEvent() {
        horseEvent = new HorseEvent();
        String jsonObject = JSONObject.toJSONString(horseEvent);
        try {
            File file = new File(configDir + "/事件.txt");
            if (!file.exists() || !file.isFile()) {
                if (file.createNewFile()) {
                    FileUtils.write(file, jsonObject, "utf-8");
                }
            } else {
                JSONObject jsonObject1 = JSONObject.parseObject(FileUtils.readFileToString(file, "utf-8"));
                List bedHorseEvent = JSONArray.parseObject(jsonObject1.get("bedHorseEvent").toString(), List.class);
                List goodHorseEvent = JSONArray.parseObject(jsonObject1.get("goodHorseEvent").toString(), List.class);
                horseEvent.getGoodHorseEvent().addAll(goodHorseEvent);
                horseEvent.getBedHorseEvent().addAll(bedHorseEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读扭蛋信息
    public synchronized static void getGachaConfig() {
        try {
            File file = new File(configDir + "/扭蛋.txt");
            if (!file.exists() || !file.isFile()) {
                if (file.createNewFile()) {
                    //准备内置的转蛋信息写入内存
                    //up池
                    JSONObject upGacha = new JSONObject();
                    upGacha.put("三星总概率", SSRChance);
                    upGacha.put("二星总概率", SRChance);
                    upGacha.put("一星总概率", RChance);
                    upGacha.put("三星人物池（除去up角）", noUpSSR);
                    upGacha.put("二星人物池（除去up角）", noUpSR);
                    upGacha.put("一星人物池（除去up角）", noUpR);
                    upGacha.put("三星人物池（up角）", SSRUp);
                    upGacha.put("二星人物池（up角）", SRUp);
                    upGacha.put("一星人物池（up角）", RUp);
                    upGacha.put("三星up总概率", upSSRChance);
                    upGacha.put("二星up总概率", upSRChance);
                    upGacha.put("一星up总概率", upRChance);
                    //白金池
                    JSONObject gacha = new JSONObject();
                    gacha.put("三星总概率", platinumSSRChance);
                    gacha.put("二星总概率", platinumSRChance);
                    gacha.put("一星总概率", platinumRChance);
                    gacha.put("三星人物池", SSR);
                    gacha.put("二星人物池", SR);
                    gacha.put("一星人物池", R);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("up池信息", upGacha);
                    jsonObject.put("白金池信息", gacha);
                    FileUtils.write(file, jsonObject.toJSONString(), "utf-8");
                }
            } else {
                JSONObject jsonObject = JSONObject.parseObject(FileUtils.readFileToString(file, "utf-8"));
                JSONObject gacha = (JSONObject) jsonObject.get("白金池信息");
                JSONObject upGacha = (JSONObject) jsonObject.get("up池信息");
                platinumRChance = (int) gacha.get("一星总概率");
                platinumSRChance = (int) gacha.get("二星总概率");
                platinumSSRChance = (int) gacha.get("三星总概率");
                R = Arrays.stream(gacha.getJSONArray("一星人物池").toArray()).map(Object::toString).toArray(String[]::new);
                SR = Arrays.stream(gacha.getJSONArray("二星人物池").toArray()).map(Object::toString).toArray(String[]::new);
                SSR = Arrays.stream(gacha.getJSONArray("三星人物池").toArray()).map(Object::toString).toArray(String[]::new);

                RChance = upGacha.getInteger("一星总概率");
                SRChance = upGacha.getInteger("二星总概率");
                SSRChance = upGacha.getInteger("三星总概率");
                noUpR = Arrays.stream(upGacha.getJSONArray("一星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                noUpSR = Arrays.stream(upGacha.getJSONArray("二星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                noUpSSR = Arrays.stream(upGacha.getJSONArray("三星人物池（除去up角）").toArray()).map(Object::toString).toArray(String[]::new);
                RUp = Arrays.stream(upGacha.getJSONArray("一星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);
                SRUp = Arrays.stream(upGacha.getJSONArray("二星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);
                SSRUp = Arrays.stream(upGacha.getJSONArray("三星人物池（up角）").toArray()).map(Object::toString).toArray(String[]::new);

                upRChance = upGacha.getInteger("一星up总概率");
                upSRChance = upGacha.getInteger("二星up总概率");
                upSSRChance = upGacha.getInteger("三星up总概率");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("扭蛋配置文件错误，是否删除了一项？");
        }
    }
}
