package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.model.entity.Weight;
import com.itmo.eva.service.WeightService;
import com.itmo.eva.mapper.WeightMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【e_weight(权重表)】的数据库操作Service实现
* @createDate 2023-03-13 10:07:02
*/
@Service
public class WeightServiceImpl extends ServiceImpl<WeightMapper, Weight>
    implements WeightService{

}




