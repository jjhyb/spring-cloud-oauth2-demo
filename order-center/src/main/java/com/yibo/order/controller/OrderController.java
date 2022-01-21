package com.yibo.order.controller;

import com.yibo.order.client.ProductFeignClient;
import com.yibo.order.domain.ProductDTO;
import com.yibo.order.domain.entity.OrderInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huangyibo
 * @Date: 2022/1/18 18:07
 * @Description:
 */

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @GetMapping("/query")
    public OrderInfo getOrder(String orderId){
        ProductDTO productDTO = productFeignClient.queryProduct("1001212");
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(productDTO,orderInfo);
        orderInfo.setId(orderId);
        return orderInfo;
    }
}
