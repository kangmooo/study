package com.example.reactiveprogramming;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

public class FluxJava {

    @Test
    public void create() {
        Flux.create(fluxSink -> {

            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();
        }).subscribe(Util.subscriber());

        Flux.create(fluxSink -> {

            for (int i = 0; i < 10; i++) {
                fluxSink.next(Util.faker().country().name());
            }
            fluxSink.complete();
        }).subscribe(Util.subscriber());
    }
}
