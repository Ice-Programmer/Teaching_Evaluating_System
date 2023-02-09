package com.itmo.eva.utils;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class DownLoadUtil {
    public static void uploadFile(HttpServletResponse response, String path) {
        try {
            File file = new File(path);
            String filename = file.getName();
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.addHeader("Access-Control-Expose-Headers", "filetype");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            response.addHeader("filepath", path);
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
