//package com.atguigu.gmall.gateway.filter;
//
//import com.alibaba.nacos.client.utils.IPUtil;
//import com.atguigu.gmall.common.utils.IpUtil;
//import com.atguigu.gmall.common.utils.JwtUtils;
//import com.atguigu.gmall.gateway.config.JwtProperties;
//import com.sun.org.apache.regexp.internal.RE;
//import lombok.Data;
//import lombok.ToString;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Baozhong Chen
// * @version 1.0
// * @date 2020/9/7 16:50
// */
//@Component
//@EnableConfigurationProperties({JwtProperties.class})
//public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {
//
//    @Autowired
//    private JwtProperties jwtProperties;
//    public AuthGatewayFilterFactory() {
//        super(PathConfig.class);
//    }
//
//    @Override
//    public GatewayFilter apply(PathConfig config) {
//        return new GatewayFilter() {
//            @Override
//            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//                System.out.println("局部过滤器，需要配置才能拦截对应的服务请求" + config);
//                ServerHttpRequest request = exchange.getRequest();
//                ServerHttpResponse response = exchange.getResponse();
//                //1、判断当前请求路径在不在名单中，不在直接放行
//                //获取当前请求路径
//                String path = request.getURI().getPath();
//                if (config.authPaths.stream().allMatch(authPath->path.indexOf(authPath)==-1)){
//                    //放行 请求路径没有在过滤名单中
//                    return chain.filter(exchange);
//                }
//                //2、获取token信息：同步请求cookie中获取，异步在header信息中获取
//                String token = request.getHeaders().getFirst("token");
//                if (StringUtils.isEmpty(token)){
//                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
//                    if (!CollectionUtils.isEmpty(cookies)&&cookies.containsKey(jwtProperties.getCookieName())){
//                        token = cookies.getFirst(jwtProperties.getCookieName()).getValue();
//                    }
//                }
//
//                //3、判断是否为空，为空直接拦截
//                if (StringUtils.isEmpty(token)){
//                    //重定向到登录页面
//                    response.setStatusCode(HttpStatus.SEE_OTHER);
//                    response.getHeaders().set(HttpHeaders.LOCATION,"http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
//                    return response.setComplete();
//                }
//
//                //4、解析jwt，有异常直接拦截
//                try {
//                    Map<String, Object> map = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
//                    //5、判断是否为同一个ip地址
//                    String ip = map.get("ip").toString();
//                    String curIp = IpUtil.getIpAddressAtGateway(request);
//                    if (!StringUtils.equals(ip,curIp)){
//                        //重定向到登录页面
//                        response.setStatusCode(HttpStatus.SEE_OTHER);
//                        response.getHeaders().set(HttpHeaders.LOCATION,"http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
//                        return response.setComplete();
//                    }
//
//                    //6、传递登录信息给后续的服务，不需要再次解析jwt，解析耗时时间太长。
//                    //mutate:转变 将userId转为request
//                    request.mutate().header("userId",map.get("userId").toString()).header("userName",map.get("username").toString()).build();
//                    exchange.mutate().request(request).build();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    //重定向
//                    response.setStatusCode(HttpStatus.SEE_OTHER);
//                    response.getHeaders().set(HttpHeaders.LOCATION,"http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
//                    return response.setComplete();
//                }
//
//                //7、放行
//                return chain.filter(exchange);
//            }
//        };
//    }
//
//    /**
//     * 指定读取字段的结果集
//     * @return
//     */
//    @Override
//    public ShortcutType shortcutType() {
//        return ShortcutType.GATHER_LIST;
//    }
//
//    /**
//     * 指定字段顺序
//     * @return
//     */
//    @Override
//    public List<String> shortcutFieldOrder() {
//        return Arrays.asList("authPaths");
//    }
//
//
//    /**
//     * 读取配置类的内部类
//     */
//    @Data
//    @ToString
//    public static class PathConfig{
//        private List<String> authPaths;
//    }
//
//
//}
