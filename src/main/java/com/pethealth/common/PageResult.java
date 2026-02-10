package com.pethealth.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应封装类
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
     * 当前页数据列表
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
     * 总页数
     */
    private int totalPages;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 是否为第一页
     */
    private boolean isFirst;

    /**
     * 是否为最后一页
     */
    private boolean isLast;

    /**
     * 构造方法
     *
     * @param total 总记录数
     * @param list 当前页数据列表
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     */
    public PageResult(long total, List<T> list, int pageNum, int pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);

        // 计算分页状态
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
        this.isFirst = pageNum == 1;
        this.isLast = pageNum == totalPages || totalPages == 0;
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象创建 PageResult
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
     * 空分页结果
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
}
