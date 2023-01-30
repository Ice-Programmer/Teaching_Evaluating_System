package com.itmo.eva.model.vo.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价体系表
 * @TableName e_system
 */
@Data
public class SystemVo implements Serializable {

    /**
     * id
     */
    private Integer sid;

    /**
     * 一级评价名称
     */
    private String name;

    /**
     * 一级评价英文名称
     */
    private String eName;

    /**
     * 储存二级评价
     */
    private List<SecondSystemVo> children;

    private static final long serialVersionUID = 1L;
}