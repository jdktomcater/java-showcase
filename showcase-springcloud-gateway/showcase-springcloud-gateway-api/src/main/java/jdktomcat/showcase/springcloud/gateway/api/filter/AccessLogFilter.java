package jdktomcat.showcase.springcloud.gateway.api.filter;

import cn.hutool.core.collection.CollectionUtil;
import jdktomcat.showcase.springcloud.gateway.api.log.GatewayLog;
import jdktomcat.showcase.springcloud.gateway.api.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * /** * 全局拦截器，作用所有的微服务 * 1. 对请求的API调用过滤，记录接口的请求时间，方便日志审计、告警、分析等运维操作 * 2. 后期可以扩展对接其他日志系统
 */
@Slf4j
@Component
class AccessLogFilter implements GlobalFilter, Ordered {
    /**
     * default HttpMessageReader.
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    @Autowired
    private AccessLogService accessLogService;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    /**
     * 顺序必须是<-1，否则标准的NettyWriteResponseFilter将在您的过滤器得到一个被调用的机会之前发送响应     * 也就是说如果不小于 -1 ，将不会执行获取后端响应的逻辑     *     * @return
     */
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();        // 请求路径
        String requestPath = request.getPath().pathWithinApplication().value();
        Route route = getGatewayRoute(exchange);
        String ipAddress = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        GatewayLog gatewayLog = new GatewayLog();
        gatewayLog.setSchema(request.getURI().getScheme());
        gatewayLog.setRequestMethod(request.getMethodValue());
        gatewayLog.setRequestPath(requestPath);
        gatewayLog.setTargetServer(route.getId());
        gatewayLog.setRequestTime(new Date());
        gatewayLog.setIp(ipAddress);
        MediaType mediaType = request.getHeaders().getContentType();
        assert mediaType != null;
        gatewayLog.setRequestContentType(mediaType.getType() + "/" + mediaType.getSubtype());        //json格式
        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            return writeBodyLog(exchange, chain, gatewayLog); //form-data格式
        } else if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType) || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return readFormData(exchange, chain, gatewayLog);
        } else {            //其他格式
            return writeBasicLog(exchange, chain, gatewayLog);
        }
    }

    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog accessLog) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer);
            final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }

                @Override
                public MultiValueMap<String, String> getQueryParams() {
                    return UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build().getQueryParams();
                }
            };
            StringBuilder builder = new StringBuilder();
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            if (CollectionUtil.isNotEmpty(queryParams)) {
                for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                    builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ","));
                }
            }
            accessLog.setRequestBody(builder.toString());            //获取响应体
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
            return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                        // 打印日志
                writeAccessLog(accessLog);
            }));
        });
    }

    /**
     * 读取form-data数据     *     * @param exchange     * @param chain     * @param accessLog     * @return
     */
    private Mono<Void> readFormData(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog accessLog) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer);
            final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }

                @Override
                public MultiValueMap<String, String> getQueryParams() {
                    return UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build().getQueryParams();
                }
            };
            final HttpHeaders headers = exchange.getRequest().getHeaders();
            if (headers.getContentLength() == 0) {
                return chain.filter(exchange);
            }
            ResolvableType resolvableType;
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(headers.getContentType())) {
                resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
            } else {                //解析 application/x-www-form-urlencoded
                resolvableType = ResolvableType.forClass(String.class);
            }
            return MESSAGE_READERS.stream().filter(reader -> reader.canRead(resolvableType, mutatedRequest.getHeaders().getContentType())).findFirst().orElseThrow(() -> new IllegalStateException("no suitable HttpMessageReader.")).readMono(resolvableType, mutatedRequest, Collections.emptyMap()).flatMap(resolvedBody -> {
                if (resolvedBody instanceof MultiValueMap) {
                    LinkedMultiValueMap map = (LinkedMultiValueMap) resolvedBody;
                    if (CollectionUtil.isNotEmpty(map)) {
                        StringBuilder builder = new StringBuilder();
                        final Part bodyPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("body");
                        if (bodyPartInfo instanceof FormFieldPart) {
                            String body = ((FormFieldPart) bodyPartInfo).value();//
                            log.info("body ==== " + body);
                            builder.append("body=").append(body);
                        }
                        final Part uidPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("uid");
                        if (uidPartInfo instanceof FormFieldPart) {
                            String uid = ((FormFieldPart) uidPartInfo).value();//
                            log.info("uid ==== " + uid);
                            accessLog.setUserId(uid);
                            if (builder.length() > 0) {
                                builder.append("&uid=").append(uid);
                            } else {
                                builder.append("uid=").append(uid);
                            }
                        }
                        final Part timeStampPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("timeStamp");
                        if (timeStampPartInfo instanceof FormFieldPart) {
                            String timeStamp = ((FormFieldPart) timeStampPartInfo).value();//
                            log.info("timeStamp ==== " + timeStamp);
                            if (builder.length() > 0) {
                                builder.append("&timeStamp=").append(timeStamp);
                            } else {
                                builder.append("timeStamp=").append(timeStamp);
                            }
                        }
                        final Part tokenPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("token");
                        if (tokenPartInfo instanceof FormFieldPart) {
                            String token = ((FormFieldPart) tokenPartInfo).value();//
                            log.info("token ==== " + token);
                            if (builder.length() > 0) {
                                builder.append("&token=").append(token);
                            } else {
                                builder.append("token=").append(token);
                            }
                        }
                        final Part signPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("sign");
                        if (signPartInfo instanceof FormFieldPart) {
                            String sign = ((FormFieldPart) signPartInfo).value();//
                            log.info("sign ==== " + sign);
                            if (builder.length() > 0) {
                                builder.append("&sign=").append(sign);
                            } else {
                                builder.append("sign=").append(sign);
                            }
                        }
                        accessLog.setRequestBody(builder.toString());
                    }
                } else {
                    accessLog.setRequestBody((String) resolvedBody);
                }                        //获取响应体
                ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
                return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                                    // 打印日志
                    writeAccessLog(accessLog);
                }));
            });
        });
    }

    /**
     * 解决 request body 只能读取一次问题，     * 参考: org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory     *     * @param exchange     * @param chain     * @param gatewayLog     * @return
     */
    @SuppressWarnings("unchecked")
    private Mono writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog) {
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            gatewayLog.setRequestBody(body);
            return Mono.just(body);
        });        // 通过 BodyInserter 插入 body(支持修改body), 避免 request body 只能获取一次
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());        // the new content type will be computed by bodyInserter        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {                    // 重新封装请求
            ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);                    // 记录响应日志
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);                    // 记录普通的
            return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                                // 打印日志
                writeAccessLog(gatewayLog);
            }));
        }));
    }

    /**
     * 打印日志     *     * @param gatewayLog 网关日志     * @author javadaily     * @date 2021/3/24 14:53
     */
    private void writeAccessLog(GatewayLog gatewayLog) {
        accessLogService.saveAccessLog(gatewayLog);
    }

    private Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

    /**
     * 请求装饰器，重新计算 headers     *     * @param exchange     * @param headers     * @param outputMessage     * @return
     */
    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {                    // TODO: this causes a 'HTTP/1.1 411 Length Required' // on                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    /**
     * 记录响应日志     * 通过 DataBufferFactory 解决响应体分段传输问题。
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, GatewayLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Date responseTime = new Date();
                    gatewayLog.setResponseTime(responseTime);                    // 计算执行时间
                    long executeTime = (responseTime.getTime() - gatewayLog.getRequestTime().getTime());
                    gatewayLog.setExecuteTime(executeTime);                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);//
                    log.info("originalResponseContentType =========== " + originalResponseContentType);
                    gatewayLog.setResponseContentType(originalResponseContentType);
                    gatewayLog.setCode(this.getStatusCode().value());//
                    if (ObjectUtils.equals(this.getStatusCode(), HttpStatus.OK)//
                            && !StringUtils.isNotEmpty(originalResponseContentType)//
                            && originalResponseContentType.contains("application/json")) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {                        // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);                        // 释放掉内存
                            DataBufferUtils.release(join);
                            String responseResult = new String(content, StandardCharsets.UTF_8);//
                            log.info("responseResult =========== " + responseResult);
                            gatewayLog.setResponseData(responseResult);
                            return bufferFactory.wrap(content);
                        }));
                    }//
                }                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
    }
}

