package com.zhw.service;

import com.zhw.utils.Files;
import com.zhw.utils.PropertiesLoader;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "zhw-excel";

    protected static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/organizations.properties"
            , "classpath:/product.properties");

    public String process(String dataDate, boolean combineToExcel, boolean processResult, List<File> files) {
        File zip = null;
        String id = null;
        try {
            id = UUID.randomUUID().toString();
            File dir = new File(TEMP_DIR + File.separator + id);
            dir.mkdirs();
            File combineCSV = combineToCSV(dir, files);
            File combineExcel = combineToExcel(dir, files);
            // 处理结果
            File res = null;
            logger.info("处理完成开始压缩文件");
            if (!combineToExcel || combineExcel == null) {
                logger.info("内存溢出，无法完成合并Excel");
                if (!processResult || res == null) {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV);
                } else {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, res);
                }

            } else {
                if (!processResult || res == null) {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, combineExcel);
                } else {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, combineExcel, res);
                }

            }

        } catch (Exception e) {
            logger.warn("解析失败", e);
            id = null;
        }
        return id;
    }

    /**
     * 将所有文件合并成CSV文件
     */
    public File combineToCSV(File dir, List<File> files) {
        File combine = null;
        PrintWriter out = null;
        try {
            combine = new File(dir.getParent() + File.separator + "combineToCSV.csv");
            // 追加输出流
            out = new PrintWriter(new FileOutputStream(combine, true));
            for (File file : files) {
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        out.println(line);
                    }
                } catch (Exception e) {
                    logger.warn("合并到CSV时读取" + file.getName() + "文件异常", e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
        return combine;
    }

    /**
     * 将所有文件合并
     */
    public File combineToExcel(File dir, List<File> files) {
        File combine = null;
        //创建工作文档对象
        Workbook wb = null;
        OutputStream stream = null;
        try {
            // 创建Excel文件
            wb = new XSSFWorkbook();
            CellStyle txtStyle = wb.createCellStyle();
            DataFormat format = wb.createDataFormat();
            txtStyle.setDataFormat(format.getFormat("@"));

            CellStyle decimalStyle = wb.createCellStyle();
            decimalStyle.setDataFormat(format.getFormat("0.00"));
            // 创建Sheet1
            Sheet sheet1 = (Sheet) wb.createSheet("sheet1");
            // 结果文件行
            int rowIndex = 0;
            BufferedReader in = null;
            // 遍历所有源文件
            for (File src : files) {
                logger.info("开始处理文件{}", src.getName());
                try {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(src), "gbk"));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        String[] arrays = line.split(",", -1);
                        Row row = (Row) sheet1.createRow(rowIndex);
                        rowIndex++;
                        for (int j = 0; j < arrays.length; j++) {
                            Cell cell = row.createCell(j);
                            // TODO:问一下哥 这里需要保留两位么？
                            if (j != 23) {
                                cell.setCellStyle(txtStyle);
                            } else {
                                cell.setCellStyle(decimalStyle);
                            }

                            cell.setCellValue(arrays[j].substring(1, arrays[j].length() - 1));
                        }
                        logger.info("合并Excel到第{}行", rowIndex);
                    }
                } catch (Exception e) {
                    IOUtils.closeQuietly(in);
                    logger.warn("写入文件" + src.getName() + "异常", e);
                    return null;
                }
            }
            // 创建文件流
            combine = new File(dir.getParent() + File.separator + "combineToExcel.xlsx");
            stream = new FileOutputStream(combine);
            // 写入数据
            wb.write(stream);
        } catch (Exception e) {
            logger.warn("写入合并文件异常", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return combine;
    }

    public static void main(String[] args) {
        String baseUrl = propertiesLoader.getProperty("baseUrl");
    }

}
