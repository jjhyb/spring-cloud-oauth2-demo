package com.yibo.product.domain.entity;

import lombok.Data;

/**
 * @author: huangyibo
 * @Date: 2022/1/18 17:41
 * @Description:
 */

@Data
public class ProductDO {

    /**
     * 主键id
     */
    private String id;

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
