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
import io.koschicken.database.service.PicService;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.SetuUtils.getSetu;

@Service
public class SetuListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetuListener.class);
    private static final String TEMP = "./temp/";
    private static final String ARTWORK_PREFIX = "https://www.pixiv.net/artworks/";
    private static final String ARTIST_PREFIX = "https://www.pixiv.net/users/";
    private static final int cd = 20;
    private static HashMap<String, LocalDateTime> coolDown;
    @Autowired
    ScoresService scoresServiceImpl;

    @Autowired
    PicService picServiceImpl;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"叫车(.*)(.*)?(|r18)", "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]"})
    public void jiaoche(GroupMsg msg, MsgSender sender) {
        // sender.SENDER.sendGroupMsg(msg.getGroupCode(), "别叫了，群主不喜欢");
        if (isCool(msg.getQQ())) {
            Scores coin = scoresServiceImpl.getById(msg.getCodeNumber());
            if (coin == null) {
                createScore(msg, sender);
            } else if (coin.getScore() >= princessConfig.getSetuCoin()) {
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
                while (m.find()) {
                    // 兼容原有的叫车功能
                    if (message.startsWith("叫车")) {
                        try {
                            num = Integer.parseInt(m.group(2).trim());
                        } catch (NumberFormatException e) {
                            LOGGER.info("数量不是数字，默认为1");
                        }
                        tag = m.group(1).trim();
                    } else {
                        try {
                            num = Integer.parseInt(m.group(1).trim());
                        } catch (NumberFormatException e) {
                            LOGGER.info("数量不是数字，默认为1");
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
                    int random = RandomUtils.nextInt(0, 100);
                    LOGGER.info("这次roll的点数是： {}", random);
                    if (!canSendImage) {
                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "机器人还不能发图片");
                        return;
                    }
                    SendSetu sendSetu = new SendSetu(msg.getGroupCode(), sender, tag, num, r18, coin, scoresServiceImpl, picServiceImpl);
                    sendSetu.start();
                    refreshCooldown(msg.getQQ());
                }
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" +
                        "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
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
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" +
                "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
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
            boolean delete = pic.delete();
            //LOGGER.info("文件删除成功了吗？{}", delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"叫车 (.*?)[点丶份张幅](.*?)的?(|r18)", "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]"})
    public void config(PrivateMsg msg, MsgSender sender) {

        if (isCool(msg.getQQ())) {
            if (!canSendImage) {
                sender.SENDER.sendGroupMsg(msg.getQQCode(), "机器人还不能发图片");
                return;
            }
            Scores coin = scoresServiceImpl.getById(msg.getCodeNumber());
            if (coin == null) {
                createScore(msg, sender);
            } else if (coin.getScore() >= princessConfig.getSetuCoin()) {
                String message = msg.getMsg();
                String regex;
                if (message.startsWith("叫车")) {
                    regex = "叫车(.*?)(|r18)";
                } else {
                    regex = "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]";
                }
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(message);
                int num = 1;
                String tag = "";
                boolean r18 = false;
                while (m.find()) {
                    try {
                        num = Integer.parseInt(m.group(1));
                    } catch (NumberFormatException e) {
                        LOGGER.info("数量不是数字，默认为1");
                    }
                    tag = m.group(2);
                    r18 = !StringUtils.isEmpty(m.group(3));
                }
                // 发图
                SendSetu sendSetu = new SendSetu(msg.getQQ(), sender, tag, num, r18, coin, scoresServiceImpl, picServiceImpl);
                sendSetu.start();
                refreshCooldown(msg.getQQ());
            } else {
                sender.SENDER.sendPrivateMsg(msg.getQQCode(), "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
            }
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "叫车CD中...");
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
    private void refreshCooldown(String QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (coolDown == null) {
            coolDown = new HashMap<>();
        }
        coolDown.put(QQ, localDateTime.plusSeconds(cd));
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

    class SendSetu extends Thread {
        private final String sendQQ;
        private final MsgSender sender;
        private final String tag;
        private final Integer num;
        private final Boolean r18;
        private final Scores coin;
        private final ScoresService scoresService;
        private final PicService picService;

        public SendSetu(String sendQQ, MsgSender sender, String tag, Integer num, Boolean r18,
                        Scores coin, ScoresService scoresService, PicService picService) {
            this.sendQQ = sendQQ;
            this.sender = sender;
            this.tag = tag;
            this.num = num;
            this.r18 = r18;
            this.coin = coin;
            this.scoresService = scoresService;
            this.picService = picService;
        }

        @Override
        public void run() {
            try {
                List<Pixiv> setu = getSetu(tag, num, r18);
                Pixiv pixiv = setu.get(0);
                // LOGGER.info("pixiv: {}", pixiv);
                String code = pixiv.getCode();
                boolean fromLolicon = "0".equals(code);
                if ("200".equals(code) || fromLolicon) {
                    coin.setScore(coin.getScore() - princessConfig.getSetuCoin() * num);
                    scoresService.updateById(coin);
                    for (Pixiv p : setu) {
                        String filename = p.getFileName();
                        URL imageUrl;
                        File pic;
                        if (filename.contains("http")) {
                            imageUrl = new URL(filename);
                            pic = new File(TEMP + filename.substring(filename.lastIndexOf("/") + 1));
                        } else {
                            imageUrl = new URL(p.getOriginal().replace("pximg.net", "pixiv.cat"));
                            pic = new File(TEMP + filename);
                        }
                        FileUtils.copyURLToFile(imageUrl, pic);
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
                        sender.SENDER.sendGroupMsg(sendQQ, message);
                    }
                } else {
                    sender.SENDER.sendGroupMsg(sendQQ, pixiv.getMsg());
                }
            } catch (IOException e) {
                e.printStackTrace();
                sender.SENDER.sendGroupMsg(sendQQ, "炸了");
            }
        }
    }
}
