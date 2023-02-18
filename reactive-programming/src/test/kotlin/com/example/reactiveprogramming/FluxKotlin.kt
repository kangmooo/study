package com.example.reactiveprogramming

import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicReference

class FluxKotlin {

    @Test
    fun just() {
        val flux =
            Flux.just(1, 2, 3, "a", Util.faker().name().fullName())
//            Flux.empty<Any>()
        flux.subscribe(
            Util.onNext(),
            Util.onError(),
            Util.onComplete(),
        )
    }

    @Test
    fun multipleSubscribers() {
        val integerFlux = Flux.just(1, 2, 3, 4)
        val evenFlux = integerFlux.filter { it % 2 == 0 }
        integerFlux.subscribe { println("Sub 1 : $it") }
        evenFlux.subscribe { println("Sub 2 : $it") }
    }

    @Test
    fun fromArray() {
        val strings = listOf("a", "b", "c")
        Flux.fromIterable(strings)
            .subscribe(Util.onNext())
        val arr = arrayOf(2, 5, 7, 8)
        Flux.fromArray(arr)
            .subscribe(Util.onNext())
    }

    @Test
    fun fromStream() {
        val list = listOf(1, 2, 3, 4, 5)
        val stream = list.stream()
//        stream.forEach { println(it) }
//        stream.forEach { println(it) } // stream has already been operated upon or closed

        val integerFlux1 = Flux.fromStream(stream)
        integerFlux1.subscribe(Util.onNext(), Util.onError(), Util.onComplete())
        integerFlux1.subscribe(
            Util.onNext(),
            Util.onError(),
            Util.onComplete()
        ) // stream has already been operated upon or closed

        val integerFlux2 = Flux.fromStream { list.stream() }
        integerFlux2.subscribe(Util.onNext(), Util.onError(), Util.onComplete())
        integerFlux2.subscribe(Util.onNext(), Util.onError(), Util.onComplete())

    }

    @Test
    fun fromRange() {
        Flux.range(3, 10)
            .map { Util.faker().name().fullName() }
            .subscribe(Util.onNext())
    }

    @Test
    fun log() {
        Flux.range(3, 10)// publisher
            .log()
            .map { Util.faker().name().fullName() } // subscriber
            .log()
            .subscribe(Util.onNext())
    }

    @Test
    fun customSubscriber() {
        val atomicReference = AtomicReference<Subscription>()
        Flux.range(1, 20)
            .subscribe(
                object : org.reactivestreams.Subscriber<Int> {
                    override fun onSubscribe(subscription: Subscription) {
                        println("Receiced Sub : $subscription")
                        atomicReference.set(subscription)
                    }

                    override fun onNext(i: Int) {
                        println("onNext : $i")
                    }

                    override fun onError(throwable: Throwable) {
                        println("onError : ${throwable.message}")
                    }

                    override fun onComplete() {
                        println("onComplete")
                    }
                }
            )
        Util.sleepSeconds(3)
        atomicReference.get().request(3)
        Util.sleepSeconds(5)
        atomicReference.get().request(3)
        Util.sleepSeconds(5)
        atomicReference.get().request(3)
        println("going to cancel")
        atomicReference.get().cancel()
        Util.sleepSeconds(5)
    }

    @Test
    fun fluxVsList() {
//        val names = NameGenerator.getNames(5)
//        println(names)

        NameGenerator.getNames(5)
            .subscribe(Util.onNext())
    }
}

class NameGenerator {
    companion object {
//        fun getNames(count: Int) : List<String> = (1..count).map { getName() }
//        private fun getName(): String = Util.sleepSeconds(1).run {
//            Util.faker().name().fullName()
//        }

        fun getNames(count: Int): Flux<String> = Flux.range(1, count).map { getName() }
        private fun getName(): String = Util.sleepSeconds(1).run {
            Util.faker().name().fullName()
        }
    }
}







