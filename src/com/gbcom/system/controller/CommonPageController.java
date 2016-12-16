package com.gbcom.system.controller;

import com.gbcom.system.manager.SysSequenceManager;
import com.gbcom.system.utils.Constants;
import com.gbcom.system.utils.ExcelUtil;
import com.gbcom.system.utils.FileUtils;
import com.gbcom.system.utils.ZipFileUtil;
import com.hc.core.controller.BaseCRUDActionController;
import com.hc.core.entity.Treeable;
import com.hc.core.service.HibernateService;
import com.hc.core.service.TreeManager;
import com.hc.core.utils.StringHelper;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

/**
 * author:Intellj
 */
@Controller
public class CommonPageController extends BaseCRUDActionController {

    private Logger logger = LoggerFactory.getLogger(CommonPageController.class);

    @Autowired
    private TreeManager treeManager;

    @Autowired
    private SysSequenceManager sysSequenceManager;

    @Autowired
    private HibernateService hibernateService;

    /**
     * 树节点向上移动
     * @param response HttpServletResponse
     * @param clazz 样式如：id=data|<id>218</id><class>BpmProcessTemplate</class><param></param>
     * @param id Long
     * @param param sql，比如type_id=1
     * @param model Model
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    @RequestMapping
    public void moveUp(HttpServletResponse response, String clazz, Long id, String param, Model model) throws Exception {
        treeManager.moveUpNode((Class<Treeable>) Class.forName(clazz), id, param);
        treeManager.flush();
        sendSuccessJSON(response, "节点上移成功");
    }

    /**
     * 树节点向移动
     * @param response HttpServletResponse
     * @param clazz 样式如：id=data|<id>218</id><class>BpmProcessTemplate</class><param></param>
     * @param id Long
     * @param param sql，比如type_id=1
     * @param model Model
     * @throws Exception .
     */
    @SuppressWarnings("unchecked")
    @RequestMapping
    public void moveDown(HttpServletResponse response, String clazz, Long id, String param, Model model) throws Exception {
        treeManager.moveDownNode((Class<Treeable>) Class.forName(clazz), id, param);
        treeManager.flush();
        sendSuccessJSON(response, "节点下移成功");
    }
    /**
     * 树重新排序
     * @param response 
     * @param clazz .样式如：id=Sample_12
     * @param column String
     * @param model Model
     * @throws Exception 
     */
    @RequestMapping
    //public String treeReorder(@RequestParam Long id,@RequestParam String column,@RequestParam Long parentId,Model model) throws Exception {
    public void treeReorder(HttpServletResponse response, String clazz, String column, Model model) throws Exception {
//        if (column != null && parentId != null) {
//            treeManager.saveReorderTree(Class.forName(className), parentId, column);
//        } else if (column != null) {
//
//            treeManager.saveReorderTree(Class.forName(className), column);
//        } else if (parentId != null) {
//            treeManager.saveReorderTree(Class.forName(className), parentId, null);
//        } else {
//            treeManager.saveReorderTree(Class.forName(className));
//        }

        if (!StringHelper.isEmpty(column)) {
            treeManager.saveReorderTree(Class.forName(clazz), column);
        } else {
            treeManager.saveReorderTree(Class.forName(clazz));
        }
        treeManager.flush();

        sendSuccessJSON(response, "树排序成功");
    }
    
    /**
     * 非树型结构的上移
     * @param response HttpServletResponse
     * @param clazz   xml方式，含有class（类名）,condition（条件）eg.and
     * 				   t.project.id=1 ,orderFieldName（排序的字段）
     * @param id Long
     * @param param String
     * @param orderFieldName String
     * @param model Model
     * @throws Exception .
     */
    @RequestMapping
    public void simpleMoveUp(HttpServletResponse response, String clazz, Long id, String param, String orderFieldName, Model model) throws Exception {
        treeManager.simpleMoveUp(Class.forName(clazz), orderFieldName, param, id);
        treeManager.flush();
        sendSuccessJSON(response, "节点上移成功");
    }
   
    /**
     * 非树型结构的下移
     * @param response HttpServletResponse
     * @param clazz   xml方式，含有class（类名）,condition（条件）eg.and
     *              t.project.id=1 ,orderFieldName（排序的字段）
     * @param id Long
     * @param param String
     * @param orderFieldName String
     * @param model Model
     * @throws Exception 
     */
    @RequestMapping
    public void simpleMoveDown(HttpServletResponse response, String clazz, Long id, String param, String orderFieldName, Model model) throws Exception {
        treeManager.simpleMoveDown(Class.forName(clazz), orderFieldName, param, id);
        treeManager.flush();
        sendSuccessJSON(response, "节点下移成功");
    }


    /**
     * 更新最大记录数
     *
     * @param response HttpServletResponse
     * @param model Model
     */
    @RequestMapping
    public void updateMaxRecord(HttpServletResponse response, Model model) {
        Map<String, Long> sequences = sysSequenceManager.findAllSysSequence();
        Map<String, String> tables = sysSequenceManager.getTables();

        for (String className : tables.keySet()) {
            String tableName = tables.get(className);
            try {
                sysSequenceManager.updateMaxRecord(sequences, className, tableName);
            } catch (Exception e) {
                logger.error("Exception:",e);
            }
        }
        sendSuccessJSON(response, "更新成功");
    }


