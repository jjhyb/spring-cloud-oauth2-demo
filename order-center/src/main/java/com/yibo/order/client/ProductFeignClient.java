package com.yibo.order.client;

import com.yibo.order.domain.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: huangyibo
 * @Date: 2022/1/18 17:35
 * @Description:
 */

@FeignClient("product-center")
public interface ProductFeignClient {

    /**
     * 查询商品
     * @param id
     * @return
     */
    @GetMapping("/product/query")
    ProductDTO queryProduct(String id);
}
