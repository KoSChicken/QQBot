package io.koschicken.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.koschicken.database.bean.Lucky;
import io.koschicken.database.dao.LuckyMapper;
import io.koschicken.database.service.LuckyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LuckyServiceImpl extends ServiceImpl<LuckyMapper, Lucky> implements LuckyService {

    @Autowired
    LuckyMapper luckyMapper;

    @Override
    public List<Lucky> listByGroupCode(String groupCode) {
        return luckyMapper.list(groupCode);
    }
}
