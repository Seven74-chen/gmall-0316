//package com.atguigu.gmall.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
///**
// * @author Baozhong Chen
// * @version 1.0
// * @date 2020/8/21 20:22
// */
//@Configuration
//public class CorsConfig {
//    @Bean
//    public CorsWebFilter corsWebFilter(){
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://manager.gmall.com");
//        config.addAllowedOrigin("http://www.gmall.com");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        corsConfigurationSource.registerCorsConfiguration("/**",config);
//        return new CorsWebFilter(corsConfigurationSource);
//    }
//}
