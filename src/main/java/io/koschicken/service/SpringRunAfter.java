package io.koschicken.service;

import io.koschicken.database.bean.Scores;
import io.koschicken.database.service.ScoresService;
import io.koschicken.utils.bilibili.BilibiliLive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static io.koschicken.listener.BilibiliListener.liveHashMap;

@Service
public class SpringRunAfter implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    ScoresService scoresService;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        //直播监听
        List<Scores> list = scoresService.getLive();

        for (Scores s : list) {
            if (s.getLive1() != 0 && liveHashMap.get(s.getLive1().toString()) == null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive1()), new BilibiliLive(String.valueOf(s.getLive1())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (s.getLive2() != 0 && liveHashMap.get(s.getLive2().toString()) == null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive2()), new BilibiliLive(String.valueOf(s.getLive2())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (s.getLive3() != 0 && liveHashMap.get(s.getLive3().toString()) == null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive3()), new BilibiliLive(String.valueOf(s.getLive3())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
