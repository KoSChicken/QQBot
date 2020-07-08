package io.koschicken.bean;

public class Gacha {
    String data;
    boolean ban;
    Integer ssrCount;

    public Gacha() {
    }

    public Gacha(String data, boolean ban, Integer ssrCount) {
        this.data = data;
        this.ban = ban;
        this.ssrCount = ssrCount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public Integer getSsrCount() {
        return ssrCount;
    }

    public void setSsrCount(Integer ssrCount) {
        this.ssrCount = ssrCount;
    }
}
