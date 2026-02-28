package com.lanjii.ai.config;

import com.lanjii.ai.tool.DateTimeTools;
import com.lanjii.ai.tool.NoticeTools;
import com.lanjii.ai.tool.UserInfoTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 工具注册配置
 *
 * @author lanjii
 */
@Configuration
public class AiToolConfig {

    /**
     * 注册自定义业务工具
     */
    @Bean
    public ToolCallbackProvider bizToolCallbackProvider(NoticeTools noticeTools,
                                                         DateTimeTools dateTimeTools,
                                                         UserInfoTools userInfoTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(noticeTools, dateTimeTools, userInfoTools)
                .build();
    }
}
