package com.sample.jetpack2

import com.sample.jetpack2.utils.epochMillis2HumanTime
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getTime3600() {
        println("3600ms = ${3600L.epochMillis2HumanTime()}")
    }

    @Test
    fun getTime0() {
        println("0ms = ${0L.epochMillis2HumanTime()}")
    }
}