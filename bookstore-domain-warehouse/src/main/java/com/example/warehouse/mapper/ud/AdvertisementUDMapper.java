package com.example.warehouse.mapper.ud;

import org.apache.ibatis.annotations.Select;
import com.example.domain.warehouse.Advertisement;

import java.util.List;

public interface AdvertisementUDMapper {
    @Select("select * from advertisement")
    List<Advertisement> findAll();
}
