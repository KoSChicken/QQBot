package io.koschicken.config;

import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.depend.DependGetter;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simbot.component.mirai.MiraiApp;
import com.simbot.component.mirai.MiraiConfiguration;
import com.simplerobot.core.springboot.configuration.SpringBootDependGetter;
import io.koschicken.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

import static io.koschicken.Constants.*;
import static io.koschicken.utils.ApiConnect.getLocalIp4AddressFromNetworkInterface;

@SimpleRobotApplication(resources = "./conf.properties")
public class MiraiSpringBootBeanFactoryApp implements MiraiApp {

    private final DependGetter dependGetter;

    public MiraiSpringBootBeanFactoryApp(SpringBootDependGetter dependGetter) {
        this.dependGetter = dependGetter;
    }

    @Override
    public void before(MiraiConfiguration configuration) {
        // 整合Spring的DependGetter
        configuration.setDependGetter(dependGetter);
        // 登陆账户
        String configDir = createConfigDir();
        File file = new File(configDir + "/qq.txt");
        Properties pro = new Properties();
        String profile;
        String pass;
        if (!file.exists()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("未检测到账户文件，请输入所要登陆的qq账号");
            System.out.print("qq号:  ");
            profile = scanner.next();
            System.out.print("密码:  ");
            pass = scanner.next();
            pro.setProperty("密码", pass);
            pro.setProperty("qq账号", profile);
            OutputStreamWriter op;
            try {
                file.createNewFile();
                op = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                pro.store(op, "the PcrTool configs");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            InputStreamReader in;
            try {
                in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                pro.load(in);
                profile = pro.getProperty("qq账号");
                pass = pro.getProperty("密码");
                if (profile == null || pass == null) {
                    System.out.println("账户文件读取失败，请输入所要登陆的qq账号");
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("qq号:  ");
                    profile = scanner.next();
                    System.out.print("密码:  ");
                    pass = scanner.next();
                    pro.setProperty("密码", pass);
                    pro.setProperty("qq账号", profile);
                    OutputStreamWriter op;
                    try {
                        op = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                        pro.store(op, "the PcrTool configs");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("账户文件读取失败，请输入所要登陆的qq账号");
                Scanner scanner = new Scanner(System.in);
                System.out.print("qq号:  ");
                profile = scanner.next();
                System.out.print("密码:  ");
                pass = scanner.next();
                pro.setProperty("密码", pass);
                pro.setProperty("qq账号", profile);
                OutputStreamWriter op;
                try {
                    op = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                    pro.store(op, "the PcrTool configs");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        configuration.registerBot(profile, pass);
    }

    @Override
    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        Constants.robotQQ = sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq
        //获取这个机器人能不能发图片
        Constants.canSendImage = true;
        //获取本机ip地址
        Constants.ip = getLocalIp4AddressFromNetworkInterface();
        //读取配置文件
        getFile();//群组配置文件
        getConfig();//通用设置
        getEvent();//马事件
        getGachaConfig();//扭蛋
    }

    private String createConfigDir() {
        File file = new File("./src/main/resources/config");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
