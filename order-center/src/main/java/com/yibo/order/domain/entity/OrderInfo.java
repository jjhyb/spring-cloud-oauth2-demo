package com.yibo.order.domain.entity;

import lombok.Data;

/**
 * @author: huangyibo
 * @Date: 2022/1/18 18:09
 * @Description:
 */

@Data
public class OrderInfo {

    /**
     * 订单id
     */
    private String id;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private Integer price;

    /**
     * 库存
     */
    private Integer stock;
}
