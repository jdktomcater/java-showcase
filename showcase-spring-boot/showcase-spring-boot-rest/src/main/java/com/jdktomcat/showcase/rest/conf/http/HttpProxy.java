package com.jdktomcat.showcase.rest.conf.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.util.Objects;

/**
 * @author luxun
 */
@Data
public class HttpProxy {

    private final String name;

    private final String ip;

    private final Integer port;

    private final boolean enabled;

    private final HttpHost host;

    public static HttpProxy of(String ip, Integer port) {
        return of(ip, port, true);
    }

    public static HttpProxy of(String ip, Integer port, boolean enabled) {
        return new HttpProxy(null, ip, port, enabled);
    }

    public static HttpProxy of(String name, String ip, Integer port, boolean enabled) {
        return new HttpProxy(name, ip, port, enabled);
    }

    public HttpProxy(String name, String ip, Integer port, boolean enabled) {
        this.ip = ip;
        this.port = port;
        this.enabled = enabled;
        this.name = defaultName(name);
        this.host = StringUtils.isNotBlank(ip) && Objects.nonNull(port) ? new HttpHost(ip, port) : null;
    }

    public String name() {
        return defaultName(this.name);
    }

    public String identity() {
        return this.ip + ":" + this.port;
    }

    public HttpHost host() {
        return this.host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpProxy that = (HttpProxy) o;
        return this.name.equals(that.name) || (this.ip.equals(that.ip) && this.port.equals(that.port));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ip, this.port);
    }

    private String defaultName(String name) {
        return StringUtils.defaultIfBlank(name, identity());
    }

    @Override
    public String toString() {
        return "HttpProxy{" +
                "name='" + this.name + '\'' +
                ", ip='" + this.ip + '\'' +
                ", port=" + this.port +
                ", enabled=" + this.enabled +
                '}';
    }
}
