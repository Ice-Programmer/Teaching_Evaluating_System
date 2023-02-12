package com.itmo.eva.model.enums;

/**
 * 队伍状态枚举
 */
public enum EmailStateEnum {
    PENDING(0, "待发送"),
    SUCCESS(1, "发送成功"),
    FAILED(2, "发送失败");

    private int value;

    private String text;

    public static EmailStateEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        EmailStateEnum[] values = EmailStateEnum.values();
        for (EmailStateEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    EmailStateEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
