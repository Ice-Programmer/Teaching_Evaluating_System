package com.itmo.eva.service;

import com.itmo.eva.model.vo.chart.ChartsVo;

public interface ChartService {

    /**
     * 获取俄方教师人员信息
     * @return
     */
    ChartsVo getStaticInfo();
}
