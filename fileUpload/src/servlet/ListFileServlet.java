package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@WebServlet(name = "ListFileServlet", urlPatterns = "/list")
public class ListFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取上传文件的目录
        String s = request.getParameter("filepath");
        Properties properties = new Properties();
//        properties.load(new FileInputStream("dir.properties"));

        String uploadFilePath = "C:\\root";
//        properties.getProperty("filepath");
        if (s != null) uploadFilePath = s;
//                request.getSession().getServletContext().getRealPath("/");
        //存储要下载的文件名
        Map<String, String> fileMap = new HashMap<>();
        //递归遍历filepath目录下的所有文件和目录，将文件的文件名存储到map集合中
        File file = new File(uploadFilePath);
        fileList(file, fileMap);
        //将Map集合发送到listfile.jsp页面进行显示
        request.setAttribute("fileMap", fileMap);
        request.setAttribute("path", uploadFilePath);
        request.getRequestDispatcher("/listfile.jsp").forward(request, response);
    }

    //递归遍历指定目录下的所有文件
    private void fileList(File file, Map fileMap) {
//        if(!file.isFile()){
//            //列出该目录下的所有文件和目录
//            File[] files = file.listFiles();
//            //遍历files[]数组
//            for (File file2 : files) {
//                System.out.println(file2.getName());
//                //递归
//                fileList(file2, fileMap);
//            }
//        }else{
//            //file.getName()得到的是文件的原始名称，这个名称是唯一的，因此可以作为key，realName是处理过后的名称，有可能会重复
//            String realName = file.getName().substring(file.getName().lastIndexOf("_")+1);
//            fileMap.put(file.getName(), realName);
//        }
        //如果file代表的不是一个文件，而是一个目录
        if (!file.isFile()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
//                if(!file2.isFile())continue;
                System.out.println(file2.getName());
                String type;
                if (!file2.isFile()) type = "file";
                else type = this.getServletContext().getMimeType(file2.getName());
                String realName = file2.getName().substring(file2.getName().lastIndexOf("_") + 1);
                fileMap.put(file2.getName(), type);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
