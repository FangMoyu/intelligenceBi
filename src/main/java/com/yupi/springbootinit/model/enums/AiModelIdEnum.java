package com.yupi.springbootinit.model.enums;

public enum AiModelIdEnum {
    BI_MODEL_ID(1757685800222842882L, "智能BI模型id"),
    COPY_WRITING_ID(1651469535875809281L, "文案模型id"),

    ;
    private final Long modelId;
    private final String message;

    AiModelIdEnum(Long modelId, String message) {
        this.modelId = modelId;
        this.message = message;
    }

    public Long getModelId() {
        return modelId;
    }

    public String getMessage() {
        return message;
    }
}
