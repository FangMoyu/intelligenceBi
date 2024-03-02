package com.yupi.springbootinit.Chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.chat")
@Data
public class ChatConfig {
    private String appId;
    private String APISecret;
    private String APIKey;

    @Bean
    public XFChat xfChat() {
        return new XFChat(appId,APISecret, APIKey);
    }
}
