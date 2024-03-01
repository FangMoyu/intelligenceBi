package com.yupi.springbootinit.model.vo;


import lombok.Data;

@Data
public class CopyWritingResponse {
    /**
     * id
     */
    private Long CopyWritingId;


    /**
     * 生成信息
     */
    private String genCopyWriting;
}
