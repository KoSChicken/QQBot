package io.koschicken.timer;

//提醒买药小助手

import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import static io.koschicken.Constants.princessConfig;
import static io.koschicken.listener.PrincessIntercept.On;

@CronTask("0 0 0,6,12,18 * * ?")
public class Tixingmaiyao implements TimeJob {
    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        try {
            File file = new File("./image/" + princessConfig.getTixingmaiyao());
            String str;
            if (file.exists()) {
                KQCodeUtils kqCodeUtils = KQCodeUtils.getInstance();
                str = kqCodeUtils.toCq("image", "file=" + file.getAbsolutePath());
            } else {
                str = "图片找不到了cnmd";
            }
            Set<String> strings = On.keySet();
            for (String s : strings) {
                if (On.get(s).isMaiyaoSwitch()) {
                    msgSender.SENDER.sendGroupMsg(s, "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n" + str);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
