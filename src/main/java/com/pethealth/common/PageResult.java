package com.pethealth.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应封装
 * 封装分页响应数据（total、list），与PageRequest对应
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据列�?
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页�?
     */
    private int totalPages;

    /**
     * 是否有上一�?
     */
    private boolean hasPrevious;

    /**
     * 是否有下一�?
     */
    private boolean hasNext;

    /**
     * 是否为第一�?
     */
    private boolean isFirst;

    /**
     * 是否为最后一�?
     */
    private boolean isLast;

    /**
     * 构造方�?
     *
     * @param total 总记录数
     * @param list 当前页数据列�?
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     */
    public PageResult(long total, List<T> list, int pageNum, int pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        // 计算总页�?
        this.totalPages = (int) Math.ceil((double) total / pageSize);

        // 计算分页状�?
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
        this.isFirst = pageNum == 1;
        this.isLast = pageNum == totalPages || totalPages == 0;
    }

    /**
     * �?MyBatis-Plus �?Page 对象创建 PageResult
     *
     * @param page MyBatis-Plus 分页对象
     * @return PageResult 分页结果
     */
    public static <T> PageResult<T> from(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
    }

    /**
     * 空分页结�?
     */
    // 修改 PageResult.empty() 方法中的调用
    public static <T> PageResult<T> empty(PageRequest pageRequest) {
        return new PageResult<>(
                0,
                List.of(),
                pageRequest.getPageNum(),   // 改为 getPageNum()
                pageRequest.getPageSize()
        );
    }

    // 显式添加getter方法
    public long getTotal() { return total; }
    public List<T> getList() { return list; }
    public int getPageNum() { return pageNum; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
    public boolean isHasPrevious() { return hasPrevious; }
    public boolean isHasNext() { return hasNext; }
    public boolean isFirst() { return isFirst; }
    public boolean isLast() { return isLast; }
}
