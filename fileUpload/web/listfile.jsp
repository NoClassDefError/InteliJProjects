<jsp:useBean id="path" scope="request" type="java.lang.String"/>
<jsp:useBean id="fileMap" scope="request" type="java.util.Map"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>网盘</title>
</head>
<body>
<h2>网盘内容 </h2>
<h3>当前路径： ${path} </h3>
<form method="post" action="delete">
    <input type="hidden" value="${path}" name="filename"/>
    <input type="submit" value="新建文件夹"/>
</form>
<!-- 遍历Map集合 -->
<table border="1">
    <c:forEach var="me" items="${fileMap}">
        <tr>
            <c:set var="tpath" value="${path}\\${me.key}"/>
            <c:url value="download" var="downurl">
                <c:param name="filename" value="${path}\\${me.key}"/>
            </c:url>
            <c:url value="image" var="lookurl">
                <%--<c:param name="namelist" value="${fileMap}"/>--%>
                <c:param name="filename" value="${path}\\${me.key}"/>
            </c:url>
            <c:url value="delete" var="deleteurl">
                <c:param name="filename" value="${path}\\${me.key}"/>
            </c:url>
            <c:url value="list" var="listurl">
                <c:param name="filepath" value="${path}\\${me.key}"/> <%--  注意jstl中字符串拼接的方法    --%>
            </c:url>
                <%--此处使用url标签为超链接添加get请求--%>
            <td>
                    ${me.key}
            </td>
            <td>

            </td>
            <td>
                <a href="${downurl}">下载</a>
            </td>
            <c:if test="${fn:contains(me.value,'video')||fn:contains(me.value, 'image')||fn:contains(me.value, 'application')}">
                <td>
                    <a href="${lookurl}">预览</a>
                </td>
            </c:if>
            <td>
                <a href="${deleteurl}">删除</a>
            </td>

            <c:if test="${me.value eq 'file'}">
                <td>
                    <a href="${listurl}">打开目录</a>
                </td>
                <%--使用jstl 构造表单post传值的方法以避免在get请求中出现中文，如下get的方法在ListFileServlet中能得到参数，
                但无法使用路径地址找到文件--%>
                <%--<form action="list" method="post" enctype="text/plain">--%>

                <%--<input type="hidden" value="${tpath}" name="filepath"/>--%>
                <%--<input type="submit" value="打开目录"/>--%>
                <%--</form>--%>
                <%--验证发现post请求出现同样的找不到文件的情况--%>
                <td>
                    <form action="unzip" method="get" style="margin:0;display:inline;">
                        <label>
                            <input value="设置密码" type="text" name="password"/>
                            <input type="hidden" value="zip" name="request"/>
                            <input type="hidden" value="${path}" name="path"/>
                            <input value="${tpath}" type="hidden" name="filepath"/>
                            <input type="submit" value="压缩为zip到当前文件夹"/>
                        </label>
                    </form>
                </td>
            </c:if>

            <c:if test="${fn:contains(me.value,'zip')}">
                <td>
                    <form action="unzip" method="get" style="margin:0;display:inline;">
                        <label>
                            <input value="解压密码" type="text" name="password"/>
                            <input type="hidden" value="unzip" name="request"/>
                            <input type="hidden" value="${path}" name="path"/>
                            <input value="${tpath}" type="hidden" name="filepath"/>
                            <input type="submit" value="解压到当前文件夹"/>
                        </label>
                    </form>
                </td>
            </c:if>
            <c:if test="${fn:contains(me.value, 'rar')}">
                <td>
                    <form action="unzip" method="get" style="margin:0;display:inline;">
                        <label>
                            <input type="hidden" value="unrar" name="request"/>
                            <input type="hidden" value="${path}" name="path"/>
                            <input value="${tpath}" type="hidden" name="filepath"/>
                            <input type="submit" value="解压到当前文件夹"/>
                        </label>
                    </form>
                </td>
            </c:if>
            <td>
                <form action="rename" method="get" style="margin:0;display:inline;">
                    <label>
                        <input type="text" value="注意输入后缀" name="newname"/>
                        <input value="${tpath}" type="hidden" name="filepath"/>
                        <input type="submit" value="重命名"/>
                    </label>
                </form>
            </td>
            <td>
                <form action="move" method="get" style="display: inline;margin: 0">
                    <label>
                        <input type="text" value="粘贴地址到这里" name="location"/>
                        <input type="hidden" value="${path}" name="path"/>
                        <input value="${tpath}" type="hidden" name="filepath"/>
                        <input type="submit" value="移动"/>
                    </label>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
