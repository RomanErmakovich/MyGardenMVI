package ru.mygarden.mvflow.myapp.android.screens.home

import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import net.pedroloureiro.mvflow.HandlerWithEffects
import net.pedroloureiro.mvflow.MVFlow
import net.pedroloureiro.mvflow.Reducer
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ApiClient


object HomeMVFlow {

    sealed class Effect {
        data class StartMainActivity(val message: String) : Effect()
    }

    data class State(
        val value: String? = "Провекра подключения...",
        val backgroundOperations: Int = 0,
        val isSavedParam: Boolean = false
    ) {
        val showProgress get() = backgroundOperations > 0
    }

    sealed class Action {
        //object GetState : Action()
        data class GetState(
            val wifiManager: WifiManager? = null
        ) : HomeMVFlow.Action()
    }

    sealed class Mutation {
        data class ArdQuery(val res: String) : Mutation()
        object BackgroundJobStarted : Mutation()
        object BackgroundJobFinished : Mutation()
        //object Reset : Mutation()
    }

    // this interface just exists for a nicer syntax, it's not required
    interface View : MVFlow.View<State, Action>

    val handler: HandlerWithEffects<State, Action, Mutation, Effect> = { _, action, effects ->
        when (action) {


            is Action.GetState -> flow {
                delay(3000)
                var res: String = "Провекра подключения...\n"
                var fok:Boolean = true
                emit(Mutation.BackgroundJobStarted)
                if (action.wifiManager!!.isWifiEnabled) {
                    res += "Wifi включен \n"
                    val wifiInfo: WifiInfo = action.wifiManager.getConnectionInfo()
                    if( wifiInfo.getNetworkId() != -1 ){
                        res+="Подключено к сети \n"
                    } else {
                        res += "Подключитесь к нужной сети!"
                        fok=false
                    }
                    try{
                        ApiClient.apiService.getAllInfo()
                    } catch (e:Exception){
                        res+="Блок упправления не отвечает!"
                        fok=false
                    }
                } else {
                    res += "Wifi выключен... Включите WiFi и подключитесь к нужной сети!"
                    fok=false
                }
                emit(Mutation.ArdQuery(res))
                emit(Mutation.BackgroundJobFinished)
                if (fok)effects.send(Effect.StartMainActivity("Background job finished"))

            }


        }
    }

    val reducer: Reducer<State, Mutation> = { state, mutation ->
        when (mutation) {
            is Mutation.ArdQuery -> state.copy(value = mutation.res)
            Mutation.BackgroundJobStarted -> state.copy(backgroundOperations = state.backgroundOperations + 1)
            Mutation.BackgroundJobFinished -> state.copy(backgroundOperations = state.backgroundOperations - 1)
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
