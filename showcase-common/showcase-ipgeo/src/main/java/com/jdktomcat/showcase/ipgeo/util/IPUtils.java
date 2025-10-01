package com.jdktomcat.showcase.ipgeo.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.net.InetAddress;

/**
 * @author Tracy
 * @ClassName AccessObjectUtil
 * @Description 获取请求对象参数工具类
 * @Date 2019/02/01
 */
public class IPUtils {

    /**
     * 全局静态变量，DatabaseReader，保证类加载时加载一次
     */

    private static DatabaseReader DB_READER = null;


    /**
     * @param ip
     * @return
     * @throws Exception
     * @description: 获得国家编码
     */
    public static String getCountryCode(String ip) throws Exception {
        try {
            return DB_READER.country(InetAddress.getByName(ip)).getCountry().getIsoCode();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取省份
     *
     * @param ip ip
     * @return 省份
     */
    public static String getProvinceCode(String ip) {
        try {
            if (StringUtils.isNotEmpty(ip)) {
                CityResponse response = DB_READER.city(InetAddress.getByName(ip));
                Country country = response.getCountry();
                Subdivision subdivision = response.getMostSpecificSubdivision();
                //国家
                String countryName = country.getNames().get("zh-CN");
                String countryIsoCode = country.getIsoCode();
                //省份
                String provinceName = subdivision.getNames().get("zh-CN");
                String provinceCode = subdivision.getIsoCode();
                if ("CN".equals(countryIsoCode) && StringUtils.isNotEmpty(provinceCode)) {
                    return provinceCode;
                } else if ("HK".equals(countryIsoCode) || "MO".equals(countryIsoCode)) {
                    //香港和澳门
                    return countryIsoCode;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return "";
    }


    static {
        try {
            ClassPathResource resource = new ClassPathResource("/GeoLite2-City.mmdb");
            InputStream inputStream = resource.getInputStream();
            DB_READER = new DatabaseReader.Builder(inputStream).build();
        } catch (Exception ignored) {
        }
    }
}
