package com.pethealth.common;

import lombok.Data;

/**
 * 分页请求封装类
 */
@Data
public class PageRequest {

    /**
     * 当前页码 (默认第1页)
     */
    private int pageNum = 1;

    /**
     * 每页大小 (默认10条)
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向 (ASC/DESC)
     */
    private String sortDirection;

    // 手动添加 getter 方法（如果Lombok 有问题）
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * 默认构造方法
     */
    public PageRequest() {}

    /**
     * 带参数的构造方法
     */
    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 带排序的构造方法
     */
    public PageRequest(int pageNum, int pageSize, String sortField, String sortDirection) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
    }

    /**
     * 获取排序SQL语句
     */
    public String getOrderBy() {
        if (sortField == null || sortField.isEmpty()) {
            return null;
        }

        String direction = "ASC";
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            direction = "DESC";
        }

        return sortField + " " + direction;
    }
}
