package com.example.warehouse.mapper.ud;

import com.example.domain.warehouse.Specification;

import java.util.List;

public interface SpecificationUDMapper {
    void saveAll(List<Specification> specifications);

    List<Specification> findByProductId(Integer id);
}
