package io.koschicken.utils.bilibili;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.ResourceUtils.isUrl;

public class BilibiliLive {
    private static final String TEMP = "./temp/bili/Live/";
    private final String mid;//主播uid
    private int roomStatus;
    private int roundStatus;
    private int liveStatus;
    private String url;
    private String title;
    private File cover;
    private int online;
    private int roomId;

    public BilibiliLive(String mid) throws IOException {
        this.mid = mid;
        frash();
    }

    /**
     * 通过传入的up主uid返回一个json数组，其中包含直播间房间号，封面，人气，标题
     *
     * @param uid up的uid
     * @return json 示例如下
     * {"code":0,"message":"0","ttl":1,
     * "data":{
     * "roomStatus":1,   //0：无房间 1：有房间
     * "roundStatus":0,   //0：未轮播 1：轮播
     * "liveStatus":0,    //0：未开播 1：直播中
     * "url":"https://live.bilibili.com/92613",
     * "title":"万能的普瑞斯特",  //直播间标题
     * "cover":"http://i0.hdslb.com/bfs/live/bef09ae4739d7005332c10dbb91d55e6a8241275.jpg",  //直播间封面
     * "online":449411, //直播间人气
     * "roomid":92613,  //直播间ID
     * "broadcast_type":0,
     * "online_hidden":0}
     * }
     */
    public static String getLive(String uid) throws IOException {
        String url = "http://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid;
        return get(url);
    }

    public static String get(String getUrl) throws IOException {
        return Request.Get(getUrl)
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0")
                .setHeader("Cookie", "rpdid=|(J~J|~RYY|u0J'ulm)RJuk|); CURRENT_FNVAL=80; DedeUserID=3296738; DedeUserID__ckMd5=397237cbc6b76925; SESSDATA=2e0c1597%2C1613983281%2C63055*81; bili_jct=a4f032287e89756d472ebb5681ea1c06; _uuid=E470E331-A9AF-033D-563D-54B3E34FAF3578688infoc; buvid3=7D8D2A43-0489-4249-B4C7-9E822367B46D138400infoc; bp_t_offset_3296738=432855531470521572; bp_video_offset_3296738=433227909431220320; blackside_state=1; LIVE_BUVID=AUTO7415988509752319; sid=k5ut68jk; _dfcaptcha=76988dc476354d59bf1c0642d21a2f5c")
                .execute().returnContent().asString();
    }

    public void frash() throws IOException {
        String live = getLive(mid);
        JSONObject jsonObject = JSONObject.parseObject(live);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            roomStatus = data.getInteger("roomStatus");
            if (roomStatus == 0) {
                return; // 无房间
            }
            roundStatus = data.getInteger("roundStatus");
            liveStatus = data.getInteger("liveStatus");
            url = data.getString("url");
            title = data.getString("title");
            online = data.getInteger("online");
            roomId = data.getInteger("roomid");

            boolean isUrl = isUrl(data.getString("cover"));
            if (isUrl) {
                String fileName = getImageName(data.getString("cover"));
                if (cover == null || cover.getName().equals(fileName)) {
                    cover = new File(TEMP + fileName);
                    FileUtils.forceMkdir(cover.getParentFile());
                    FileUtils.deleteQuietly(cover);
                    FileUtils.touch(cover);
                    URL imageUrl = new URL(data.getString("cover"));
                    FileUtils.copyURLToFile(imageUrl, this.cover);
                }
            }
        }
    }

    private String getImageName(String url) {
        String regex = "http://i0.hdslb.com/bfs/live/(.*.jpg)";
        String result = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public String getMid() {
        return mid;
    }

    public int getRoomStatus() {
        return roomStatus;
    }

    public int getRoundStatus() {
        return roundStatus;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public File getCover() {
        return cover;
    }

    public int getOnline() {
        return online;
    }

    public int getRoomId() {
        return roomId;
    }

    public static void main(String[] args) throws IOException {
        BilibiliLive bilibiliLive = new BilibiliLive("");
    }
}
