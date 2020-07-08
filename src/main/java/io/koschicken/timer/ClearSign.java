package io.koschicken.timer;

//提醒买药小助手

import io.koschicken.database.service.ScoresService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ClearSign {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearSign.class);

    @Autowired
    ScoresService scoresServiceImpl;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        scoresServiceImpl.clearSign();
        LOGGER.info("签到已重置");
    }
}
