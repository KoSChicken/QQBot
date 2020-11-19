package io.koschicken.timer;

import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.simplerobot.modules.utils.KQCodeUtils;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.koschicken.listener.BilibiliListener.liveHashMap;

@Component
@EnableScheduling
public class BilibiliLive {

    private static final Logger LOGGER = LoggerFactory.getLogger(BilibiliLive.class);

    @Autowired
    ScoresService scoresServiceImpl;
    @Autowired
    BotManager botManager;

    @Scheduled(cron = "0/30 * * * * ? ")
    public void execute() {
//        LOGGER.info("直播提醒正在运行......");
        Set<String> strings = liveHashMap.keySet();
        HashMap<String, io.koschicken.utils.bilibili.BilibiliLive> live = new HashMap<>();
        io.koschicken.utils.bilibili.BilibiliLive cache;
        int i;
        for (String s : strings) {
            cache = liveHashMap.get(s);
            i = cache.getLiveStatus();
            try {
                cache.fresh();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            //刷新前没开播刷新后开播了
            if (i == 0 && cache.getLiveStatus() == 1) {
                live.put(s, cache);
            }
        }
        BotSender msgSender = botManager.defaultBot().getSender();
        List<Scores> livePeople = scoresServiceImpl.getLive();
        StringBuilder stringBuilder = new StringBuilder();
        io.koschicken.utils.bilibili.BilibiliLive live1, live2, live3;
        for (Scores people : livePeople) {
//            if (people.getLive1() != 0 || people.getLive2() != 0 || people.getLive3() != 0) {
//                LOGGER.info("people = {}", people);
//            }
            stringBuilder.delete(0, stringBuilder.length());
            Set<String> groupSet = new HashSet<>();
            String up = "\nUP：";
            String title = "\n标题：";
            String url = "\n链接：";
            String imageTag = "image";
            if (people.getLive1() != 0) {
                groupSet.addAll(scoresServiceImpl.groupCodeByMid(String.valueOf(people.getLive1())));
                live1 = live.get(people.getLive1().toString());
                if (live1 != null) {
                    stringBuilder.append("开播啦！").append(up).append(live1.getUser().getUname())
                            .append(title).append(live1.getTitle()).append(url).append(live1.getUrl()).append("\n")
                            .append(KQCodeUtils.getInstance().toCq(imageTag, "file=" + live1.getCover().getAbsolutePath()));
                }
            }
            if (people.getLive2() != 0) {
                groupSet.addAll(scoresServiceImpl.groupCodeByMid(String.valueOf(people.getLive2())));
                live2 = live.get(people.getLive2().toString());
                if (live2 != null) {
                    stringBuilder.append(up).append(live2.getUser().getUname())
                            .append(title).append(live2.getTitle()).append(url).append(live2.getUrl()).append("\n")
                            .append(KQCodeUtils.getInstance().toCq(imageTag, "file=" + live2.getCover().getAbsolutePath()));
                }
            }
            if (people.getLive3() != 0) {
                groupSet.addAll(scoresServiceImpl.groupCodeByMid(String.valueOf(people.getLive3())));
                live3 = live.get(people.getLive3().toString());
                if (live3 != null) {
                    stringBuilder.append(up).append(live3.getUser().getUname())
                            .append(title).append(live3.getTitle()).append(url).append(live3.getUrl()).append("\n")
                            .append(KQCodeUtils.getInstance().toCq(imageTag, "file=" + live3.getCover().getAbsolutePath()));
                }
            }
            if (stringBuilder.length() > 0) {
                LOGGER.info("群友{}的提醒消息为{}", people.getQQ(), stringBuilder);
                for (String groupCode : groupSet) {
                    msgSender.SENDER.sendGroupMsg(groupCode, stringBuilder.toString());
                }
            }
        }
    }
}
