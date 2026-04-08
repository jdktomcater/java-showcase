package com.jdktomcat.showcase.legacy.system.component.httpclient.util;

import org.apache.commons.lang.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * 对象、字符、时间验证
 */
public class CommonUtil {

    /**
     * 整数
     */
    private static String isInteger = "^[-\\+]?[\\d+.+\\d]*$";


    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile(isInteger);
        return pattern.matcher(str).matches();
    }

    /**
     * 判断对象为空
     *
     * @param obj 对象名
     * @return 是否为空
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (obj == null || obj == "") {
            return true;
        }
        if ((obj instanceof List)) {
            return ((List) obj).isEmpty();
        }
        if ((obj instanceof String)) {
            return "".equals(((String) obj).trim());
        }
        if ((obj instanceof Map)) {
            return ((Map) obj).isEmpty();
        }
        return false;
    }

    /**
     * 判断对象不为空
     *
     * @param obj 对象名
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断文件是否是图片
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("image png tif jpg jpeg bmp");
        String mimetype = mtftp.getContentType(file);
        String type = mimetype.split("/")[0];
        return "image".equals(type);
    }

    /**
     * 根据图片后缀名判断该图片是否为图片
     *
     * @param fileName
     * @return
     */
    public static boolean isImageByName(String fileName) {
        String[] imagType = {"gif", "jpeg", "bmp", "png", "x-png", "x-bmp", "x-ms-bmp", "jpg", "svg"};
        List<String> imageTyepLists = Arrays.asList(imagType);
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (imageTyepLists.contains(type)) {
            return true;
        }
        return false;
    }

    /**
     * md5字符串加密
     *
     * @param s
     * @return
     */
    public static String MD5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 数组转list
     *
     * @param data
     * @return
     */
    public static List<Integer> revertArrayToList(String[] data) {
        if (data == null || data.length < 0) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            list.add(Integer.parseInt(data[i]));
        }
        return list;

    }

    /**
     * 日期推迟
     *
     * @param count
     * @return
     */
    public static String countDate(Integer count) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, count);
        return sf.format(c.getTime());
    }

    /**
     * 时间校验
     *
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // log.error("error:",e);
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 获取当前时间(string 格式)
     *
     * @return
     */
    public static String parseDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 检查n个String是否为空
     *
     * @param params 参数组成的数组
     * @return 其中有一个为空则返回true，全不为空则返回false
     * @date 2020/10/18 11:11
     * @author Cofess
     */
    public static boolean checkParamsIsBlank(String... params) {
        if (params == null) {
            return true;
        }
        //当参数类型不是String或者不是Object时 都返回true
        for (String s : params) {
            if (StringUtils.isBlank(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查n个Object参数是否为空，注意:主要用于Object == null的校验
     *
     * @param params 参数组成的数组
     * @return 其中有一个为空则返回true，全不为空则返回false
     * @date 2020/10/18 11:11
     * @author Cofess
     */
    public static boolean checkParamsIsBlank(Object... params) {
        if (params == null) {
            return true;
        }
        for (Object obj : params) {
            if (obj == null) {
                return true;
            }
        }
        // 检查全非空返回false
        return false;
    }

    /**
     * 根据key连接参数
     *
     * @param key    连接参数的字符
     * @param params 参数数组
     * @return
     */
    public static String joinParamsByKey(String key, String... params) {
        String value = StringUtils.join(params, key);
        return value;
    }

    /**
     * unicode编码转中文
     *
     * @param content unicode内容
     * @return 中文内容
     */
    public static String unicodeConvertChinese(String content) {
        StringBuffer str = new StringBuffer();
        String[] hex = content.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            str.append((char) data);
        }
        return str.toString();
    }

    /**
     * 获取银行名称：如有扩展，请在此扩展
     *
     * @param bankCode 银行代码
     * @return 银行名字
     */
    public static String getBankName(String bankCode) {
        switch (bankCode) {
            case "ICBC":
                return "工商银行";
            case "CMB":
                return "招商银行";
            case "CCB":
                return "建设银行";
            case "BOC":
                return "中国银行";
            case "ABC":
                return "农业银行";
            case "BCM":
                return "交通银行";
            case "SPDB":
                return "浦发银行";
            case "CGB":
                return "广发银行";
            case "CEB":
                return "光大银行";
            case "CIB":
                return "兴业银行";
            case "PAB":
                return "平安银行";
            case "CMBC":
                return "民生银行";
            case "HXB":
                return "华夏银行";
            case "PSBC":
                return "邮储银行";
            case "NBB":
                return "宁波银行";
            case "BCCB":
                return "北京银行";
            case "CZB":
                return "浙商银行";
            case "GZCB":
                return "广州市商业银行";
            case "BCS":
                return "长沙银行";
            case "BGB":
                return "广西北部湾银行";
            case "GDRC":
                return "广东农信";
            case "GLBANK":
                return "桂林银行";
            case "BOD":
                return "东莞银行";
            case "HZCB":
                return "杭州银行";
            case "JSBANK":
                return "江苏银行";
            case "GXRCC":
                return "广西农信";
            case "JLBANK":
                return "吉林银行";
            case "NJCB":
                return "南京银行";
            case "HF":
                return "恒丰银行";
            case "SZNS":
                return "深圳农商银行";
            case "CNCB":
                return "中信银行";
            case "shb":
                return "上海银行";
            case "HRBB":
                return "哈尔滨银行";
            case "QDCCB":
                return "青岛银行";
            case "HNB":
                return "海南农信";
            case "GDNY":
                return "广东南粤银行";
            case "LJBANK":
                return "龙江银行 ";
            case "ZYCBANK":
                return "贵州银行";
            case "RXVB":
                return "融兴村镇银行";
            case "GCB":
                return "广州银行";
            case "ZJNX":
                return "浙江省农村信用社联合社";
            case "DLB":
                return "大连银行";
            case "SHBANK":
                return "上海银行";

            default:
                return bankCode;
        }
    }
}
