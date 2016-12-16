package com.gbcom.system.manager;

import com.gbcom.system.domain.ConfigTable;
import com.hc.core.orm.log.ITableConfigService;
import com.hc.core.orm.log.TableLogConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: User: Chenjp Date: 13-1-11 下午3:26
 */
@Service
public class ConfigTableManager implements ITableConfigService {
	/**
	 * 通过类名获取表日志配置信息
	 * 
	 * @param className
	 *            String
	 * @return ConfigTable
	 */
	public ConfigTable getByClassName(String className) {
		@SuppressWarnings("unused")
		String hql = "from ConfigTable t where className ='" + className + "' ";

		// List<ConfigTable> list = configTableService.find(hql);
		//
		// if (list != null) {
		// return list.get(0);
		// }

		return null;
	}

	/**
	 * 通过表名获取表日志配置信息
	 * 
	 * @param tableName
	 *            String
	 * @return ConfigTable
	 */
	public ConfigTable getByTableName(String tableName) {
		// String hql = "from ConfigTable t where tableName ='" + tableName +
		// "' ";
		//
		// List<ConfigTable> list = configTableService.find(hql);
		//
		// if (list != null) {
		// return list.get(0);
		// }

		return null;
	}

	/**
	 * @return Map<String, TableLogConfig>
	 */
	public Map<String, TableLogConfig> getAllConfig() {
		Map<String, TableLogConfig> map = new HashMap<String, TableLogConfig>();
		//
		// String hql =
		// "from ConfigTable t where isLog = '1' order by tableName asc";
		//
		// List<ConfigTable> list = configTableService.find(hql);
		//
		// for (ConfigTable tmp : list) {
		// map.put(tmp.getClassName(), tmp);
		// }

		return map;
	}

	/**
	 * 得到所有业务类名及中文表名
	 * 
	 * @return List<Map>
	 */
	@SuppressWarnings("unchecked")
	public List<Map> getAllBizClass() {
		List<Map> bizClasses = new ArrayList<Map>();
		// Map<String, String> comments = getComments();
		// Map allClassMetadata =
		// hibernateService.getSession().getSessionFactory().getAllClassMetadata();
		// for (Object key : allClassMetadata.keySet()) {
		// Object value = allClassMetadata.get(key);
		// if (value instanceof AbstractEntityPersister) {
		// AbstractEntityPersister abstractEntityPersister =
		// (AbstractEntityPersister) value;
		// Class clazz =
		// abstractEntityPersister.getMappedClass(EntityMode.POJO);
		// String packageName = clazz.getPackage().getName();
		// if (packageName.endsWith("exchange")) {
		// Map<String, String> bizClass = new HashMap<String, String>();
		// String tableName =
		// abstractEntityPersister.getTableName().toUpperCase();
		// String comment = comments.get(tableName);
		// bizClass.put("tableName", tableName);
		// if (StringHelper.isEmpty(comment)) {
		// comment = tableName;
		// }
		// // System.out.println();
		// bizClass.put("comment", comment);
		// bizClass.put("allClassName", clazz.getSimpleName());
		// bizClass.put("className",clazz.getCanonicalName());
		//
		//
		// bizClasses.add(bizClass);
		// }
		// }
		// }
		// //顺序
		// Comparator mycmp = ComparatorUtils.NATURAL_COMPARATOR;
		// ArrayList<Object> sortFields = new ArrayList<Object>();
		// //按字段submitTime排序
		// sortFields.add(new BeanComparator("className", mycmp));
		// ComparatorChain multiSort = new ComparatorChain(sortFields);
		// Collections.sort(bizClasses, multiSort);
		return bizClasses;
	}

	/**
	 * 从数据库中获取表名和注释
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getComments() {
		Map<String, String> comments = new HashMap<String, String>();
		// String sql =
		// "select u.table_name,u.comments from user_tab_comments u";
		// List<Object[]> list = hibernateService.findBySql(sql);
		// for (Object[] objects : list) {
		// String tableName = (String) objects[0];
		// String comment = (String) objects[1];
		// if (comment == null) {
		// comment = tableName;
		// }
		// comments.put(tableName, comment);
		// }
		return comments;
	}
}
