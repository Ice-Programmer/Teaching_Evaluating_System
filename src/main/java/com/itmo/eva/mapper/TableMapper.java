package com.itmo.eva.mapper;

import com.itmo.eva.model.dto.system.SecondSystemQueryRequest;

import java.util.List;

/**
 * 二级指标数据库操作
 */
public interface TableMapper {

    void removeTable(String tableName);

    List<Integer> getSecond(SecondSystemQueryRequest secondSystemQueryRequest);
}

