package javaapitest;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.*;

public class MachineMethod {

    public static void main(String args[]) throws Exception {

        String machineTypesPath = "C:\\Users\\aimran\\Desktop\\machine_methods.txt";
        String machineTypesMappingRIDPath = "C:\\Users\\aimran\\Desktop\\test_def_matching_rid_with_machine_method - Copy.txt";
        String resultInsertMachine = "C:\\Users\\aimran\\Desktop\\insertMachine.sql";
        String resultUpdaTetestDef = "C:\\Users\\aimran\\Desktop\\updateTestDef.sql";

        String insertMachine = "INSERT INTO \"public\".\"lkp_machine_method\" VALUES ('%1$d', '%2$s', '%3$s', '%4$s', '0', '2017-07-30 13:29:04', null, '1', null, '1');";
        String updateTestDef = "UPDATE test_definition SET machine_method_id = '%1$d' WHERE rid='%2$d';";

        List<String> lines = new ArrayList<>();
        List<MM> mmZ = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(machineTypesPath))) {

            lines = stream.collect(Collectors.toList());
        }

        Map<String, MM> machineTypesMAP = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String originalValue = lines.get(i).replaceAll("\"", "");

            String value = originalValue;

            MM mm = new MM();
            if (value.indexOf("(") != -1) {
                value = value.substring(0, value.indexOf("("));
                if (value.length() >= 255) {
                    value = value.substring(0, 255);
                }
                mm.code = lines.get(i).substring(lines.get(i).indexOf("("), lines.get(i).indexOf(")") + 1);
                if (mm.code.length() >= 50) {
                    mm.code = mm.code.substring(0, 50);
                }
            } else {
                if (value.length() >= 255) {
                    value = value.substring(0, 255);
                }
                mm.code = "noCodeFound";

            }

            mm.name = value;

            mm.desc = originalValue;

            machineTypesMAP.put(originalValue, mm);

            mmZ.add(mm);

        }
        File insertMachineFile = new File(resultInsertMachine);
        insertMachineFile.delete();
        insertMachineFile.createNewFile();
        try (FileWriter fw = new FileWriter(insertMachineFile)) {
            StringBuilder sb = new StringBuilder();
            int rid = 10;
            for (MM mm : mmZ) {
                sb.append(String.format(insertMachine, rid,mm.name,mm.code,mm.desc));
                sb.append("\n");
                
                machineTypesMAP.get(mm.desc).rid = rid;
                rid++;
                
            }
           fw.write(sb.toString());
        }

        List<String> linesRID = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(machineTypesMappingRIDPath))) {

            linesRID = stream
                    .map(s -> s.replaceAll("\"\\s\"", ","))
                    .map(s -> s.substring(1, s.length() - 1))
                    .collect(Collectors.toList());
        }
        StringBuilder sb2 = new StringBuilder();
        for (String val : linesRID) {
            System.out.println(val);
            String original = val;
            val = val.substring(val.indexOf(",") + 1);
            int testDefRid=Integer.parseInt(original.substring(0,original.indexOf(",")));
            
            if (machineTypesMAP.containsKey(val)) {
                sb2.append(String.format(updateTestDef,machineTypesMAP.get(val).rid, testDefRid));
                sb2.append("\n");
            } else {
                throw new Exception("sad " + val + "\n" + original);

            }
        }
        File insertUpdateTestFile = new File(resultUpdaTetestDef);
        insertUpdateTestFile.delete();
        insertUpdateTestFile.createNewFile();
        try (FileWriter fw = new FileWriter(insertUpdateTestFile)) {
            fw.write(sb2.toString());
        }
        //System.out.println(Arrays.toString(machineTypesMAP.entrySet().toArray()));
        //linesRID.forEach(System.out::println);

    }

    public static class MM {

        String code;
        String name;
        String desc;
        int rid;

        @Override
        public String toString() {
            return "MM{" + "code=" + code + ", name=" + name + ", desc=" + desc + ", rid=" + rid + '}';
        }



    }
}

