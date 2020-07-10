package io.koschicken.service;

import com.forte.qqrobot.bot.BotManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpringShutDownHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringShutDownHook.class);

    @Autowired
    BotManager botManager;

    public void destroy() {
        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "    机器人关闭");
    }
}
