package io.koschicken.bean;

public class PrincessConfig {
    private String tixingmaiyao;//提醒买药小助手文件名
    private int gachaLimit;//抽卡上限
    private int gachaCooldown;//抽卡冷却时间，以秒为单位
    private boolean globalSwitch;//总开关
    private boolean gachaSwitch;//扭蛋开关
    private boolean maiyaoSwitch;//买药小助手提示
    private boolean horseSwitch;//赛马开关
    private String masterQQ;//主人qq
    private int signCoin;//签到一次给的钱
    private int setuCoin;//发一次图给的钱
    private String loliconApiKey; //loliconApi的APIKEY
    private boolean r18Private; //r18图片的私聊开关
    private String bilibiliCookie; //r18图片的私聊开关

    public PrincessConfig() {
    }

    public PrincessConfig(String tixingmaiyao, int gachaLimit, int gachaCooldown, boolean globalSwitch,
                          boolean gachaSwitch, boolean maiyaoSwitch, boolean horseSwitch, String masterQQ,
                          int signCoin, int setuCoin, String loliconApiKey, boolean r18Private, String bilibiliCookie) {
        this.tixingmaiyao = tixingmaiyao;
        this.gachaLimit = gachaLimit;
        this.gachaCooldown = gachaCooldown;
        this.globalSwitch = globalSwitch;
        this.gachaSwitch = gachaSwitch;
        this.maiyaoSwitch = maiyaoSwitch;
        this.horseSwitch = horseSwitch;
        this.masterQQ = masterQQ;
        this.signCoin = signCoin;
        this.setuCoin = setuCoin;
        this.loliconApiKey = loliconApiKey;
        this.r18Private = r18Private;
        this.bilibiliCookie = bilibiliCookie;
    }

    public String getTixingmaiyao() {
        return tixingmaiyao;
    }

    public void setTixingmaiyao(String tixingmaiyao) {
        this.tixingmaiyao = tixingmaiyao;
    }

    public int getGachaLimit() {
        return gachaLimit;
    }

    public void setGachaLimit(int gachaLimit) {
        this.gachaLimit = gachaLimit;
    }

    public int getGachaCooldown() {
        return gachaCooldown;
    }

    public void setGachaCooldown(int gachaCooldown) {
        this.gachaCooldown = gachaCooldown;
    }

    public boolean isGlobalSwitch() {
        return globalSwitch;
    }

    public void setGlobalSwitch(boolean globalSwitch) {
        this.globalSwitch = globalSwitch;
    }

    public boolean isGachaSwitch() {
        return gachaSwitch;
    }

    public void setGachaSwitch(boolean gachaSwitch) {
        this.gachaSwitch = gachaSwitch;
    }

    public boolean isMaiyaoSwitch() {
        return maiyaoSwitch;
    }

    public void setMaiyaoSwitch(boolean maiyaoSwitch) {
        this.maiyaoSwitch = maiyaoSwitch;
    }

    public boolean isHorseSwitch() {
        return horseSwitch;
    }

    public void setHorseSwitch(boolean horseSwitch) {
        this.horseSwitch = horseSwitch;
    }

    public String getMasterQQ() {
        return masterQQ;
    }

    public void setMasterQQ(String masterQQ) {
        this.masterQQ = masterQQ;
    }

    public int getSignCoin() {
        return signCoin;
    }

    public void setSignCoin(int signCoin) {
        this.signCoin = signCoin;
    }

    public int getSetuCoin() {
        return setuCoin;
    }

    public void setSetuCoin(int setuCoin) {
        this.setuCoin = setuCoin;
    }

    public String getLoliconApiKey() {
        return loliconApiKey;
    }

    public void setLoliconApiKey(String loliconApiKey) {
        this.loliconApiKey = loliconApiKey;
    }

    public boolean isR18Private() {
        return r18Private;
    }

    public void setR18Private(boolean r18Private) {
        this.r18Private = r18Private;
    }

    public String getBilibiliCookie() {
        return bilibiliCookie;
    }

    public void setBilibiliCookie(String bilibiliCookie) {
        this.bilibiliCookie = bilibiliCookie;
    }

    @Override
    public String toString() {
        return "PrincessConfig{" +
                "tixingmaiyao='" + tixingmaiyao + '\'' +
                ", gachaLimit=" + gachaLimit +
                ", gachaCooldown=" + gachaCooldown +
                ", globalSwitch=" + globalSwitch +
                ", gachaSwitch=" + gachaSwitch +
                ", maiyaoSwitch=" + maiyaoSwitch +
                ", horseSwitch=" + horseSwitch +
                ", masterQQ='" + masterQQ + '\'' +
                ", signCoin=" + signCoin +
                ", setuCoin=" + setuCoin +
                ", loliconApiKey='" + loliconApiKey + '\'' +
                ", r18Private=" + r18Private +
                ", bilibiliCookie='" + bilibiliCookie + '\'' +
                '}';
    }
}
