package servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(description = "负责图片与pdf的在线查看",name = "ImageServlet", urlPatterns = "/image")
public class ImageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File file = new File(request.getParameter("filename"));
        //注意，File类没有获取文件后缀名的方法
        String type = this.getServletContext().getMimeType(file.getName());
        response.setContentType(type);
        if(type.contains("video")){
            System.out.println(file.getAbsolutePath());
//            request.setAttribute("filePath",file.getAbsolutePath());
//            request.getRequestDispatcher("/video.jsp").forward(request,response);
            response.sendRedirect("video.jsp?filePath="+ URLEncoder.encode(file.getAbsolutePath()));
        }else
        response.setHeader("Content-Disposition","inline;filename="+file.getName());
//        System.out.println(response.getHeader("Content-Type"));
        //将一个流复制到另一个流可以使用commons-io的工具
        FileInputStream fileInputStream = new FileInputStream(file);
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(fileInputStream,outputStream);
        fileInputStream.close();
//        byte[] buffer = new byte[1024];
//        int len;
//        while ((len = fileInputStream.read(buffer)) > 0) response.getOutputStream().write(buffer, 0, len);
        //要实现切换下一张的功能，如何在网页中添加按钮？使用一个jsp文件进行动态包含
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
