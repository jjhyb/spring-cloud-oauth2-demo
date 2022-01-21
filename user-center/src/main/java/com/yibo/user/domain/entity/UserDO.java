package com.yibo.user.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user")
public class UserDO {

    /**
     * 主键id
     */
    @Id
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
    @Column(name = "head_portrait")
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
    @Column(name = "account_non_expired")
    private Byte accountNonExpired;

    /**
     * 帐户是否锁定:1未锁定，0已锁定
     */
    @Column(name = "account_non_locked")
    private Byte accountNonLocked;

    /**
     * 用户的凭据(密码)是否过期:1未过期，0已过期
     */
    @Column(name = "credentials_non_expired")
    private Byte credentialsNonExpired;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建人id
     */
    @Column(name = "creator_id")
    private String creatorId;

    /**
     * 更新人id
     */
    @Column(name = "updater_id")
    private String updaterId;
}