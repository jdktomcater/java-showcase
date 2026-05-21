package com.jdktomcat.showcase.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContentHandler {


    public static void main(String[] args) throws FileNotFoundException {
        String inputFilePath = "E:\\workspace\\github\\jdktomcater\\java-showcase\\showcase-common\\showcase-json\\src\\main\\resources\\ target.txt";
        String outputFilePath = "E:\\workspace\\github\\jdktomcater\\java-showcase\\showcase-common\\showcase-json\\src\\main\\resources\\ target_sql.txt";
        Scanner scanner = new Scanner(new FileInputStream(inputFilePath));
        String sql = "select merchant_order_id,platform_order_id\n" +
                "from matchup_pay.mp_withdraw_order where lock_time < '2026-05-17 01:00:00' and lock_account in (";
        Pattern pattern = Pattern.compile("UEWF_\\d+_\\d+");
        Set<String> targets = new HashSet<>();

        while (scanner.hasNext()) {
            String content = scanner.nextLine();
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String[] params = matcher.group().split("_");
                targets.add(params[2]);
            }
        }
        PrintWriter printWriter = new PrintWriter(outputFilePath);
        int i=0;
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        for (String target : targets) {
            sb.append("\"").append(target).append("\",");
            i++;
            if(i==500){
                sb.deleteCharAt(sb.length()-1);
                sb.append(");");
                printWriter.println(sb);
                printWriter.println();
                printWriter.flush();
                i=0;
                sb = new StringBuilder();
                sb.append(sql);
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        printWriter.println(sb);
        printWriter.flush();
        printWriter.close();
    }
}
