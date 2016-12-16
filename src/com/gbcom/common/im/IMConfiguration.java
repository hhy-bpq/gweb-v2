/**
 * @(#)IMConfiguration.java       07/11
 *
 * Copyright (c) 2007 GBCom Communication Technology Co.Ltd
 * All right reserved.
 */

package com.gbcom.common.im;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import com.gbcom.common.im.desc.*;
import com.gbcom.common.im.ds.IIDNameStruct;
import com.gbcom.common.im.exception.IMInitialException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 信息模型的配置项，单实例，用于根据.xml文件生成信息模型
 * 
 * @author fengjing
 * @version 2.0
 * 
 */
public class IMConfiguration extends AbstractConfiguration {
	/**
	 * 信息模型XML文件中各个标签名称
	 */
	private static final String IM_TAG = "im";
	private static final String IM_VER_TAG = "version";
	private static final String IM_TYPE_TAG = "type";

	private static final String COMMON_ID_TAG = "id";
	private static final String COMMON_NAME_TAG = "name";

	private static final String CLASS_TAG = "class";;
	private static final String CLASS_ALIAS_TAG = "aliasName";
	private static final String CLASS_ALMPARSER_TAG = "alarmParser";
	private static final String CLASS_RSPPARSER_TAG = "responseParser";
	private static final String CLASS_VECTOR_TAG = "vector";
	private static final String CLASS_MAXINDEX_TAG = "maxIndex";

	private static final String ATTR_TAG = "attribute";

	// 属性相关
	private static final String ATTR_NAME_TAG = "AttrName";
	private static final String ATTR_ALIAS_TAG = "AliasName";
	private static final String ATTR_DATA_TAG = "DataType";
	private static final String ATTR_CTRL_TAG = "ControlType";
	private static final String ATTR_Length_TAG = "Length";
	private static final String ATTR_DEF_TAG = "DefaultValue";
	private static final String ATTR_MAX_TAG = "MaxValue";
	private static final String ATTR_MIN_TAG = "MinValue";
	private static final String ATTR_MASK_TAG = "Mask";
	private static final String ATTR_ATCMD_TAG = "AtCmd";
	private static final String ATTR_RSPPARSER_TAG = "RspParser";

	// 枚举属性相关
	private static final String ENUM_TAG = "enum";
	private static final String ENUM_ETY_TAG = "entry";
	private static final String ENUM_GID_TAG = "groupId";
	private static final String ENUM_GNM_TAG = "groupName";

	// 枚举名值
	private static final String ENUM_NAME_TAG = "name";
	private static final String ENUM_VLE_TAG = "value";

	private static final String CLASS_ELM_TAG = "classElement";
	private static final String ATTR_ELM_TAG = "attrElement";

	// 操作
	private static final String OPT_TAG = "opt";

	/**
	 * 日志记录
	 */
	private static final Logger log = Logger.getLogger(IMConfiguration.class);

	/**
	 * 唯一实例
	 */
	public static IMConfiguration instance = null;

	/**
	 * 构造函数
	 */
	private IMConfiguration() {
	}

	/**
	 * 获得实例
	 * 
	 * @return 唯一实例
	 */
	public static IMConfiguration getInstance() {
		if (instance == null) {
			instance = new IMConfiguration();
		}

		return instance;
	}

	/**
	 * 通过文件路径构造IIM
	 * 
	 * @param filePath
	 *            文件的路径
	 * @return 生成的信息模型
	 * 
	 * @throws com.gbcom.common.im.exception.IMInitialException
	 *             信息模型初始化异常
	 */
	public IIM buildIM(String filePath) throws IMInitialException {
		return this.buildIM(new File(filePath));
	}

	/**
	 * 通过配置文件URL构造IIM
	 * 
	 * @param url
	 *            文件的URL
	 * @return 生成的信息模型
	 * @throws IMInitialException
	 *             信息模型初始化异常
	 */
	public IIM buildIM(URL url) throws IMInitialException {
		return this.buildIM(url.getFile());
	}

