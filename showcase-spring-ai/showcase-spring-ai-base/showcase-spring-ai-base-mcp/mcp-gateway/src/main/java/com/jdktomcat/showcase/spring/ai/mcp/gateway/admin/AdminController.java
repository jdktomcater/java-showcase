package com.jdktomcat.showcase.spring.ai.mcp.gateway.admin;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.ToolDescriptor;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.UpstreamRegistry;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.UpstreamStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UpstreamRegistry upstreamRegistry;

    public AdminController(UpstreamRegistry upstreamRegistry) {
        this.upstreamRegistry = upstreamRegistry;
    }

    @GetMapping("/upstreams")
    public List<UpstreamStatus> upstreams() {
        return upstreamRegistry.upstreamStatuses();
    }

    @GetMapping("/tools")
    public Collection<ToolDescriptor> tools() {
        return upstreamRegistry.tools();
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        return upstreamRegistry.refresh();
    }
}
