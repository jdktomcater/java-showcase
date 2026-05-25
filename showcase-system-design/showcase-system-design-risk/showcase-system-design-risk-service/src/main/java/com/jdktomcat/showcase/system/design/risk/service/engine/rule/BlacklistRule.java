package com.jdktomcat.showcase.system.design.risk.service.engine.rule;

import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskRule;
import com.jdktomcat.showcase.system.design.risk.service.engine.RuleResult;
import org.springframework.stereotype.Component;

/**
 * Hard-BLOCK rule: immediately rejects requests from blacklisted users, IPs, or addresses.
 *
 * <p>Priority 1 — runs first in the chain and is <em>terminal</em>, so no other rule
 * wastes compute cycles once a blacklist hit is detected.
 *
 * <p>Blacklist membership is pre-fetched by {@code RiskCheckService} into
 * {@link RiskContext} before the engine runs, meaning this rule itself performs
 * zero I/O and stays under 1 µs.
 *
 * <p>Interview talking point: the blacklist is stored in a Redis SET per dimension
 * (user / IP / address). SISMEMBER is O(1).  A shadow copy in a local Bloom filter
 * (Guava / Caffeine) can further eliminate Redis RTT for the 99 % clean-traffic case.
 */
@Component
public class BlacklistRule implements RiskRule {

    @Override
    public String name() {
        return "BlacklistRule";
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public RuleResult evaluate(RiskContext ctx) {
        if (ctx.isUserBlacklisted()) {
            return RuleResult.block(name(),
                    "User " + ctx.getUserId() + " is on the global blacklist");
        }
        if (ctx.isIpBlacklisted()) {
            return RuleResult.block(name(),
                    "Source IP " + ctx.getUserIp() + " is on the global blacklist");
        }
        if (ctx.isAddressBlacklisted()) {
            return RuleResult.block(name(),
                    "Destination address " + ctx.getToAddress() + " is on the sanctions list");
        }
        return RuleResult.pass(name());
    }
}
