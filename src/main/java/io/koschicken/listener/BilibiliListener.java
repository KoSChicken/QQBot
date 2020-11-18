package io.koschicken.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import io.koschicken.utils.bilibili.BilibiliLive;
import io.koschicken.utils.bilibili.BilibiliUser;
import io.koschicken.utils.bilibili.BilibiliVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BilibiliListener {

    public static final Map<String, BilibiliLive> liveHashMap = new HashMap<>();
    private static final String CQ_TYPE = "image";
    private static final String CQ_PARAMS = "file=";

    @Autowired
    ScoresService scoresServiceImpl;

    //查询UP主
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#UP主", "#up主"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void searchUp(GroupMsg msg, MsgSender sender) throws IOException {
        String mid = msg.getMsg().substring(4).trim();
        BilibiliUser bilibiliUser = new BilibiliUser(mid);
        String message = "UP主：" + bilibiliUser.getUname();
        if (bilibiliUser.getRoomId() != 0) {
            message += "\n直播间：" + "https://live.bilibili.com/" + bilibiliUser.getRoomId();
        }
        message += "\n签名:" + bilibiliUser.getSign();

        String image = KQCodeUtils.getInstance()
                .toCq(CQ_TYPE, CQ_PARAMS + bilibiliUser.getFace().getAbsolutePath());
        message += "\n" + image;
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), message);
    }

    //视频封面 av114514
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#封面", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void videoFace(GroupMsg msg, MsgSender sender) {
        String videoCode = msg.getMsg().substring(3).trim();
        String av = "";
        String bv = "";
        BilibiliVideo bilibiliVideo = null;
        if (videoCode.startsWith("videoCode") || videoCode.startsWith("AV")) {
            bilibiliVideo = new BilibiliVideo(videoCode.substring(2), false);
            av = videoCode;
            bv = bilibiliVideo.getBv();
        } else if (videoCode.startsWith("bv") || videoCode.startsWith("BV")) {
            bilibiliVideo = new BilibiliVideo(videoCode.substring(2), true);
            av = bilibiliVideo.getAv();
            bv = videoCode;
        }
        if (bilibiliVideo != null) {
            String image = KQCodeUtils.getInstance()
                    .toCq(CQ_TYPE, CQ_PARAMS + bilibiliVideo.getPic().getAbsolutePath());
            sender.SENDER.sendGroupMsg(msg.getGroupCode(),
                    "av号：" + av + "\nbv号：" + bv + "\n视频标题:" + bilibiliVideo.getTitle() + "\n" + image);
        }
    }

    //查询直播状态
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#直播"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void searchLive(GroupMsg msg, MsgSender sender) {
        String mid = msg.getMsg().substring(3).trim();
        BilibiliLive bilibiliLive = liveHashMap.get(mid);
        if (bilibiliLive == null) {
            try {
                bilibiliLive = new BilibiliLive(mid);
            } catch (IOException e) {
                if (e.getMessage().contains("412")) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "Cookie过期");
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "网络链接错误，请稍后再试");
                }
                return;
            }
        }
        if (bilibiliLive.getRoomStatus() == 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "该用户还未开通直播间");
            return;
        }
        if (bilibiliLive.getLiveStatus() == 0) {
            if (bilibiliLive.getRoundStatus() == 1) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "在轮播中");
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还未开播");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "开播啦！\n标题:" + bilibiliLive.getTitle() +
                    "\n链接:" + bilibiliLive.getUrl() + KQCodeUtils.getInstance()
                    .toCq(CQ_TYPE, CQ_PARAMS + bilibiliLive.getCover().getAbsolutePath()));
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"设置开播提示"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void setLive(PrivateMsg msg, MsgSender sender) {
        Pattern pattern = Pattern.compile("[0-9.]");
        Matcher matcher = pattern.matcher(msg.getMsg());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        int i = scoresServiceImpl.setLive(msg.getCodeNumber(), sb.toString());
        if (i == -1) {
            sender.SENDER.sendPrivateMsg(msg, "槽位已满，请去掉一个");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已添加，记录在" + i + "号槽上");
        }
        //开始监听直播间
        addLive(sb.toString());
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"查看开播提示"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getLive(PrivateMsg msg, MsgSender sender) {
        Scores scores = scoresServiceImpl.getById(msg.getCodeNumber());
        if (scores == null) {
            sender.SENDER.sendPrivateMsg(msg, "还没有关注的主播哦");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "开启状态:" + scores.getLiveON() +
                    "\n一号槽：uid：" + scores.getLive1() +
                    "\n二号槽：uid：" + scores.getLive2() +
                    "\n三号槽：uid：" + scores.getLive3() +
                    "\nuid为0的槽未则为为使用的槽");
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"清除开播提示"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void clearLive(PrivateMsg msg, MsgSender sender) {
        scoresServiceImpl.clearLive(msg.getCodeNumber(), "live" + msg.getMsg().substring(6).trim());
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"开启开播提示"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void openLive(PrivateMsg msg, MsgSender sender) {
        int i = scoresServiceImpl.updateLiveOn(msg.getCodeNumber(), true);
        if (i < 1) {
            sender.SENDER.sendPrivateMsg(msg, "还没有直播关注记录");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已开启开播提示功能");
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"关闭开播提示"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void closeLive(PrivateMsg msg, MsgSender sender) {
        int i = scoresServiceImpl.updateLiveOn(msg.getCodeNumber(), false);
        if (i < 1) {
            sender.SENDER.sendPrivateMsg(msg, "还没有直播关注记录");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已关闭开播提示功能");
        }
    }

    private void addLive(String mid) {
        new AddLive(mid).start();
    }

    static class AddLive extends Thread {
        private final String mid;

        AddLive(String mid) {
            this.mid = mid;
        }

        @Override
        public void run() {
            if (liveHashMap.get(mid) == null) {
                //没有则加入一个
                boolean flag = true;
                do {
                    try {
                        BilibiliLive bilibiliLive = new BilibiliLive(mid);
                        liveHashMap.put(mid, bilibiliLive);
                        flag = false;
                    } catch (IOException e) {
                        //出现了问题则等一会再加上去
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                } while (flag);
            }
        }
    }
}
