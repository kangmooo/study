package com.example.javareactiveprogramming

import com.github.javafaker.Faker
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Stream

class MonoKotlin {

    private fun onNext(): Consumer<Any> = Consumer { println("Received : $it") }
    private fun onError(): Consumer<Throwable> = Consumer { println("ERROR : " + it.message) }
    private fun onComplete(): Runnable = Runnable { println("Completed") }

    @Test
    fun stream() {
        val stream = Stream.of(1).map { i: Int ->
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            i * 2
        }
        println(stream)
        stream.forEach { x: Int? -> println(x) }
        stream.forEach { x: Int? -> println(x) }
        // steam 파이프 라인은 한번 사용 하면 닫힌다
    }

    @Test
    fun just() {
        // publisher
        val mono = 1.toMono()
        println(mono)
        mono.subscribe { i: Int -> println("Received : $i") }
        mono.subscribe { i: Int -> println("Received : $i") }
    }

    @Test
    fun subscribe() {
        // publisher
//        val mono = Mono.just("ball")
        val mono = "ball".toMono()
            .map { obj: String -> obj.length }
            .map { l: Int -> l / 1 }
            .map { it.apply { println("wow : $it") } }

        mono.subscribe()

        mono.subscribe(
            onNext(), onError(), onComplete()
        )
        mono.doOnNext(onNext())
            .doOnError(onError())
            .doAfterTerminate(onComplete())
            .subscribe()
    }

    @Test
    fun emptyOrError() {
        fun userRepository(userId: Int): Mono<String> = when (userId) {
            1 -> Mono.just(Faker().name().firstName())
            2 -> Mono.empty()
            else -> Mono.error(RuntimeException("Not in the allowed range"))
        }

        userRepository(1).subscribe(
            onNext(), onError(), onComplete()
        )
    }

    @Test
    fun fromSupplier() {
        fun getName(): String {
            println("Generating name..")
            return Faker().name().fullName()
        }

        getName().toMono()
            .subscribe(onNext())
    }

    @Test
    fun supplierRefactoring() {
        fun getName(): Mono<String> {
            println("entered getName method")
            return Mono.fromSupplier {
                println("Generating name..")
                Thread.sleep(3000)
                Faker().name().fullName()
            }.map { it.uppercase() }
        }

        getName()
        val name: String? = getName()
            .subscribeOn(Schedulers.boundedElastic())
            .block()
        println(name)
        getName()

        Thread.sleep(4000)
    }

    @Test
    fun async() {
        fun getName(): Mono<String> {
            println("entered getName method")
            return Mono.fromSupplier {
                println("Generating name..")
                Thread.sleep(3000)
                Faker().name().fullName()
            }.map { it.uppercase() }
        }
        getName()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext())
        getName()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext())
        getName()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(onNext())
        Thread.sleep(4000)
    }
    @Test
    fun fromFuture() {
        fun getName(): CompletableFuture<String> {
            return CompletableFuture.supplyAsync(Supplier { Faker().name().fullName() })
        }
        getName().toMono()
            .subscribe(onNext())
        Thread.sleep(1000)
    }

    @Test
    fun fromRunnable() {

        fun timeConsumingProcess() = Runnable {
            Thread.sleep(3)
            println("Operation completed")
        }

        timeConsumingProcess().toMono()
            .subscribe(
                onNext(),
                onError(),
                { println("process is done. Sending emails...") }
            )
    }
}