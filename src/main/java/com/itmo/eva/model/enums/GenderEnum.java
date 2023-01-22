package com.itmo.eva.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 性别枚举
 *
 * @author chenjiahan
 */
public enum GenderEnum {
    FEMALE("女", 0),
    MALE("男", 1);

    private final String sex;

    private final int value;


    GenderEnum(String sex, int value) {
        this.sex = sex;
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

    public int getValue() {
        return value;
    }

    public String getSex() {
        return sex;
    }
}
