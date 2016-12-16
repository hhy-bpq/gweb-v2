package com.gbcom.system.controller;

import com.gbcom.common.hibernate.GridJq;
import com.gbcom.common.hibernate.QueryTranslateJq;
import com.gbcom.system.aop.UserLog;
import com.gbcom.system.daoservice.TimePlanService;
import com.gbcom.system.domain.TimePlan;
import com.hc.core.controller.BaseCRUDActionController;
import com.hc.core.orm.hibernate.Page;
import com.hc.core.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: fengjing
 * Date: 14-7-10
 * Time: 下午6:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TimePlanController extends BaseCRUDActionController<TimePlan> {

    @Autowired
    private TimePlanService timePlanService;

    /**
     * 列表显示页面
     *
     * @param model .
     * @return .
     */
    @RequestMapping
    @UserLog
    public String grid(Model model) {
        //判断是否有编辑权限
        //model.addAttribute("canEdit", true);

        return "view/system/timePlan/grid";
    }

    /**
     * 获取列表数据
     *
     * @param response .
     * @param filters  .
     * @param columns  .
     * @param page     .
     * @param rows     .
     * @param ownerType Integer
     */
    @RequestMapping
    public void gridDataCustom(HttpServletResponse response, String filters, String columns, int page, int rows, Integer ownerType) {
        try {
            Page<TimePlan> pageModel = new Page<TimePlan>(page, rows, true);
            String hql = "from TimePlan ";
            //增加自定义查询条件
            if(ownerType != null){
                hql+="where ownerType = "+ownerType;
            }
            hql += " order by id desc";

            //执行查询
            QueryTranslateJq queryTranslate = new QueryTranslateJq(hql, filters);
            pageModel = timePlanService.findByPage(pageModel, queryTranslate.toString());

            //输出显示
            String json = GridJq.toJSON(columns, pageModel);
            sendJSON(response, json);

        } catch (Exception e) {
            super.processException(response, e);
        }
    }

    /**
     * 新增录入页面
     *
     * @param model .
     * @param ownerType Integer
     * @return .
     */
    @RequestMapping
    @UserLog
    public String add(Model model, Integer ownerType) {
        TimePlan timePlan = new TimePlan();

        timePlan.setOwnerType(ownerType);

        //如需增加其他默认值请在此添加
        model.addAttribute("bean", timePlan);

        return "view/system/timePlan/input";
    }


    /**
     * 修改显示页面
     *
     * @param id    .
     * @param model .
     * @return .
     */
    @RequestMapping
    @UserLog
    public String modify(Model model, Long id) {
        TimePlan timePlan = timePlanService.get(id);

        //处理其他业务逻辑
        model.addAttribute("bean", timePlan);

        return "view/system/timePlan/input";
    }

    /**
     * 查看页面
     *
     * @param id    .
     * @param model .
     * @return .
     */
    @RequestMapping
    @UserLog
    public String view(Model model, Long id) {
        TimePlan timePlan = timePlanService.get(id);

        model.addAttribute("bean", timePlan);
        return "view/system/timePlan/view";
    }

    /**
     * 删除操作
     *
     * @param response .
     * @param id       .
     * @throws Exception .
     */
    @RequestMapping
    @UserLog
    public void delete(HttpServletResponse response, Long id) throws Exception {
        timePlanService.delete(id);

        sendSuccessJSON(response, "删除成功");
    }

    /**
     * 保存操作
     *
     * @param response .
     * @param entity   .
     * @param request  .
     * @throws Exception .
     */
    @RequestMapping
    public void save(HttpServletResponse response, @ModelAttribute("bean") TimePlan entity, HttpServletRequest request) throws Exception {
        try {
            TimePlan target;
            if (entity.getId() != null) {
                target = timePlanService.get(entity.getId());
                ReflectionUtils.copyBean(entity, target, new String[]{
                        "id", "ownerType", "type", "beginTime", "repeatTime", "selectWeek", "selectDay", "intervalTime", "state"
                });

            } else {
                target = entity;
            }
            timePlanService.save(target);

            sendSuccessJSON(response, "保存成功");
        } catch (Exception e) {
            super.processException(response, e);
        }
    }

}
