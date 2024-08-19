package com.jdktomcat.showcase.spring.webflux.demo;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class ReactorDemo {


    public static void main(String[] args) {
        String[] strs = {"1","2","3","4","5","6","7","8","9"};

        Subscriber<Integer> subscriber = new Subscriber<>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }
            @Override
            public void onNext(Integer integer) {
                System.out.println("接收到数据："+integer);
                this.subscription.request(1);
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                this.subscription.cancel();
            }
            @Override
            public void onComplete() {
                System.out.println("处理完成");
            }
        };
        Flux.fromArray(strs).map(Integer::parseInt).subscribe(subscriber);
    }

}
