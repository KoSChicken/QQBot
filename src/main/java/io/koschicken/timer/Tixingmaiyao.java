package io.koschicken.timer;

import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import static io.koschicken.Constants.princessConfig;
import static io.koschicken.listener.PrincessIntercept.On;

@Component
@EnableScheduling
public class Tixingmaiyao {

    @Autowired
    BotManager botManager;

    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    public void execute() {
        BotSender msgSender = botManager.defaultBot().getSender();
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "image/" + princessConfig.getTixingmaiyao());
            String str;
            if (file.exists()) {
                KQCodeUtils kqCodeUtils = KQCodeUtils.getInstance();
                str = kqCodeUtils.toCq("image", "file" + "=" + file.getAbsolutePath());
            } else {
                str = "图片又找不到了cnmd";
            }
            Set<String> strings = On.keySet();
            for (String s : strings) {
                if (On.get(s).isMaiyaoSwitch()) {
                    msgSender.SENDER.sendGroupMsg(s, "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n" + str);
                }
            }
        } catch (NullPointerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
