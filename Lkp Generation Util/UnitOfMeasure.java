package javaapitest;

import java.util.*;
import java.util.stream.Collectors;

public class UnitOfMeasure {

    public static void main(String args[]) throws Exception {
        String test_result_distinct = "C:\\Users\\aimran\\Desktop\\backup\\unit of measure\\test_result_distinct.txt";
        String test_result_map = "C:\\Users\\aimran\\Desktop\\backup\\unit of measure\\test_result_map.txt";

        String insertUnitMeasure = "INSERT INTO \"public\".\"lab_units\" VALUES ('%1$s', '%1$s', '%2$d', null, '2017-10-10 11:33:58.105253', null, null, null, '0');";
        String updateTestDef = "UPDATE test_result SET unit_of_measure_id = '%1$d' WHERE rid='%2$d';";

        List<String> measureTypes = FileUtil.readTxtFile("C:\\Users\\aimran\\Desktop\\backup\\unit of measure\\all_measures_from_table.txt");
        measureTypes = measureTypes.stream()
                .map(s -> s.replaceAll("\"\\s\"", ","))
                .map(s -> s.toLowerCase())
                .map(s -> s.substring(1, s.length() - 1)).collect(Collectors.toList());

        Map<String, Integer> map = new HashMap<>();
        for (String value : measureTypes) {
            map.put(value.substring(value.indexOf(",") + 1), Integer.parseInt(value.substring(0, value.indexOf(","))));
        }

        List<String> measureTypesMapped = FileUtil.readTxtFile(test_result_map);
        measureTypesMapped = measureTypesMapped.stream()
                .map(s -> s.replaceAll("\"\\s\"", ","))
                .map(s -> s.substring(1, s.length() - 1)).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String value : measureTypesMapped) {
            String rid = value.substring(0, value.indexOf(","));
            String key = value.substring(value.indexOf(",") + 1);
            if (map.containsKey(key.toLowerCase())) {
                sb.append(String.format(updateTestDef, map.get(key.toLowerCase()), Integer.parseInt(rid)));
                sb.append("\n");
            } else {
                System.out.println("ELSE " + key);
            }

        }
        
        FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\backup\\unit of measure\\update_stat_test_result.sql", sb.toString());

    }
}
