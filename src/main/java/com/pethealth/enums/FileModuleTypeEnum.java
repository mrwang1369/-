package com.pethealth.enums;

import lombok.Getter;

/**
 * 文件模块类型枚举
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Getter
public enum FileModuleTypeEnum {

    /**
     * 宠物头像
     */
    PET_AVATAR("pet_avatar", "宠物头像"),

    /**
     * 病历记录
     */
    MEDICAL_RECORD("medical_record", "病历记录"),

    /**
     * 疫苗记录
     */
    VACCINATION_RECORD("vaccination_record", "疫苗记录"),

    /**
     * 体检记录
     */
    CHECKUP_RECORD("checkup_record", "体检记录"),

    /**
     * 驱虫记录
     */
    DEWORMING_RECORD("deworming_record", "驱虫记录"),

    /**
     * 症状记录
     */
    SYMPTOM_RECORD("symptom_record", "症状记录"),

    /**
     * 成长事件
     */
    GROWTH_EVENT("growth_event", "成长事件"),

    /**
     * 通用文件
     */
    GENERAL("general", "通用文件");

    private final String code;
    private final String displayName;

    FileModuleTypeEnum(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 根据code获取枚举
     */
    public static FileModuleTypeEnum getByCode(String code) {
        for (FileModuleTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}