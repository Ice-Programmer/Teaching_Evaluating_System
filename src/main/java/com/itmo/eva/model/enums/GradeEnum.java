package com.itmo.eva.model.enums;

import org.springframework.cglib.core.ReflectUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum GradeEnum {

    FIRST("第一学期", 1),
    SECOND("第二学期", 2),
    THIRD("第三学期", 3),
    FORTH("第四学期", 4),
    FIFTH("第五学期", 5),
    SIXTH("第六学期", 6),
    SEVENTH("第七学期", 7),
    EIGHT("第八学期", 8);

    private final String grade;

    private final int value;


    GradeEnum(String grade, int value) {
        this.grade = grade;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values())
                .map(item -> item.value)
                .collect(Collectors.toList());
    }

    public static GradeEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        GradeEnum[] values = GradeEnum.values();
        for (GradeEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }




    public int getValue() {
        return value;
    }

    public String getGrade() {
        return grade;
    }}
