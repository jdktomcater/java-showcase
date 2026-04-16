package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public class CheckResult {

    private final String name;
    private final boolean passed;
    private final String detail;

    public CheckResult(String name, boolean passed, String detail) {
        this.name = name;
        this.passed = passed;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getDetail() {
        return detail;
    }
}
