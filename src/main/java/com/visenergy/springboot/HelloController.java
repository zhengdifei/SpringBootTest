package com.visenergy.springboot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/13 0013.
 */
@RestController
public class HelloController {

    @RequestMapping(method = RequestMethod.GET,path="/hello")
    public Map hello(){
        Map map = new HashMap();
        map.put("name","T_test1");
        map.put("age",34);
        map.put("sex",true);
        map.put("addr","abc");
        return map;
    }
}
