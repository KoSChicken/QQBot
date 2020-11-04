package io.koschicken.utils.bilibili;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.koschicken.Constants.PRINCESS_CONFIG;
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
        fresh();
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
                .setHeader("Cookie", PRINCESS_CONFIG.getBilibiliCookie())
                .execute().returnContent().asString();
    }

    public void fresh() throws IOException {
        String live = getLive(mid);
        JSONObject jsonObject = JSON.parseObject(live);
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

            String coverFromJson = data.getString("cover");
            boolean isUrl = isUrl(coverFromJson);
            if (isUrl) {
                String fileName = getImageName(coverFromJson);
                if (this.cover == null || this.cover.getName().equals(fileName)) {
                    this.cover = new File(TEMP + fileName);
                    FileUtils.forceMkdir(this.cover.getParentFile());
                    FileUtils.deleteQuietly(this.cover);
                    FileUtils.touch(this.cover);
                    URL imageUrl = new URL(coverFromJson);
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
}
