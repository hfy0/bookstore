package com.example.warehouse.service;

import com.example.domain.warehouse.Advertisement;

import com.example.warehouse.mapper.ud.AdvertisementUDMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementService {

    private static final Logger log = LoggerFactory.getLogger(AdvertisementService.class);

    @Autowired
    private AdvertisementUDMapper adUDMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public List<Advertisement> getAllAdverts() {
        return adUDMapper.findAll();
    }
}
