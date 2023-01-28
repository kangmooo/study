package com.example.javareactiveprogramming

import com.github.javafaker.Faker
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Stream

class MonoTest {

    private fun onNext(): Consumer<Any> = Consumer { println("Received : $it") }
    private fun onError(): Consumer<Throwable> = Consumer { println("ERROR : " + it.message) }
    private fun onComplete(): Runnable = Runnable { println("Completed") }

    @Test
    fun `Stream test`() {
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
        println(stream)
    }

    @Test
    fun `Mono just test`() {
        // publisher
//        val mono = Mono.just(1)
        val mono = 1.toMono()
        println(mono)
        mono.subscribe { i: Int -> println("Received : $i") }
    }

    @Test
    fun `Mono subscribe test`() {
        // publisher
//        val mono = Mono.just("ball")
        val mono = "ball".toMono().map { obj: String -> obj.length }.map { l: Int -> l / 1 }

        mono.subscribe()

        mono.subscribe(
            onNext(), onError(), onComplete()
        )
        mono.doOnNext(onNext()).doOnError(onError()).doAfterTerminate(onComplete()).subscribe()
    }

    @Test
    fun `Mono Empty || Error`() {
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
    fun `Mono fromSupplier`() {
        fun getName(): String {
            println("Generating name..")
            return Faker().name().fullName()
        }

        // use just only when you have data already
        // Mono<String> mono = Mono.just(getName());

//        val mono = getName().toMono()
        val stringSupplier = Supplier<String> { getName() }
        val mono = Mono.fromSupplier(stringSupplier)
        mono.subscribe(onNext())

        val stringCallable = Callable<String> { getName() }
        Mono.fromCallable(stringCallable)
            .subscribe(onNext())
    }

    @Test
    fun `Mono Supplier Refactoring`() {
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
    fun `Mono fromFuture`() {
        fun getName(): CompletableFuture<String> {
            return CompletableFuture.supplyAsync(Supplier { Faker().name().fullName() })
        }
        Mono.fromFuture(getName())
            .subscribe(onNext())
        Thread.sleep(1000)
    }

    @Test
    fun `Mono fromRunnable`() {

        fun timeConsumingProcess() = Runnable {
            Thread.sleep(3)
            println("Operation completed")
        }

        Mono.fromRunnable<Any>(timeConsumingProcess())
            .subscribe(
                onNext(),
                onError(),
                { println("process is done. Sending emails...") }
            )

        timeConsumingProcess().toMono()
            .subscribe(
                onNext(),
                onError(),
                { println("process is done. Sending emails...") }
            )
    }
}