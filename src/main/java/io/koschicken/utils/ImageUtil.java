package io.koschicken.utils;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ImageUtil {
    private static final Integer HEIGHT_AND_WIDTH = 128; //一个图片的长宽高为64
    private static final Integer LINE_SIZE_MAX = 4;//一行最多4个图片
    private static final String OUT = "./temp/gacha/";//输出图片的临时文件夹

    //制作抽卡出货的图
    public static String composeImg(ArrayList<String> characters) throws IOException {
        if (characters.size() > 0) {
            int rows = characters.size() >= LINE_SIZE_MAX ? LINE_SIZE_MAX : characters.size();
            int cols = characters.size() / LINE_SIZE_MAX + (characters.size() % LINE_SIZE_MAX == 0 ? 0 : 1);
            BufferedImage thumbImage = new BufferedImage(rows * HEIGHT_AND_WIDTH, cols * HEIGHT_AND_WIDTH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbImage.createGraphics();
            //设置白底
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rows * HEIGHT_AND_WIDTH, cols * HEIGHT_AND_WIDTH);
            //画人物图片在上面
            for (int i = 0; i < characters.size(); i++) {
                File file = new File("./image/" + characters.get(i) + ".png");
                Image src;
                if (file.exists()) { //如果外部图片包 有资源则使用外部资源
                    src = ImageIO.read(file);
                } else { //否则使用内部资源
                    InputStream inputStream = ImageUtil.class.getResourceAsStream("/image/" + characters.get(i) + ".png");
                    if (inputStream == null) { //内部资源也没有就显示问号占位图片
                        inputStream = ImageUtil.class.getResourceAsStream("/image/no.png");
                    }
                    src = ImageIO.read(inputStream);
                }
                //画图片
                g.drawImage(src.getScaledInstance(HEIGHT_AND_WIDTH, HEIGHT_AND_WIDTH, Image.SCALE_SMOOTH), (i % LINE_SIZE_MAX) * HEIGHT_AND_WIDTH, (i / LINE_SIZE_MAX) * HEIGHT_AND_WIDTH, null);
            }
            //生成uuid作为名字，防止图片相互覆盖
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            //输出图片
            String path = OUT + uuid + ".jpg";
            File exOut = new File(OUT);
            if (!exOut.exists()) {
                FileUtils.forceMkdir(exOut);
            }
            String formatName = path.substring(path.lastIndexOf(".") + 1);
            ImageIO.write(thumbImage, formatName, new File(path));
            return path;
        } else {
            return null;
        }
    }
}