    /**
     * 列表导出
     * @param model Model
     * @param cols String
     * @param showtitles String
     * @param filename String
     * @param session HttpSession
     * @return String
     */
    @RequestMapping
    public String exportGrid(Model model, String cols, String showtitles, String filename, HttpSession session) {
        String[] col = cols.split(",");
        List<String> colList = new ArrayList<String>(Arrays.asList(col));
        String[] showtitle = showtitles.split(",");
        List<String> showtitleList = new ArrayList<String>(Arrays.asList(showtitle));
        List list = new ArrayList();
        Map<String, String> map;
        for (int i = 0; i < colList.size(); i++) {
            if (!colList.get(i).equals("operation")) {
                map = new HashMap();
                map.put("cols", colList.get(i));
                map.put("titles", showtitleList.get(i));
                list.add(map);
            }
        }
        if (session.getAttribute(Constants.GRID_SQL_KEY) != null) {
            model.addAttribute("button", "on");
        } else {
            model.addAttribute("button", "off");
        }

        model.addAttribute("bean", list);
        model.addAttribute("filename", filename);
        return "common/export";
    }

    /**
     * excel导出
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filename String
     * @param index String
     */
    @RequestMapping
    public void exportGridExcute(HttpServletRequest request, HttpServletResponse response, String filename, String index) {
        filename = "ExportExcel";//指定英文名,使得winrar可以解压
        String properties = request.getParameter("cols");
        String showtitles = request.getParameter("showtitles");
        String[] showtitle = showtitles.split(",");
        String[] indexes = index.split(",");
        ExcelUtil exUtil = new ExcelUtil();
        // 设置导出列名
        exUtil.setProperties(properties);// 设置需要导出的列
        String query = request.getSession().getAttribute(Constants.GRID_SQL_KEY).toString();
        String tablename = query.substring(query.indexOf("from ") + 5, query.indexOf(" ", query.indexOf("from ") + 5));
        //返回总行数
        String countquery;
        int countrow;
        org.hibernate.Session session = hibernateService.openSession();
        Transaction tx = session.beginTransaction();
        if (!tablename.contains("_")) {
            countquery = "select count(*) " + query.substring(query.trim().indexOf("from "));
            countrow = Integer.parseInt(session.createQuery(countquery).uniqueResult().toString());
        } else {
            countquery = "select count(*) " + query.substring(query.trim().toLowerCase().indexOf(" from ") + 1);
            countrow = Integer.parseInt(session.createSQLQuery(countquery).uniqueResult().toString());
        }
        tx.commit();
        session.close();

        session = hibernateService.openSession();
        tx = session.beginTransaction();
        Query quer;
        if (!tablename.contains("_")) {
            quer = session.createQuery(query);
        } else {
            quer = session.createSQLQuery(query);
        }
        List listall = new ArrayList();
        String filePath = "c://" + Calendar.getInstance().getTimeInMillis() + File.separator;
        File delPath = new File(filePath);
        delPath.mkdir();
        int count = 0;
        for (int i = 1; i <= countrow / 5000 + 1; i++) {
            List list = quer.setFirstResult((i - 1) * 5000).setMaxResults(5000).list();
            session.flush();
            listall.addAll(list);
            list = null;
            if (!tablename.contains("_")) {
                exUtil.exportExcelwhenhql(filename + i, showtitle, listall, indexes, filePath);
                listall.removeAll(listall);
            } else {
                exUtil.exportExcelwhensql(filename + i, showtitle, listall, indexes, filePath);
                listall.removeAll(listall);
            }
            session.clear();
            count++;
        }
        tx.commit();
        session.close();
        if (count == 1) {//如果只有一个文件,不打包直接下载
            try {
                FileUtils.download(request, response, filePath + filename + "1.xls", filename + ".xls", true);
            } catch (Exception e) {
                logger.error("Exception:",e);
            }
        } else {
            File temppath = new File("c://temp");
            if (!temppath.exists()) {
                temppath.mkdir();
            }
            String tempfile = "c://temp//" + Calendar.getInstance().getTimeInMillis() + ".rar";
            File targetfile = new File(tempfile);
//        zipUtil.CreateZipFile(filePath, tempfile);//因为某些机器解压为空,改用新方法
            try {
            	ZipFileUtil.zip(filePath, tempfile);
                FileUtils.download(request, response, tempfile, filename + ".rar", true);
            } catch (Exception e) {
            	logger.error("Exception:",e);
            }
            FileUtils.removeDir(targetfile);
        }
        FileUtils.removeDir(delPath);
        //转化hql to sql
//        SessionFactory sessionFactory = hibernateService.getSession().getSessionFactory();
//        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(shql, shql,
//        Collections.EMPTY_MAP, (org.hibernate.engine.SessionFactoryImplementor)sessionFactory);
//        queryTranslator.compile(Collections.EMPTY_MAP, false);
//        String query=queryTranslator.getSQLString();
    }

    /**
     * 跳转页面
     * commonPage/jump.do?url=<page>sqlQuery/init.do</page>
     *
     * @param model Model
     * @param request HttpServletRequest
     * @return String
     */
    @RequestMapping
    public String jump(Model model, HttpServletRequest request) {
        String url = request.getParameter("url");
        String page = StringHelper.getElementValue(url, "page");
        model.addAttribute("page", page);
        model.addAttribute("isHttp", page.startsWith("http://"));
        return "common/jump";
    }

}
