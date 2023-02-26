package com.itmo.eva.service.impl;

import  com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.model.dto.system.SystemRussianUpdateRequest;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.system.SecondSystemVo;
import com.itmo.eva.model.vo.system.SystemVo;
import com.itmo.eva.mapper.SystemMapper;
import com.itmo.eva.service.SystemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_system(评价体系表)】的数据库操作Service实现
 * @createDate 2023-01-24 10:02:52
 */
@Service
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System>
        implements SystemService {


    /**
     * 获取俄方教学评价体系
     *
     * @return 俄方教学评价体系
     */
    @Override
    public List<SystemVo> getRussianSystem() {
        // 俄方所有一级评价
        List<System> firstSystem = baseMapper.getRussianFirstSystem();

        List<SystemVo> systemVoList = firstSystem.stream().map(system -> {
            SystemVo systemVo = new SystemVo();
            BeanUtils.copyProperties(system, systemVo);
            systemVo.setSid(system.getId());
            return systemVo;
        }).collect(Collectors.toList());

        for (SystemVo systemVo : systemVoList) {
            // 获取二级目录信息
            List<System> secondSystem = baseMapper.getRussianSecondSystem(systemVo.getSid());
            List<SecondSystemVo> secondSystemVoList = new ArrayList<>();
            for (System system : secondSystem) {
                SecondSystemVo secondSystemVo = new SecondSystemVo();
                secondSystemVo.setName(system.getName());
                secondSystemVo.setEName(system.getEName());
                secondSystemVoList.add(secondSystemVo);
            }
            systemVo.setChildren(secondSystemVoList);
        }
        return systemVoList;
    }

    /**
     * 中方教学评价体系
     */
    @Override
    public List<SystemVo> getChineseSystem() {
        // 俄方所有一级评价
        List<System> firstSystem = baseMapper.getChineseFirstSystem();

        List<SystemVo> systemVoList = firstSystem.stream().map(system -> {
            SystemVo systemVo = new SystemVo();
            BeanUtils.copyProperties(system, systemVo);
            systemVo.setSid(system.getId());
            return systemVo;
        }).collect(Collectors.toList());

        for (SystemVo systemVo : systemVoList) {
            // 获取二级目录信息
            List<System> secondSystem = baseMapper.getChineseSecondSystem(systemVo.getSid());

            List<SecondSystemVo> secondSystemVoList = new ArrayList<>();
            for (System system : secondSystem) {
                SecondSystemVo  secondSystemVo = new SecondSystemVo();
                secondSystemVo.setName(system.getName());
                secondSystemVo.setEName(system.getEName());
                secondSystemVoList.add(secondSystemVo);
            }
            systemVo.setChildren(secondSystemVoList);
        }
        return systemVoList;
    }

    /**
     * 更新俄方评价体系
     * @param systemRussianUpdateRequest 新的俄方评价体系
     * @return 更新成功
     */
    @Override
    public Boolean updateRussianSystem(SystemRussianUpdateRequest systemRussianUpdateRequest) {

        return null;
    }

    /**
     * 更新中方评价体系
     * @param systemRussianUpdateRequest 新的中方评价体系
     * @return 更新成功
     */
    @Override
    public Boolean updateChineseSystem(SystemRussianUpdateRequest systemRussianUpdateRequest) {
        return null;
    }

}




