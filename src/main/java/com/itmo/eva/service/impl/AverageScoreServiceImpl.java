package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.model.entity.AverageScore;
import com.itmo.eva.service.AverageScoreService;
import com.itmo.eva.mapper.AverageScoreMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【e_average_score(平均分表)】的数据库操作Service实现
* @createDate 2023-01-25 16:02:56
*/
@Service
public class AverageScoreServiceImpl extends ServiceImpl<AverageScoreMapper, AverageScore>
    implements AverageScoreService{

}




