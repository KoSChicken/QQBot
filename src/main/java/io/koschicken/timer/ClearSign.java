package io.koschicken.timer;

import io.koschicken.database.service.ScoresService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 重置签到，清理临时文件夹
 */
@Component
@EnableScheduling
public class ClearSign {

    @Autowired
    ScoresService scoresServiceImpl;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() throws IOException {
        scoresServiceImpl.clearSign(); // 重置签到
        clearTemp();
    }

    private void clearTemp() throws IOException {
        File gachaFolder = new File("temp/gacha/");
        if (gachaFolder.exists()) {
            FileUtils.deleteDirectory(gachaFolder);
        }
        File bilibiliFolder = new File("temp/bili/");
        if (bilibiliFolder.exists()) {
            FileUtils.deleteDirectory(bilibiliFolder);
        }
    }
}
