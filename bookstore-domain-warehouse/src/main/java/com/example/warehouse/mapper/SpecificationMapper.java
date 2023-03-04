package com.example.warehouse.mapper;

import com.example.domain.warehouse.Specification;

public interface SpecificationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Specification record);

    int insertSelective(Specification record);

    Specification selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Specification record);

    int updateByPrimaryKey(Specification record);
}