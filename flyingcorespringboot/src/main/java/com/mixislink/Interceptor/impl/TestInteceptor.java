package com.mixislink.Interceptor.impl;

import com.mixislink.Interceptor.AbstractInterceptor;
import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;

public class TestInteceptor extends AbstractInterceptor{
    @Override
    public void before(EngineParameter ep) throws Exception {
        System.out.println("before");
        System.out.println(ep.getParamMap());
        System.out.println(ep.getResultMap());
        ep.putParam("description","world1");
        EngineParameter selfEp = new EngineParameter("");
        Engine.execute(selfEp);
        ep.putParam("abc",selfEp.getResult("abc"));
    }

    @Override
    public void after(EngineParameter ep) throws Exception {
        System.out.println("after");
        System.out.println(ep.getParamMap());
        System.out.println(ep.getResultMap());
    }
}
