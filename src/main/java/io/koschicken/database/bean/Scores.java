package io.koschicken.database.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("Scores")
public class Scores implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "QQ")
    private Long QQ;

    private Boolean iSign;

    private Integer score;

    private Integer live1 = 0;
    private Integer live2 = 0;
    private Integer live3 = 0;
    private Boolean liveON = true;
    private String groupCode;
    private Integer roll;

    public Long getQQ() {
        return QQ;
    }

    public void setQQ(Long QQ) {
        this.QQ = QQ;
    }

    public Boolean getiSign() {
        return iSign;
    }

    public void setiSign(Boolean iSign) {
        this.iSign = iSign;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLive1() {
        return live1;
    }

    public void setLive1(Integer live1) {
        this.live1 = live1;
    }

    public Integer getLive2() {
        return live2;
    }

    public void setLive2(Integer live2) {
        this.live2 = live2;
    }

    public Integer getLive3() {
        return live3;
    }

    public void setLive3(Integer live3) {
        this.live3 = live3;
    }

    public Boolean getLiveON() {
        return liveON;
    }

    public void setLiveON(Boolean liveON) {
        this.liveON = liveON;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getRoll() {
        return roll;
    }

    public void setRoll(Integer roll) {
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "QQ=" + QQ +
                ", iSign=" + iSign +
                ", score=" + score +
                ", live1=" + live1 +
                ", live2=" + live2 +
                ", live3=" + live3 +
                ", liveON=" + liveON +
                ", groupCode='" + groupCode + '\'' +
                ", roll=" + roll +
                '}';
    }
}
