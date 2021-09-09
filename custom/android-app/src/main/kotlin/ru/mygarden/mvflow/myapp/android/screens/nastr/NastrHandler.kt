package ru.mygarden.mvflow.myapp.android.screens.nastr


import kotlinx.coroutines.flow.flow
import net.pedroloureiro.mvflow.HandlerWithEffects
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun

object NastrHandler {

    val handler: HandlerWithEffects<NastrMVFlow.State, NastrMVFlow.Action,
            NastrMVFlow.Mutation, NastrMVFlow.Effect> = { _, action, effects ->
        when (action) {

            is NastrMVFlow.Action.GetNastrBean -> flow {
                var nb = CommonFun.getNastrBean(action.db!!)
                emit(NastrMVFlow.Mutation.ChangeNastrBean(nb))
                effects.send(NastrMVFlow.Effect.GetNastrBean(nb))
            }
            is NastrMVFlow.Action.SaveNastrBean -> flow {
                CommonFun.saveNastrBean(action.nastrBean, action.db!!)
                emit(NastrMVFlow.Mutation.ChangeNastrBean(action.nastrBean))
                effects.send(NastrMVFlow.Effect.Saved("Парамеры сохранены"))
            }


        }
    }

}