<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-CN">
<head>
    <title>Excel处理 - <sitemesh:write property='title'/></title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>

    <%--IE兼容模式 通知IE采用其所支持的最新的模式--%>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <%--国产浏览器默认采用高速模式webkit内核--%>
    <meta name="renderer" content="webkit">

    <link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/images/favicon.ico"/>

    <link type="text/css" rel="stylesheet" href="${ctx}/static/bootstrap/bootstrap-3.2.0/css/bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="${ctx}/static/bootstrap/bootstrap-datetimepicker-2.3.1/css/bootstrap-datetimepicker.min.css"/>
    <link type="text/css" rel="stylesheet" href="${ctx}/static/jquery/jquery-validation-1.13.0/validate.css"/>


    <script type="text/javascript" src="${ctx}/static/jquery/jquery-1.11.1/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/bootstrap/bootstrap-3.2.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/bootstrap/bootstrap-datetimepicker-2.3.1/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/bootstrap/bootstrap-datetimepicker-2.3.1/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>


    <!--[if lt IE 9]>
    <script type="text/javascript" src="${ctx}/static/html5css3/html5shiv.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/html5css3/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript">
        window.publicPath = "${ctx}";

        //跳转到想要去的页面
        function goTo(href, flag) {
            var f = 0;
            if (flag)
                f = flag;
            if (f == 0)
                window.location.href = href;
            else
                window.open(href);
        }

        function logout() {
            $.ajax()
        }
    </script>

    <sitemesh:write property='head'/>
</head>
<body>
<div class="container">
    <nav class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button aria-controls="navbar" aria-expanded="false" data-target="#navbar" data-toggle="collapse"
                        class="navbar-toggle collapsed" type="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="#" class="navbar-brand">首页</a>
            </div>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        <span class="glyphicon glyphicon-user"></span>
                        <span class="caret"></span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>
    <sitemesh:write property='body'/>
    <footer class="footer" style="text-align: center;clear:both;">
        Copyright © 1986-2014. Shao guoqing. All rights reserved
    </footer>
</div>
</body>
</html>