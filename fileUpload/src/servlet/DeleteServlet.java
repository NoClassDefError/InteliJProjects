package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "DeleteServlet", urlPatterns = "/delete")
public class DeleteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String s = request.getParameter("filename");
        System.out.println("DeleteServlet info: filename=" + s);
        File file = new File(s);
        delete(file);
//        request.getRequestDispatcher("/list").forward(request,response);
        response.sendRedirect("index.jsp");
    }

    static void delete(File file) {
        if (file.exists())
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) delete(f);
                if(file.listFiles().length==0)file.delete();
            } else file.delete();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = request.getParameter("filename");
        File file= new File(s,"新建文件夹");
        file.mkdir();
        response.sendRedirect("index.jsp");
    }
}
