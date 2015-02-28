<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>portal</title>
</head>
<body>
<ul class="breadcrumb">
    <li class="active">Excel处理</li>
    <li class="active">导入源数据</li>
</ul>

<form id="form" action="${ctx}/excel/upload" class="form-horizontal" method="post"
      <%--enctype="multipart/form-data"--%>>
    <div class="form-group">
        <label class="col-lg-3 col-md-3 control-label" for="recordDay">
            <span style="color: red">*</span>记账日期：
        </label>

        <div class="col-lg-5 col-md-5">
            <div id="record" class="input-group date form_date">
                <input id="recordDay" class="form-control input-sm" name="recordDay" type="text"/>
                <span class="input-group-addon input-sm btn">
                    <i class="glyphicon glyphicon-calendar"></i>
                </span>
            </div>
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-3 col-md-3 control-label">催收时间：</label>

        <div class="col-lg-2 col-md-2">
            <div id="minDay" class="input-group date form_date" style="width:200px;">
                <input id="min" class="form-control input-sm" name="min" type="text"/>
                <span class="input-group-addon input-sm btn">
                    <i class="glyphicon glyphicon-calendar"></i>
                </span>
            </div>
        </div>

        <div class="col-lg-1 col-md-1" style="width:10px;margin-top：:30px;margin-left:30px;">—</div>

        <div class="col-lg-2 col-md-2">
            <div id="maxDay" class="input-group date form_date" style="width:200px;margin-left:20px">
                <input id="max" class="form-control input-sm" name="max" type="text"/>
                <span class="input-group-addon input-sm btn">
                    <i class="glyphicon glyphicon-calendar"></i>
                </span>
            </div>
        </div>
    </div>

    <div class="form-group">
        <label class="col-lg-3 col-md-3 control-label" for="file_input">
            <span style="color: red">*</span>源文件：</label>

        <div class="col-lg-5 col-md-5">
            <%--text--%>
            <%--<input id="file_input" name="file" type="file" multiple="multiple">--%>
                <input id="file_input" name="file" type="text" style="width: 100%;">
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-offset-2 col-md-offset-2 col-lg-2 col-md-2 control-label">
            <input type="checkbox" name="combineToExcel" checked> 合并到Excel
        </label>
        <label class="col-lg-2 col-md-2 control-label">
            <input type="checkbox" name="processResult" checked> 输出最终结果
        </label>
    </div>
    <div class="col-lg-offset-5  col-md-offset-5 col-lg-3 col-md-3">
        <button id="submit" type="button" class="btn btn-primary">提交</button>
        <button type="reset" class="btn btn-info">重置</button>
    </div>
</form>
<script type="text/javascript">
    $(function () {
        /*IE9不支持文件多选*/
        /*$("#file_input").fileinput({
            overwriteInitial: true,
            allowedPreviewTypes: ['text'],
            showUpload: false,
            showPreview: false,
            removeLabel: "清空",
            removeClass: "btn btn-danger",
            browseLabel: "选择文件",
            browseClass: "btn btn-primary"
        });*/

        $('.form_date').datetimepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            minView: 2,
            forceParse: 0
        });

        function download(id) {
            var url = "${ctx}/excel/download";
            var form = document.createElement("form");
            var frag = document.createDocumentFragment();
            frag.appendChild(form);
            form.method = 'post';
            form.action = url;
            form.target = '_blank';
            // 创建隐藏表单
            var element = document.createElement("input");
            element.setAttribute("type", "hidden");
            element.setAttribute("name", "id");
            element.setAttribute("value", id);
            form.appendChild(element);
            document.body.appendChild(frag);
            form.submit();
        }

        $("#minDay").datetimepicker().on('changeDate', function (ev) {
            $('#maxDay').datetimepicker('setStartDate', ev.date);
        });

        $("#maxDay").datetimepicker().on('changeDate', function (ev) {
            $('#minDay').datetimepicker('setEndDate', ev.date);
        });

        $("#submit").click(function () {
            if ($("#recordDay").val().length == 0) {
                bs_error("请选择记账日期");
                return;
            }
            if ($("#file_input").val().length == 0) {
                bs_error("请选择要上传的文件");
                return;
            }
            bs_info('开始上传文件等待处理!');
            $("#submit").attr("disabled", true);
            $("#form").ajaxSubmit({
                url: "${ctx}/excel/upload?timestamp=" + new Date().getTime(),
                success: function (event, status, xhr) {
                    console.info(event);
                    // IE9特殊处理
                    if (event.indexOf("<pre>") > -1) {
                        event = event.substring(5, event.length - 6);

                    }
                    console.info(event);
                    // 为了兼容IE9 返回值类型为 text/plain;charset=UTF-8
                    event = JSON.parse(event);
                    console.info(event);
                    if (event.success == true) {
                        download(event.id);
                        $("#submit").attr("disabled", false);
                    } else {
                        bs_error(event.msg);
                        $("#submit").attr("disabled", false);
                    }
                },
                error: function (event) {
                    bs_error("处理失败");
                    $("#submit").attr("disabled", false);
                }
            });
        });
    });

</script>

</body>
</html>
