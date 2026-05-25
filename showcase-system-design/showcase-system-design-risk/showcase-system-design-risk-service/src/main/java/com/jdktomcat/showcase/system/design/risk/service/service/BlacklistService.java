package com.jdktomcat.showcase.system.design.risk.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * O(1) blacklist membership check backed by Redis SETs.
 *
 * <p>Three independent dimensions are maintained:
 * <ul>
 *   <li>{@code risk:blacklist:user}    – platform user IDs</li>
 *   <li>{@code risk:blacklist:ip}      – source IP addresses</li>
 *   <li>{@code risk:blacklist:address} – on-chain destination addresses (sanctions)</li>
 * </ul>
 *
 * <p>Entries are written asynchronously by the compliance team via the admin API or
 * a Kafka consumer that subscribes to a Chainalysis / OFAC sanctions feed.
 *
 * <p>Interview talking point: for the ultra-hot path we could layer a local
 * Bloom filter (Guava) in front of the Redis calls.  A Bloom filter with a
 * false-positive rate of 0.01 % eliminates ~99.99 % of Redis round trips for
 * clean traffic, at the cost of ~1.2 MB per 1 million entries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private static final String KEY_USER    = "risk:blacklist:user";
    private static final String KEY_IP      = "risk:blacklist:ip";
    private static final String KEY_ADDRESS = "risk:blacklist:address";

    private final RedissonClient redissonClient;

    // ── Read ────────────────────────────────────────────────────────────────

    public boolean isUserBlacklisted(Long userId) {
        if (userId == null) return false;
        return userSet().contains(userId.toString());
    }

    public boolean isIpBlacklisted(String ip) {
        if (ip == null || ip.isBlank()) return false;
        return ipSet().contains(ip);
    }

    public boolean isAddressBlacklisted(String address) {
        if (address == null || address.isBlank()) return false;
        return addressSet().contains(address.toLowerCase());
    }

    // ── Write ───────────────────────────────────────────────────────────────

    public void addUser(Long userId) {
        userSet().add(userId.toString());
        log.warn("User {} added to blacklist", userId);
    }

    public void removeUser(Long userId) {
        userSet().remove(userId.toString());
        log.info("User {} removed from blacklist", userId);
    }

    public void addIp(String ip) {
        ipSet().add(ip);
        log.warn("IP {} added to blacklist", ip);
    }

    public void removeIp(String ip) {
        ipSet().remove(ip);
    }

    public void addAddress(String address) {
        addressSet().add(address.toLowerCase());
        log.warn("Address {} added to sanctions list", address);
    }

    public void removeAddress(String address) {
        addressSet().remove(address.toLowerCase());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private RSet<String> userSet() {
        return redissonClient.getSet(KEY_USER);
    }

    private RSet<String> ipSet() {
        return redissonClient.getSet(KEY_IP);
    }

    private RSet<String> addressSet() {
        return redissonClient.getSet(KEY_ADDRESS);
    }
}
