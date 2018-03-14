package com.mixislink.controller;

import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping({"/table"})
public class TableController {


    @RequestMapping(value = "/insert")
    public String insert(HttpServletRequest request) throws Exception {
        EngineParameter ep = new EngineParameter("T_BASE_TABLE.insert");
        ep.putParam("BMC", "city");
        ep.putParam("BZS", "city");
        ep.putParam("BMS", "city");
        ep.putParam("X", 200);
        ep.putParam("Y", 300);
        ep.putParam("HEIGHT", 100);
        ep.putParam("WIDTH", 100);
        ep.putParam("STATE", 1);
        Engine.execute(ep);
        return "success";
    }

    @RequestMapping(value = "/update")
    public String update(HttpServletRequest request) throws Exception {
        EngineParameter ep = new EngineParameter("T_BASE_TABLE.update");
        ep.putParam("BID", 3);
        ep.putParam("BMC", "city1");
        ep.putParam("BZS", "city1");
        ep.putParam("BMS", "city1");
        Engine.execute(ep);
        return "success";
    }

    @RequestMapping(value = "/delete")
    public String delete(HttpServletRequest request) throws Exception {
        EngineParameter ep = new EngineParameter("T_BASE_TABLE.delete");
        ep.putParam("BID", 4);
        Engine.execute(ep);
        return "success";
    }

    @RequestMapping(value = "/selectOne")
    public Map selectOne(HttpServletRequest request) throws Exception {
        EngineParameter ep = new EngineParameter("T_BASE_TABLE.selectById");
        ep.putParam("BID", 5);
        Engine.execute(ep);
        return ep.getResultMap();
    }

    @RequestMapping(value = "/selectAll")
    public Map selectAll(HttpServletRequest request) throws Exception {
        EngineParameter ep = new EngineParameter("T_BASE_TABLE.selectAll");
        ep.putParam("BID", 5);
        Engine.execute(ep);
        return ep.getResultMap();
    }
}
