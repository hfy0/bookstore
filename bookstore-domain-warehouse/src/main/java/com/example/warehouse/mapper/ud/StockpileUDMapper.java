package com.example.warehouse.mapper.ud;

import org.apache.ibatis.annotations.Select;
import com.example.domain.warehouse.Stockpile;

public interface StockpileUDMapper {
    @Select("select * from stockpile where product_id = #{productId,jdbcType=INTEGER}")
    Stockpile selectByProductId(Integer productId);
}
