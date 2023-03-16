package com.example.warehouse.service;

import com.example.util.RedisKeyUtil;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.mapper.SpecificationMapper;
import com.example.warehouse.mapper.ud.ProductUDMapper;
import com.example.warehouse.mapper.ud.SpecificationUDMapper;
import com.example.domain.warehouse.Product;
import com.example.domain.warehouse.Specification;
import com.example.dto.Item;
import com.example.dto.ProductInfo;
import com.example.dto.Settlement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 产品
 */
@Service
@Transactional
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductUDMapper productUDMapper;

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationUDMapper specificationUDMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据结算单中货物的ID，填充货物的完整信息到结算单对象上
     */
    public void replenishProductInformation(Settlement bill) {
        List<Integer> ids = bill.getItems().stream().map(Item::getProductId).collect(Collectors.toList());
        bill.productMap = productUDMapper.findByIdIn(ids).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    /**
     * 获取仓库中所有的货物信息
     */
    public List<ProductInfo> getAllProducts() {
        List<Product> products = productUDMapper.findAll();
        List<ProductInfo> productInfos = new ArrayList<>();
        for (Product product : products) {
            ProductInfo productInfo = new ProductInfo();
            BeanUtils.copyProperties(product, productInfo);
            productInfos.add(productInfo);

            // 设置商品规格
            List<Specification> specifications = specificationUDMapper.findByProductId(product.getId());
            productInfo.setSpecifications(specifications);
        }
        return productInfos;
    }

    /**
     * 获取仓库中指定的货物信息
     */
    public ProductInfo getProduct(Integer id) {
        ProductInfo productInfo = (ProductInfo) redisTemplate.opsForHash().get(RedisKeyUtil.getBookInfosKey(), String.valueOf(id));

        if (productInfo == null) {
            productInfo = new ProductInfo();
            Product product = productMapper.selectByPrimaryKey(id);
            BeanUtils.copyProperties(product, productInfo);

            // 设置商品规格
            List<Specification> specifications = specificationUDMapper.findByProductId(product.getId());
            productInfo.setSpecifications(specifications);

            redisTemplate.opsForHash().put(RedisKeyUtil.getBookInfosKey(), String.valueOf(id), productInfo);
            log.info("ID为{}的书籍加入Redis缓存及系统", productInfo.getId());
        } else {
            log.info("从Redis缓存系统中获取ID为{}的书籍", productInfo.getId());
        }
        return productInfo;
    }

    /**
     * 创建
     */
    public ProductInfo saveProduct(ProductInfo productInfo) {
        Product product = new Product();
        BeanUtils.copyProperties(productInfo, product);
        productMapper.insert(product);

        // 为商品规格设置productId
        List<Specification> specifications = productInfo.getSpecifications();
        for (Specification specification : specifications) {
            specification.setProductId(product.getId());
        }
        specificationUDMapper.saveAll(specifications);
        return productInfo;
    }

    /**
     * 更新产品信息
     */
    public Product updateProduct(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
        return product;
    }

    /**
     * 删除指定产品
     */
    public void removeProduct(Integer id) {
        productMapper.deleteByPrimaryKey(id);
    }
}
