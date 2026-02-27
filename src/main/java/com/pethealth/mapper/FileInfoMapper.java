package com.pethealth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethealth.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件信息Mapper接口
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
}