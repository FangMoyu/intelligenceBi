package com.yupi.springbootinit.Chat;

import cn.hutool.core.lang.UUID;
import com.unfbx.sparkdesk.SparkDeskClient;
import com.unfbx.sparkdesk.entity.*;
import com.unfbx.sparkdesk.listener.ChatListener;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Data
public class XFChat {
    private String appId;
    private String apiSecret;
    private String apiKey;
    private static final String MODEL_HOST = "http://spark-api.xf-yun.com/v3.5/chat";
    private List<String> chatText = new ArrayList<>();
    public XFChat(String appId, String apiSecret, String apiKey) {
        this.apiKey = apiKey;
        this.appId = appId;
        this.apiSecret = apiSecret;
    }

    public static void main(String[] args) {
        //构建客户端
        SparkDeskClient sparkDeskClient = SparkDeskClient.builder()
                .host(MODEL_HOST)
                .appid("78f8a5db")
                .apiKey("7839c47ac25a80a6f51bcc0932ebbd15")
                .apiSecret("NzY0ZjE3ZjVlMjc4MjQ0ODZiMjIzMTNi")
                .build();
        //构建请求参数
        InHeader header = InHeader.builder().uid(UUID.randomUUID().toString().substring(0, 10)).appid("78f8a5db").build();
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain("generalv2").maxTokens(2048).temperature(0.3).build()).build();
        List<Text> text = new ArrayList<>();
        text.add(Text.builder().role(Text.Role.USER.getName()).content("使用md文档格式写出一个三行三列的表格，表头包含：姓名，性别，爱好。数据随机即可。").build());
        InPayload payload = InPayload.builder().message(Message.builder().text(text).build()).build();
        AIChatRequest aiChatRequest = AIChatRequest.builder().header(header).parameter(parameter).payload(payload).build();

        //发送请求
        sparkDeskClient.chat(new ChatListener(aiChatRequest) {
            //异常回调
            @SneakyThrows
            @Override
            public void onChatError(AIChatResponse aiChatResponse) {
                System.out.println(String.valueOf(aiChatResponse));
            }

            //输出回调
            @Override
            public void onChatOutput(AIChatResponse aiChatResponse) {
                System.out.println("产生了数据结果:" + aiChatResponse.getPayload().getChoices().getText().get(0).getContent());
            }

            //会话结束回调
            @Override
            public void onChatEnd() {
                System.out.println("当前会话结束了");
            }

            //会话结束 获取token使用信息回调
            @Override
            public void onChatToken(Usage usage) {
                System.out.println("token 信息：" + usage);
            }
        });

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
