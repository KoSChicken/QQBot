package io.koschicken.config;

import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.depend.DependGetter;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simbot.component.mirai.MiraiApp;
import com.simbot.component.mirai.MiraiConfiguration;
import com.simplerobot.core.springboot.configuration.SpringBootDependGetter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Scanner;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.ApiConnect.getLocalIp4AddressFromNetworkInterface;

@SimpleRobotApplication(resources = "./conf.properties")
public class MiraiSpringBootBeanFactoryApp implements MiraiApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiraiSpringBootBeanFactoryApp.class);
    private final DependGetter dependGetter;

    public MiraiSpringBootBeanFactoryApp(SpringBootDependGetter dependGetter) {
        this.dependGetter = dependGetter;
    }

    @Override
    public void before(MiraiConfiguration configuration) {
        // 整合Spring的DependGetter
        configuration.setDependGetter(dependGetter);
        // 登陆账户
        String configDir = null;
        try {
            configDir = createConfigDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(configDir + "/qq.txt");
        Properties pro = new Properties();
        String profile = null;
        String pass = null;
        if (!file.exists()) {
            Scanner scanner = new Scanner(System.in);
            LOGGER.info("未检测到账户文件，请输入所要登陆的qq账号");
            LOGGER.info("qq号:  ");
            profile = scanner.next();
            LOGGER.info("密码:  ");
            pass = scanner.next();
            pro.setProperty("密码", pass);
            pro.setProperty("qq账号", profile);
            try (OutputStreamWriter op = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                Files.createFile(file.toPath());
                pro.store(op, "the PcrTool configs");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try (InputStreamReader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                pro.load(in);
                profile = pro.getProperty("qq账号");
                pass = pro.getProperty("密码");
                if (profile == null || pass == null) {
                    try (OutputStreamWriter op = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                        LOGGER.info("账户文件读取失败，请输入所要登陆的qq账号");
                        Scanner scanner = new Scanner(System.in);
                        LOGGER.info("qq号:  ");
                        profile = scanner.next();
                        LOGGER.info("密码:  ");
                        pass = scanner.next();
                        pro.setProperty("密码", pass);
                        pro.setProperty("qq账号", profile);
                        pro.store(op, "the PcrTool configs");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration.registerBot(profile, pass);
    }

    @Override
    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        ROBOT_QQ = sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq
        //获取这个机器人能不能发图片
        CAN_SEND_IMAGE = true;
        //获取本机ip地址
        IP = getLocalIp4AddressFromNetworkInterface();
        //读取配置文件
        getFile();//群组配置文件
        getConfig();//通用设置
        getEvent();//马事件
        getGachaConfig();//扭蛋
    }

    private String createConfigDir() throws IOException {
        File file = new File("config");
        if (!file.exists()) {
            FileUtils.forceMkdir(file);
        }
        return file.getAbsolutePath();
    }
}
