<%@ page import="org.springframework.security.web.authentication.AbstractProcessingFilter" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%-- 把 content 属性关联到 HTTP 头部。(response  响应报文,request 头信息无法修改)  content-type expires refresh set-cookie 
    <meta http-equiv="expires" content="11111"/>
	<meta http-equiv="Refresh" content="50 ; url=http://www.163.com" /> --%>
	<link rel="shortcut icon" href="assets/img/oem/${oem.vendor.imgPath}/favicon.ico" type="image/vnd.microsoft.icon"> 
	<link rel="icon" href="assets/img/oem/${oem.vendor.imgPath}/favicon.ico" type="image/vnd.microsoft.icon">
	<title>G+ web开发框架 - 登录</title>
    <%@include file="/common/header.jsp" %>
    <script type="text/javascript">
		$(document).mouseup(function(e){
		  var _con = $(".popover-content");   // 设置目标区域
		  if(!_con.is(e.target) && _con.has(e.target).length === 0){
		    $("input").popover("destroy");
		  }
		});
		
        $(function () {	//根据异常，弹出异常弹框
        	$("[data-toggle='popover']").popover();	//启用bootstrap弹出框
            <%
                if (session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null) {
            %>

            var errMsg = "${SPRING_SECURITY_LAST_EXCEPTION.message}";
            var emptyErr = "${not empty param.login_error}";
            if (errMsg != "" && emptyErr == "true") {           
                $(".e_validation").show();
                if(errMsg.includes("用户")){
	                //$("#j_username").parent().addClass("has-error");     
           		 	document.getElementById("j_username").focus(); 
                	$("#j_username").attr("data-content",errMsg);
                	$("#j_username").popover('show');
                }else if(errMsg.includes("密码")){
                	$("#j_password").attr("data-content",errMsg);
                 	$("#j_password").popover('show');     
            	}else if(errMsg.includes("验证码")){  
                 	$("#j_validation_code").attr("data-content",errMsg);
                 	$("#j_validation_code").popover('show');    
            	}else{	//temp test
                	$("#j_username").attr("data-content",errMsg);
                	$("#j_username").popover('show');
            	}
            }
            <%
                }
            %>
        });

        function reloadImage() {
            document.getElementById("pic").src = "${ctx}/common/imageCode.jsp?p=" + Math.random();
        }
		
        function submitForm(form) {
            //判断验证码是否隐藏
            $(".help-block").css("visibility","hidden");
            $(".has-error").removeClass("has-error");
            var temp=$(".e_validation").is(":hidden");                
            var loginName = document.getElementById("j_username").value;
            var password = document.getElementById("j_password").value;
            var validation_code = document.getElementById("j_validation_code").value;
            if (loginName == null || loginName == "" || loginName == "请输入用户名") {
           		 document.getElementById("j_username").focus(); 
                 //$("#j_username").parent().addClass("has-error");      
                 $("#j_username").attr("data-content","请输入用户名");
                 $("#j_username").popover('show');
                return false;
            }

            if (password == null || password == "" || password == "请输入登录密码") {
           		 document.getElementById("j_password").focus(); 
                 //$("#j_password").parent().addClass("has-error");      
                 $("#j_password").attr("data-content","请输入登录密码");
                 $("#j_password").popover('show');    
                return false;
            }
         if(!temp){   
            if (validation_code == null || validation_code == "") {
           		 document.getElementById("j_validation_code").focus(); 
                //$("#j_validation_code").parent().addClass("has-error");      
                 $("#j_validation_code").attr("data-content","请输入您的验证码");
                 $("#j_validation_code").popover('show');    
                return false;
            }
          }
            return true;
        }
        function reset(){
        	document.getElementById("j_username").value="";
        	document.getElementById("j_password").value="";
        	document.getElementById("j_validation_code").value="";       	
        }
        //鼠标点击文本框提示信息消失
        function notice(){
        $("#msgDiv").empty();
        $("#msgDiv").hide();
        }
        
        
        
    </script>

    <link type="text/css" rel="stylesheet" href="${ctx}/html/css/login.css" media="screen"/>
</head>

<body class="userlogin-body body-content">

    <div class="middle-box text-center loginscreen  animated fadeInDown">
        <div>
            <div>

                <h1 class="logo-name">G+</h1>

            </div>
            <h3>欢迎使用 G+</h3>

            <form class="m-t" role="form"  action="j_spring_security_check" 
            method="post" onsubmit="return submitForm(this)">
                <div class="form-group">
                    <input name="j_username" id="j_username" type="text" class="form-control" 
                    placeholder="用户名" data-toggle="popover"  data-html="true"  data-placement="bottom">
                </div>
                
                <div class="form-group">
                    <input  name="j_password" id="j_password" type="password" class="form-control" 
                    placeholder="密码" data-toggle="popover"   data-html="true" data-placement="bottom">
                </div>
                
               <div style="display: none;" class="form-group e_validation">
               		<div class="input-group m-b">
	                    <input  name="j_validation_code" id="j_validation_code" type="text" class="form-control" 
	                    placeholder="请输入验证码" data-toggle="popover"   data-html="true" data-placement="bottom">
		                <span class="input-group-addon span-pic">
		                   <img id="pic" src="<c:url value="common/imageCode.jsp"/>" onclick="reloadImage();" height="32"
		                        title="点击更换图片" style="vertical-align: middle;cursor:pointer"/>
		                </span>
		            </div>
                </div>
                <button type="submit" class="btn btn-primary block full-width m-b">登 录</button>

                <p class="text-muted text-center"> <a href="#"><small>忘记密码了？</small></a> | <a href="register.jsp">注册一个新账号</a>
                </p>

            </form>
        </div>
    </div>



</body>
</html>