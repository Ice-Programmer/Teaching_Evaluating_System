package com.itmo.eva.service;

import com.itmo.eva.model.dto.system.SystemAddRequest;
import com.itmo.eva.model.dto.system.SystemDeleteRequest;
import com.itmo.eva.model.dto.system.SystemUpdateRequest;
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
     * 增加教学评价体系
     * @param systemAddRequest
     * @return
     */
    Boolean addSystem(SystemAddRequest systemAddRequest);

    /**
     * 更新教学评价体系
     * @param systemUpdateRequest
     * @return
     */
    Boolean updateSystem(SystemUpdateRequest systemUpdateRequest);

    /**
     * 删除教学评价系统
     * @param systemDeleteRequest
     * @return
     */
    Boolean deleteSystem(SystemDeleteRequest systemDeleteRequest);
}
