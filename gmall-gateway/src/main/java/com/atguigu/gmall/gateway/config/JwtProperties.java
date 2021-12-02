//package com.atguigu.gmall.gateway.config;
//
//import com.atguigu.gmall.common.exception.UserException;
//import com.atguigu.gmall.common.utils.RsaUtils;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//import javax.annotation.PostConstruct;
//import java.io.File;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//
///**
// * @author Baozhong Chen
// * @version 1.0
// * @date 2020/9/6 22:52
// */
//
//@Data
//@Slf4j
//@ConfigurationProperties(prefix = "auth.jwt")
//public class JwtProperties {
//    private String pubKeyPath;
//    private String cookieName;
//
//    private PublicKey publicKey;
//
//    /**
//     * 该方法在构造方法之前执行
//     */
//    @PostConstruct
//    public void init(){
//        //根据路径读取公钥和私钥
//        try {
//            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new UserException("生成公钥和私钥出错" + e.getMessage());
//        }
//
//
//    }
//
//
//}
