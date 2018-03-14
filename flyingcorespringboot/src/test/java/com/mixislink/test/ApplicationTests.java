package com.mixislink.test;

import com.mixislink.Application;
import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    @Test
    @Rollback
    public void findByName() throws Exception{
        EngineParameter ep = new EngineParameter("city.findByName");
        ep.putParam("cityName", "hello");
        ep.putParam("province_id2", 2);
        Engine.execute(ep);
        System.out.println(ep.getResultMap());
        //Assert.assertEquals(1, city.getProvinceId().intValue());
    }
}
