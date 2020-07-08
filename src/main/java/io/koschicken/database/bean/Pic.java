package io.koschicken.database.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName("pic")
public class Pic implements Serializable {

    private static final long serialVersionUID = -5731692021951818137L;

    @TableId(value = "pid")
    private Integer pid;

    private Date lastSendTime;

    public Pic() {
    }

    public Pic(Integer pid, Date lastSendTime) {
        this.pid = pid;
        this.lastSendTime = lastSendTime;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    @Override
    public String toString() {
        return "Pic{" +
                "pid=" + pid +
                ", lastSendTime=" + lastSendTime +
                '}';
    }
}
