package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import io.koschicken.bean.Pixiv;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.SetuUtils.getSetu;

@Service
public class SetuListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetuListener.class);
    private static final String PREFIX = "https://cdn.jsdelivr.net/gh/laosepi/setu/pics/";
    private static final String TEMP = "./temp/";
    private static final String ARTWORK_PREFIX = "https://www.pixiv.net/artworks/";
    private static final String ARTIST_PREFIX = "https://www.pixiv.net/users/";

    @Autowired
    ScoresService ScoresServiceImpl;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"叫车.*", "车来.*", "来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩][图圖]"})
    public void jiaoche(GroupMsg msg, MsgSender sender) {
        // sender.SENDER.sendGroupMsg(msg.getGroupCode(), "别叫了，群主不喜欢");
        Scores coin = ScoresServiceImpl.getById(msg.getCodeNumber());
        if (coin == null) {
            Scores scores = new Scores();
            scores.setiSign(false);
            scores.setQQ(msg.getCodeNumber());
            scores.setScore(0);
            ScoresServiceImpl.save(scores);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" +
                    "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
        } else if (coin.getScore() >= princessConfig.getSetuCoin()) {
            // 发图
            int random = RandomUtils.nextInt(0, 100);
            LOGGER.info("这次roll的点数是： {}", random);
            if (random >= 30) {
                if (!canSendImage) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "机器人还不能发图片");
                    return;
                }
                // int num = RandomUtils.nextInt(1, 10);
                int num = 1; // 暂时限制为1
                // int type = RandomUtils.nextInt(0, 3);
                Integer type = random >= 90 ? null : 0; // 尽量减少R-18出现
                String tag = "";
                String[] split = msg.getMsg().split(" +");
                if (split.length > 2) {
                    tag = split[2];
                } else if (split.length > 1) {
                    tag = split[1];
                }
                if ("杏子".equals(tag)) {
                    String AVATAR_API = "http://thirdqq.qlogo.cn/g?b=qq&nk=";
                    String XINGZI = "670238987";
                    String api = AVATAR_API + XINGZI + "&s=640";
                    try {
                        InputStream imageStream = Request.Get(api).execute().returnResponse().getEntity().getContent();
                        File pic = new File(TEMP + "杏子.jpg");
                        FileUtils.copyInputStreamToFile(imageStream, pic);
                        CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(pic.getAbsolutePath());
                        LOGGER.info(pic.getAbsolutePath());
                        String message = cqCodeImage.toString();
                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), message);
                        boolean delete = pic.delete();
                        LOGGER.info("文件删除成功了吗？{}", delete);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendSetu sendSetu = new sendSetu(msg.getGroupCode(), sender, tag, num, type, coin, ScoresServiceImpl);
                    sendSetu.start();
                }
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "歇会，不要贪杯");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" +
                    "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"图来！", "图来"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void config(PrivateMsg msg, MsgSender sender) {
        if (!canSendImage) {
            sender.SENDER.sendGroupMsg(msg.getQQCode(), "机器人还不能发图片");
            return;
        }
        Scores coin = ScoresServiceImpl.getById(msg.getCodeNumber());
        if (coin == null) {
            Scores scores = new Scores();
            scores.setiSign(false);
            scores.setQQ(msg.getCodeNumber());
            scores.setScore(0);
            ScoresServiceImpl.save(scores);
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
        } else if (coin.getScore() >= princessConfig.getSetuCoin()) {
            // int num = RandomUtils.nextInt(1, 10);
            int num = 1; // 暂时限制为1
            // int type = RandomUtils.nextInt(0, 3);
            Integer type = null;
            String tag = "";
            String[] split = msg.getMsg().split(" +");
            if (split.length > 1) {
                tag = split[2];
            }
            sendSetu sendSetu = new sendSetu(msg.getQQ(), sender, tag, num, type, coin, ScoresServiceImpl);
            sendSetu.start();
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "你没钱了，请发送#签到获取今日5000币，如果已获取过请明天再来吧");
        }
    }

    static class sendSetu extends Thread {
        private final String sendQQ;
        private final MsgSender sender;
        private final String tag;
        private final Integer num;
        private final Integer type;
        private final Scores coin;
        private final ScoresService scoresService;

        public sendSetu(String sendQQ, MsgSender sender, String tag, Integer num, Integer type,
                        Scores coin, ScoresService scoresService) {
            this.sendQQ = sendQQ;
            this.sender = sender;
            this.tag = tag;
            this.num = num;
            this.type = type;
            this.coin = coin;
            this.scoresService = scoresService;
        }

        @Override
        public void run() {
            try {
                Pixiv pixiv = getSetu(tag, num, type);
                LOGGER.info("pixiv: {}", pixiv);
                String code = pixiv.getCode();
                boolean fromLolicon = "0".equals(code);
                if ("200".equals(code) || fromLolicon) {
                    coin.setScore(coin.getScore() - princessConfig.getSetuCoin());
                    scoresService.updateById(coin);
                    String filename = pixiv.getFileName();
                    URL imageUrl;
                    File pic;
                    if (filename.contains("http")) {
                        imageUrl = new URL(filename);
                        pic = new File(TEMP + filename.substring(filename.lastIndexOf("/") + 1));
                    } else {
                        imageUrl = new URL(PREFIX + filename);
                        pic = new File(TEMP + filename);
                    }
                    FileUtils.copyURLToFile(imageUrl, pic);
                    // 发送图片
                    CQCode cqCodeImage = CQCodeUtil.build().getCQCode_Image(pic.getAbsolutePath());
                    String message = cqCodeImage.toString() + "\n" +
                            pixiv.getTitle() + "\n" +
                            ARTWORK_PREFIX + pixiv.getArtwork() + "\n" +
                            pixiv.getAuthor() + "\n" +
                            ARTIST_PREFIX + pixiv.getArtist();
                    if (fromLolicon) {
                        message += "\n" + "今日剩余额度：" + pixiv.getQuota();
                    }
                    sender.SENDER.sendGroupMsg(sendQQ, message);
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
