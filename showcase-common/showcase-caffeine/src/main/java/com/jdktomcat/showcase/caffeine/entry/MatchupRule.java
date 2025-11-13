package com.jdktomcat.showcase.caffeine.entry;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 撮合规则
 */
@Data
public class MatchupRule {

    /**
     * 标识
     */
    private int id;

    /**
     * 名称
     */
    private String name;


    /**
     * 模拟生成相关撮合列表
     *
     * @param type 类型
     * @return 撮合列表
     */
    public static List<MatchupRule> getMatchupRules(int type) {
        List<MatchupRule> matchupRules = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        for (int i = 0; i < type; i++) {
            MatchupRule matchupRule = new MatchupRule();
            matchupRule.setId(i);
            matchupRule.setName("matchupRule-" + i + ":" + sdf.format(new Date()));
            matchupRules.add(matchupRule);
        }
        return matchupRules;
    }
}
