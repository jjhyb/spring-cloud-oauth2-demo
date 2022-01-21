package com.yibo.product.controller;

import com.yibo.product.domain.entity.ProductDO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huangyibo
 * @Date: 2021/8/21 22:43
 * @Description:
 */

@RestController
@RequestMapping("/product")
public class ProductController {

    @GetMapping("/query")
    public ProductDO getProduct(String id){
        ProductDO productDO = new ProductDO();
        productDO.setId(id);
        productDO.setProductName("iphone13");
        productDO.setPrice(5999);
        productDO.setStock(100000);
        return productDO;
    }
}
