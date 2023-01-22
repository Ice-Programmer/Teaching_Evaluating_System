package com.itmo.eva.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 国籍枚举
 *
 * @author chenjiahan
 */
public enum IdentityEnum {
    RUSSIA("俄罗斯", 0),
    CHINA("中国", 1);

    private final String country;

    private final int value;


    IdentityEnum(String country, int value) {
        this.country = country;
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

    public String getCountry() {
        return country;
    }
}
