package com.jdktomcat.showcase.system.design.order.service.feign;

import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckRequest;
import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "risk-service")
public interface RiskFeignClient {

    @PostMapping("/api/v1/risk/check")
    ResponseEntity<RiskCheckResponse> check(@RequestBody RiskCheckRequest request);
}
