package com.jdktomcat.showcase.common.opencsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainHandler {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("E:\\workspace\\github\\jdktomcater\\java-showcase\\showcase-common\\showcase-opencsv\\src\\main\\resources\\log-data.txt");
        List<String> lines = Files.readAllLines(path);
        System.out.println("总行数：" + lines.size());
        Map<String, Integer> map = new HashMap<>();
        String target = "merchant_order_id=";
        for (String line : lines) {
            int index = line.indexOf(target) + target.length();
            int endIndex = line.indexOf(",", index);
            String merchantOrderId = line.substring(index, endIndex);
            map.put(merchantOrderId, map.getOrDefault(merchantOrderId, 0) + 1);
        }
        System.out.println("总单数：" + map.size());
//        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
