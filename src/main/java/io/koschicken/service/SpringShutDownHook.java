package io.koschicken.service;

import com.forte.qqrobot.bot.BotManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpringShutDownHook {

    Object target;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringShutDownHook.class);

    @Autowired
    BotManager botManager;

    public void destroy() throws FileNotFoundException {
        LOGGER.info("机器人关闭" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//        BotSender msgSender = botManager.defaultBot().getSender();
//        // 获取全局配置文件
//        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "config/config.txt");
//        if (file.exists()) {
//            String jsonString;
//            try {
//                jsonString = FileUtils.readFileToString(file, "utf-8");
//                JSONObject jsonObject = JSONObject.parseObject(jsonString);
//                Set<String> keySet = jsonObject.keySet();
//                for (String s : keySet) {
//                    msgSender.SENDER.sendGroupMsg(s, "机器人即将关闭");
//                }
//            } catch (IOException | NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
