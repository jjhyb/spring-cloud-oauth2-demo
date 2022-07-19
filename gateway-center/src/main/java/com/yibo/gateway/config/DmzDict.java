package com.yibo.gateway.config;

import cn.hutool.core.builder.EqualsBuilder;
import cn.hutool.core.builder.HashCodeBuilder;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: huangyibo
 * @Date: 2022/7/15 19:02
 * @Description:
 */

@ToString
public class DmzDict implements Map.Entry<String, Boolean>, Serializable {

    private String urlPath;

    /**
     * true 允许外网访问
     * false 外网禁止访问
     */
    private Boolean accessible;

    public DmzDict() {}

    public DmzDict(String urlPath, Boolean accessible) {
        this.urlPath = urlPath;
        this.accessible = accessible;
    }

    @Override
    public String getKey() {
        return urlPath;
    }

    @Override
    public Boolean getValue() {
        return accessible;
    }

    @Override
    public Boolean setValue(Boolean value) {
        Boolean old = this.accessible;
        this.accessible = value;
        return old;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DmzDict dmzDict = (DmzDict) o;

        return new EqualsBuilder()
                .append(urlPath, dmzDict.urlPath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(urlPath)
                .toHashCode();
    }
}

