package com.itmo.eva.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MajorEnum {
    COMPUTER("计算机科学与技术", 0),
    AUTOMATION("自动化", 1);

    private final String major;

    private final int value;


    MajorEnum(String major, int value) {
        this.major = major;
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

    public String getMajor() {
        return major;
    }
}
