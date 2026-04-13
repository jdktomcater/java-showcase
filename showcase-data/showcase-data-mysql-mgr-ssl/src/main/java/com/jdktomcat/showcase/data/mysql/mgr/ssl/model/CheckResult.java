package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public record CheckResult(
        String name,
        boolean passed,
        String detail
) {
}
