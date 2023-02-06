package com.itmo.eva.service;

import com.itmo.eva.model.entity.RedlineHistory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author chenjiahan
* @description 针对表【e_redline_history(红线表)】的数据库操作Service
* @createDate 2023-02-06 19:41:33
*/
public interface RedlineHistoryService extends IService<RedlineHistory> {
    /**
     * 统计教师红线记录
     * @param eid 评测id
     */
    void recordRedline(Integer eid);
}
