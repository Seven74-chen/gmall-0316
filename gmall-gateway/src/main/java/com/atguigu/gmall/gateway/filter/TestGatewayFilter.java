//package com.atguigu.gmall.gateway.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author Baozhong Chen
// * @version 1.0
// * @date 2020/9/7 16:42
// */
//
//@Component
//public class TestGatewayFilter implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        System.out.println("无需配置，拦截所有网关的请求");
//        //放行
//        return chain.filter(exchange);
//    }
//
//    /**
//     * 通过实现Ordered中的getOrder方法控制全局过滤器的执行顺序
//     * @return
//     */
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
