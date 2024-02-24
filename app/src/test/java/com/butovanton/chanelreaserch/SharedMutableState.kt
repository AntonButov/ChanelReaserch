package com.butovanton.chanelreaserch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
        println("Counter = $counter")
    }

    @Test
    fun atomicSolution() = runBlocking {
        val counter = AtomicInteger(0)
            massiveRun {
                counter.incrementAndGet()
        }
        assertEquals(100000, counter.get())
        println("Atomic completed $counter actions ")
    }

    @Test
    fun sharedSolution() = runBlocking {
        var counter = 0
        val time = measureTimeMillis {
            massiveRunShared()
                .collect {
                    it.collect {
                        counter = it
                    }
                }
        }
        assertEquals(100000, counter)
        println("Flow completed $counter actions in $time ms")
    }
}