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
@TableName("chart")
public class Chart implements Serializable {
    @TableId(
            type = IdType.ASSIGN_ID
    )
    private Long id;
    private String status;
    private String execMessage;
    private String name;
    private String goal;
    private String chartData;
    private String chartType;
    private String genChart;
    private String genResult;
    private Long userId;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Byte isDelete;
    @TableField(
            exist = false
    )
    private static final long serialVersionUID = 1L;


}
