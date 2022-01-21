package com.yibo.auth.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {

    /**
     * 主键id
     */

    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String headPortrait;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别:1男,0女
     */
    private Byte sex;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态:1未禁用，0已禁用
     */
    private Byte status;

    /**
     * 帐户是否过期:1未过期，0已过期
     */
    private Byte accountNonExpired;

    /**
     * 帐户是否锁定:1未锁定，0已锁定
     */
    private Byte accountNonLocked;

    /**
     * 用户的凭据(密码)是否过期:1未过期，0已过期
     */
    private Byte credentialsNonExpired;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人id
     */
    private String creatorId;

    /**
     * 更新人id
     */
    private String updaterId;

    /**
     * 账户是否过期(1未过期，0已过期),过期无法验证
     * @return
     */
    public boolean isAccountNonExpired(){
        return 1 == this.accountNonExpired;
    }

    /**
     * 指定用户是否被锁定或者解锁(1未锁定，0已锁定),锁定的用户无法进行身份验证
     *
     * @return
     */
    public boolean isAccountNonLocked(){
        return 1 == this.accountNonLocked;
    }

    /**
     * 指示是否已过期的用户的凭据(密码，1未过期，0已过期),过期的凭据防止认证
     * @return
     */
    public boolean isCredentialsNonExpired(){
        return 1 == this.credentialsNonExpired;
    }

    /**
     * 是否被禁用(1未禁用，0已禁用),禁用的用户不能身份验证
     * @return
     */
    public boolean isEnabled(){
        return 1 == this.status;
    }
}