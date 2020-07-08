package io.koschicken.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.koschicken.database.bean.Pic;
import io.koschicken.database.dao.PicMapper;
import io.koschicken.database.service.PicService;
import org.springframework.stereotype.Service;

@Service
public class PicServiceImpl extends ServiceImpl<PicMapper, Pic> implements PicService {

}
