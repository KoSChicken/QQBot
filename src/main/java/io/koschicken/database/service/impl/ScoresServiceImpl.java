package io.koschicken.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.koschicken.database.bean.Scores;
import io.koschicken.database.dao.ScoresMapper;
import io.koschicken.database.service.ScoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoresServiceImpl extends ServiceImpl<ScoresMapper, Scores> implements ScoresService {
    @Autowired
    ScoresMapper scoresMapper;

    public void clearSign() {
        scoresMapper.changeTeamSum();
    }

    @Override
    public Boolean selectSign(long qq) {
        return scoresMapper.selectSign(qq);
    }

    @Override
    public void sign(long qq) {
        scoresMapper.sign(qq);
    }

    @Override
    public int setLive(long qq, String live) {
        QueryWrapper<Scores> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("QQ", qq);
        Scores scores = scoresMapper.selectOne(queryWrapper);
        if (scores == null) {
            scores = new Scores();
            scores.setQQ(qq);
            scores.setScore(1);
            scores.setiSign(false);
            scoresMapper.insert(scores);
        }
        if (scores.getLive1() == 0) {
            scores.setLive1(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 1;
        } else if (scores.getLive2() == 0) {
            scores.setLive2(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 2;
        } else if (scores.getLive3() == 0) {
            scores.setLive3(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 3;
        } else {
            return -1;
        }
    }

    @Override
    public int clearLive(long qq, String size) {
        return scoresMapper.clear(qq, size);
    }

    @Override
    public List<Scores> getLive() {
        return scoresMapper.selectlive();
    }

    @Override
    public int updateLiveOn(long qq, boolean on) {
        if (on) {
            return scoresMapper.openlive(qq);
        } else {
            return scoresMapper.closelive(qq);
        }
    }

    @Override
    public void allRich() {
        scoresMapper.allRich();
    }

    @Override
    public void financialCrisis(long qq) {
        scoresMapper.financialCrisis(qq);
    }

    @Override
    public void refundWu(long qq, Integer refund) {
        scoresMapper.refundWu(qq, refund);
    }

    @Override
    public Long findQQByNickname(String nickname) {
        return scoresMapper.selectQQByNickname(nickname);
    }

    @Override
    public List<Scores> rank(String groupCode) {
        return scoresMapper.rank(groupCode);
    }
}
