package com.jdktomcat.showcase.mybatis.plus;

import com.jdktomcat.showcase.mybatis.plus.entry.MatchupRule;
import com.jdktomcat.showcase.mybatis.plus.mapper.MatchupRuleMapper;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.mybatis.plus.mapper")
public class MybatisPlusApplication implements CommandLineRunner {

    @Resource
    private MatchupRuleMapper matchupRuleMapper;

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        MatchupRule matchupRule = new MatchupRule();
        matchupRule.setRuleKey(2L);
        matchupRule.setMatchUrl("http://txdev-matchuppayapi.9uymfl.com/");
        int update = matchupRuleMapper.updateById(matchupRule);
        System.out.println(update);
    }
}