package com.yibo.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2022/5/23 17:41
 * @Description:
 */
@RestController
@RequestMapping(value = "rsa")
public class KeyPairController {

    @Autowired
    private KeyPair keyPair;

    @GetMapping(value = "publicKey")
    public Map<String, Object> getKey(){
        RSAPublicKey aPublic = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(aPublic).build();
        return new JWKSet(key).toJSONObject();
    }
}
