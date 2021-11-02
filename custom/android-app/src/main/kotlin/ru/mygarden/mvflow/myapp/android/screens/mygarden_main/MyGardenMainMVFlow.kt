package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import net.pedroloureiro.mvflow.MVFlow
import net.pedroloureiro.mvflow.Reducer
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ArdBean
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.DB
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrBean
import javax.inject.Inject

object MyGardenMainMVFlow {

    var db: AppDatabase?= null

    sealed class Effect {
        object DataRefreshed: Effect()
        data class ShowToast(val message: String) : Effect()
        data class ConnectLost(val message: String) : Effect()
        data class RemoteIP(val ip: String) : Effect()
    }

    data class State(
        val value: List<ArdBean>? = null,
        //val commandValue: ArdBean? = null,
        val backgroundOperations: Int = 0,
        val isSavedParam: Boolean = false,

        val autoWater :Boolean = false,
        val autoHeat :Boolean = false,
        val autoWind :Boolean = false,
        val lastWaterL1 :String = "",

        val heat10 :Boolean = false,
        val autoHeat10 :Boolean = false,

        val nastrBean: NastrBean? = null,

        val remoteIP: String? = null,

    ) {
        val showProgress get() = backgroundOperations > 0

        fun getBeanByName(beanName: String): ArdBean?{
            for (b: ArdBean in this.value!!){
                if (b.name.equals(beanName, ignoreCase = true)){
                    return b
                }
            }
            return null
        }
    }

    sealed class Action {
        var db: AppDatabase?= MyGardenMainMVFlow.db

        data class Open1Gr (val fromWeb : Boolean) : Action()
        data class Open12Gr (val fromWeb : Boolean) : Action()
        data class Open123Gr (val fromWeb : Boolean) : Action()

        data class GetAllInfo (val fromWeb : Boolean) : Action()
        data class CloserAllGr (val fromWeb : Boolean) : Action()
        data class HeatOn (val fromWeb : Boolean) : Action()
        data class HeatOff (val fromWeb : Boolean) : Action()
        data class Water1On (val fromWeb : Boolean) : Action()
        data class Water1Off (val fromWeb : Boolean) : Action()

        data class AutoWater ( val fromWeb : Boolean) : Action()
        data class AutoHeat (val fromWeb : Boolean) : Action()
        data class AutoWind (val fromWeb : Boolean) : Action()
        data class GetAllAutoState (val fromWeb : Boolean) :
        Action()

        object AutoRobotWind : Action()
        object AutoRobotOthers : Action()

        object GetLastWater : Action()

        object GetPar : Action()

        data class Heat10On (val fromWeb : Boolean) : Action()

        object GetIP: Action()
    }

    sealed class Mutation {
        data class ArdQuery(val beans : List<ArdBean>) : Mutation()
        data class ArdCommandQuery(val beans : List<ArdBean>) : Mutation()
        object BackgroundJobStarted : Mutation()
        object BackgroundJobFinished : Mutation()
        data class ChangeAutoHeat(val p: Boolean) : Mutation()
        data class ChangeAutoWind(val p: Boolean) : Mutation()
        data class ChangeAutoWater(val p: Boolean) : Mutation()
        data class ChangeLastWaterL1(val s: String) : Mutation()
        data class ChangeRemoteIP(val s: String) : Mutation()
        data class ChangeHeat10(val p: Boolean) : Mutation()
    }

    interface View : MVFlow.View<State, Action>

    val handler = MyFlowHandler.handler

    val reducer: Reducer<State, Mutation> = { state, mutation ->
        when (mutation) {
            is Mutation.ArdQuery -> state.copy(value = mutation.beans)
            is Mutation.ArdCommandQuery -> state.copy(value = mutation.beans)
            Mutation.BackgroundJobStarted -> state.copy(backgroundOperations = state.backgroundOperations + 1)
            Mutation.BackgroundJobFinished -> state.copy(backgroundOperations = state.backgroundOperations - 1)
            is Mutation.ChangeAutoHeat -> state.copy(autoHeat = mutation.p)
            is Mutation.ChangeAutoWater -> state.copy(autoWater = mutation.p)
            is Mutation.ChangeAutoWind -> state.copy(autoWind = mutation.p)
            is Mutation.ChangeLastWaterL1 -> state.copy(lastWaterL1 = mutation.s)
            is Mutation.ChangeRemoteIP -> state.copy(remoteIP = mutation.s)
            is Mutation.ChangeHeat10 -> state.copy(heat10 = mutation.p)
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
