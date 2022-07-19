package com.yibo.gateway.constant;

/**
 * @Author: huangyibo
 * @Date: 2022/7/15 15:55
 * @Description:
 */
public class TokenConstant {

    /**
     * JWT的秘钥
     * TODO 实际项目中需要统一配置到配置文件中，资源服务也需要用到
     */
    public final static String SIGN_KEY="mayrhm0G_cKVQDplscKCShtdJKqvSMH8Hi3zHK9anCPrtVyosDp22WaGU3u-RruZMm7gJbG1h6RQ7Jl5qWnJ96ML6ZKMcZc84_y0BXqccFlBVOo_yGzOfANUHVctJNoQGO4ftdcUuNmKOkdSat2DXL-ZUtHMNU2O_h3MhOXjeN1rGVcMyFFafl_gCdbXsUwHeiWCenTwWxcn2KOjqXbGZJhK1oEHjZZLoTQF-y6TO6dhuiaTIqwDbkLSbqH32gAwSBRJjZO71wXk5fkL2V6VkjEZ6pjtOX7HZgRixkItJKvXuyGkbRN2fbmWhZ6_fRrkcKnav_FN9TJPeAQ5Alnc0w";

    public final static String TOKEN_NAME="jwt-token";

    public final static String PRINCIPAL_NAME="principal";

    public static final String AUTHORITIES_NAME="authorities";

    public static final String USER_ID="user_id";

    public static final String JTI="jti";

    public static final String EXPR="expr";
}
