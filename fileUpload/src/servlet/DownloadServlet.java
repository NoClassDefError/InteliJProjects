package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "DownloadServlet",urlPatterns = "/download")
public class DownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String s= request.getParameter("filename");
        System.out.println("DownloadServlet info: filename="+s);
        File file = new File(s);
        response.setHeader("Content-Disposition","attachment;filename="+file.getName());
        response.setContentType(this.getServletContext().getMimeType(file.getName()));
        InputStream in = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) response.getOutputStream().write(buffer, 0, len);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
