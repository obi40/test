package javaapitest;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Loinc {

    static List<TestResDef> mappedTestDefRidLoincCode = new ArrayList<>();
    static List<TestResDef> mappedTestResRidLoincCode = new ArrayList<>();
    static List<TestResDef> mappedLoincCode = new ArrayList<>();
    static List<TestResDef> mappedLabSection = new ArrayList<>();

    static String insertLoincAtt = "INSERT INTO \"public\".\"loinc_attributes\" VALUES ('%1$s', '1', '0', '1', '2017-10-15 10:19:01', '1', '2017-10-15 10:19:04','%2$s', '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', '%8$s', '%9$s', '%10$s', '%11$s', '%12$s', '%13$s','%14$s', '%15$s', '%16$s', '%17$s', '%18$s', '%19$s', '%20$s', '%21$s', '%22$s', '%23$s', '%24$s', '%25$s','%26$s', '%27$s', '%28$s', '%29$s', '%30$s', '%31$s', '%32$s', '%33$s', '%34$s', '%35$s', '%36$s', '%37$s','%38$s', '%39$s', '%40$s', '%41$s', '%42$s', '%43$s', '%44$s', '%45$s', '%46$s', '%47$s');";
    static String insertLabSection = "INSERT INTO \"public\".\"lab_section\" VALUES ('%1$s', '{\"en_us\":\"%2$s\",\"ar_jo\":\"%2$s\"}', '1', '2017-10-10 00:00:00', null, '1', '1', null, '1');";

    static String testDefUpdateLoinCode = "UPDATE test_definition SET loinc_attributes_id = '%1$s' WHERE rid ='%2$s'; ";
    static String testDefUpdateSection = "UPDATE test_definition SET section_id = '%1$s' WHERE rid ='%2$s'; ";

    static String testResUpdateLoinCode = "UPDATE test_result SET loinc_attributes_id = '%1$s' WHERE rid ='%2$s'; ";

    public static void main(String[] args) throws Exception {
        getTestDefResList();
        //parseLoincAttributes();
        StringBuilder sb = new StringBuilder();
        for (TestResDef trd : mappedTestResRidLoincCode) {

            for (TestResDef loinc : mappedLoincCode) {
                if (trd.loinCode.equals(loinc.loinCode)) {
                    sb.append(String.format(testResUpdateLoinCode, String.valueOf(loinc.rid), String.valueOf(trd.rid)));
                    sb.append("\n");

                }
            }

        }
        //FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\backup\\loinc\\updateTestRes.sql", sb.toString());
        System.out.println("--------------");
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        for (TestResDef trd : mappedTestDefRidLoincCode) {

            for (TestResDef loinc : mappedLoincCode) {
                if (trd.loinCode.equals(loinc.loinCode)) {
                    sb2.append(String.format(testDefUpdateLoinCode, String.valueOf(loinc.rid), String.valueOf(trd.rid)));
                    sb2.append("\n");
                    boolean flag = false;
                    for (TestResDef trd2 : mappedLabSection) {
                        if (loinc.section.equals(trd2.section)) {
                            sb3.append(String.format(testDefUpdateSection, String.valueOf(trd2.rid), String.valueOf(trd.rid)));
                            sb3.append("\n");
                            flag =true;
                            break;
                        }
                    }
                    if(flag==false){
                        System.out.println("cannot find section: " + loinc.section);
                    }
                }

            }

        }
        //FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\backup\\loinc\\updateTestDef.sql", sb2.toString());
        //FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\backup\\loinc\\updateTestDef_labSection.sql", sb3.toString());

    }

    public static void parseLoincAttributes() throws Exception {

        File excelFile = new File("C:\\Users\\aimran\\Desktop\\loinc workbook.xlsx");
        FileInputStream inputStream = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);

        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> iterator = firstSheet.iterator();
        //skip the first row
        iterator.next();

        Map<String, String> rowMap = new HashMap<String, String>();

        long counter = 1L;
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            int lastColumn = Math.max(nextRow.getLastCellNum(), 46);

            for (int cn = 0; cn < lastColumn; cn++) {
                Cell cell = nextRow.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String cellValue = null;

                if (cell != null) {
                    cellValue = dataFormatter.formatCellValue(cell);
                }
                getLoincAttributesMap(cn, rowMap, cellValue);
            }

            sb.append(getLoincAttributesFromMap(rowMap, String.valueOf(counter)));
            //sb.append("\n");
            //counter++;

        }
        //FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\backup\\loinc\\loinc_insert.sql", sb.toString());
        workbook.close();
        inputStream.close();

    }

    private static String getLoincAttributesFromMap(Map<String, String> rowMap, String rid) {

        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            rowMap.put(entry.getKey(), entry.getValue() != null ? entry.getValue().replaceAll("\'", "\'\'") : null);
        }

        /*String component = rowMap.get("component");
        component = component != null ? component.replaceAll("\'", "\'\'") : null;

        String clazz = rowMap.get("class");
        clazz = clazz != null ? clazz.replaceAll("\'", "\'\'") : null;

        String related = rowMap.get("relatedNames2");
        related = related != null ? related.replaceAll("\'", "\'\'") : null;

        String def = rowMap.get("definitionDescription");
        def = def != null ? def.replaceAll("\'", "\'\'") : null;

        String longname = rowMap.get("longCommonName");
        longname = longname != null ? longname.replaceAll("\'", "\'\'") : null;

        String shortName = rowMap.get("shortName");
        shortName = shortName != null ? shortName.replaceAll("\'", "\'\'") : null;

        String exampleucum = rowMap.get("exampleUcumUnits");
        exampleucum = exampleucum != null ? exampleucum.replaceAll("\'", "\'\'") : null;

        String statusText = rowMap.get("statusText");
        statusText = statusText != null ? statusText.replaceAll("\'", "\'\'") : null;*/
        return String.format(insertLoincAtt, rid,
                rowMap.get("loincNum"),
                rowMap.get("component"),
                rowMap.get("exampleUnits"),
                rowMap.get("timeAspect"),
                rowMap.get("surveyQuestionSource"),
                rowMap.get("externalCopyrightNotice"),
                rowMap.get("methodType"),
                rowMap.get("surveyQuestionText"),
                rowMap.get("property"),
                rowMap.get("unitsRequired"),
                rowMap.get("class"),
                rowMap.get("submittedUnits"),
                rowMap.get("relatedNames2"),
                rowMap.get("definitionDescription"),
                rowMap.get("orderObservation"),
                rowMap.get("changeType"),
                rowMap.get("cdiscCommonTests"),
                rowMap.get("versionLastChanged"),
                rowMap.get("system"),
                rowMap.get("hl7FieldSubfieldId"),
                rowMap.get("scaleType"),
                rowMap.get("species"),
                rowMap.get("formula"),
                rowMap.get("shortName"),
                rowMap.get("classType"),
                rowMap.get("status"),
                rowMap.get("consumerName"),
                rowMap.get("longCommonName"),
                rowMap.get("unitsAndRange"),
                rowMap.get("documentSection"),
                rowMap.get("exampleUcumUnits"),
                rowMap.get("exampleSiUcumUnits"),
                rowMap.get("statusReason"),
                rowMap.get("statusText"),
                rowMap.get("changeReasonPublic"),
                rowMap.get("commonTestRank"),
                rowMap.get("commonOrderRank"),
                rowMap.get("commonSiTestRank"),
                rowMap.get("hl7AttachmentStructure"),
                rowMap.get("externalCopyrightLink"),
                rowMap.get("panelType"),
                rowMap.get("askAtOrderEntry"),
                rowMap.get("associatedObservations"),
                rowMap.get("versionFirstReleased"),
                rowMap.get("validHl7AttachmentRequest"),
                rowMap.get("exampleAnswers"));
    }

    private static void getLoincAttributesMap(int j, Map<String, String> rowMap, String cellValue) {
        switch (j) {
            case 0:
                rowMap.put("loincNum", cellValue);
                break;
            case 1:
                rowMap.put("component", cellValue);
                break;
            case 2:
                rowMap.put("property", cellValue);
                break;
            case 3:
                rowMap.put("timeAspect", cellValue);
                break;
            case 4:
                rowMap.put("system", cellValue);
                break;
            case 5:
                rowMap.put("scaleType", cellValue);
                break;
            case 6:
                rowMap.put("methodType", cellValue);
                break;
            case 7:
                rowMap.put("class", cellValue);
                break;
            case 8:
                rowMap.put("versionLastChanged", cellValue);
                break;
            case 9:
                rowMap.put("changeType", cellValue);
                break;
            case 10:
                rowMap.put("definitionDescription", cellValue);
                break;
            case 11:
                rowMap.put("status", cellValue);
                break;
            case 12:
                rowMap.put("consumerName", cellValue);
                break;
            case 13:
                rowMap.put("classType", cellValue);
                break;
            case 14:
                rowMap.put("formula", cellValue);
                break;
            case 15:
                rowMap.put("species", cellValue);
                break;
            case 16:
                rowMap.put("exampleAnswers", cellValue);
                break;
            case 17:
                rowMap.put("surveyQuestionText", cellValue);
                break;
            case 18:
                rowMap.put("surveyQuestionSource", cellValue);
                break;
            case 19:
                rowMap.put("unitsRequired", cellValue);
                break;
            case 20:
                rowMap.put("submittedUnits", cellValue);
                break;
            case 21:
                rowMap.put("relatedNames2", cellValue);
                break;
            case 22:
                rowMap.put("shortName", cellValue);
                break;
            case 23:
                rowMap.put("orderObservation", cellValue);
                break;
            case 24:
                rowMap.put("cdiscCommonTests", cellValue);
                break;
            case 25:
                rowMap.put("hl7FieldSubfieldId", cellValue);
                break;
            case 26:
                rowMap.put("externalCopyrightNotice", cellValue);
                break;
            case 27:
                rowMap.put("exampleUnits", cellValue);
                break;
            case 28:
                rowMap.put("longCommonName", cellValue);
                break;
            case 29:
                rowMap.put("unitsAndRange", cellValue);
                break;
            case 30:
                rowMap.put("documentSection", cellValue);
                break;
            case 31:
                rowMap.put("exampleUcumUnits", cellValue);
                break;
            case 32:
                rowMap.put("exampleSiUcumUnits", cellValue);
                break;
            case 33:
                rowMap.put("statusReason", cellValue);
                break;
            case 34:
                rowMap.put("statusText", cellValue);
                break;
            case 35:
                rowMap.put("changeReasonPublic", cellValue);
                break;
            case 36:
                rowMap.put("commonTestRank", cellValue);
                break;
            case 37:
                rowMap.put("commonOrderRank", cellValue);
                break;
            case 38:
                rowMap.put("commonSiTestRank", cellValue);
                break;
            case 39:
                rowMap.put("hl7AttachmentStructure", cellValue);
                break;
            case 40:
                rowMap.put("externalCopyrightLink", cellValue);
                break;
            case 41:
                rowMap.put("panelType", cellValue);
                break;
            case 42:
                rowMap.put("askAtOrderEntry", cellValue);
                break;
            case 43:
                rowMap.put("associatedObservations", cellValue);
                break;
            case 44:
                rowMap.put("versionFirstReleased", cellValue);
                break;
            case 45:
                rowMap.put("validHl7AttachmentRequest", cellValue);
                break;
        }
    }

    private static void getTestDefResList() {

        try {
            String testDefFile = "C:\\Users\\aimran\\Desktop\\backup\\loinc\\test_def_ridAndloinccode.txt";
            String testResFile = "C:\\Users\\aimran\\Desktop\\backup\\loinc\\test_res_ridAndloinccode.txt";
            String loincFile = "C:\\Users\\aimran\\Desktop\\backup\\loinc\\loinc_rid_code_section.txt";
            String labSectionFile = "C:\\Users\\aimran\\Desktop\\backup\\loinc\\lab_section_rid_name.txt";

            List<String> testDefLines = null;
            try (Stream<String> stream = Files.lines(Paths.get(testDefFile))) {
                testDefLines = stream
                        .map(s -> s.replaceAll("\"\\s\"", ","))
                        .map(s -> s.substring(1, s.length() - 1))
                        .collect(Collectors.toList());
            }

            for (String value : testDefLines) {
                String[] values = value.split(",");
                TestResDef trd = new TestResDef();
                trd.rid = values[0];
                trd.loinCode = values[1];
                mappedTestDefRidLoincCode.add(trd);

            }

            List<String> testResLines = null;
            try (Stream<String> stream = Files.lines(Paths.get(testResFile))) {
                testResLines = stream
                        .map(s -> s.replaceAll("\"\\s\"", ","))
                        .map(s -> s.substring(1, s.length() - 1))
                        .collect(Collectors.toList());
            }

            for (String value : testResLines) {
                String[] values = value.split(",");
                TestResDef trd = new TestResDef();
                trd.rid = values[0];
                trd.loinCode = values[1];
                mappedTestResRidLoincCode.add(trd);

            }

            List<String> loincLines = null;
            try (Stream<String> stream = Files.lines(Paths.get(loincFile))) {
                loincLines = stream.collect(Collectors.toList());
            }

            for (String value : loincLines) {
                String[] values = value.split(",");
                TestResDef trd = new TestResDef();
                trd.rid = values[0];
                trd.loinCode = values[1];
                trd.section = values[2];
                mappedLoincCode.add(trd);

            }

            List<String> labSectionLines = null;
            try (Stream<String> stream = Files.lines(Paths.get(labSectionFile))) {
                labSectionLines = stream
                        .map(s -> s.replaceAll("\"\\s\"", ","))
                        .map(s -> s.substring(1, s.length() - 1))
                        .collect(Collectors.toList());
            }

            for (String value : labSectionLines) {
                String[] values = value.split(",");
                TestResDef trd = new TestResDef();
                trd.rid = values[0];
                trd.section = values[1];
                mappedLabSection.add(trd);

            }

        } catch (Exception e) {

        }

    }

    public static class TestResDef {

        String section;
        String loinCode;
        String rid;

        @Override
        public String toString() {
            return rid + "," + loinCode + "," + section;
        }

    }
}
