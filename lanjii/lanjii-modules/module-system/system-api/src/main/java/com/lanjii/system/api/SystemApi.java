package com.lanjii.system.api;

/**
 * 系统模块对外API接口
 *
 * @author lanjii
 */
public interface SystemApi {

    /**
     * 创建租户默认管理员
     *
     * @param tenantId   租户ID
     * @param tenantCode 租户编码
     */
    void createTenantAdmin(Long tenantId, String tenantCode);

    /**
     * 发布系统通知
     *
     * @param title       通知标题
     * @param content     通知内容
     * @param publisherId 发布人ID
     * @return 通知ID
     */
    Long publishNotice(String title, String content, Long publisherId);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息，不存在时返回 null
     */
    String getUserInfo(Long userId);
}
