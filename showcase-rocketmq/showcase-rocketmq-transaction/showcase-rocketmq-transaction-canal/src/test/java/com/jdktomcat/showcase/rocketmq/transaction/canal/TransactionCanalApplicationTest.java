package com.jdktomcat.showcase.rocketmq.transaction.canal;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class TransactionCanalApplicationTest {

    private ConfigurableApplicationContext context;


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(TransactionCanalApplicationTest.class.getName()+".*")
                .warmupIterations(1).measurementIterations(20).forks(2).build();
        new Runner(options).run();
    }

    /**
     * setup初始化容器的时候只执行一次
     */
    @Setup(Level.Trial)
    public void init(){
        context = SpringApplication.run(TransactionCanalApplication.class);
    }

    @Benchmark
    public void testSetIfAbsent() {
    }
}