package com.jdktomcat.showcase.json;

import com.jdktomcat.showcase.json.entry.MatchAmountDto;
import com.jdktomcat.showcase.json.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<MatchAmountDto> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MatchAmountDto matchAmountDto = new MatchAmountDto();
            matchAmountDto.setMerchantID("" + i);
            matchAmountDto.setMatchAmountList("amount" + i);
            matchAmountDto.setMatchAmountListNew("newAmount" + i);
            list.add(matchAmountDto);
        }
        Object value = ReflectionUtil.getFieldValue(list, "merchantID");
        System.out.println(value);
    }
}
