//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("interface_info")
public class InterfaceInfo implements Serializable {
    @TableId(
            type = IdType.AUTO
    )
    private Long id;
    private String requestParams;
    private String name;
    private String description;
    private String url;
    private String requestHeader;
    private String responseHeader;
    private Integer status;
    private String method;
    private Long userId;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
    @TableField(
            exist = false
    )
    private static final long serialVersionUID = 1L;


}
