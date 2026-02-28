package com.lanjii.ai.tool;

import com.lanjii.framework.context.tenant.TenantContext;
import com.lanjii.system.api.SystemApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * AI 用户信息工具 - 获取当前登录用户的信息
 *
 * @author lanjii
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoTools {

    private final SystemApi systemApi;

    @Tool(description = "获取当前登录用户的信息，包括用户名、昵称、部门、邮箱、手机号。当用户询问\"我是谁\"、\"我的信息\"等问题时调用此工具。")
    public String getCurrentUserInfo(ToolContext toolContext) {
        try {
            Long userId = ((Number) toolContext.getContext().get("userId")).longValue();
            Long tenantId = ((Number) toolContext.getContext().get("tenantId")).longValue();

            TenantContext.setTenantId(tenantId);

            String userInfo = systemApi.getUserInfo(userId);
            return userInfo != null ? userInfo : "未找到当前用户信息";
        } catch (Exception e) {
            log.error("AI Tool 获取用户信息失败", e);
            return "获取用户信息失败: " + e.getMessage();
        } finally {
            TenantContext.clear();
        }
    }
}
