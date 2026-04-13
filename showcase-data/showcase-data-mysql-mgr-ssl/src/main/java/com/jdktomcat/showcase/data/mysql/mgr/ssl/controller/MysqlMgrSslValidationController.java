package com.jdktomcat.showcase.data.mysql.mgr.ssl.controller;

import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ClusterValidationResponse;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.service.MysqlMgrSslValidationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mysql/mgr")
public class MysqlMgrSslValidationController {

    private final MysqlMgrSslValidationService validationService;

    public MysqlMgrSslValidationController(MysqlMgrSslValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/validate")
    public ClusterValidationResponse validate() {
        return validationService.validateCluster();
    }
}
