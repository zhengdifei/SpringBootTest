package com.mixislink.controller;

import com.mixislink.exception.FlyingException;
import com.mixislink.exception.FlyingExceptionTranslator;
import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/"})
public class CommonController {

    @RequestMapping(value = "/common.action")
    public Map common(HttpServletRequest request) throws Exception {
        EngineParameter ep = (EngineParameter) request.getAttribute("ep");// 获取参数
        /* 业务操作 */
        try{
            Engine.execute(ep);

            return ep.getResultMap();//返回结果json
        }catch(Throwable e){//处理系统级错误
            FlyingException fue = FlyingExceptionTranslator.translate(e);
            Map errorMap = new HashMap();
            errorMap.put("success", false);
            errorMap.put("msg", fue.getMessage());
            return errorMap;//执行失败，向前台输出错误信息json
        }
    }
}
