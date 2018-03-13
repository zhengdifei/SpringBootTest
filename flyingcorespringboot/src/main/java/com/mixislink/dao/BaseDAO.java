package com.mixislink.dao;

import com.mixislink.logging.Log;
import com.mixislink.logging.LogFactory;
import com.mixislink.service.EngineParameter;
import com.mixislink.util.FlyingUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <B>描述：</B>dao层，数据操作基本类<br/>
 * <B>版本：</B>v3.0<br/>
 * <B>创建时间：</B>2018-03-13<br/>
 * <B>版权：</B>flying团队<br/>
 * 
 * @author zdf
 *
 */
@Component
public class BaseDAO implements IDAO {
	
	private static Log log = LogFactory.getLog(BaseDAO.class);//日志

	private final SqlSession sqlSession;

	public BaseDAO(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	/**
	 * 删除操作
	 * 
	 * @param ep EngineParameter对象
	 * @exception
	 */
	public void delete(EngineParameter ep) throws Exception {
        this.sqlSession.delete(ep.getCommand(),ep.getParamMap());
		log.debug("执行 "+ep.getCommand()+" 删除操作");
		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
	}
	/**
	 * 批量删除
	 * 
	 * @param epList EngineParameter对象
	 * @exception
	 */
	public void deleteAll(final List<EngineParameter> epList) throws Exception {
		/*this.sqlSession.execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor sqlmapexecutor) throws SQLException {
            	sqlmapexecutor.startBatch();
                for(EngineParameter ep : epList) {
                	sqlmapexecutor.delete(ep.getCommand(), ep.getParamMap());
                    log.debug("插入删除 "+ep.getCommand());
            		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
                }
                sqlmapexecutor.executeBatch();
                return null;
            }
        });*/
	}
	/**
	 * 插入操作
	 * 
	 * @param ep EngineParameter对象
	 * @return 插入，返回主键
	 * @exception
	 */
	public Object insert(EngineParameter ep) throws Exception {
		Object resultObj = this.sqlSession.insert(ep.getCommand(), ep.getParamMap());
		log.debug("执行 "+ep.getCommand()+" 插入操作，返回值  "+resultObj);
		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
		if(resultObj instanceof String){
			return (String)resultObj;
		}else if(resultObj instanceof Integer){
			return (Integer)resultObj;
		}else{
			return null;
		}
	}
	/**
	 * 批量插入操作
	 * 
	 * @param epList List对象
	 * @return 插入，返回主键
	 * @exception
	 */
	public int insertAll(final List<EngineParameter> epList) throws Exception {
		/*this.sqlSession.execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor sqlmapexecutor) throws SQLException {
            	sqlmapexecutor.startBatch();
                for(EngineParameter ep : epList) {
                	Object resultInt = sqlmapexecutor.insert(ep.getCommand(), ep.getParamMap());
                    log.debug("插入执行 "+ep.getCommand()+" 插入操作，返回值  "+resultInt);
            		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
                }
                sqlmapexecutor.executeBatch();
                return null;
            }
        });

		return epList.size();*/
		return 0;
	}
	/**
	 * 更新操作
	 * 
	 * @param ep EngineParameter对象
	 * @exception
	 */
	public void update(EngineParameter ep) throws Exception {
		this.sqlSession.update(ep.getCommand(), ep.getParamMap());
		log.debug("执行 "+ep.getCommand()+" 更新操作");
		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
	}
	/**
	 * 批量更新
	 * 
	 * @param epList List对象
	 * @exception
	 */
	public void updateAll(final List<EngineParameter> epList) throws Exception {
		/*this.sqlSession.execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor sqlmapexecutor) throws SQLException {
            	sqlmapexecutor.startBatch();
                for(EngineParameter ep : epList) {
                	sqlmapexecutor.update(ep.getCommand(), ep.getParamMap());
                	log.debug("更新执行 "+ep.getCommand());
            		log.log(Thread.currentThread().getId()+"", ep.getCommand(), ep.getCommandType(), FlyingUtil.changeMap2JsonString(ep.getParamMap()));
                }

                sqlmapexecutor.executeBatch();
                return null;
            }
        });*/
	}
	/**
	 * 查询一个对象
	 *
	 * @param ep EngineParameter对象
	 * @return 返回一个对象，一般使用Map存储
	 * @exception
	 */
	public Object selectOne(EngineParameter ep) throws Exception {
		Object obj = this.sqlSession.selectOne(ep.getCommand(),ep.getParamMap());
		log.debug("执行 "+ep.getCommand()+" 查询对象操作");
		return obj==null?new HashMap():obj;
	}
	
	/**
	 * 查询一组对象
	 * 
	 * @param ep EngineParameter对象
	 * @return 返回一组对象，使用List对象
	 * @exception
	 */
	public List<Object> selectList(EngineParameter ep)
			throws Exception {
		List<Object> listObj = this.sqlSession.selectList(ep.getCommand(),
				ep.getParamMap());
		log.debug("执行 "+ep.getCommand()+" 查询列表操作，不含分页信息");
		return listObj==null?new ArrayList():listObj;
	}
	/**
	 * 查询一组数据，带分页信息
	 * 
	 * @param ep EngineParameter对象
	 * @return 返回一个Map对象，其中key=data，存放数据，key=total存在总数量
	 * @exception
	 */
	public Map<String, Object> selectByPaging(EngineParameter ep)
			throws Exception {
		Map<String, Object> mapData = new HashMap<String, Object>();
		List<Object> listData = this.sqlSession.selectList(ep.getCommand(), ep.getParamMap());//列表
		int totalNum = 0;//总数量
		/*初始化列表*/
		if(listData == null){
			listData = new ArrayList();
		}else{
			totalNum = listData.size();
		}
		/*初始化总数量*/
		Object totalObj = this.sqlSession.selectOne(ep.getCommand() + "Total", ep.getParamMap());
		
		if(totalObj != null && (totalObj instanceof Integer)){
			totalNum = (Integer)totalObj;
		}
		mapData.put("total", totalNum);
		mapData.put("data", listData);

		log.debug("执行 "+ep.getCommand()+" 查询列表操作，含分页信息");
		return mapData;
	}
	/**
	 * 查询执行的sql语句
	 * 
	 * @param ep EngineParameter对象
	 * @return 返回执行sql字符串
	 * @exception
	 */
	public String getCommandSql(EngineParameter ep) throws Exception{
		/*SqlMapExecutorDelegate delegate = ((ExtendedSqlMapClient) (this.sqlSession
				.getSqlMapClient())).getDelegate();
		MappedStatement ms = delegate.getMappedStatement(ep.getCommand());
		RequestScope requestScope = new RequestScope();
		ms.initRequest(requestScope);

		Sql sqls = ms.getSql();
		ParameterMap parameterMap = sqls
				.getParameterMap(requestScope, ep.getParamMap());
		requestScope.setParameterMap(parameterMap);
		
		String sqlStr = sqls.getSql(requestScope, ep.getParamMap());
		log.debug("执行的语句："+ sqlStr);
		return sqlStr;*/
		return null;
	}
}
