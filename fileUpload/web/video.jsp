<%@ page import="java.io.File" %>
<%@ page import="java.net.URLDecoder" %>
<%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 2019/3/6
  Time: 22:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>视频播放</title>
    <meta charset="utf-8">
    <style type="text/css">
        .videoPlayer {
            border: 1px solid #000;
            width: 600px;
        }

        #video {
            margin-top: 0;
        }

        #videoControls {
            width: 600px;
            margin-top: 0;
        }

        .show {
            opacity: 1;
        }

        .hide {
            opacity: 0;
        }

        #progressWrap {
            background-color: black;
            height: 25px;
            cursor: pointer;
        }

        #playProgress {
            background-color: red;
            width: 0;
            height: 25px;
            border-right: 2px solid blue;
        }

        #showProgress {
            background-color: antiquewhite;
            font-weight: 600;
            font-size: 20px;
            line-height: 25px;
        }
    </style>
</head>
<body>

<%--<jsp:include page='<%="/image"%>'>--%>
<%--<jsp:param name="fileName" value='<%=request.getParameter("filename") %>'/>--%>
<%--</jsp:include>--%><%
    String path = URLDecoder.decode(request.getQueryString().substring(9), "gbk");
    System.out.println(path + "\n" + request.getSession().getServletContext().getRealPath("/"));
    File file = new File(path);
//    FileInputStream fileInputStream = new FileInputStream(file);
//    FileOutputStream outputStream = new FileOutputStream(request.getSession().getServletContext().getRealPath("/"));
//    IOUtils.copy(fileInputStream, outputStream);
    System.out.println(file.getAbsolutePath() + " " + application.getMimeType(file.getName()));
%>
<div class="">
    <h3><%=file.getName()%></h3>
    <div class="videoPlayer" id="videoContainer">
        <video id="video">
            <%--<source src='爱宠大机密.mp4' type='video/mp4'/>--%>
            <source src='<%="/root/"+file.getName() %>' type='<%=application.getMimeType(file.getName()) %>'/>
            您的浏览器不支持 HTML5 video 标签。
        </video>

        <div id="videoControls">
            <div id="progressWrap">
                <div id="playProgress">
                    <span id="showProgress">0</span>
                </div>
            </div>
            <div>
                <button id="playBtn" title="Play"> 播放</button>
                <button id="fullScreenBtn" title="FullScreen Toggle"> 全屏</button>
            </div>
        </div>
    </div>
</div>
<script>
    // 为了不随意的创建全局变量，我们将我们的代码放在一个自己调用自己的匿名函数中，这是一个好的编程习惯
    (function (window, document) {
        // 获取要操作的元素
        var video = document.getElementById("video");
        var videoControls = document.getElementById("videoControls");
        var videoContainer = document.getElementById("videoContainer");
        var controls = document.getElementById("video_controls");
        var playBtn = document.getElementById("playBtn");
        var fullScreenBtn = document.getElementById("fullScreenBtn");
        var progressWrap = document.getElementById("progressWrap");
        var playProgress = document.getElementById("playProgress");
        var fullScreenFlag = false;
        var progressFlag;

        // 创建我们的操作对象，我们的所有操作都在这个对象上。
        var videoPlayer = {
            init: function () {
                var that = this;
                video.removeAttribute("controls");
                bindEvent(video, "loadeddata", videoPlayer.initControls);
                videoPlayer.operateControls();
            },
            initControls: function () {
                videoPlayer.showHideControls();
            },
            showHideControls: function () {
                bindEvent(video, "mouseover", showControls);
                bindEvent(videoControls, "mouseover", showControls);
                bindEvent(video, "mouseout", hideControls);
                bindEvent(videoControls, "mouseout", hideControls);
            },
            operateControls: function () {
                bindEvent(playBtn, "click", play);
                bindEvent(video, "click", play);
                bindEvent(fullScreenBtn, "click", fullScreen);
                bindEvent(progressWrap, "mousedown", videoSeek);
            }
        };

        videoPlayer.init();

        // 原生的JavaScript事件绑定函数
        function bindEvent(ele, eventName, func) {
            if (window.addEventListener) {
                ele.addEventListener(eventName, func);
            } else {
                ele.attachEvent('on' + eventName, func);
            }
        }

        // 显示video的控制面板
        function showControls() {
            videoControls.style.opacity = 1;
        }

        // 隐藏video的控制面板
        function hideControls() {
            // 为了让控制面板一直出现，我把videoControls.style.opacity的值改为1
            videoControls.style.opacity = 1;
        }

        // 控制video的播放
        function play() {
            if (video.paused || video.ended) {
                if (video.ended) {
                    video.currentTime = 0;
                }
                video.play();
                playBtn.innerHTML = "暂停";
                progressFlag = setInterval(getProgress, 60);
            } else {
                video.pause();
                playBtn.innerHTML = "播放";
                clearInterval(progressFlag);
            }
        }

        // 控制video是否全屏，额这一部分没有实现好，以后有空我会接着研究一下
        function fullScreen() {
            if (fullScreenFlag) {
                videoContainer.webkitCancelFullScreen();
            } else {
                videoContainer.webkitRequestFullscreen();
            }
        }

        // video的播放条
        function getProgress() {
            var percent = video.currentTime / video.duration;
            playProgress.style.width = percent * (progressWrap.offsetWidth) - 2 + "px";
            showProgress.innerHTML = (percent * 100).toFixed(1) + "%";
        }

        // 鼠标在播放条上点击时进行捕获并进行处理
        function videoSeek(e) {
            if (video.paused || video.ended) {
                play();
                enhanceVideoSeek(e);
            } else {
                enhanceVideoSeek(e);
            }

        }

        function enhanceVideoSeek(e) {
            clearInterval(progressFlag);
            var length = e.pageX - progressWrap.offsetLeft;
            var percent = length / progressWrap.offsetWidth;
            playProgress.style.width = percent * (progressWrap.offsetWidth) - 2 + "px";
            video.currentTime = percent * video.duration;
            progressFlag = setInterval(getProgress, 60);
        }

    }(this, document))
</script>
</body>
</html>
