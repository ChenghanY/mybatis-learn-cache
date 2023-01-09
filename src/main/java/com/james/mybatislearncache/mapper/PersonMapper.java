package com.james.mybatislearncache.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonMapper {

    void insert(@Param("id") Integer id, @Param("name") String name);

    void updateNameById(@Param("name") String name, @Param("name") Integer id);

    Integer selectIdByName(@Param("name") String name);

    void deleteAll();
}
