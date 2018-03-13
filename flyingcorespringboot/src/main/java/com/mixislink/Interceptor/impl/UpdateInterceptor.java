package com.mixislink.Interceptor.impl;

import com.mixislink.Interceptor.AbstractInterceptor;
import com.mixislink.exception.FlyingException;
import com.mixislink.service.Engine;
import com.mixislink.service.EngineParameter;
import com.mixislink.util.FlyingUtil;

import java.util.Map;

/**
 * <B>描述：</B>update操作拦截器<br/>
 * <B>版本：</B>v2.0<br/>
 * <B>创建时间：</B>2012-10-10<br/>
 * <B>版权：</B>flying团队<br/>
 * 
 * @author zdf
 *
 */
public class UpdateInterceptor extends AbstractInterceptor {
	@Override
	public void before(EngineParameter ep) throws Exception{
		EngineParameter newEp = null;
		//执行的命令
		String command = ep.getCommand();
		
		if(ep.getParam("MANUAL_CHANGE_PK") != null){
			//主键
			String pk = ep.getParam("MANUAL_CHANGE_PK").toString();
			if(!ep.getParam(pk).equals(ep.getParam("OLD_"+pk))){
				//构造新参数，
				newEp = new EngineParameter(command.substring(0, command.indexOf(".")+1)+"selectById");
				newEp.putParam(pk, ep.getParam(pk));
				Engine.execute(newEp);
				if(((Map)newEp.getResult("data")).size() > 0){
					throw new FlyingException("主键已经存在！");
				}
			}
		}
		
		newEp = new EngineParameter(command.substring(0, command.indexOf(".")+1)+"selectById");
		//相当深拷贝
		FlyingUtil.change(ep.getParamMap(),newEp.getParamMap());
		newEp.setCommandType("object");
		Engine.execute(newEp);
		//执行
		Map oldUser = (Map)newEp.getResult("data");
		//置换方法
		FlyingUtil.change(oldUser,ep.getParamMap());
	}
	@Override
	public void after(EngineParameter ep){	
	}
}
