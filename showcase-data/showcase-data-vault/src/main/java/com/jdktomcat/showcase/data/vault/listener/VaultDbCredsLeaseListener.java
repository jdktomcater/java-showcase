package com.jdktomcat.showcase.data.vault.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.event.*;

import jakarta.annotation.PostConstruct;

import java.util.Map;

/**
 * vault监听器
 */
@Slf4j
@Component
public class VaultDbCredsLeaseListener extends LeaseListenerAdapter {

    /**
     * 说明：Spring Cloud Vault（databases）会去读 {@code <backend>/creds/<role>} 来获取动态账号密码。
     * 这里从配置拼出期望监听的 path，避免把 role 写死导致“监听不到”。
     */
    @Value("${spring.cloud.vault.database.backend:database}")
    private String databaseBackend;

    @Value("${spring.cloud.vault.database.role:}")
    private String databaseRole;

    private String expectedDbCredsPathPrefix;

    @Resource
    private SecretLeaseContainer secretLeaseContainer;

    @PostConstruct
    public void init() {
        String backend = normalizePath(databaseBackend);
        String role = normalizePath(databaseRole);
        if (role.isEmpty()) {
            expectedDbCredsPathPrefix = backend + "/creds/";
            log.warn("Vault DB lease listener started, but spring.cloud.vault.database.role is empty. Will match path prefix: {}", expectedDbCredsPathPrefix);
        } else {
            expectedDbCredsPathPrefix = backend + "/creds/" + role;
            log.info("Vault DB lease listener started. Will match path prefix: {}", expectedDbCredsPathPrefix);
        }
        secretLeaseContainer.addErrorListener(this);
        secretLeaseContainer.addLeaseListener(this);
    }

    private static String normalizePath(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public void onLeaseEvent(@NonNull SecretLeaseEvent event) {
        String path = event.getSource().getPath();
        path = normalizePath(path);
        if (path.isEmpty()) {
            log.debug("Vault lease event without path: type={}", event.getClass().getSimpleName());
            return;
        }
        if (expectedDbCredsPathPrefix == null || expectedDbCredsPathPrefix.isEmpty() || !path.startsWith(expectedDbCredsPathPrefix)) {
            // 关键：别静默 return，否则你会误以为“监听器没起作用”
            log.debug("Ignoring Vault lease event: type={}, path={}, expectedPrefix={}", event.getClass().getSimpleName(), path, expectedDbCredsPathPrefix);
            return;
        }
        if (event instanceof SecretLeaseCreatedEvent) {
            Map<String, Object> secrets = ((SecretLeaseCreatedEvent) event).getSecrets();
            String username = String.valueOf(secrets.get("username"));
            // 注意：不要打印明文 password，这里只做是否存在的校验/埋点
            boolean passwordPresent = secrets.get("password") != null;
            // 建议不要打明文日志，这里只是演示：可以改成写入指标/告警/回调
            log.info("Vault DB creds CREATED: path={}, username={}, passwordPresent={}", path, username, passwordPresent);
        } else if (event instanceof AfterSecretLeaseRenewedEvent) {
            AfterSecretLeaseRenewedEvent renewedEvent = (AfterSecretLeaseRenewedEvent) event;
            log.info("Vault DB creds RENEWED: path={}", renewedEvent.getSource().getPath());
        } else if (event instanceof SecretLeaseExpiredEvent) {
            SecretLeaseExpiredEvent expiredEvent = (SecretLeaseExpiredEvent) event;
            log.warn("Vault DB creds EXPIRED: path={}", expiredEvent.getSource().getPath());
        } else {
            log.info("Vault lease event: type={}, path={}", event.getClass().getSimpleName(), path);
        }
    }

    @Override
    public void onLeaseError(@NonNull SecretLeaseEvent leaseEvent, @NonNull Exception exception) {
        super.onLeaseError(leaseEvent, exception);
        log.error("onLeaseError:{}", leaseEvent, exception);
    }
}
