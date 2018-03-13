package com.mixislink.builder;

import com.mixislink.Interceptor.Interceptor;
import com.mixislink.exception.FlyingException;
import com.mixislink.init.Item;
import com.mixislink.init.Operation;
import com.mixislink.init.StaticVariable;
import com.mixislink.logging.Log;
import com.mixislink.logging.LogFactory;
import com.mixislink.task.ITask;
import com.mixislink.util.FileUtil;
import com.mixislink.util.FlyingUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  
 * <B>描述：</B>tablename操作工具类<br/>
 * <B>版本：</B>v2.0<br/>
 * <B>创建时间：</B>2012-10-10<br/>
 * <B>版权：</B>flying团队<br/>
 * 
 * @author zdf
 *
 */
public class TableNameFile {
	private static Log log = LogFactory.getLog(TableNameFile.class);//日志
//	/**
//	 * 解析系统中tablename*的配置文件
//	 * 
//	 * @return 返回Item集合
//	 */
//	public static List<Item> parse(){
//		log.debug("解析 tablename*.xml 开始");
//		// 构建tablename*.xml
//		String tablenamePath = BuilderUtil.getAllTableNameXmlPath();
//		String path = tablenamePath.substring(0,tablenamePath.lastIndexOf("/")+1);
//		String fileName = tablenamePath.substring(tablenamePath.lastIndexOf("/")+1);
//		File[] tableNameFiles = FileUtil.getFiles(path, fileName);
//		//存储需要生产操作的表数据
//		List<Item> needGenerateTable = new ArrayList<Item>();
//		//遍历所有文件
//		for(int i = 0;i<tableNameFiles.length;i++){
//			needGenerateTable.addAll(TableNameFile.singleParse(tableNameFiles[i]));
//		}
//		//主配置tablename.xml
//		Document tableNameDocument = FileUtil.readXml(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/tablename-core.xml"));
//		baseParse(tableNameDocument);
//
//		return needGenerateTable;
//	}
	/**
	 *解析系统中tablename-import的配置文件,获取所有配置
	 * 
	 * @return 返回Item集合
	 * @throws FlyingException 
	 */
	public static List<Item> parse() throws FlyingException{
		log.debug("解析 tablename-import.xml 开始");
		//存储需要生产操作的表数据
		List<Item> needGenerateTable = new ArrayList<Item>();
		log.info(BuilderUtil.getTableNameImportXmlPath());
		Document tableNameImportDocument = FileUtil.readXml(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/tablename-import.xml"));
		List tableList = tableNameImportDocument.selectNodes("/tablenames/import");
		// 遍历
		Iterator tableIter = tableList.iterator();
		while (tableIter.hasNext()) {
			Element itemElement = (Element) tableIter.next();
			
			String resource = "";
			if(FlyingUtil.validateData(itemElement.attribute("resource")) && FlyingUtil.validateData(itemElement.attribute("resource").getValue())){
				resource = itemElement.attribute("resource").getValue().trim();
			}else{
				log.warn("解析的resource为空 ！");
				continue;
			}
			
			log.debug("解析： " + resource +" 开始");

			needGenerateTable.addAll(singleParse(resource));
			
			log.debug("解析 "+ resource +" 结束");
		}
		
		return needGenerateTable;
		
	}
	/**
	 * @param tablenamePath
	 * @return
	 * @throws FlyingException 
	 */
	private static List<Item> singleParse(String tablenamePath) throws FlyingException{
		//存储需要生产操作的表数据
		List<Item> needGenerateTable = new ArrayList<Item>();
		//解析成document
		Document tableNameDocument = FileUtil.readXml(Thread.currentThread().getContextClassLoader().getResourceAsStream(tablenamePath));
		if(tableNameDocument == null){
			return needGenerateTable;
		}
		//将tablename*加载到内存
		baseParse(tableNameDocument);
		/**
		 * 解析tablename.xml
		 * 1.将load=false的数据放入List列表，false表示需要处理的表，true表示已经处理完毕或者暂时无需处理的表
		 * 2.load=false的需要table=“T_XG_STUDENT”,alias="学生管理",通过这两个表就可以构建学生管理模块。
		 * 学生模块包括
		 * 1.通过表名，就可以生产T_XG_STUDENT这个表的ibatis配置文件。
		 * 2.将学生管理菜单注册到数据库。
		 * 3.通过Ext创建默认前台列表页面。 
		 */
		//文件写回标记
		boolean isWrite = false;
		// 获取tablename标签下的item标签
		List tableList = tableNameDocument.selectNodes("/tablename/item[@load='false']");
		// 遍历
		Iterator tableIter = tableList.iterator();
		while (tableIter.hasNext()) {
			isWrite = true;
			Element itemElement = (Element) tableIter.next();
			// 将tablename，chineseName放入Item
			Item item = new Item();
			if(FlyingUtil.validateData(itemElement.attribute("name")) && FlyingUtil.validateData(itemElement.attribute("name").getValue())){
				item.setName(itemElement.attribute("name").getValue().trim());
				String alias = itemElement.attribute("alias") == null?"":itemElement.attribute("alias").getValue();
				if(!FlyingUtil.validateData(alias)){
					alias = "未定义别名";
				}
				item.setAlias(alias);
				//存储需要生产的记录
				needGenerateTable.add(item);
				// 修改配置文件属性load=false
				itemElement.attribute("load").setValue("true");
			}else{
				log.warn("解析的tablename的item的name为空 ！");
				continue;
			}
			
		}
		// 将文件写会原文件
		if(isWrite){
			FileUtil.writeXml(tableNameDocument, FileUtil.createFile(BuilderUtil.getRootPath() + tablenamePath));
		}

		return needGenerateTable;
	}
	/**
	 * 基本解析过程
	 * @param tableNameDocument
	 */
	private static void baseParse(Document tableNameDocument){
		//系统级拦截器
		List interceptorSystemList = tableNameDocument.selectNodes("/tablename/interceptors/interceptor-system");
		// 遍历
		Iterator interceptorSystemIter = interceptorSystemList.iterator();
		while (interceptorSystemIter.hasNext()) {
			Element systemElement = (Element) interceptorSystemIter.next();
			//获取拦截器
			List<Element> systemInterceptorsElement = systemElement.elements("interceptor");
			//遍历节点
			for(Iterator systemInters=systemInterceptorsElement.iterator();systemInters.hasNext();){
				Element systemInter = (Element) systemInters.next();
				//获取拦截
				Interceptor systemInterceptor;
				try {
					if(FlyingUtil.validateData(systemInter.attribute("class")) && FlyingUtil.validateData(systemInter.attribute("class").getText())){
						systemInterceptor = (Interceptor)(Class.forName(systemInter.attribute("class").getText().trim()).newInstance());
						systemInterceptor.initLog(systemInterceptor.getClass());//初始化日志方法
						StaticVariable.INTERCEPTOR_SYSTEM.add(systemInterceptor);
					}else{
						log.warn("interceptor-system中有class为空的拦截器！");
						continue;
					}
				} catch (ClassNotFoundException e) {
					log.error("找不到此类："+systemInter.attribute("class").getText(),e);
				} catch (InstantiationException e) {
					log.error("实例化："+systemInter.attribute("class").getText()+"失败！",e);
				} catch (IllegalAccessException e) {
					log.error("实例化："+systemInter.attribute("class").getText()+"失败！",e);
				}
			}
		}
		//拦截器集合
		List interceptorStackList = tableNameDocument.selectNodes("/tablename/interceptors/interceptor-stack");
		// 遍历
		Iterator interceptorStackIter = interceptorStackList.iterator();
		while (interceptorStackIter.hasNext()) {
			Element stackElement = (Element) interceptorStackIter.next();
			//获取拦截器
			List<Element> stackInterceptorsElement = stackElement.elements("interceptor");
			//拦截器对象列表
			List<Interceptor> stackInterceptorsList = new ArrayList();
			//遍历节点
			for(Iterator stackInters=stackInterceptorsElement.iterator();stackInters.hasNext();){
				Element stackInter = (Element) stackInters.next();
				//获取拦截
				Interceptor stackInterceptor;
				try {
					if(FlyingUtil.validateData(stackInter.attribute("class")) && FlyingUtil.validateData(stackInter.attribute("class").getText())){
						stackInterceptor = (Interceptor)(Class.forName(stackInter.attribute("class").getText().trim()).newInstance());
						stackInterceptor.initLog(stackInterceptor.getClass());//初始化日志方法
						stackInterceptorsList.add(stackInterceptor);
					}else{
						log.warn("interceptor-stack中有class为空的拦截器！");
						continue;
					}
				} catch (ClassNotFoundException e) {
					log.error("找不到此类："+stackInter.attribute("class").getText(),e);
				} catch (InstantiationException e) {
					log.error("实例化："+stackInter.attribute("class").getText()+"失败！",e);
				} catch (IllegalAccessException e) {
					log.error("实例化："+stackInter.attribute("class").getText()+"失败！",e);
				}
			}
			StaticVariable.INTERCEPTOR_COLLECTION.put(stackElement.attribute("name").getText(), stackInterceptorsList);
		}
		//模块集合
		List tableList = tableNameDocument.selectNodes("/tablename/item");
		// 遍历
		Iterator tableIter = tableList.iterator();
		while (tableIter.hasNext()) {
			Element itemElement = (Element) tableIter.next();
			//遍历items
			boolean isLoad = "false".equals(itemElement.attribute("load").getValue())?false:true;
			// 将tablename，chineseName放入Item
			Item item = new Item();
			if(FlyingUtil.validateData(itemElement.attribute("name")) && FlyingUtil.validateData(itemElement.attribute("name").getValue())){
				item.setName(itemElement.attribute("name").getValue().trim());
				String alias = itemElement.attribute("alias") == null?"":itemElement.attribute("alias").getValue();
				if(!FlyingUtil.validateData(alias)){
					alias = "未定义别名";
				}
				item.setAlias(alias);
			}else{
				log.warn("解析的tablename的item的name为空 ！");
				continue;
			}
			
			//操作节点名称op
			List<Element> zyElementList = (List<Element>) itemElement.elements("op");
			//遍历节点
			for(Iterator it=zyElementList.iterator();it.hasNext();){
				Element op = (Element) it.next();
				//配置操作类
				Operation operation = new Operation();
				if(FlyingUtil.validateData(op.attribute("sqlid")) && FlyingUtil.validateData(op.attribute("sqlid").getText())){
					//中文名称 必填
					String alias = op.attribute("alias") == null?"":op.attribute("alias").getText();
					if(!FlyingUtil.validateData(alias)){
						alias = "未定义别名";
					}
					operation.setAlias(alias);
					//执行的sqlid 必填
					operation.setSqlid(op.attribute("sqlid").getText().trim());
					//执行的类型type 必填
					operation.setType(op.attribute("type").getText());
					//执行的类型validate 可填
					operation.setValidate(Boolean.parseBoolean(op.attribute("validate")==null?"false":op.attribute("validate").getText()));
					//redis过期时间
					operation.setExpire(Integer.parseInt(op.attribute("expire")==null?"-1":op.attribute("expire").getText()));
				}else{
					log.warn("解析的op的sqlid为空 ！");
					continue;
				}
				//获取拦截器
				List<Element> interceptorsElement = op.elements("interceptor");
				//遍历节点
				for(Iterator inters=interceptorsElement.iterator();inters.hasNext();){
					Element inter = (Element) inters.next();
					//获取拦截
					Interceptor interceptor;
					try {
						if(FlyingUtil.validateData(inter.attribute("class")) && FlyingUtil.validateData(inter.attribute("class").getText())){
							interceptor = (Interceptor)(Class.forName(inter.attribute("class").getText().trim()).newInstance());
							interceptor.initLog(interceptor.getClass());//初始化日志方法
							operation.setInterceptor(interceptor);
						}else{
							log.warn("interceptor中有class为空的拦截器！");
							continue;
						}
					} catch (ClassNotFoundException e) {
						log.error("找不到此类："+inter.attribute("class").getText(),e);
					} catch (InstantiationException e) {
						log.error("实例化："+inter.attribute("class").getText()+"失败！",e);
					} catch (IllegalAccessException e) {
						log.error("实例化："+inter.attribute("class").getText()+"失败！",e);
					}
				}
				
				//获取拦截引用
				List<Element> interceptorRefElement = op.elements("interceptor-ref");
				//遍历节点
				for(Iterator intersRef = interceptorRefElement.iterator();intersRef.hasNext();){
					Element interRef = (Element) intersRef.next();
					if(FlyingUtil.validateData(interRef.attribute("name")) && FlyingUtil.validateData(interRef.attribute("name").getText())){
						String refName = interRef.attribute("name").getText().trim();
						List<Interceptor> refList = StaticVariable.INTERCEPTOR_COLLECTION.get(refName);
						for(int m =0;m<refList.size();m++){
							operation.setInterceptor(refList.get(m));
						}
					}else{
						log.warn("interceptor-ref中有name为空！");
						continue;
					}
					
				}
				//将操作存入静态变量
				item.setOperation(operation);
			}
			//将配置存入内存中
			StaticVariable.FLYINGCONFIG.put(item.getName(), item);
		}
		//系统期待时，启动的任务
		//模块集合
		List startupList = tableNameDocument.selectNodes("/tablename/startup/startup-item");
		// 遍历
		Iterator startupIter = startupList.iterator();
		while (startupIter.hasNext()) {
			Element itemElement = (Element) startupIter.next();
			try {
				if(FlyingUtil.validateData(itemElement.attribute("class")) && FlyingUtil.validateData(itemElement.attribute("class").getText())){
					StaticVariable.TASKS.add((ITask) Class.forName(itemElement.attribute("class").getText().trim()).newInstance());
				}else{
					log.warn("startup-item的name为空！");
					continue;
				}
			} catch (InstantiationException e) {
				log.error("实例化："+itemElement.attribute("class").getText()+"失败！",e);
			} catch (IllegalAccessException e) {
				log.error("实例化："+itemElement.attribute("class").getText()+"失败！",e);
			} catch (ClassNotFoundException e) {
				log.error("找不到此类："+itemElement.attribute("class").getText(),e);
			}
		}
		//登陆之后的白名单
		List loginWhiteList = tableNameDocument.selectNodes("/tablename/whiteList/login-whiteList-item");
		// 遍历
		Iterator loginWhiteIter = loginWhiteList.iterator();
		while (loginWhiteIter.hasNext()) {
			Element loginWhiteElement = (Element) loginWhiteIter.next();
			if(FlyingUtil.validateData(loginWhiteElement.attribute("name")) && FlyingUtil.validateData(loginWhiteElement.attribute("name").getText())){
				StaticVariable.LOGIN_WHITE_LIST.put(loginWhiteElement.attribute("name").getText().trim(),loginWhiteElement.attribute("name").getText().trim());
			}else{
				log.warn("login-whiteList-item的name为空！");
				continue;
			}
		}
		
		//无需登陆的白名单
		List logoutWhiteList = tableNameDocument.selectNodes("/tablename/whiteList/logout-whiteList-item");
		// 遍历
		Iterator logoutWhiteIter = logoutWhiteList.iterator();
		while (logoutWhiteIter.hasNext()) {
			Element logoutWhiteElement = (Element) logoutWhiteIter.next();
			if(FlyingUtil.validateData(logoutWhiteElement.attribute("name")) && FlyingUtil.validateData(logoutWhiteElement.attribute("name").getText())){
				StaticVariable.LOGOUT_WHITE_LIST.put(logoutWhiteElement.attribute("name").getText().trim(),logoutWhiteElement.attribute("name").getText().trim());
			}else{
				log.warn("logout-whiteList-item的name为空！");
				continue;
			}
		}
	} 
	
	/**
	 * 根据表名称，删除tablename的节点
	 * 
	 * @param bmc
	 * @throws FlyingException 
	 */
	public static void delete(String bmc) throws FlyingException{
		// 构建tablename.xml
		File tableNameFile = FileUtil.createFile(BuilderUtil.getTableNameXmlPath());
		// 读取文档
		Document tableNameDocument = FileUtil.readXml(tableNameFile);
		// 获取tablename标签下的item标签
		List tableList = tableNameDocument.selectNodes("/tablename/item[@name='"+bmc+"']");
		// 遍历
		Iterator tableIter = tableList.iterator();
		while (tableIter.hasNext()) {
			Element itemElement = (Element) tableIter.next();//元素
			
			itemElement.getParent().remove(itemElement);//删除元素
		}
		FileUtil.writeXml(tableNameDocument, tableNameFile);//写回操作
		
		log.debug("删除name="+bmc+"的item节点！");
	}
	
	/**
	 * 检查有tablename-module.xml配置文件是否存在，如果不存在，则生成。
	 * 
	 * @throws FlyingException 
	 * 
	 */
	public static void checkTablename() throws FlyingException{
		/* 检查有没有tablename-module.xml配置文件*/
		File tableNameFile = FileUtil.createFile(BuilderUtil.getTableNameXmlPath());
		if(!tableNameFile.exists()){
			Document tableNameDocument  = DocumentHelper.createDocument();//创建document文件
			
			tableNameDocument.addElement("tablename");//创建根节点
			
			FileUtil.writeXml(tableNameDocument, tableNameFile);//写回操作
			
			/*将tablename注册入系统*/
			String tableNameImportXmlPath = BuilderUtil.getTableNameImportXmlPath();
			File tableNameImportXmlFile = FileUtil.createFile(tableNameImportXmlPath);
			Document tableNameImportDocument = FileUtil.readXml(tableNameImportXmlFile);
			Element tablenamesElement = (Element) tableNameImportDocument.getRootElement();
			// 构建新增节点
			Element importElement = tablenamesElement.addElement("import");
			importElement.addAttribute("resource", "config/tablename-"+StaticVariable.MODULE+".xml");
			// 保存到文件
			FileUtil.writeXml(tableNameImportDocument,tableNameImportXmlFile);
			
			log.debug("在tablename-import.xml中注册 "+ StaticVariable.MODULE +" 子系统");
		}
	}
	/**
	 * 
	 * @param newTableList 需要生成的table集合
	 * @param dir 生成到指定目录
	 * @throws FlyingException
	 */
	public static void generateItem(List<Map> newTableList,String dir) throws FlyingException{
		/** 解析tablename.xml开始 **/
		// 构建tablename.xml
		File tableNameFile = null;
		//是否通过系统生成
		String isGen = "false";
		
		if(dir == null){
			//检查有tablename-module.xml
			TableNameFile.checkTablename();
			// 构建tablename.xml
			tableNameFile = FileUtil.createFile(BuilderUtil.getTableNameXmlPath());
		}else{
			// 构建tablename.xml
			tableNameFile = FileUtil.createFile(dir + "/tablename-" + StaticVariable.MODULE + ".xml");
			
			Document tableNameDocument  = DocumentHelper.createDocument();//创建document文件
			
			tableNameDocument.addElement("tablename");//创建根节点
			
			FileUtil.writeXml(tableNameDocument, tableNameFile);//写回操作
			
			isGen = "true";
		}
		
		// 读取文
		Document tableNameDocument = FileUtil.readXml(tableNameFile);
		//添加一个元素
		Element root = tableNameDocument.getRootElement();

		for(int m=0;m<newTableList.size();m++){
			//添加一个items节点
			Element item = root.addElement("item");
			
			String bmc = newTableList.get(m).get("BMC")==null?"":newTableList.get(m).get("BMC").toString().toUpperCase();
			String bzs = newTableList.get(m).get("BZS")==null?bmc:newTableList.get(m).get("BZS").toString();
			item.addAttribute("name",bmc);
			item.addAttribute("alias", bzs);
			item.addAttribute("load", isGen);
			
			Element addElem = item.addElement("op");
			addElem.addAttribute("alias", "添加【"+bzs+"】");
			addElem.addAttribute("sqlid", bmc+".insert");
			addElem.addAttribute("type", "insert");
			
			Element addInterceptorRef = addElem.addElement("interceptor-ref");
			addInterceptorRef.addAttribute("name", "insertInterceptor");
			
			Element updateElem = item.addElement("op");
			updateElem.addAttribute("alias", "修改【"+bzs+"】");
			updateElem.addAttribute("sqlid", bmc+".update");
			updateElem.addAttribute("type", "update");
			
			Element updateInterceptorRef = updateElem.addElement("interceptor-ref");
			updateInterceptorRef.addAttribute("name", "updateInterceptor");
			
			Element deleteElem = item.addElement("op");
			deleteElem.addAttribute("alias", "根据ID删除【"+bzs+"】");
			deleteElem.addAttribute("sqlid", bmc+".delete");
			deleteElem.addAttribute("type", "delete");
			
			Element selectAllElem = item.addElement("op");
			selectAllElem.addAttribute("alias", "查询【"+bzs+"】所有数据");
			selectAllElem.addAttribute("sqlid", bmc+".selectAll");
			selectAllElem.addAttribute("type", "map");
			
			Element selectByIdElem = item.addElement("op");
			selectByIdElem.addAttribute("alias", "查询【"+bzs+"】通过ID查询");
			selectByIdElem.addAttribute("sqlid", bmc+".selectById");
			selectByIdElem.addAttribute("type", "object");
		}

		//写回操作
		FileUtil.writeXml(tableNameDocument, tableNameFile);
		log.debug("向tablename.xml注册"+newTableList.size()+"个item");
	}
}
