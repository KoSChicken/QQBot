package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import io.koschicken.bean.Pixiv;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.SetuUtils.getSetu;

@Service
public class SetuListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetuListener.class);
    private static final String TEMP = "./temp/SETU/";
    private static final String ARTWORK_PREFIX = "https://www.pixiv.net/artworks/";
    private static final String ARTIST_PREFIX = "https://www.pixiv.net/users/";
    private static final String AWSL = "https://setu.awsl.ee/api/setu!";
    private static final String MJX = "https://api.66mz8.com/api/rand.tbimg.php?format=pic";
    private static final String UA = "User-Agent";
    private static final String UA_STRING = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3";
    private static final int CD = 20;
    private static final HashMap<String, Integer> NUMBER;
    private static HashMap<String, HashMap<String, LocalDateTime>> coolDown;

    static {
        NUMBER = new HashMap<>();
        NUMBER.put("一", 1);
        NUMBER.put("二", 2);
        NUMBER.put("俩", 2);
        NUMBER.put("两", 2);
        NUMBER.put("三", 3);
        NUMBER.put("四", 4);
        NUMBER.put("五", 5);
        NUMBER.put("六", 6);
        NUMBER.put("七", 7);
        NUMBER.put("八", 8);
        NUMBER.put("九", 9);
        NUMBER.put("十", 10);
        NUMBER.put("几", RandomUtils.nextInt(1, 4));
    }

    static {
        File setuFolder = new File(TEMP);
        if (!setuFolder.exists()) {
            try {
                FileUtils.forceMkdir(setuFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private ScoresService scoresServiceImpl;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"叫车(.*)(.*)?(|r18)", "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]"})
    public void jiaoche(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQ(), msg.getGroupCode())) {
            Scores coin = scoresServiceImpl.getById(msg.getCodeNumber());
            if (coin == null) {
                createScore(msg, sender);
            } else if (coin.getScore() >= PRINCESS_CONFIG.getSetuCoin()) {
                String message = msg.getMsg();
                String regex;
                if (message.startsWith("叫车")) {
                    regex = "叫车(.*)(.*)?(|r18)";
                } else {
                    regex = "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]";
                }
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(message);
                int num = 1;
                String tag = "";
                boolean r18 = false;
                String number;
                while (m.find()) {
                    // 兼容原有的叫车功能
                    if (message.startsWith("叫车")) {
                        number = m.group(2).trim();
                        try {
                            if (NUMBER.get(number) == null) {
                                num = Integer.parseInt(number);
                            } else {
                                num = NUMBER.get(number);
                            }
                        } catch (NumberFormatException ignore) {
                            LOGGER.info("number set to 1");
                        }
                        tag = m.group(1).trim();
                    } else {
                        try {
                            number = m.group(1).trim();
                            if (NUMBER.get(number) == null) {
                                num = Integer.parseInt(number);
                            } else {
                                num = NUMBER.get(number);
                            }
                        } catch (NumberFormatException ignore) {
                            LOGGER.info("number set to 1");
                        }
                        tag = m.group(2).trim();
                    }
                    r18 = !StringUtils.isEmpty(m.group(3).trim());
                }
                // 发图
                Long QQ = scoresServiceImpl.findQQByNickname(tag);
                if (QQ != null) {
                    groupMember(msg, sender, QQ);
                } else {
                    if (!CAN_SEND_IMAGE) {
                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "机器人还不能发图片");
                        return;
                    }
                    SendSetu sendSetu = new SendSetu(msg.getGroupCode(), msg.getQQ(), sender, tag, num, r18, coin, scoresServiceImpl);
                    sendSetu.start();
                    refreshCooldown(msg.getQQ(), msg.getGroupCode());
                }
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" +
                        "你没钱了，请尝试签到或找开发者PY");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "叫车CD中...");
        }
    }

    private void createScore(GroupMsg msg, MsgSender sender) {
        Scores scores = new Scores();
        scores.setiSign(false);
        scores.setQQ(msg.getCodeNumber());
        scores.setScore(0);
        scoresServiceImpl.save(scores);
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQ_AT + msg.getQQCode() + "]" +
                "你没钱了，请尝试签到或找开发者PY");
    }

    private void groupMember(GroupMsg msg, MsgSender sender, Long QQ) {
        String AVATAR_API = "http://thirdqq.qlogo.cn/g?b=qq&nk=";
        String api = AVATAR_API + QQ.toString() + "&s=640";
        try {
            InputStream imageStream = Request.Get(api).execute().returnResponse().getEntity().getContent();
            File pic = new File(TEMP + QQ.toString() + System.currentTimeMillis() + ".jpg");
            FileUtils.copyInputStreamToFile(imageStream, pic);
            CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(pic.getAbsolutePath());
            LOGGER.info(pic.getAbsolutePath());
            String message = cqCodeImage.toString();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), message);
            FileUtils.deleteQuietly(pic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"叫车(.*)(.*)?(|r18)", "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]"})
    public void siche(PrivateMsg msg, MsgSender sender) {
        if (isCool(msg.getQQ(), "0")) {
            if (!CAN_SEND_IMAGE) {
                sender.SENDER.sendGroupMsg(msg.getQQCode(), "机器人还不能发图片");
                return;
            }
            Scores coin = scoresServiceImpl.getById(msg.getCodeNumber());
            if (coin == null) {
                createScore(msg, sender);
            } else if (coin.getScore() >= PRINCESS_CONFIG.getSetuCoin()) {
                String message = msg.getMsg();
                String regex;
                if (message.startsWith("叫车")) {
                    regex = "叫车(.*)(.*)?(|r18)";
                } else {
                    regex = "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]";
                }
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(message);
                int num = 1;
                String tag = "";
                boolean r18 = false;
                String number;
                while (m.find()) {
                    // 兼容原有的叫车功能
                    if (message.startsWith("叫车")) {
                        number = m.group(2).trim();
                        try {
                            if (NUMBER.get(number) == null) {
                                num = Integer.parseInt(number);
                            } else {
                                num = NUMBER.get(number);
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.info("不是数字，默认为1");
                        }
                        tag = m.group(1).trim();
                    } else {
                        try {
                            number = m.group(1).trim();
                            if (NUMBER.get(number) == null) {
                                num = Integer.parseInt(number);
                            } else {
                                num = NUMBER.get(number);
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.info("不是数字，默认为1");
                        }
                        tag = m.group(2).trim();
                    }
                    r18 = !StringUtils.isEmpty(m.group(3).trim());
                }
                // 发图
                SendSetu sendSetu = new SendSetu(null, msg.getQQ(), sender, tag, num, r18, coin, scoresServiceImpl);
                sendSetu.start();
                refreshCooldown(msg.getQQ(), "0");
            } else {
                sender.SENDER.sendPrivateMsg(msg.getQQCode(), "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
            }
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "叫车CD中...");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#awsl")
    public void awsl(GroupMsg msg, MsgSender sender) throws IOException {
        String groupCode = msg.getGroupCode();
        if (isCool(msg.getQQ(), groupCode)) {
            HttpResponse httpResponse = Request.Get(AWSL).addHeader(UA, UA_STRING).execute().returnResponse();
            InputStream content = httpResponse.getEntity().getContent();
            String uuid = UUID.randomUUID().toString();
            Path path = Paths.get(TEMP + uuid + ".jpg");
            Files.copy(content, path);
            CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(path.toAbsolutePath().toString());
            sender.SENDER.sendGroupMsg(groupCode, cqCodeImage.toString());
        } else {
            sender.SENDER.sendGroupMsg(groupCode, "CD中...");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#mjx")
    public void mjx(GroupMsg msg, MsgSender sender) throws IOException {
        String groupCode = msg.getGroupCode();
        if (isCool(msg.getQQ(), groupCode)) {
            InputStream content = Request.Get(MJX)
                    .setHeader(UA, UA_STRING)
                    .execute().returnResponse().getEntity().getContent();
            String uuid = UUID.randomUUID().toString();
            Path path = Paths.get(TEMP + uuid + ".jpg");
            Files.copy(content, path);
            CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(path.toAbsolutePath().toString());
            sender.SENDER.sendGroupMsg(groupCode, cqCodeImage.toString());
        } else {
            sender.SENDER.sendGroupMsg(groupCode, "CD中...");
        }
    }

    private void createScore(PrivateMsg msg, MsgSender sender) {
        Scores scores = new Scores();
        scores.setiSign(false);
        scores.setQQ(msg.getCodeNumber());
        scores.setScore(0);
        scoresServiceImpl.save(scores);
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
    }

    /**
     * 刷新冷却时间
     */
    private void refreshCooldown(String QQ, String groupCode) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (coolDown == null) {
            coolDown = new HashMap<>();
        }
        HashMap<String, LocalDateTime> hashMap = coolDown.get(groupCode);
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        hashMap.put(QQ, localDateTime.plusSeconds(CD));
        coolDown.put(groupCode, hashMap);
    }

    /**
     * 获取冷却时间是不是到了
     *
     * @param QQ
     */
    private boolean isCool(String QQ, String groupCode) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            HashMap<String, LocalDateTime> hashMap = coolDown.get(groupCode);
            if (hashMap != null) {
                LocalDateTime localDateTime = hashMap.get(QQ);
                if (localDateTime != null) {
                    return localDateTime.isBefore(LocalDateTime.now());
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    static class SendSetu extends Thread {
        private final String groupCode;
        private final String privateQQ;
        private final MsgSender sender;
        private final String tag;
        private final Integer num;
        private final Boolean r18;
        private final Scores coin;
        private final ScoresService scoresService;

        public SendSetu(String groupCode, String privateQQ, MsgSender sender, String tag, Integer num,
                        Boolean r18, Scores coin, ScoresService scoresService) {
            this.groupCode = groupCode;
            this.privateQQ = privateQQ;
            this.sender = sender;
            this.tag = tag;
            this.num = num;
            this.r18 = r18;
            this.coin = coin;
            this.scoresService = scoresService;
        }

        @Override
        public void run() {
            int sendCount = 0; // 记录实际发送的图片张数
            try {
                List<Pixiv> setu = getSetu(tag, num, r18);
                Pixiv pixiv = setu.get(0);
                String code = pixiv.getCode();
                boolean fromLolicon = "0".equals(code);
                if ("200".equals(code) || fromLolicon) {
                    for (Pixiv p : setu) {
                        String filename = p.getFileName();
                        File pic;
                        String imageUrl;
                        if (filename.contains("http")) {
                            imageUrl = filename;
                            pic = new File(TEMP + filename.substring(filename.lastIndexOf("/") + 1));
                        } else {
                            imageUrl = p.getOriginal().replace("pximg.net", "pixiv.cat");
                            pic = new File(TEMP + filename);
                        }
                        InputStream content = Request.Get(imageUrl)
                                .setHeader(UA, UA_STRING)
                                .execute().returnResponse().getEntity().getContent();
                        if (!pic.exists()) {
                            FileUtils.copyInputStreamToFile(content, pic);
                        }
                        // 发送图片
                        CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(pic.getAbsolutePath());
                        String message = cqCodeImage.toString() + "\n" +
                                p.getTitle() + "\n" +
                                ARTWORK_PREFIX + p.getArtwork() + "\n" +
                                p.getAuthor() + "\n" +
                                ARTIST_PREFIX + p.getArtist() + "\n";
                        // + "tags:" + Arrays.toString(p.getTags());
                        if (fromLolicon) {
                            message += "\n" + "今日剩余额度：" + p.getQuota();
                        }
                        if (StringUtils.isEmpty(groupCode)) { // 不是群消息，则直接私聊
                            sender.SENDER.sendPrivateMsg(privateQQ, message);
                        } else {
                            if (!p.isR18()) { // 非R18且叫车的是群消息
                                sender.SENDER.sendGroupMsg(groupCode, message);
                            } else {  // R18则发送私聊
                                sender.SENDER.sendPrivateMsg(privateQQ, message);
                            }
                        }
                        sendCount++;
                    }
                    coin.setScore(coin.getScore() - PRINCESS_CONFIG.getSetuCoin() * sendCount);
                    scoresService.updateById(coin); // 按照实际发送的张数来扣除叫车者的币
                } else {
                    String msg = "没找到这样的色图，要不你发一张？";
                    if (StringUtils.isEmpty(groupCode)) {
                        sender.SENDER.sendPrivateMsg(privateQQ, msg);
                    } else {
                        sender.SENDER.sendGroupMsg(groupCode, msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                sender.SENDER.sendGroupMsg(groupCode, "炸了");
            }
        }
    }
}
