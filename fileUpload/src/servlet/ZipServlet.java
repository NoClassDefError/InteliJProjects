package servlet;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ZipServlet", urlPatterns = "/unzip")
public class ZipServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String req = request.getParameter("request");
        String password = request.getParameter("password");
        String filename = request.getParameter("filepath");
        String path = request.getParameter("path");
        try {
            switch (req) {
                case "zip":
                    zip(filename, filename, password);
                    break;
                case "unrar":
                    unrar(filename, path);
                    break;
                case "unzip":
                    unZip(filename, path, password);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("filepath", path);
        request.getRequestDispatcher("list").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 压缩
     *
     * @param srcFile 源目录
     * @param dest    要压缩到的目录
     * @param passwd  密码 不是必填
     * @throws ZipException 异常
     */
    private static void zip(String srcFile, String dest, String passwd) throws ZipException {
        File srcfile = new File(srcFile);
        //创建目标文件
        String destname = buildDestFileName(srcfile, dest);
        ZipParameters par = new ZipParameters();
        par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        if (passwd != null) {
            par.setEncryptFiles(true);
            par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            par.setPassword(passwd.toCharArray());
        }
        ZipFile zipfile = new ZipFile(destname+".zip");
        if (srcfile.isDirectory()) zipfile.addFolder(srcfile, par);
        else zipfile.addFile(srcfile, par);
    }

    /**
     * 解压
     *
     * @param zipfile 压缩包文件
     * @param dest    目标文件
     * @param passwd  密码
     * @throws ZipException 抛出异常
     */
    private static void unZip(String zipfile, String dest, String passwd) throws ZipException {
        ZipFile zfile = new ZipFile(zipfile);
        zfile.setFileNameCharset("UTF-8");//在GBK系统中需要设置
        if (!zfile.isValidZipFile()) throw new ZipException("压缩文件不合法，可能已经损坏！");
        File file = new File(dest);
        if (file.isDirectory() && !file.exists()) file.mkdirs();
        if (zfile.isEncrypted()) zfile.setPassword(passwd.toCharArray());
        zfile.extractAll(dest);
    }

    private static String buildDestFileName(File srcfile, String dest) {
        String substring = srcfile.getName();
        if (substring.contains("."))
            substring = substring.substring(0, srcfile.getName().lastIndexOf("."));
        if (dest == null) {
            if (srcfile.isDirectory()) dest = srcfile.getParent() + File.separator + srcfile.getName() + ".zip";
            else dest = srcfile.getParent() + File.separator + substring + ".zip";
        } else {
            createPath(dest);//路径的创建
            if (dest.endsWith(File.separator)) {
                String filename;
                if (srcfile.isDirectory()) filename = srcfile.getName();
                else filename = substring;
                dest += filename + ".zip";
            }
        }
        return dest;
    }

    private static void createPath(String dest) {
        File destDir;
        if (dest.endsWith(File.separator)) destDir = new File(dest);//给出的是路径时
        else destDir = new File(dest.substring(0, dest.lastIndexOf(File.separator)));
        if (!destDir.exists()) destDir.mkdirs();
    }

    /**
     * @param rarFileName rar file name
     * @param outFilePath output file path
     * @throws Exception
     * @author zhuss
     */
    private static void unrar(String rarFileName, String outFilePath) throws Exception {
        try {
            Archive archive = new Archive(new File(rarFileName));
            if (archive == null) throw new FileNotFoundException(rarFileName + " NOT FOUND!");
            if (archive.isEncrypted()) throw new Exception(rarFileName + " IS ENCRYPTED!");
            List<FileHeader> files = archive.getFileHeaders();
            for (FileHeader fh : files) {
                if (fh.isEncrypted()) throw new Exception(rarFileName + " IS ENCRYPTED!");
                String fileName = fh.getFileNameW();
                if (fileName != null && fileName.trim().length() > 0) {
                    String saveFileName = outFilePath + "\\" + fileName;
                    File saveFile = new File(saveFileName);
                    File parent = saveFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();
                    if (!saveFile.exists()) saveFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    try {
                        archive.extractFile(fh, fos);
                        fos.flush();
                        fos.close();
                    } catch (RarException e) {
                        e.getType();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
