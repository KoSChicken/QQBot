package io.koschicken;

import io.koschicken.utils.InitDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动器
 */
@SpringBootApplication
public class MySpringApplication {

    public static void main(String[] args) {
        //初始化数据库文件
        InitDatabase InitDatabase = new InitDatabase();
        InitDatabase.initDB();

        //开启服务
        SpringApplication.run(MySpringApplication.class, args);
    }
}
