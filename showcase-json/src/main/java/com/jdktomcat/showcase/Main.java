package com.jdktomcat.showcase;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    private static void handle(String filePath) throws IOException {
        Scanner scanner = new Scanner(new File(filePath), String.valueOf(StandardCharsets.UTF_8));
        StringBuilder stringBuffer = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuffer.append(scanner.next());
        }
        JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
        JSONObject hitsObject = jsonObject.getJSONObject("hits");
        if (hitsObject != null && hitsObject.containsKey("hits")) {
            JSONArray jsonArray = hitsObject.getJSONArray("hits");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject subObject = jsonArray.getJSONObject(i);
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
