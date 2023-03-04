package com.example.warehouse.controller;

import com.example.warehouse.mapper.ud.AdvertisementUDMapper;
import com.example.domain.warehouse.Advertisement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告
 */
@RestController
@RequestMapping("/restful")
public class AdvertisementController {

    @Autowired
    private AdvertisementUDMapper adUDMapper;

    /**
     * 获取所有的广告
     */
    @GetMapping("/advertisements")
    public List<Advertisement> getAllAdvertisements() {
        return adUDMapper.findAll();
    }
}
