package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import org.junit.Assert
import org.junit.Test

class CounterMVlowReducerTest {
    // see comment in CounterMVFlowHandlerTest::setUp
    val reducer = MyGardenMainMVFlow.reducer

    @Test
    fun testReducerIncrement() {

        val actual = reducer.invoke(
            MyGardenMainMVFlow.State(0, 0),
            MyGardenMainMVFlow.Mutation.Increment(2)
        )

        Assert.assertEquals(
            MyGardenMainMVFlow.State(
                value = 2,
                backgroundOperations = 0
            ),
            actual
        )
    }

    @Test
    fun testReducerBackgroundMutation() {

        val actual = reducer.invoke(
            MyGardenMainMVFlow.State(0, 0),
            MyGardenMainMVFlow.Mutation.BackgroundJobStarted
        )

        Assert.assertEquals(
            MyGardenMainMVFlow.State(
                value = 0,
                backgroundOperations = 1
            ),
            actual
        )
    }

    // more mutations/scenarios to test
}
