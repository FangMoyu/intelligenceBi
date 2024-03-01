package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 调用 AI 的接口
 * 加入到 IOC 容器中
 * 便于直接调用
 */
@Service
public class AiManager {
    @Resource
    private YuCongMingClient client;

    public String doChat(Long modelId,String message){
        // AI 消息发送请求对象
        DevChatRequest devChatRequest = new DevChatRequest();
        // 设置模型 id 和需求
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        // 调用 AI 对话，得到返回结果
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if(response == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 响应错误");
        }
        // 返回结果
        return response.getData().getContent();
    }
}
