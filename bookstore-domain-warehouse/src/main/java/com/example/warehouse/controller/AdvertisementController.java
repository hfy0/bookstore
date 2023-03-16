package com.example.warehouse.controller;

import com.example.domain.warehouse.Advertisement;
import com.example.warehouse.service.AdvertisementService;
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
    private AdvertisementService advertisementService;

    /**
     * 获取所有的广告
     */
    @GetMapping("/advertisements")
    public List<Advertisement> getAllAdverts() {
        return advertisementService.getAllAdverts();
    }
}
