package io.koschicken.database.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.koschicken.database.bean.Lucky;

import java.util.List;

public interface LuckyService extends IService<Lucky> {

    List<Lucky> listByGroupCode(String groupCode);
}
