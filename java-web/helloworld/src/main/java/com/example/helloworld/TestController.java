package com.example.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 进击的烧年.
 * @Date: 2021/5/31 21:37
 * @Description:
 */

@RestController
public class TestController {

    @GetMapping("/show")
    public String show(){
        return "hello world";
    }
}
