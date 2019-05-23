package javaapitest;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportExcel {

    public static final String resultPath = "C:\\Users\\aimran\\Desktop\\result.xlsx";

    public static void main(String args[]) throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");
        List<ColMapping> mappings = prepareColumns(User.class);
        System.out.println("javaapitest.ExportExcel.main() "+User.class.getSuperclass().getSuperclass().getSuperclass());
        int rowNum = 0;
        int colNum = 0;
        Row row = sheet.createRow(rowNum++);
        for (ColMapping cm : mappings) {
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(cm.colName);
        }

        List<Object> data = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            data.add(new User(i, "User " + i, "Status " + i));
        } 

        for (Object obj : data) {

            row = sheet.createRow(rowNum++);
            colNum = 0;
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Cell cell = row.createCell(colNum++);
                if (field.get(obj) instanceof Long) {
                    cell.setCellValue((Long) field.get(obj));
                } else {
                    cell.setCellValue((String) field.get(obj));
                }

            }

        }
        //FileOutputStream outputStream = new FileOutputStream(resultPath);
        ByteArrayOutputStream bos  = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] xls = bos.toByteArray();
        if (Desktop.isDesktopSupported()) {
//            Desktop.getDesktop().edit(new File(resultPath));
        }

    }

    public static List<ColMapping> prepareColumns(Class<?> clazz) {

        List<ColMapping> colMappings = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        int counter = 0;
        for (Field f : fields) {
            String fieldName = f.getName();
            if (fieldName.equalsIgnoreCase("rid")) {
                fieldName = "id";
            }
            if (fieldName.startsWith("lkp")) {
                fieldName = fieldName.substring(3);
            }

            ColMapping cm = new ColMapping(modifyName(fieldName), f.getName(), counter);
            colMappings.add(cm);
            counter++;
        }
        //super classes
        return colMappings;

    }

    public static String modifyName(String fieldName) {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return String.join(" ", fieldName.split("(?=[A-Z])"));
    }

    static class ColMapping {

        public ColMapping(String colName, String fieldName, int colNumber) {
            this.colName = colName;
            this.fieldName = fieldName;
            this.colNumber = colNumber;
        }

        
        String colName;
        String fieldName;
        int colNumber;
    }

    static class Base {

        long createdBy;

        Base(long createdBy) {
            this.createdBy = createdBy;
        }
    }

    static class User extends Base {

        public User(long rid, String userName, String lkpUserStatus) {
            super(rid);
            this.rid = rid;
            this.userName = userName;
            this.lkpUserStatus = lkpUserStatus;
        }

        private long rid;
        private String userName;
        private String lkpUserStatus;

        public long getRid() {
            return rid;
        }

        public void setRid(long rid) {
            this.rid = rid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLkpUserStatus() {
            return lkpUserStatus;
        }

        public void setLkpUserStatus(String lkpUserStatus) {
            this.lkpUserStatus = lkpUserStatus;
        }

        public long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(long createdBy) {
            this.createdBy = createdBy;
        }

    }

}
