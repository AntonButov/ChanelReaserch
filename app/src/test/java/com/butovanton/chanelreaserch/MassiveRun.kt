package com.butovanton.chanelreaserch

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    coroutineScope { // scope for coroutines
        repeat(n) {
            launch {
                repeat(k) { action() }
            }
        }
    }
}

fun massiveRunShared(): Flow<Flow<Int>> = flow {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    (1..n).forEach { n ->
        (1..k).forEach { k ->
            emit(
                flow {
                    emit(k * n)
                }
            )
        }
    }
}

suspend fun Channel<Int>.chanelSender(max: Int, job: CompletableJob) {
    for (oldValue in this) {
        if (oldValue >= max) {
            job.cancel()
            return
        }
        val newValue = oldValue + 1
        send(newValue)
      //  println("Send $newValue")
    }
}