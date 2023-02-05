package com.example.javareactiveprogramming;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoJava {

    private Consumer<Object> onNext = t -> System.out.println("Received : " + t);
    private Consumer<Throwable> onError = e -> System.out.println("ERROR : " + e.getMessage());
    private Runnable onComplete = () -> System.out.println("Completed");

    private void sleepSeconds(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Mono<String> userRepository(int userId) {
        if (userId == 1) {
            return Mono.just(new Faker().name().firstName());
        } else if (userId == 2) {
            return Mono.empty(); // null
        } else
            return Mono.error(new RuntimeException("Not in the allowed range"));
    }

    private String getName() {
        System.out.println("Generating name..");
        return new Faker().name().fullName();
    }

    private Mono<String> getNameMono() {
        System.out.println("entered getName method");
        return Mono.fromSupplier(() -> {
            System.out.println("Generating name..");
            sleepSeconds(3);
            return new Faker().name().fullName();
        }).map(String::toUpperCase);
    }

    private CompletableFuture<String> getNameFuture() {
        return CompletableFuture.supplyAsync(() -> new Faker().name().fullName());
    }

    private Runnable timeConsumingProcess() {
        return () -> {
            sleepSeconds(3);
            System.out.println("Operation completed");
        };
    }

    @Test
    public void stream() {
        Stream<Integer> stream = Stream.of(1)
            .map(i -> {
                sleepSeconds(1);
                return i * 2;
            });

        System.out.println(stream);
        stream.forEach(System.out::println);
        stream.forEach(System.out::println);
        // steam pipeline 한번 사용 하면 닫힌다
    }

    @Test
    public void just() {
        // publisher
        Mono<Integer> mono = Mono.just(1);
        System.out.println(mono);
        mono.subscribe(i -> System.out.println("Received : " + i));
        mono.subscribe(i -> System.out.println("Received : " + i));
        // reactive pipeline 재사용 가능
    }

    @Test
    public void subscribe() {
        // publisher
        Mono<Integer> mono = Mono.just("ball")
            .map(String::length)
            .map(l -> l / 1)
            .map(it -> {
                System.out.println("wow : " + it);
                return it;
            });

        //
        // mono.subscribe();
        //
        // mono.subscribe(
        //     onNext, onError, onComplete
        // );
        mono.doOnNext(onNext)
            .doOnError(onError)
            .doAfterTerminate(onComplete)
            .subscribe();
    }

    @Test
    public void emptyOrError() {
        // userRepository(1)
        // userRepository(2)
        userRepository(3)
            .subscribe(
                onNext,
                onError,
                onComplete
            );
    }

    @Test
    public void fromSupplier() {
        // use just only when you have data already
        Mono.just(getName())
            .subscribe(onNext)
        ;
        // subscribe 진행 될 시기에 getName이 진행 됨. 예외를 throw할 가능성이 거의 없는 경우 Supplier권장
        Supplier<String> stringSupplier = () -> getName();
        Mono.fromSupplier(stringSupplier)
            .subscribe(onNext)
        ;

        // 예외가 발생할 것으로 예상하고 그에 따라 예외를 처리할 것임 을 의미
        Callable<String> stringCallable = () -> getName();
        Mono.fromCallable(stringCallable)
            .subscribe(onNext)
        ;
    }

    @Test
    public void supplierRefactoring() {
        getNameMono();
        String name = getNameMono()
            .subscribeOn(Schedulers.boundedElastic())
            .block();
        System.out.println(name);
        getNameMono();
        sleepSeconds(4);
    }

    @Test
    public void async() {
        getNameMono()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext);
        getNameMono()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext);
        getNameMono()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext);

        sleepSeconds(4);
    }

    @Test
    public void fromFuture() {
        Mono.fromFuture(getNameFuture())
            .subscribe(onNext);
    }

    @Test
    public void fromRunnable() {
        Mono.fromRunnable(timeConsumingProcess())
            .subscribe(
                onNext,
                onError,
                () -> System.out.println("process is done. Sending emails...")
            );
    }
}
