package io.koschicken.bean;

public class GroupPower {
    private boolean globalSwitch;//总开关
    private boolean gachaSwitch;//扭蛋开关
    private boolean maiyaoSwitch;//买药小助手提示
    private boolean horseSwitch;//赛马开关

    public boolean isGlobalSwitch() {
        return globalSwitch;
    }

    public GroupPower setGlobalSwitch(boolean globalSwitch) {
        this.globalSwitch = globalSwitch;
        return this;
    }

    public boolean isGachaSwitch() {
        return gachaSwitch;
    }

    public GroupPower setGachaSwitch(boolean gachaSwitch) {
        this.gachaSwitch = gachaSwitch;
        return this;
    }

    public boolean isMaiyaoSwitch() {
        return maiyaoSwitch;
    }

    public GroupPower setMaiyaoSwitch(boolean maiyaoSwitch) {
        this.maiyaoSwitch = maiyaoSwitch;
        return this;
    }

    public boolean isHorseSwitch() {
        return horseSwitch;
    }

    public GroupPower setHorseSwitch(boolean horseSwitch) {
        this.horseSwitch = horseSwitch;
        return this;
    }

    @Override
    public String toString() {
        return "groupPower{" +
                "on=" + globalSwitch +
                ", eggon=" + gachaSwitch +
                ", buton=" + maiyaoSwitch +
                ", horse=" + horseSwitch +
                '}';
    }
}
