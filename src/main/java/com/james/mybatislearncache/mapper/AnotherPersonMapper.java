package com.james.mybatislearncache.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnotherPersonMapper {
    void updateNameById(@Param("name") String name, @Param("id") Integer id);

}
