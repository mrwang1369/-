package com.pethealth.utils;

import com.pethealth.enums.FileModuleTypeEnum;

/**
 * 文件工具类
 *
 * @author pethealth
 * @since 2026-02-27
 */
public class FileUtils {

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 根据模块类型获取存储路径
     *
     * @param moduleType 模块类型
     * @return 存储路径
     */
    public static String getModulePath(String moduleType) {
        FileModuleTypeEnum moduleTypeEnum = FileModuleTypeEnum.getByCode(moduleType);
        if (moduleTypeEnum == null) {
            return "general";
        }

        switch (moduleTypeEnum) {
            case PET_AVATAR:
                return "avatars";
            case MEDICAL_RECORD:
                return "medical_records";
            case VACCINATION_RECORD:
                return "vaccination_records";
            case CHECKUP_RECORD:
                return "checkup_records";
            case DEWORMING_RECORD:
                return "deworming_records";
            case SYMPTOM_RECORD:
                return "symptom_records";
            case GROWTH_EVENT:
                return "growth_events";
            default:
                return "general";
        }
    }

    /**
     * 格式化文件大小显示
     *
     * @param size 文件大小(字节)
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 验证文件名是否安全
     *
     * @param filename 文件名
     * @return 是否安全
     */
    public static boolean isFileNameSafe(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        // 检查非法字符
        String illegalChars = "<>:\"/\\|?*";
        for (char c : illegalChars.toCharArray()) {
            if (filename.indexOf(c) != -1) {
                return false;
            }
        }

        // 检查保留文件名
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        String nameWithoutExtension = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
        for (String reserved : reservedNames) {
            if (nameWithoutExtension.equalsIgnoreCase(reserved)) {
                return false;
            }
        }

        return true;
    }
}