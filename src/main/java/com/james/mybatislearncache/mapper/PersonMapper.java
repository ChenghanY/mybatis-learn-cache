package com.james.mybatislearncache.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonMapper {

    void insert(@Param("id") Integer id, @Param("name") String name);

    void updateNameById(@Param("name") String name, @Param("id") Integer id);

    String selectNameById(@Param("id") Integer id);

    void deleteAll();

    /**
     * 禁用一级缓存的查询
     */
    String selectNameByIdWithoutL1Cache(@Param("id") Integer id);
}
