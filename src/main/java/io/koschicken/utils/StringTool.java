package io.koschicken.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

import static io.koschicken.Constants.coolQAt;

public class StringTool {

    private static final Random RANDOM = new Random();

    //将cq码at 提取出qq号 long形式返回
    public static long cqAtoNumber(String string) {
        return Long.parseLong(string.substring(10, string.length() - 1));
    }

    //找这个字符串里at人的cq码出现了多少次
    public static int searchAtNumber(String str) {
        int n = 0;
        int length = coolQAt.length();
        int index = str.indexOf(coolQAt);
        while (index != -1) {
            n++;
            index = str.indexOf(coolQAt, index + length);
        }
        return n;
    }

    //将年-月-日字符串转换为locale date
    public static LocalDate strToLocaleDate(String str) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(str, formatter);
    }

    //存在这种命令  #命令名 @机器人 参数1
    //返回参数1并忽略所有空格
    //只是找到第一个】之后截断
    public static String getVar(String msg) {
        String work = msg.replace(" ", "");
        return work.substring(work.indexOf("]") + 1);
    }

    //将byte(8位)分割成俩4位
    public static int[] spiltByte(byte b) {
        int[] i = new int[2];
        i[1] = b / 16;
        i[0] = (b - i[1] * 16);
        return i;
    }

    //将byte(8位)分割成俩4位
    public static int[] spiltByte(int b) {
        int[] i = new int[2];
        i[1] = b / 16;
        i[0] = (b - i[1] * 16);
        return i;
    }

    //将俩4位转换成一个8位
    public static byte reSpiltByte(int b, int c) {
        return (byte) ((b * 16 + c) > 127 ? 127 - (b * 16 + c) : b * 16 + c);
    }

    public static char[] getChars(Byte[] bytes) {
        byte[] b = toPrimitives(bytes);
        Charset cs = StandardCharsets.UTF_8;
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(b).flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    private static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    //获取字符集名字
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GB2312
                return encode;      //是的话，返回“GB2312“，以下代码同理
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是ISO-8859-1
                return encode;
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {   //判断是不是UTF-8
                return encode;
            }
        } catch (Exception exception2) {
            exception2.printStackTrace();
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GBK
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        return "";        //如果都不是，说明输入的内容不属于常见的编码格式。
    }

    public static String random(int count) {
        int start;
        int end;
        end = 'z' + 1;
        start = ' ';

        final StringBuilder builder = new StringBuilder(count);
        final int gap = end - start;

        while (count-- != 0) {
            int codePoint;
            codePoint = RANDOM.nextInt(gap) + start;

            switch (Character.getType(codePoint)) {
                case Character.UNASSIGNED:
                case Character.PRIVATE_USE:
                case Character.SURROGATE:
                    count++;
                    continue;
            }

            final int numberOfChars = Character.charCount(codePoint);
            if (count == 0 && numberOfChars > 1) {
                count++;
                continue;
            }

            if (Character.isLetter(codePoint) || Character.isDigit(codePoint)) {
                builder.appendCodePoint(codePoint);

                if (numberOfChars == 2) {
                    count--;
                }
            } else {
                count++;
            }
        }
        return builder.toString();
    }
}