	/**
	 * 通过文件句柄构造IIM
	 * 
	 * @param file
	 *            文件句柄
	 * @return 生成的信息模型
	 * 
	 * @throws IMInitialException
	 *             信息模型初始化异常
	 */
	public IIM buildIM(File file) throws IMInitialException {
		Element root = this.configure(file);
		long timeStamp = file.lastModified();

		// 文件解析错误
		if (root == null) {
			String errMsg = "IM file error";
			log.error(errMsg);
			throw new IMInitialException(errMsg);
		}

		// 文件数据类型错误
		if (!root.getTagName().equals(IM_TAG)) {
			String errMsg = "IM data error";
			log.error(errMsg);
			throw new IMInitialException(errMsg);
		}

		String ver = root.getAttribute(IM_VER_TAG).trim();
		String type = root.getAttribute(IM_TYPE_TAG).trim();

		NodeList classList = root.getElementsByTagName(CLASS_TAG);

		// 没有找到任何的类描述
		if (classList == null || classList.getLength() == 0) {
			throw new IMInitialException("no class descriptions were found");
		} else {
			ArrayList<IIDNameStruct> data = new ArrayList<IIDNameStruct>();
			IClassDesc classRoot = buildChild((Element) classList.item(0),
					data, type, ver, true);
			return new DefaultIM(type, ver, classRoot, data, timeStamp);
		}
	}

