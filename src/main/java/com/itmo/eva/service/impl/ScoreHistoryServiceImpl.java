package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.model.entity.ScoreHistory;
import com.itmo.eva.service.ScoreHistoryService;
import com.itmo.eva.mapper.ScoreHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【e_score_history(总分表)】的数据库操作Service实现
* @createDate 2023-01-25 13:28:07
*/
@Service
public class ScoreHistoryServiceImpl extends ServiceImpl<ScoreHistoryMapper, ScoreHistory>
    implements ScoreHistoryService {

}




