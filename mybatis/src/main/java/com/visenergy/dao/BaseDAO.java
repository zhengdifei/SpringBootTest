package com.visenergy.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class BaseDAO{
    private final SqlSession sqlSession;

    public BaseDAO(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
    /**
     * 根据名称查询
     * @param cityName
     * @return
     */
    public Map findByName(Map paramMap) throws IOException {

        Map city = this.sqlSession.selectOne("city.findByName", paramMap);

        return city;
    }
}
