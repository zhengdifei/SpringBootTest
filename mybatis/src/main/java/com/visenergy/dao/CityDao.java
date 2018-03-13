package com.visenergy.dao;

import com.visenergy.domain.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface CityDao {
    /**
     * 根据名称查询
     * @param cityName
     * @return
     */
    Map findByName(Map paramMap);
}
