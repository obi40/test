package javaapitest;

import java.util.*;
import java.util.stream.Collectors;

public class ResultValueType {

    public static void main(String args[]) throws Exception {
        String test_result_distinct = "C:\\Users\\aimran\\Desktop\\1.txt";
        
        String updateTestDef = "UPDATE test_result SET result_value_type_id = '%1$d' WHERE rid='%2$d';";

        List<String> measureTypes = FileUtil.readTxtFile(test_result_distinct);
        measureTypes = measureTypes.stream()
                .map(s -> s.replaceAll("\"\\s\"", ","))
                .map(s -> s.substring(1, s.length() - 1)).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String value : measureTypes) {
            Integer rid = Integer.parseInt(value.substring(0, value.indexOf(",")));
            Integer rid2 = Integer.parseInt(value.substring(value.indexOf(",")+1));
            sb.append(String.format(updateTestDef, rid2,rid));
            sb.append("\n");
        }
        FileUtil.createWriteFile("C:\\Users\\aimran\\Desktop\\1_Result.txt", sb.toString());

        //test_result_distinct_types.forEach(System.out::println);

        //System.out.println(Arrays.toString(measureTypeMap.entrySet().toArray()));
        
    }
}
