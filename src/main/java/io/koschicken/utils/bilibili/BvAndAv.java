package io.koschicken.utils.bilibili;

import org.apache.http.Consts;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

//bv号转av号工具
public class BvAndAv {
    private static final String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static final HashMap<String, Integer> mp = new HashMap<>();
    private static final HashMap<Integer, String> mp2 = new HashMap<>();
    private static final int[] ss = {11, 10, 3, 8, 4, 6, 2, 9, 5, 7};
    private static final long xor = 177451812;
    private static final long add = 8728348608L;

    public static void main(String[] args) throws IOException {
        //System.out.println(b2v("BV1bD4y1Q7h5"));
        //System.out.println(v2b("av711138001"));
        String api = "http://api.yuban10703.xyz:2333/setu_v3";
        ResponseHandler<String> myHandler = response -> EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static long power(int a, int b) {
        long power = 1;
        for (int c = 0; c < b; c++)
            power *= a;
        return power;
    }

    /**
     * bv转av
     *
     * @param s
     */
    public static String b2v(String s) {
        long r = 0;
        for (int i = 0; i < 58; i++) {
            String s1 = table.substring(i, i + 1);
            mp.put(s1, i);
        }
        for (int i = 0; i < 6; i++) {
            r = r + mp.get(s.substring(ss[i], ss[i] + 1)) * power(58, i);
        }
        return "av" + ((r - add) ^ xor);
    }

    /**
     * av转bv
     *
     * @param st
     */
    public static String v2b(String st) {
        long s = Long.parseLong(st.split("av")[0]);
        StringBuilder sb = new StringBuilder("BV1  4 1 7  ");
        s = (s ^ xor) + add;
        for (int i = 0; i < 58; i++) {
            String s1 = table.substring(i, i + 1);
            mp2.put(i, s1);
        }
        for (int i = 0; i < 6; i++) {
            String r = mp2.get((int) (s / power(58, i) % 58));
            sb.replace(ss[i], ss[i] + 1, r);
        }
        return sb.toString();
    }
}
