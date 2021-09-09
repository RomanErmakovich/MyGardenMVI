package ru.mygarden.mvflow.myapp.android.screens.nastr

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import net.pedroloureiro.mvflow.MVFlow
import net.pedroloureiro.mvflow.Reducer
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase

object NastrMVFlow {

    sealed class Effect {
        data class GetNastrBean(val nb: NastrBean) : Effect()
        data class Saved(val message: String) : Effect()
        data class Canceled(val message: String) : Effect()
    }

    data class State(
        val value: NastrBean? = null,
        val backgroundOperations: Int = 0
    )

    sealed class Action {

        data class GetNastrBean (val db : AppDatabase? = null) : Action()
        data class SaveNastrBean (val nastrBean: NastrBean,val db : AppDatabase? = null) : Action()

        //data class GetPar (val db : AppDatabase? = null) : Action()
    }

    sealed class Mutation {
        data class ChangeNastrBean(val bean : NastrBean) : Mutation()
        object BackgroundJobStarted : Mutation()
        object BackgroundJobFinished : Mutation()
        //object Reset : Mutation()
    }

    // this interface just exists for a nicer syntax, it's not required
    interface View : MVFlow.View<State, Action>

    val handler = NastrHandler.handler

    val reducer: Reducer<State, Mutation> = { state, mutation ->
        when (mutation) {
            is Mutation.ChangeNastrBean -> state.copy(value = mutation.bean)
            Mutation.BackgroundJobStarted -> state.copy(backgroundOperations = state
            .backgroundOperations + 1)
            Mutation.BackgroundJobFinished -> state.copy(backgroundOperations = state.backgroundOperations - 1)

            /*
            Mutation.Reset -> {
                // we still have the "background operations" going on so we don't change that value
                state.copy(value = 0)
            }
             */
        }
    }

    fun create(
        initialState: State = State(),
        coroutineScope: CoroutineScope
    ) = MVFlow(
        initialState,
        handler,
        reducer,
        coroutineScope,
        { Log.d("MYAPP", it) }
    )


}
