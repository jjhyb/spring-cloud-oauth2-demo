package com.yibo.order.domain;

import lombok.Data;

/**
 * @author: huangyibo
 * @Date: 2022/1/18 17:41
 * @Description:
 */

@Data
public class ProductDTO {

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
