<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>portal</title>
    <%--<style type="text/css">
        /* <![CDATA[ */
        /*css 内容*/
        /* ]]> */
    </style>

    <script type="text/javascript">
        // <![CDATA[
        // JavaScrip 内容
        // ]]>
    </script>--%>

</head>
<body>
<ul class="breadcrumb">
    <li class="active">Excel处理</li>
    <li class="active">导入源数据</li>
</ul>

<form id="app_form" action="${ctx}/excel/upload" class="form-horizontal" method="post"
      enctype="multipart/form-data">
    <div class="form-group">
        <label class="col-lg-3 col-md-3 control-label" for="dateFrom">
            <span style="color: red">*</span>记账日期：
        </label>

        <div class="col-lg-6 col-md-6" style="vertical-align: middle; padding: 4px 8px; ">
            <div id="duedate" class="input-group date" style="width: 47%; float: left">
                <input class="form-control input-sm" id="dateFrom" name="dateFrom" type="text"/>
                <span class="input-group-addon input-sm btn">
                    <i class="glyphicon glyphicon-calendar"></i>
                </span>
            </div>
        </div>


    </div>

    <div class="form-group">
        <label for="dtp_input2" class="col-lg-3 col-md-3 control-label">
            <span style="color: red">*</span>记账日期：
        </label>
        <div class="input-group date form_date col-lg-6 col-md-6" style="vertical-align: middle; padding: 4px 8px; "
             data-date="" data-date-format="dd MM yyyy" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd">
            <input class="form-control" style="width: 47%; float: left" size="16" type="text" value="" readonly />
            <span class="input-group-addon input-sm btn"><span class="glyphicon glyphicon-remove"></span></span>
            <span class="input-group-addon input-sm btn"><span class="glyphicon glyphicon-calendar"></span></span>
        </div>
        <input type="hidden" id="dtp_input2" value="" /><br/>
    </div>

    <div class="form-group">
        <label class="col-lg-3 col-md-3 control-label" for="file-input">
            <span style="color: red">*</span>源文件：</label>

        <div class="col-lg-8 col-md-8">
            <input id="file-input" type="file" class="file" multiple=true data-preview-file-type="any">
            <%--<input id="file-input" name="file" type="file" class="file" multiple=true data-show-preview="false">--%>
        </div>
    </div>
    <%--<button class="btn btn-default kv-fileinput-upload" type="submit" style="display: none;"/>--%>

    <script type="text/javascript">
        $(function () {
            $('.form_date').datetimepicker({
                language:  'fr',
                weekStart: 1,
                todayBtn:  1,
                autoclose: 1,
                todayHighlight: 1,
                startView: 2,
                minView: 2,
                forceParse: 0
            });
        });
    </script>
</form>
</body>
</html>