	/**
	 * 将xml的Element（名为opt）解析成NodeClassDesc中的opeartion对象， 也就是OperationDesc
	 * 
	 * @param e
	 *            xml的Element
	 * @return 节点的操作描述
	 */
	private OperationDesc parseOparationDesc(Element e) {
		OperationDesc oper = new OperationDesc();

		// 将xml的opt元素的group, id, name等属性解析，
		// 需要注意解析时发生错误的处理
		int group = -1;
		int id = -1;
		try {
			group = Integer.parseInt(e.getAttribute(IOperationDesc.GROUP)
					.trim());
			id = Integer.parseInt(e.getAttribute(IOperationDesc.ID).trim());
		} catch (NumberFormatException ex) {
			log.error("error while parsing operation goup or id");
		}

		String name = e.getAttribute(IOperationDesc.NAME);
		oper.setOperGroup(group);
		oper.setOperID(id);
		oper.setOperName(name);

		NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				e = (Element) list.item(i);
				if (!e.getNodeName().equals(IOperationDesc.ENTRY)) {
					continue;
				}

				if (e.getAttribute(IOperationDesc.KEY).equals(
						IOperationDesc.AT_CMD)) {
					// 以16进制表示：
					String strValue = e.getTextContent().trim();
					// Integer.decode(strValue).intValue();
					oper.setAtCmd(strValue);
				} else if (e.getAttribute(IOperationDesc.KEY).equals(
						IOperationDesc.RSP_PARSER)) {
					String strValue = e.getTextContent().trim();
					oper.setRspParserClassName(strValue);
				} else if (e.getAttribute(IOperationDesc.KEY).equals(
						IOperationDesc.LEVEL)) {
					int level = Integer.parseInt(e.getTextContent());
					// 增加操作level
					oper.setOperLevel(level);
				}
			}
		}

		// 得到这个operationDesc后，
		// 将它添加到DefaultVM的OprationDesc的集合中
		return oper;
	}

	/**
	 * 递归方法，为一个类描述添加所有的子描述，包括子类
	 * 
	 * @param parent
	 *            父节点
	 * @param data
	 *            数据列表
	 * @param type
	 *            网元类型
	 * @param version
	 *            版本号
	 * 
	 * @param isRoot
	 *            true: 是根节点网元描述 false：不是根几点网元对象描述
	 * 
	 * @return 类描述
	 */
	private ClassDesc buildChild(Element parent, ArrayList<IIDNameStruct> data,
			String type, String version, boolean isRoot) {
		// cid
		String classID = parent.getAttribute(COMMON_ID_TAG);
		String className = parent.getAttribute(COMMON_NAME_TAG).trim();
		ClassDesc parentClass = null;

		if (isRoot) {
			parentClass = new RootClassDesc(classID, className);
			((RootClassDesc) parentClass).setType(type);
			((RootClassDesc) parentClass).setVersion(version);
		} else {
			parentClass = new ClassDesc(classID, className);
		}

		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				String tagName = ((Element) child).getTagName().trim();

				// 子类
				if (tagName.equals(CLASS_TAG)) {
					ClassDesc childClass = buildChild((Element) child, data,
							type, version, false);
					parentClass.addChild(childClass);
					childClass.setParent(parentClass);
				} else if (tagName.equals(CLASS_ELM_TAG)) {
					// 类的基本属性

					String attrName = ((Element) child).getAttribute(
							COMMON_NAME_TAG).trim();
					String attrValue = child.getTextContent().trim();

					// 类的别名
					if (attrName.equals(CLASS_ALIAS_TAG)) {
						parentClass.setAliasName(attrValue);
						// 类的告警解析器名
					} else if (attrName.equals(CLASS_ALMPARSER_TAG)) {
						parentClass.setAlarmParserName(attrValue);
						// 主动发起请求得到响应的解析类
					} else if (attrName.equals(CLASS_RSPPARSER_TAG)) {
						parentClass.setResponseParserClassName(attrValue);
					} else if (attrName.equals(CLASS_VECTOR_TAG)) {
						parentClass.setVector(Boolean.parseBoolean(attrValue));
					} else if (attrName.equals(CLASS_MAXINDEX_TAG)) {
						parentClass.setMaxIndex(Integer.parseInt(attrValue));
					}

				} else if (tagName.equals(ATTR_TAG)) {
					// 类的属性

					String attrID = ((Element) child).getAttribute(
							COMMON_ID_TAG).trim();
					AttributeDesc attrDesc = null;

					// 判断是否枚举
					if (((Element) child).getElementsByTagName(ENUM_TAG)
							.getLength() > 0) {
						attrDesc = new EnumAttributeDesc();
					} else {
						attrDesc = new AttributeDesc();
					}

					attrDesc.setAttrID(attrID);
					// 设置属性值
					NodeList attrItems = ((Element) child).getChildNodes();
					for (int j = 0; j < attrItems.getLength(); j++) {
						Node attrItem = attrItems.item(j);
						if (attrItem instanceof Element) {
							String name = ((Element) attrItem).getTagName();

							// 属性的通用内容
							if (name.equals(ATTR_ELM_TAG)) {
								String attrName = ((Element) attrItem)
										.getAttribute(COMMON_NAME_TAG).trim();
								String attrValue = attrItem.getTextContent()
										.trim();

								// 属性名
								if (attrName.equals(ATTR_NAME_TAG)) {
									attrDesc.setName(attrValue);
									// 属性值
								} else if (attrName.equals(ATTR_ALIAS_TAG)) {
									attrDesc.setAliasName(attrValue);
									// 属性的数据类型
								} else if (attrName.equals(ATTR_DATA_TAG)) {
									attrDesc.setData(attrValue);
									// 属性的控制类型
								} else if (attrName.equals(ATTR_CTRL_TAG)) {
									attrDesc.setControl(attrValue);
									// 属性的长度
								} else if (attrName.equals(ATTR_Length_TAG)) {
									attrDesc.setLength(attrValue);
									// 属性的默认值
								} else if (attrName.equals(ATTR_DEF_TAG)) {
									attrDesc.setDefault(attrValue);
									// 属性的最大值
								} else if (attrName.equals(ATTR_MAX_TAG)) {
									attrDesc.setMax(attrValue);
									// 属性的最小值
								} else if (attrName.equals(ATTR_MIN_TAG)) {
									attrDesc.setMin(attrValue);
									// 属性掩码
								} else if (attrName.equals(ATTR_MASK_TAG)) {
									attrDesc.setMask(attrValue);
									// 属性设置请求对应的AT指令
								} else if (attrName.equals(ATTR_ATCMD_TAG)) {
									attrDesc.setAtCmd(attrValue);
									// 属性设置应答解析类
								} else if (attrName.equals(ATTR_RSPPARSER_TAG)) {
									attrDesc.setRspParser(attrValue);
								}
							} else if (name.equals(ENUM_TAG)) {
								// 枚举属性的内容
								String gID = ((Element) attrItem)
										.getAttribute(ENUM_GID_TAG);
								String gName = ((Element) attrItem)
										.getAttribute(ENUM_GNM_TAG);

								((EnumAttributeDesc) attrDesc).addGroup(gID,
										gName);

								NodeList enumAttrs = ((Element) attrItem)
										.getChildNodes();
								for (int k = 0; k < enumAttrs.getLength(); k++) {
									Node enumItem = enumAttrs.item(k);

									if ((enumItem instanceof Element)
											&& (((Element) enumItem)
													.getTagName()
													.equals(ENUM_ETY_TAG))) {
										// 名
										String name2 = ((Element) enumItem)
												.getAttribute(ENUM_NAME_TAG)
												.trim();
										// 值
										String value = ((Element) enumItem)
												.getAttribute(ENUM_VLE_TAG)
												.trim();
										// 界面显示
										String enumName = enumItem
												.getTextContent();
										((EnumAttributeDesc) attrDesc)
												.addEntry(gID, gName, name2,
														value, enumName);
									}
								}
							}
						}
					}

					parentClass.addAttr(attrDesc);
				} else if (tagName.equals(OPT_TAG)) {
					// 操作
					OperationDesc opt = this
							.parseOparationDesc(((Element) child));
					opt.setParentClass(parentClass);
					parentClass.addOperationDescs(opt);
				}
			}
		}

		// 放入数据源
		data.add(parentClass);
		return parentClass;
	}

}
