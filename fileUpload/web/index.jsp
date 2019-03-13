<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页</title>
    <style type="text/css">
        div {
            overflow: scroll;
        }
    </style>
</head>
<body>
<h1>小创云网盘</h1>
<h3>版本 1.1 beta</h3>
<h4>上传文件</h4>
<form action="upload" name="upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file1"/>
    <input type="submit"/>
</form>
<h4>网盘内容</h4>
<form action="list" name="download" method="post">
    <input type="submit" value="查看">
</form>
<div>
    <h4>注意：</h4>
    1.由于数据表与持久层设计过于繁琐，暂不实现账户管理与文件地址管理功能，本网盘所有文件均可公开访问<br>
    2.网盘里的资源皆为学习交流使用，不可用于商业用途，侵删<br>
    3.作者电气1701葛翰臣，制作不易，欢迎大家为云盘界面上与交互上的设计提出各种意见，也非常希望大家能在技术上提供帮助<p>
    <h4>1.0版本功能介绍:</h4>
    1.在线查看MP4视频，图片，与pdf文档<br>
    2.视频边下边播<br>
    3.内置js播放器，支持进度条查看与拖动<p>
    <h4>1.1版本新功能介绍：</h4>
    1.界面美化，表格呈现<br>
    2.支持不带密码的rar与带密码的zip压缩包在线解压<br>
    3.在线将文件夹压缩为zip文件<br>
    4.支持子目录创建与查看<br>
    5.通过tomcat的虚拟路径配置，服务器端存储目录可不在项目根目录下，随意移动<br>
    6.文件夹的删除<br>
    7.文件的重命名<br>
    8.支持文件复制与移动<br>
    <h4>1.2版本功能预告</h4>
    1.html文档的在线查看<br>
    2.txt,md,java,jsp,js,sql,m,c,cpp,css,py等一切文本文档的查看<br>
    3.在线编辑文本文档<br>
    4.文件上传速度优化与进度显示
</div>
</body>
</html>
