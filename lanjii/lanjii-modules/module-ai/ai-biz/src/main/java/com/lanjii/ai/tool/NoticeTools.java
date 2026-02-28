package com.lanjii.ai.tool;

import com.lanjii.framework.context.req.BizReqContext;
import com.lanjii.framework.context.req.BizReqContextHolder;
import com.lanjii.framework.context.tenant.TenantContext;
import com.lanjii.system.api.SystemApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * AI 通知工具 - 通过自然语言发布系统通知
 *
 * @author lanjii
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeTools {

    private final SystemApi systemApi;

    @Tool(description = "发布系统通知给所有用户。当用户要求发布通知、公告、消息时调用此工具。")
    public String publishNotice(
            @ToolParam(description = "通知标题，简洁明了地概括通知内容") String title,
            @ToolParam(description = "通知正文内容，支持富文本") String content,
            ToolContext toolContext) {
        try {

            Long publisherId = ((Number) toolContext.getContext().get("userId")).longValue();
            Long tenantId = ((Number) toolContext.getContext().get("tenantId")).longValue();
            BizReqContext bizReqContext = (BizReqContext) toolContext.getContext().get("bizReqContext");

            TenantContext.setTenantId(tenantId);
            BizReqContextHolder.setContext(bizReqContext);

            Long noticeId = systemApi.publishNotice(title, content, publisherId);

            log.info("AI Tool 发布通知成功, noticeId={}, title={}", noticeId, title);
            return String.format("通知发布成功！通知ID: %d, 标题: %s", noticeId, title);
        } catch (Exception e) {
            log.error("AI Tool 发布通知失败, title={}", title, e);
            return "通知发布失败: " + e.getMessage();
        } finally {
            TenantContext.clear();
            BizReqContextHolder.clear();
        }
    }
}
