package com.yupi.springbootinit.model.enums;

/**
 * 图表状态枚举类
 */
public enum ChartStatusEnum {
    WAIT("wait","图表等待生成中"),
    RUNNING("running","图表生成中"),
    SUCCEED("succeed","图表生成成功"),
    FAIL("fail","图表生成失败");
    ;

    ChartStatusEnum(String status, String execMessage) {
        this.status = status;
        this.execMessage = execMessage;
    }

    private String status;
    private String execMessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecMessage() {
        return execMessage;
    }

    public void setExecMessage(String execMessage) {
        this.execMessage = execMessage;
    }
}
