package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import kotlinx.coroutines.test.runBlockingTest
import ru.mygarden.mvflow.myapp.android.CollectingEffectSender
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class MyGardenMainMVFlowHandlerTest {

    val handler = MyGardenMainMVFlow.handler
    lateinit var effectSender: CollectingEffectSender<MyGardenMainMVFlow.Effect>

    @Before
    fun setUp() {
        // (a bit of reflection here: )
        // typically you'd want to instantiate a new handler and assign it to `handler` but the way things were done,
        // handler is just one lambda in a kotlin object (aka a singleton). Depending on the
        // implementation details of your particular project, you might be able to instantiate the handler on demand
        //  handler = [not happening]
        effectSender = CollectingEffectSender()
    }

    @Test
    fun testAddOne() = runBlockingTest {
        val flow = handler.invoke(
            MyGardenMainMVFlow.State(),
            MyGardenMainMVFlow.Action.AddOne,
            effectSender
        ).toList()

        Assert.assertEquals(
            listOf<MyGardenMainMVFlow.Mutation>(
                MyGardenMainMVFlow.Mutation.Increment(1)
            ),
            flow
        )

        Assert.assertEquals(
            emptyList<MyGardenMainMVFlow.Effect>(),
            effectSender.effectsSeen
        )
    }

    @Test
    fun testAddMany() = runBlockingTest {
        val flow = handler.invoke(
            MyGardenMainMVFlow.State(),
            MyGardenMainMVFlow.Action.AddMany,
            effectSender
        ).toList()

        Assert.assertEquals(
            listOf(
                MyGardenMainMVFlow.Mutation.BackgroundJobStarted,
                MyGardenMainMVFlow.Mutation.Increment(1),
                MyGardenMainMVFlow.Mutation.Increment(2),
                MyGardenMainMVFlow.Mutation.Increment(1),
                MyGardenMainMVFlow.Mutation.BackgroundJobFinished
            ),
            flow
        )

        Assert.assertEquals(
            listOf(
                MyGardenMainMVFlow.Effect.ShowToast("This might take a while..."),
                MyGardenMainMVFlow.Effect.ShowToast("Background job finished")
            ),
            effectSender.effectsSeen
        )
    }

    // more actions to test
}
