package com.butovanton.chanelreaserch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SharedMutableState {

    @Test
    fun theProblem() = runBlocking {
        var counter = 0
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++
            }
        }
        println("Problem completed Counter = $counter")
    }

    @Test
    fun simpleSolution() = runBlocking {
        val counterContext = newSingleThreadContext("CounterContext")
        var counter = 0
        val time = measureTimeMillis {
            withContext(counterContext) {
                massiveRun {
                    counter++
                }
            }
        }
        println("Simple thread completed $counter actions in $time ms")
    }

    @Test
    fun atomicSolution() = runBlocking {
        val counter = AtomicInteger(0)
        val time = measureTimeMillis {
            massiveRun {
                counter.incrementAndGet()
            }
        }
        assertEquals(100000, counter.get())
        println("Atomic completed $counter actions time $time ms")
    }

    @Test
    fun sharedSolution() = runBlocking {
        var counter = 0
        val time = measureTimeMillis {
            withContext(Dispatchers.Default) {
                massiveRunShared()
                    .collect {
                        it.collect {
                            counter = it
                        }
                    }
            }
        }
        assertEquals(100000, counter)
        println("Flow completed $counter actions in $time ms")
    }

    @Test
    fun chanelSolution() = runBlocking {
        val chanel = Channel<Int>()
        val job = Job()
        val time = measureTimeMillis {
            withContext(job + Dispatchers.Default) {
                val k = 2 // times an action is repeated by each coroutine
                (1..k).forEach {
                    launch(job) {
                        chanel.chanelSender(100000, job)
                    }
                }
                chanel.send(0)
            }
            job.join()
            chanel.close()
        }
        println("Channel completed actions in $time ms")
    }
}