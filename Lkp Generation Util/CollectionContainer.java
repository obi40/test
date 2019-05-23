package javaapitest;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionContainer {

    public static void main(String args[]) throws Exception {
        String lkpContainerTypeInsertPath = "C:\\Users\\aimran\\Desktop\\backup\\collection container\\lkpContainerTypeInsert.sql";
        String testDefUpdatePath = "C:\\Users\\aimran\\Desktop\\backup\\collection container\\testDefUpdate.sql";
        String insertContainer = "INSERT INTO \"public\".\"lkp_container_type\" VALUES ('%1$d', '%2$s', '%3$s', '%4$s', '0', '2017-10-10 00:00:00', null, '1', null, '1');";
        String updateTestDef = "UPDATE test_definition SET collection_container_id = '%1$d' WHERE rid='%2$d';";

        List<String> collectionTypes = FileUtil.readTxtFile("C:\\\\Users\\\\aimran\\\\Desktop\\\\backup\\\\collection container\\\\collection_container_types.txt");
        collectionTypes = collectionTypes.stream().map(s -> s.substring(1, s.length() - 1)).collect(Collectors.toList());

        List<CT> ctZ = new ArrayList<>();
        Map<String, CT> map = new HashMap<>();
        for (String value : collectionTypes) {
            //String orginalValue = value;
            CT ct = new CT();
            ct.original = value;
            ct.desc = "{\"en_us\":\"" + value + "\"}";
            if (value.indexOf(":") != -1) {

                if (value.chars().filter(s -> s == ':').count() > 1) {
                    ct.code = "noCodeFound";
                    ct.name = "{\"en_us\":\"" + value + "\"}";
                } else {
                    ct.name = "{\"en_us\":\"" + (value.substring(0, value.indexOf(":")).trim()) + "\"}";
                    ct.code = value.substring(value.indexOf(":")+1).trim();
                }

            } else {
                ct.code = "noCodeFound";
                ct.name = "{\"en_us\":\"" + value + "\"}";
            }

            ctZ.add(ct);
            map.put(value, ct);
        }
        StringBuilder sb = new StringBuilder();
        int counter = 3;
        for (CT ct : ctZ) {
            sb.append(String.format(insertContainer, counter, ct.code, ct.name, ct.desc));
            sb.append("\n");
            map.get(ct.original).rid = counter;
            counter++;
        }
        FileUtil.createWriteFile(lkpContainerTypeInsertPath,sb.toString());
        //ctZ.forEach(System.out::println);
        List<String> collectionMapping = FileUtil.readTxtFile("C:\\Users\\aimran\\Desktop\\backup\\collection container\\collection_container_mapping.txt");
        collectionMapping = collectionMapping.stream()
                .map(s -> s.replaceAll("\"\\s\"", ","))
                .map(s -> s.substring(1, s.length() - 1))
                .collect(Collectors.toList());

        //collectionMapping.forEach(System.out::println);
        StringBuilder sb2 = new StringBuilder();
        for (String value : collectionMapping) {
            int rid = Integer.parseInt(value.substring(0, value.indexOf(",")));
            String key = value.substring(value.indexOf(",") + 1);

            if (map.containsKey(key)) {
                sb2.append(String.format(updateTestDef, map.get(key).rid,rid));
                sb2.append("\n");
            } else {
                System.out.println("FALSE " + key);
            }
        }
        FileUtil.createWriteFile(testDefUpdatePath,sb2.toString());

    }

    public static class CT {

        String code;
        String name;
        String desc;
        int rid;
        String original;

        @Override
        public String toString() {
            return "CT{" + "code=" + code + ", name=" + name + ", desc=" + desc + ", rid=" + rid + '}';
        }
    }
}
