package com.zhw.service;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        File src = new File("C:\\Users\\Administrator\\Desktop\\demo.csv");
        File dest = new File("C:\\Users\\Administrator\\Desktop\\dest.csv");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(src), "GBK"));
            PrintWriter out = new PrintWriter(new FileWriter(dest), true);
            String line = in.readLine();
            while ((line = in.readLine()) != null) {
                String[] rows = line.split(",", -1);
                StringBuilder builder = new StringBuilder("");
                for (int i = 0; i < rows.length; i++) {
                    builder.append("\"" + rows[i].trim() + "\"");
                    if (i != rows.length - 1) {
                        builder.append(",");
                    }
                }

                out.println(builder.toString());
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
