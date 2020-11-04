package io.koschicken.timer;

import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static io.koschicken.listener.PrincessIntercept.On;

@Component
@EnableScheduling
public class JJC {

    private static final Logger LOGGER = LoggerFactory.getLogger(JJC.class);
    @Autowired
    BotManager botManager;

    @Scheduled(cron = "0 40 14 * * ?")
    public void execute() {
        LOGGER.info("提醒背刺");
        BotSender msgSender = botManager.defaultBot().getSender();
        Set<String> strings = On.keySet();
        for (String s : strings) {
            if (On.get(s).isMaiyaoSwitch()) {
                msgSender.SENDER.sendGroupMsg(s, "该上号背刺了hxdm");
            }
        }
    }
}
