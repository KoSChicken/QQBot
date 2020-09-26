package io.koschicken.database.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.koschicken.database.bean.Scores;

import java.util.List;

public interface ScoresService extends IService<Scores> {

    void clearSign();

    Boolean selectSign(long qq);

    void sign(long qq);

    //设置一个直播记录，返回设置的槽位，满了返回-1
    int setLive(long qq, String live);

    //清除指定位置的直播记录
    int clearLive(long qq, String size);

    //获取数据库中开启直播监听的人的数据
    List<Scores> getLive();

    int updateLiveOn(long qq, boolean on);

    void allRich();

    void financialCrisis(long qq);

    void refundWu(long qq, Integer refund);

    Long findQQByNickname(String nickname);

    List<Scores> rank(String groupCode);

    List<String> groupCodeByMid(String mid);
}
