package servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "UploadServlet", urlPatterns = "/upload")
public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload sfm = new ServletFileUpload(factory);
        sfm.setHeaderEncoding("utf-8");
        try {
            for (FileItem item : sfm.parseRequest(request)) {
                //如果fileitem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    System.out.println(name + "=" + value);
                } else {//如果fileitem中封装的是上传文件
                    String filename = item.getName();
                    if (filename == null || filename.trim().equals("")) {
                        continue;
                    }
                    //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    filename = filename.substring(filename.lastIndexOf("\\") + 1);
                    //获取item中的上传文件的输入流
                    InputStream in = item.getInputStream();
                    int i = 1;//避免文件名重复
                    while (contains(filename)) {
                        filename = filename + "("+i+")";
                        i++;
                    }
                    //创建一个文件输出流
                    FileOutputStream out = new FileOutputStream("C:\\root\\" + filename);
                    IOUtils.copy(in, out);
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write("上传成功,重定向中。。。");
                    response.sendRedirect("index.jsp");
                    in.close();
                    out.close();
                    item.delete();
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    private boolean contains(String filename) {
        File file = new File("C:\\root");
        File[] files = file.listFiles();
        for (File f : files)
            if (f.getName().equals(filename))
                return true;
        return false;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
