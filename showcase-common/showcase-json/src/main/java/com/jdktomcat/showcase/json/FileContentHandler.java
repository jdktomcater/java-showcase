package com.jdktomcat.showcase.json;

import com.jdktomcat.showcase.json.entry.WithdrawOrders;
import com.jdktomcat.showcase.json.util.MD5Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class FileContentHandler {


    public static void main(String[] args) throws FileNotFoundException {
        String inputFilePath = "E:/install/sites/jmeter/plans/data/withdraw_M_K_orders_typayorders_payamt.txt";
        String outputFilePath = "E:/install/sites/jmeter/plans/data/withdraw_M_K_orders_typayorders_payamt_new.txt";
        Scanner scanner = new Scanner(new FileInputStream(inputFilePath));
        int i = 0;
        List<WithdrawOrders> dataList = new ArrayList<>();
        StringBuilder merchantIds = new StringBuilder();
        StringBuilder merchantOrderIds = new StringBuilder();
        StringBuilder typayOrderIds = new StringBuilder();
        StringBuilder signs = new StringBuilder();
        BigDecimal paidAmount = new BigDecimal(0);
        while (scanner.hasNext()) {
            String content = scanner.nextLine();
            String[] params = content.split(",");
            merchantIds.append(params[0]).append(",");
            merchantOrderIds.append(params[2]).append(",");
            typayOrderIds.append(params[3]).append(",");
            String signP = "merchant_id=" + params[0] + "&merchant_order_id=" + params[2] + "&pay_paid_amt=" + paidAmount + "&request_type=" + 3 + "&key=" + params[1];
            signs.append(MD5Util.encryption(signP).toLowerCase().trim()).append(",");
            if ((++i) % 10 == 0) {
                WithdrawOrders withdrawOrders = new WithdrawOrders();
                withdrawOrders.setPay_paid_amt(new BigDecimal(0));
                withdrawOrders.setRequest_type(3);
                withdrawOrders.setMerchant_ids(merchantIds.subSequence(0, merchantIds.length() - 1).toString());
                withdrawOrders.setMerchant_order_ids(merchantOrderIds.subSequence(0, merchantOrderIds.length() - 1).toString());
                withdrawOrders.setTypay_order_ids(typayOrderIds.subSequence(0, typayOrderIds.length() - 1).toString());
                withdrawOrders.setSigns(signs.subSequence(0, signs.length() - 1).toString());
                dataList.add(withdrawOrders);
                merchantIds = new StringBuilder();
                merchantOrderIds = new StringBuilder();
                typayOrderIds = new StringBuilder();
                signs = new StringBuilder();
            }
        }
        if (!merchantIds.isEmpty()) {
            WithdrawOrders withdrawOrders = new WithdrawOrders();
            withdrawOrders.setPay_paid_amt(new BigDecimal(0));
            withdrawOrders.setRequest_type(3);
            withdrawOrders.setMerchant_ids(merchantIds.subSequence(0, merchantIds.length() - 1).toString());
            withdrawOrders.setMerchant_order_ids(merchantOrderIds.subSequence(0, merchantOrderIds.length() - 1).toString());
            withdrawOrders.setTypay_order_ids(typayOrderIds.subSequence(0, typayOrderIds.length() - 1).toString());
            withdrawOrders.setSigns(signs.subSequence(0, signs.length() - 1).toString());
            dataList.add(withdrawOrders);
        }
        PrintWriter printWriter = new PrintWriter(outputFilePath);
        for (WithdrawOrders withdrawOrders : dataList) {
            String content = withdrawOrders.getMerchant_ids() + "=" +
                    withdrawOrders.getMerchant_order_ids() + "=" +
                    withdrawOrders.getTypay_order_ids() + "=" +
                    withdrawOrders.getSigns() + "=" +
                    withdrawOrders.getRequest_type() + "=" +
                    withdrawOrders.getPay_paid_amt();
            printWriter.println(content);
            printWriter.flush();
        }
        printWriter.close();
    }
}
