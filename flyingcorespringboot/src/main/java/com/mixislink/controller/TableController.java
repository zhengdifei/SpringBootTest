package com.mixislink.controller;

import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/city"})
public class CityRestController {


    @RequestMapping(value = "/name")
    public String findByName(String cityName) throws Exception {
        EngineParameter ep = new EngineParameter("city.findByName");
        ep.putParam("cityName", "hello");
        ep.putParam("province_id2", 2);
        Engine.execute(ep);
        return "success";
    }

}
