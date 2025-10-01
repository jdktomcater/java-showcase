package com.jdktomcat.showcase.ipgeo;

import com.jdktomcat.showcase.ipgeo.util.IPUtils;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        String ip = "103.24.52.37";
        System.out.println(IPUtils.getCountryCode(ip));
        System.out.println(IPUtils.getProvinceCode(ip));
    }
}