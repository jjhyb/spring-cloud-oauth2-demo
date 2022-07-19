package com.yibo.gateway.config;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @Author: huangyibo
 * @Date: 2022/7/15 19:02
 * @Description:
 */
@Data
@RefreshScope
//@ConfigurationProperties(prefix = DmzConfig.DMZ_KEY)
@Slf4j
public class DmzConfig implements Serializable, InitializingBean, DisposableBean {

    public static final String DMZ_KEY = "dmz";

    private Set<DmzDict> urls;

    /**
     * true 配置整体生效
     * false 配置整体关闭
     */
    private boolean enable;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[afterPropertiesSet()] {}", toString());
    }

    @Override
    public void destroy() throws Exception {
        log.info("[destroy()] {}", toString());
    }

    public static class CustomDmzConfigParseProcess implements ExtraProcessor, ExtraTypeProvider {


        @Override
        public void processExtra(Object object, String key, Object value) {
            DmzDict dmzDict = (DmzDict) object;
            dmzDict.setUrlPath(key);
            dmzDict.setAccessible(Boolean.parseBoolean(String.valueOf(value)));
        }

        @Override
        public Type getExtraType(Object object, String key) {
            return String.class;
        }
    }
}

