package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import  com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.system.SystemAddRequest;
import com.itmo.eva.model.dto.system.SystemDeleteRequest;
import com.itmo.eva.model.dto.system.SystemUpdateRequest;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.system.SecondSystemVo;
import com.itmo.eva.model.vo.system.SystemVo;
import com.itmo.eva.mapper.SystemMapper;
import com.itmo.eva.service.SystemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                secondSystemVo.setSid(system.getId());
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
                secondSystemVo.setSid(system.getId());
                secondSystemVoList.add(secondSystemVo);
            }
            systemVo.setChildren(secondSystemVoList);
        }
        return systemVoList;
    }

    @Override
    public Boolean addSystem(SystemAddRequest systemAddRequest) {
        // 判断是添加一级评价还是二级评价
        Integer sid = systemAddRequest.getSid();
        String name = systemAddRequest.getName();
        System system = new System();

        if (sid != null) {
            // 添加二级评价
            // 判断是否存在对应的一级评价
            LambdaQueryWrapper<System> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(System::getId, sid);
            System firstSystem = this.getOne(queryWrapper);
            if (firstSystem == null || firstSystem.getLevel() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "对应一级评测信息错误");
            }
            system.setSid(sid);
            system.setLevel(2);
        } else {
            // 添加一级评价
            system.setLevel(1);
            system.setSid(0);
        }
        system.setName(name);
        system.setKind(systemAddRequest.getKind());
        system.setEName(systemAddRequest.getEName());

        boolean save = this.save(system);

        return save;
    }

    @Override
    public Boolean updateSystem(SystemUpdateRequest systemUpdateRequest) {
        // 判断是否存在改教学评价指标
        Integer id = systemUpdateRequest.getId();
        LambdaQueryWrapper<System> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(System::getId, id);
        System system = this.getOne(queryWrapper);
        if (system == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "不存在改评测");
        }
        if (systemUpdateRequest.getCName() != null) {
            system.setName(systemUpdateRequest.getCName());
        }
        if (systemUpdateRequest.getEName() != null) {
            system.setEName(systemUpdateRequest.getEName());
        }
        boolean update = this.updateById(system);

        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSystem(SystemDeleteRequest systemDeleteRequest) {
        Integer id = systemDeleteRequest.getId();
        LambdaQueryWrapper<System> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(System::getId, id);
        System oldSystem = this.getOne(queryWrapper);
        if (oldSystem == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 删除的为二级评价
        if (oldSystem.getLevel() == 2) {
            return this.removeById(oldSystem);
        }
        // 删除的为一级评价 需要将一级评价下所有的二级评价全部删除
        LambdaQueryWrapper<System> systemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        systemLambdaQueryWrapper.eq(System::getSid, id);
        // 找出对应的所有二级评价信息
        List<System> systemList = this.list(systemLambdaQueryWrapper);
        if (systemList.size() == 0) {
            return this.removeById(oldSystem);
        }
        List<Integer> systemIdList = systemList.stream().map(System::getId).collect(Collectors.toList());
        boolean delete = this.removeBatchByIds(systemIdList);
        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除二级指标失败");
        }
        return this.removeById(oldSystem);
    }


}




