package com.example.warehouse.mapper;

import com.example.domain.warehouse.Stockpile;

public interface StockpileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Stockpile record);

    int insertSelective(Stockpile record);

    Stockpile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Stockpile record);

    int updateByPrimaryKey(Stockpile record);
}