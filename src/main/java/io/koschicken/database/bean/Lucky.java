package io.koschicken.database.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName("Lucky")
public class Lucky implements Serializable {

    private static final long serialVersionUID = 3146650876135600178L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long qq;

    private Date date;

    private Integer coin;

    private Integer count;

    public Lucky(Long qq, Date date, Integer coin) {
        this.qq = qq;
        this.date = date;
        this.coin = coin;
    }

    public Lucky(Integer id, Long qq, Date date, Integer coin, Integer count) {
        this.id = id;
        this.qq = qq;
        this.date = date;
        this.coin = coin;
        this.count = count;
    }

    public Lucky(Long qq, Integer count) {
        this.qq = qq;
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Lucky{" +
                "id=" + id +
                ", qq=" + qq +
                ", date=" + date +
                ", coin=" + coin +
                '}';
    }
}
