package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import com.jdktomcat.showcase.legacy.system.component.httpclient.config.DynamicProxyTemplateHolder;
import org.springframework.web.client.RestTemplate;

/**
 * @author luxun
 */
public final class TemplateHolderHelper {

    public static RestTemplate internal() {
        return DynamicProxyTemplateHolder.get(DynamicProxyTemplateHolder.getInternalProxy());
    }

    public static RestTemplate external() {
        return DynamicProxyTemplateHolder.get(DynamicProxyTemplateHolder.chooseDefaultProxy());
    }

    public static RestTemplate special() {
        return DynamicProxyTemplateHolder.get(DynamicProxyTemplateHolder.chooseDefaultSpecialProxy());
    }

    public static RestTemplate custom(String proxyIp, Integer proxyPort) {
        return DynamicProxyTemplateHolder.get(proxyIp, proxyPort);
    }


}
