package com.lanjii.sys.api;

import com.lanjii.sys.entity.SysDept;
import com.lanjii.sys.entity.SysUser;
import com.lanjii.sys.service.SysConfigService;
import com.lanjii.sys.service.SysDeptService;
import com.lanjii.sys.service.SysNoticeService;
import com.lanjii.sys.service.SysUserService;
import com.lanjii.system.api.dto.NoticeDTO;
import com.lanjii.common.constant.SysConfigKeys;
import com.lanjii.framework.context.tenant.TenantContext;
import com.lanjii.system.api.SystemApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统模块API实现类
 *
 * @author lanjii
 */
@Service
public class SystemApiImpl implements SystemApi {

    private final SysUserService sysUserService;
    private final SysDeptService sysDeptService;
    private final SysConfigService sysConfigService;
    private final SysNoticeService sysNoticeService;
    private final PasswordEncoder passwordEncoder;

    public SystemApiImpl(SysUserService sysUserService,
                         SysDeptService sysDeptService,
                         SysConfigService sysConfigService,
                         @Lazy SysNoticeService sysNoticeService,
                         PasswordEncoder passwordEncoder) {
        this.sysUserService = sysUserService;
        this.sysDeptService = sysDeptService;
        this.sysConfigService = sysConfigService;
        this.sysNoticeService = sysNoticeService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTenantAdmin(Long tenantId, String tenantCode) {
        Long previousTenantId = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(tenantId);

            // 创建默认部门
            SysDept dept = new SysDept();
            dept.setTenantId(tenantId);
            dept.setParentId(0L);
            dept.setAncestors("0");
            dept.setDeptName(tenantCode + "总部");
            dept.setSortOrder(1);
            dept.setIsEnabled(1);
            dept.setLeader("admin");
            sysDeptService.save(dept);

            // 创建管理员用户
            String defaultPwd = sysConfigService.getConfigValue(SysConfigKeys.DEFAULT_USER_PWD);
            SysUser admin = new SysUser();
            admin.setTenantId(tenantId);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(defaultPwd));
            admin.setNickname(tenantCode + "-管理员");
            admin.setIsEnabled(1);
            admin.setIsAdmin(1);
            admin.setDeptId(dept.getId());
            sysUserService.save(admin);

        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    @Override
    public Long publishNotice(String title, String content, Long publisherId) {
        NoticeDTO dto = new NoticeDTO();
        dto.setTitle(title);
        dto.setContent(content);
        return sysNoticeService.publishNotice(dto, publisherId);
    }

    @Override
    public String getUserInfo(Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return null;
        }
        String deptName = null;
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptService.getById(user.getDeptId());
            if (dept != null) {
                deptName = dept.getDeptName();
            }
        }
        return String.format("用户名: %s, 昵称: %s, 部门: %s, 邮箱: %s, 手机: %s",
                user.getUsername(),
                user.getNickname(),
                deptName != null ? deptName : "未分配",
                user.getEmail() != null ? user.getEmail() : "未设置",
                user.getPhone() != null ? user.getPhone() : "未设置");
    }
}
