package com.visenergy.controller;

import com.visenergy.dao.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/city"})
public class CityRestController {

    @Autowired
    private BaseDAO baseDAO;

    @RequestMapping(value = "/name")
    public String findByName(String cityName){
        Map paramMap = new HashMap();
        paramMap.put("cityName", "hello");
        paramMap.put("province_id2", 2);
        Map city = null;
        try {
            city = baseDAO.findByName(paramMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(city);
        return "success";
    }
}
