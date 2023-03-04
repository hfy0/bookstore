package com.example.warehouse.mapper.ud;

import org.apache.ibatis.annotations.Select;
import com.example.domain.warehouse.Product;

import java.util.List;

public interface ProductUDMapper {
    List<Product> findByIdIn(List<Integer> ids);

    @Select("select * from product")
    List<Product> findAll();
}
