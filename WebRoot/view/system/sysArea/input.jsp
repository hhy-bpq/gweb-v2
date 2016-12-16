<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var formId = "bean";
    $(function () {
        //页面验证初始化
       var validateCondition = [
            //{name: "code", rule: "validate[required,maxSize[40]]"},
            {name: "areaName", rule: "validate[required,maxSize[50]]"},
            {name: "description", rule: "validate[maxSize[100]]"}
        ];
        validateInit(validateCondition, formId);


        //更改按钮
        var btns="<button class=\"btn\" data-dismiss=\"modal\" aria-hidden=\"true\" id=\"cancelBtn\" ><i class='glyphicon glyphicon-remove'></i>取消</button>" +
                "<button type=\"button\" class=\"btn btn-primary\" id='confirmBtn' onclick=\"save(this)\" ><i class='glyphicon glyphicon-ok'></i>确定</button>";

        $("#"+openWindowId+" .modal-footer").html(btns);

    });

    //保存操作
    function save(btn) {
        if (!validateForm(formId)) {
            return;
        }
        disableBtn(btn);
        //提交表单
        saveAjaxData("${ctx}/sysArea/save.do?parentId=${parentId}", formId);
    }
</script>
<form:form commandName="bean" cssClass="form-horizontal">
	<form:hidden path="id" />
    <input style="display: none"/>
	<div class="form-group-hz">
		<label>
			名称：
		</label>
		<form:input path="areaName" cssClass="input_text"/>
	</div>
	<c:if test="${isAdd}">
		<div class="form-group-hz">
			<label> 是否叶子节点： </label>
			<form:checkbox path="isLeaf" value="1" />
		</div>
	</c:if>
	<div class="form-group-hz">
		<label> 备注： </label>
		<form:textarea path="description" cssClass="input_textarea" />
	</div>

    
</form:form>