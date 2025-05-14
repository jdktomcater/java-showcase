package com.jdktomcat.showcase;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.PropertyFilter;
import com.alibaba.fastjson2.filter.ValueFilter;

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

    class Entry {
        private Integer merchant_id;

        public Integer getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(Integer merchant_id) {
            this.merchant_id = merchant_id;
        }
    }

    public static void main(String[] args) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name","timmy");
//        JSONObject sub = new JSONObject();
//        sub.put("age","25");
//        jsonObject.put("sub",sub);
//        ValueFilter valueFilter = (o, s, o1) -> {
//            if("age".equals(s)){
//                return "27";
//            }
//            return o1;
//        };
//        System.out.println(JSON.toJSONString(jsonObject, valueFilter));
//        System.out.println(JSON.toJSONString(jsonObject));

        String json = "{\"merchant_id\":\"200058\"}";
        Entry entry = JSON.parseObject(json,Entry.class);
        System.out.println(entry.getMerchant_id());

    }

}
