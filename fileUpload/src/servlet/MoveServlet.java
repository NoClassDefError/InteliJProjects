package servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@WebServlet(name = "MoveServlet", urlPatterns = "/move")
public class MoveServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File file = new File(request.getParameter("filepath"));
        File location = new File(request.getParameter("location") + "\\" + file.getName());
        if (!location.exists())
            location.createNewFile();
        IOUtils.copy(new FileInputStream(file), new FileOutputStream(location));
        DeleteServlet.delete(file);
        response.sendRedirect("index.jsp");
    }
}
