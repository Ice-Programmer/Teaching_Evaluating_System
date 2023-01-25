package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.model.entity.MarkHistory;
import com.itmo.eva.service.MarkHistoryService;
import com.itmo.eva.mapper.MarkHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【e_mark_history(一级指标表)】的数据库操作Service实现
* @createDate 2023-01-25 14:27:22
*/
@Service
public class MarkHistoryServiceImpl extends ServiceImpl<MarkHistoryMapper, MarkHistory>
    implements MarkHistoryService {

}




