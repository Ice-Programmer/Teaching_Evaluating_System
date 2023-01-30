package com.itmo.eva.service.system;

import com.itmo.eva.model.dto.system.SystemRussianUpdateRequest;
import com.itmo.eva.model.entity.System;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.system.SystemVo;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_system(评价体系表)】的数据库操作Service
* @createDate 2023-01-24 10:02:52
*/
public interface SystemService extends IService<System> {

    /**
     * 获取俄方评价体系
     */
    List<SystemVo> getRussianSystem();

    /**
     * 获取中方教学评价体系
     */
    List<SystemVo> getChineseSystem();

    /**
     * 更新俄方评价体系
     * @param systemRussianUpdateRequest
     * @return
     */
    Boolean updateRussianSystem(SystemRussianUpdateRequest systemRussianUpdateRequest);

    /**
     * 更新中方评价体系
     * @param systemRussianUpdateRequest
     * @return
     */
    Boolean updateChineseSystem(SystemRussianUpdateRequest systemRussianUpdateRequest);

}
