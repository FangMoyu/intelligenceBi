package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = -4723199296109571210L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String userAccount;
    private String userAvatar;
    private Integer gender;
    private String userRole;
    private String userPassword;
    private Date createTime;
    private Date updateTime;

    @TableLogic
    private Integer isDelete;



}