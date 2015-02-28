package com.zhw.service;

import com.google.common.collect.Lists;
import com.zhw.utils.Files;
import com.zhw.utils.PropertiesLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "zhw-excel";

    // 日期是记账日期
    private static final String[] TITLES = new String[]{"序号", "分行号", "分行", "客户号", "客户名", "货币", "余额", "折港币余额", "折港币余额（万元）", "放款/还款到期日", "逾期期数", "逾期天数范围", "逾期天数", "综合评", "产品码", "贷款业务品种", "抵押物类型", "征信/法院网/抵押物查封查询结果", "采取的措施", "催收时间", "催收实施人", "逾期原因及催收结果进展", "联系电话", "归还标志", "备注（如果采取措施为上门催收，应填写上门催收信息，）", "", "电话催收", "上门催收"};
    private static DateTimeFormatter YYYY_MM_DD = DateTimeFormat.forPattern("yyyy-MM-dd");
    protected static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/auto.properties",
            "classpath:/organizations.properties", "classpath:/product.properties");

    public String process(String recordDay, boolean combineToExcel, boolean processResult, String min, String max, List<File> files) {
        File zip = null;
        String id = null;
        try {
            id = UUID.randomUUID().toString();
            File dir = new File(TEMP_DIR + File.separator + id);
            dir.mkdirs();
            File combineCSV = combineToCSV(dir, files);
            File combineExcel = null;
            if (combineToExcel) {
                combineExcel = combineToExcel(dir, files);
            }
            // 处理结果
            File resultExcel = null;
            if (processResult) {
                resultExcel = this.resultExcel(recordDay, dir, combineCSV, min, max);
            }

            logger.info("处理完成开始压缩文件");
            if (combineExcel == null) {
                if (resultExcel == null) {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV);
                } else {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, resultExcel);
                }
            } else {
                if (resultExcel == null) {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, combineExcel);
                } else {
                    zip = Files.zip(TEMP_DIR + File.separator + id + File.separator + "结果.zip", combineCSV, combineExcel, resultExcel);
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
        logger.info("开始合并CSV文件");
        File combine = null;
        PrintWriter out = null;
        try {
            combine = new File(dir.getAbsolutePath() + File.separator + "combineToCSV.csv");
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
        logger.info("开始合并Excel文件");
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
                    }
                } catch (Exception e) {
                    logger.warn("合并Excel到第{}行", rowIndex);
                    logger.warn("写入文件" + src.getName() + "异常", e);
                    return null;
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            // 创建文件流
            combine = new File(dir.getAbsolutePath() + File.separator + "combineToExcel.xlsx");
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

    public File resultExcel(String recordDay, File dir, File combineCSV, String min, String max) {
        logger.info("开始处理最终结果文件");
        File result = null;
        //创建工作文档对象
        Workbook wb = null;
        OutputStream stream = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(combineCSV), "gbk"));

            // 创建Excel文件
            wb = new XSSFWorkbook();

            // 创建文本格式
            CellStyle txtStyle = wb.createCellStyle();
            DataFormat format = wb.createDataFormat();
            txtStyle.setDataFormat(format.getFormat("@"));
            // 创建小数点后保留两位的数字格式
            CellStyle decimalStyle = wb.createCellStyle();
            decimalStyle.setDataFormat(format.getFormat("0.00"));
            // 创建Sheet1
            Sheet sheet1 = (Sheet) wb.createSheet("sheet1");
            // 结果文件行,0行是表头
            int rowIndex = 1;

            // 写表头
            Row row = (Row) sheet1.createRow(0);
            List<String> titleList = Lists.newArrayList(TITLES);
            titleList.set(25, recordDay);
            for (int i = 0; i < titleList.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(txtStyle);
                cell.setCellValue(titleList.get(i));
            }

            // 遍历合并后的CSV文件
            String line = null;
            while ((line = in.readLine()) != null) {
                row = (Row) sheet1.createRow(rowIndex);
                String[] src = line.split(",", -1);
                // 序号	分行号 分行	客户号 客户名 货币 余额 折港币余额 折港币余额（万元） 放款/还款到期日 逾期期数 逾期天数范围 逾期天数 综合评 产品码 贷款业务品种
                // 循环处理每个字段
                for (int i = 0; i < 16; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(txtStyle);
                    if (i == 0) {
                        // 序号
                        cell.setCellValue(rowIndex);
                    } else if (i == 1) {
                        // 分行号
                        cell.setCellValue(getValue(src[1]));
                    } else if (i == 2) {
                        // 分行
                        cell.setCellValue(getCode(src[1]));
                    } else if (i == 3) {
                        // 客户号
                        cell.setCellValue(getValue(src[14]));
                    }
//                        else if (i == 4) {
//                            // 客户名
//                        }
                    else if (i == 5) {
                        // 货币
                        cell.setCellValue("CNY");
                    } else if (i == 6) {
                        // 余额
                        cell.setCellValue(getMoneyValue(src[22]));
                    } else if (i == 7) {
                        // 折港币余额
                        cell.setCellValue(getMoneyValue(src[23]));
                    } else if (i == 8) {
                        // 折港币余额（万元）
                        cell.setCellStyle(decimalStyle);
                        cell.setCellValue(divisibleTenThousand(getMoneyValue(src[23]), 2));
                    } else if (i == 9) {
                        // 放款/还款到期日
                        cell.setCellValue(getValue(src[27]));
                    } else if (i == 10) {
                        // 逾期期数
                        // 不予期
                        cell.setCellValue(getOverdue(recordDay, getValue(src[27])));
                    } else if (i == 11) {
                        // 逾期天数范围
                        cell.setCellValue(getOverdueDayRange(recordDay, getValue(src[27])));
                    } else if (i == 12) {
                        // 逾期天数
                        cell.setCellValue(getOverdueDay(recordDay, getValue(src[27])));
                    } else if (i == 13) {
                        // 综合评 用自动评代码取寻找
                        cell.setCellValue(getCode(src[31]));
                    } else if (i == 14) {
                        // 产品码
                        cell.setCellValue(getValue(src[150]));
                    } else if (i == 15) {
                        // 贷款业务品种
                        cell.setCellValue(getCode(src[150]));
                    }
                }
                rowIndex++;
            }
            // 统一设置 采取的措施 有效性数据 电话催收/上门催收
            DataValidation dataValidation = getDataValidation((XSSFSheet) sheet1, 1, rowIndex, 18, 18);
            sheet1.addValidationData(dataValidation);

            // 统一设置 催收时间 时间范围
            DataValidation dataRangeValidation = getDataRangeValidation((XSSFSheet) sheet1, 1, rowIndex, 19, 19, min, max);
            if (dataRangeValidation != null) {
                sheet1.addValidationData(dataRangeValidation);
            }
            // 创建文件流
            result = new File(dir.getAbsolutePath() + File.separator + "result.xlsx");
            stream = new FileOutputStream(result);
            // 写入数据
            wb.write(stream);
        } catch (Exception e) {
            logger.warn("处理最终结果文件异常", e);
            return null;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(wb);
        }
        return result;

    }

    public static String getValue(String src) {
        return StringUtils.isNotBlank(src) ? src.substring(1, src.length() - 1) : "";
    }

    /**
     * CSV文件中货币前面带有+号
     */
    public static String getMoneyValue(String src) {
        if (StringUtils.isNotBlank(src) && src.length() > 2 && src.startsWith("\"+")) {
            return src.substring(2, src.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * 根据机构代码得到分行
     * 根据产品码返回贷款业务品种
     * 根据自动评得到综合评汉字内容 TODO:确认需求
     */
    public static String getCode(String src) {
        String code = getValue(src);
        try {
            String org = propertiesLoader.getProperty(code);
            return StringUtils.isNotBlank(org) ? org : "";
        } catch (Exception e) {
            logger.warn("无法找到编码 {} 对应的内容，请检查auto.properties，organizations.properties，product.properties三个配置文件", code);
            return "";
        }
    }

    /**
     * 折港币余额（万元）
     * 除以10000，结果保留2位小数
     */
    public static String divisibleTenThousand(Object digits, int decimal) {
        try {
            double number = NumberUtils.toDouble(digits.toString());
            BigDecimal bd = new BigDecimal(number);
            bd = bd.divide(new BigDecimal(10000));
            bd = bd.setScale(decimal, BigDecimal.ROUND_HALF_UP);
            return bd.doubleValue() + "";
        } catch (Exception e) {
            logger.warn("转换 折港币余额 失败，源数据 " + digits, e);
            return "";
        }
    }

    /**
     * 计算逾期期数
     * 超过每个月的“日”，逾期期数+1
     *
     * @param recordDay 记账日期
     * @param overDay   还款日期
     */
    private String getOverdue(String recordDay, String overDay) {
        if ("0000-00-00".equals(overDay)) {
            return "不逾期";
        }
        DateTime record = null;
        DateTime over = null;
        try {
            record = DateTime.parse(recordDay, YYYY_MM_DD);
        } catch (Exception e) {
            logger.warn("无法解析记账日期{},第{}行", recordDay);
            return "异常";
        }
        try {
            over = DateTime.parse(overDay, YYYY_MM_DD);
        } catch (Exception e) {
            logger.warn("无法解析还款日期{},第{}行", overDay);
            return "异常";
        }
        if (record.isEqual(over) || record.isBefore(over)) {
            return "不逾期";
        } else if (record.getYear() == over.getYear()) {
            // 如果在同一年中
            // 只要逾期基础为1
            int i = 1;
            // 加上月份相减的结果
            i = i + (record.getMonthOfYear() - over.getMonthOfYear());
            // 如果记账日期的“日”小于还款的“日”再减去1期
            if (record.getDayOfMonth() < over.getDayOfMonth()) {
                i--;
            }
            return i + "";
        } else {
            // 如果不在同一年 分三部分
            // 还款年的剩余的月份（包含当前月）
            int i = 12 - over.getMonthOfYear() + 1;
            // 中间年每经过一年增加12个月
            i = i + (12 * (record.getYear() - over.getYear() - 1));
            // 记账年的月份
            i = i + record.getMonthOfYear();
            // 如果记账日期的“日”小于还款的“日”再减去1期
            if (record.getDayOfMonth() < over.getDayOfMonth()) {
                i--;
            }
            return i + "";
        }
    }

    /**
     * 计算逾期多少天
     */
    private String getOverdueDay(String recordDay, String overDay) {
        if ("0000-00-00".equals(overDay)) {
            return "不逾期";
        }
        DateTime record = null;
        DateTime over = null;
        try {
            record = DateTime.parse(recordDay, YYYY_MM_DD);
        } catch (Exception e) {
            logger.warn("无法解析记账日期{},第{}行", recordDay);
            return "异常";
        }
        try {
            over = DateTime.parse(overDay, YYYY_MM_DD);
        } catch (Exception e) {
            logger.warn("无法解析还款日期{},第{}行", overDay);
            return "异常";
        }
        if (record.isEqual(over) || record.isBefore(over)) {
            return "不逾期";
        } else {
            Days days = Days.daysBetween(over, record);
            return days.getDays() + "";
        }
    }

    /**
     * 逾期多少天所在范围
     * <p>=IF(M3>90,"90+",IF(M3>60,"61-90",IF(M3>30,"31-60","8-30")))</p>
     */
    private String getOverdueDayRange(String recordDay, String overDay) {
        String days = this.getOverdueDay(recordDay, overDay);
        if (!NumberUtils.isNumber(days)) {
            return "异常";
        } else {
            Integer day = Integer.valueOf(days);
            if (day > 90) {
                return "90+";
            } else if (day > 60) {
                return "61-90";
            } else if (day > 30) {
                return "31-60";
            } else {
                return "8-30";
            }
        }
    }

    /**
     * 设置采取的措施
     */
    private DataValidation getDataValidation(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        //设置下拉列表的内容
        String[] textlist = {"电话催收", "上门催收"};
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上。 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        return helper.createValidation(constraint, regions);
    }

    private DataValidation getDataRangeValidation(XSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, String min, String max) {
        DateTime minDay = null;
        DateTime maxDay = null;
        try {
            minDay = DateTime.parse(min, YYYY_MM_DD);
            maxDay = DateTime.parse(max, YYYY_MM_DD);
        } catch (Exception e) {
            logger.info("设置时间范围失败 min : {}, max : {}", min, max);
            return null;
        }
        List<String> arrays = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            minDay = minDay.plusDays(1);
            arrays.add(minDay.toString(YYYY_MM_DD));
            if (minDay.equals(maxDay)) {
                break;
            }
        }
        String[] days = (String[]) arrays.toArray(new String[]{});
        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(days);
        // 设置数据有效性加载在哪个单元格上。 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        return helper.createValidation(constraint, regions);
    }

    /**
     * 清空临时文件夹
     */
    public long flushTemp() {
        File file = new File(TEMP_DIR);
        if (!file.exists()) {
            return 0l;
        }
        // 得到大小
        long size = FileUtils.sizeOfDirectory(file) / 1024 / 1024;
        try {
            // 删除文件夹
            FileUtils.deleteDirectory(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


}
