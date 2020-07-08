package io.koschicken.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.koschicken.bean.Pixiv;
import org.apache.http.Consts;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.koschicken.Constants.princessConfig;

public class SetuUtils {
    private static final String YUBAN1073API = "http://api.yuban10703.xyz:2333/setu_v3";
    private static final String LOLICONAPI = "https://api.lolicon.app/setu/";
    private static final Map<Integer, String> codeMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(SetuUtils.class);

    static {
        codeMap = new HashMap<>();
        codeMap.put(-1, "接口发生内部错误");
        codeMap.put(0, "成功");
        codeMap.put(401, "APIKEY 不存在或被封禁");
        codeMap.put(403, "由于不规范的操作而被拒绝调用");
        codeMap.put(404, "找不到符合关键字的色图");
        codeMap.put(429, "达到调用额度限制");
    }

    public static Pixiv getSetu(String tag, Integer num, Integer type) throws IOException {
        num = Objects.isNull(num) || num == 0 ? 1 : num;
        String api = YUBAN1073API + "?num=" + num;
        if (Objects.isNull(type)) {
            type = 3;
        }
        api += "&type=" + type;
        if (!StringUtils.isEmpty(tag)) {
            api += "&tag=" + tag;
        }
        LOGGER.info("这次请求的地址为： {}", api);
        ResponseHandler<String> myHandler = response -> EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        String response = Request.Get(api).execute().handleResponse(myHandler);
        JSONObject jsonObject = JSON.parseObject(response);
        Pixiv pixiv = new Pixiv();
        pixiv.setCode(jsonObject.getString("code"));
        pixiv.setMsg(jsonObject.getString("msg"));
        if ("200".equals(jsonObject.getString("code"))) { // 请求成功
            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject data = dataArray.getJSONObject(0);
            fillPixiv(pixiv, data);
        } else { // 没请求成功，去请求lolicon的API
            fetchFromLolicon(tag, myHandler, pixiv);
        }
        return pixiv;
    }

    private static void fetchFromLolicon(String tag, ResponseHandler<String> myHandler, Pixiv pixiv) throws IOException {
        String response;
        JSONObject jsonObject;
        String loliconApi = LOLICONAPI + "?apikey=" + princessConfig.getLoliconApiKey() + "&r18=2";
        if (!StringUtils.isEmpty(tag)) {
            loliconApi += "&keyword=" + tag;
        }
        LOGGER.info("这次请求的Lolicon地址为： {}", loliconApi);
        response = Request.Get(loliconApi).execute().handleResponse(myHandler);
        jsonObject = JSON.parseObject(response);
        Integer code = jsonObject.getInteger("code");
        pixiv.setCode(code.toString());
        if (code == 0) {
            pixiv.setQuota(jsonObject.getInteger("quota"));
            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject data = dataArray.getJSONObject(0);
            fillPixivLolicon(pixiv, data);
        } else {
            pixiv.setMsg(codeMap.get(code));
        }
    }

    private static void fillPixiv(Pixiv pixiv, JSONObject data) {
        pixiv.setTitle(data.getString("title"));
        pixiv.setArtwork(data.getString("artwork"));
        pixiv.setAuthor(data.getString("author"));
        pixiv.setArtist(data.getString("artist"));
        pixiv.setTags(data.getString("tags").split(","));
        pixiv.setType(data.getString("type"));
        pixiv.setFileName(data.getString("filename"));
        pixiv.setOriginal(data.getString("original"));
    }

    private static void fillPixivLolicon(Pixiv pixiv, JSONObject data) {
        pixiv.setTitle(data.getString("title"));
        pixiv.setArtwork(data.getString("pid"));
        pixiv.setAuthor(data.getString("author"));
        pixiv.setArtist(data.getString("uid"));
        pixiv.setTags(data.getString("tags").split(","));
        pixiv.setType(data.getBoolean("r18") ? "r18" : "normal");
        pixiv.setFileName(data.getString("url"));
        pixiv.setOriginal(data.getString("url"));
    }
}
