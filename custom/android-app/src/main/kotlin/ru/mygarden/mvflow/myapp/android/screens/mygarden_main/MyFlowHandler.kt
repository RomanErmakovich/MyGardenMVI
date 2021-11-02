package ru.mygarden.mvflow.myapp.android.screens.mygarden_main


import android.text.format.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import net.pedroloureiro.mvflow.HandlerWithEffects
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ApiClient
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ArdBean
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.inet.InetApiClient
import java.text.DateFormat
import java.text.Format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object MyFlowHandler {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    fun <T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
        val callBackKt = CallBackKt<T>()
        callback.invoke(callBackKt)
        this.enqueue(callBackKt)
    }

    class CallBackKt<T>: Callback<T> {

        var onResponse: ((Response<T>) -> Unit)? = null
        var onFailure: ((t: Throwable?) -> Unit)? = null

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure?.invoke(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse?.invoke(response)
        }

    }


    fun getDateDiff(date1: Date, date2: Date, timeUnit: TimeUnit): Long {
        val diffInMillies = date2.time - date1.time
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    val handler: HandlerWithEffects<MyGardenMainMVFlow.State, MyGardenMainMVFlow.Action,
            MyGardenMainMVFlow.Mutation, MyGardenMainMVFlow.Effect> = { state, action, effects ->
        when (action) {

            MyGardenMainMVFlow.Action.GetIP -> flow {

                try {
                    var ip = InetApiClient.apiService.getRemoteIP()
                    //var ip = CommonFun.getLocalIpAddress()
                    emit(
                        MyGardenMainMVFlow.Mutation.ChangeRemoteIP(
                            ip!!
                        )
                    )
                    kotlinx.coroutines.delay(100)
                    effects.send(MyGardenMainMVFlow.Effect.RemoteIP(ip))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            is MyGardenMainMVFlow.Action.AutoHeat-> flow {
                var pf = CommonFun.instance.getParamBoolean("autoHeat", action.db)
                if (pf == false) pf = true else if (pf == true) pf = false
                CommonFun.instance.setParamBoolean("autoHeat", pf!!, action.db)
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoHeat(pf))
                try {
                    if (action.fromWeb) emit(
                        MyGardenMainMVFlow.Mutation.ArdQuery(
                            ApiClient
                                .apiService.getAllInfo()
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.AutoWind -> flow {
                var pw = CommonFun.instance.getParamBoolean("autoWind", action.db)
                if (pw == false) pw = true else if (pw == true) pw = false
                CommonFun.instance.setParamBoolean("autoWind", pw!!, action.db)
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoWind(pw))
                try {
                    if (action.fromWeb) emit(
                        MyGardenMainMVFlow.Mutation.ArdQuery(
                            ApiClient
                                .apiService.getAllInfo()
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.AutoWater -> flow {
                var pp = CommonFun.instance.getParamBoolean("autoWater", action.db)
                if (pp == false) pp = true else if (pp == true) pp = false
                CommonFun.instance.setParamBoolean("autoWater", pp!!, action.db)
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoWater(pp))
                try {
                    if (action.fromWeb) emit(
                        MyGardenMainMVFlow.Mutation.ArdQuery(
                            ApiClient
                                .apiService.getAllInfo()
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.GetAllAutoState -> flow {
                val p_p: Boolean = CommonFun.instance.getParamBoolean(
                    "autoWater"
                    , action.db
                )!!
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoWater(p_p))
                val p_w: Boolean = CommonFun.instance.getParamBoolean(
                    "autoWind"
                    , action.db
                )!!
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoWind(p_w))
                val p_f: Boolean = CommonFun.instance.getParamBoolean(
                    "autoHeat"
                    , action.db
                )!!
                emit(MyGardenMainMVFlow.Mutation.ChangeAutoHeat(p_f))
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.Open1Gr -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.open1Gr
                                (), state.value!!)
                        )
                    )

                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.Open12Gr -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {

                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService
                                .open12Gr(), state.value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.Open123Gr -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {

                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.open123Gr(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.GetAllInfo -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(MyGardenMainMVFlow.Mutation.ArdQuery(ApiClient.apiService.getAllInfo()))
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(
                    MyGardenMainMVFlow.Mutation.ChangeLastWaterL1(
                        CommonFun.instance.getParam(
                            "p_last_line1"
                            , action.db
                        )
                    )
                )
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.CloserAllGr -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.closeAllGr(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.HeatOn -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.heat1(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.HeatOff -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                CommonFun.instance.setParamBoolean("heat10", false, action.db)
                emit(MyGardenMainMVFlow.Mutation.ChangeHeat10(false))
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.heat0(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.Water1On -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.water1(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(
                    MyGardenMainMVFlow.Mutation.ChangeLastWaterL1(
                        CommonFun.instance.updateLastPolivL1
                            (action.db)
                    )
                )
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.Water1Off -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                try {
                    emit(
                        MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                            CommonFun.instance.ardCommandHandler(ApiClient.apiService.water0(), state
                                .value!!)
                        )
                    )
                } catch (e: Exception) {
                    effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                }
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }

            is MyGardenMainMVFlow.Action.GetLastWater -> flow {
                emit(
                    MyGardenMainMVFlow.Mutation.ChangeLastWaterL1(
                        CommonFun.instance.getParam(
                            "p_last_line1"
                            , action.db
                        )
                    )
                )
            }

            is MyGardenMainMVFlow.Action.GetPar -> flow {
                effects.send(
                    MyGardenMainMVFlow.Effect.ShowToast(
                        CommonFun.instance.getParam(
                            "t_stop_water_line2"
                            , action.db
                        )
                    )
                )
            }

            is MyGardenMainMVFlow.Action.Heat10On -> flow {
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobStarted)
                val formatFull: DateFormat =
                    SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                var dateStart = Date()
                CommonFun.instance.setParamBoolean("heat10", true, action.db)
                CommonFun.instance.setParam("t_start_heat_fact", formatFull.format(dateStart), action.db)
                emit(MyGardenMainMVFlow.Mutation.ChangeHeat10(true))
                emit(MyGardenMainMVFlow.Mutation.BackgroundJobFinished)
                kotlinx.coroutines.delay(500)
                if (action.fromWeb) effects.send(MyGardenMainMVFlow.Effect.DataRefreshed)
            }


            is MyGardenMainMVFlow.Action.AutoRobotWind -> flow {
                // АвтоПроветривание
                if (CommonFun.instance.getParamBoolean("autoWind", action.db) == true) {
                    val t_gr1 = CommonFun.instance.getParamInt("t_gr1", action.db)
                    val t_gr2 = CommonFun.instance.getParamInt("t_gr2", action.db)
                    val t_gr3 = CommonFun.instance.getParamInt("t_gr3", action.db)

                    val vgr1 = state.getBeanByName("GR1")!!.value
                    val vgr2 = state.getBeanByName("GR2")!!.value
                    val vgr3 = state.getBeanByName("GR3")!!.value
                    val tavg: Float = state.getBeanByName("T1V")!!.value.toFloat()
                    if (tavg >= t_gr3) {
                        if (!(vgr1 == "O" && vgr2 == "O" && vgr3 == "O")
                        ) {
                            try {

                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService
                                            .open123Gr(), state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                        }
                    } else if (tavg >= t_gr2) {
                        if (!(vgr1 == "O" && vgr2 == "O" && vgr3 == "C")
                        ) {
                            try {

                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService
                                            .open12Gr(), state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                        }

                    } else if (tavg >= t_gr1) {
                        if (!(vgr1 == "O" && vgr2 == "C" && vgr3 == "C")
                        ) {
                            try {

                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService.open1Gr
                                            (), state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                        }
                    } else {   //если все нормально закрывакм все группы
                        if (!(vgr1 == "C" && vgr2 == "C" && vgr3 == "C")
                        ) {
                            try {

                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService
                                            .closeAllGr(), state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                        }
                    }
                }
            }

            is MyGardenMainMVFlow.Action.AutoRobotOthers -> flow {

                // АвтоПодогрев
                if (CommonFun.instance.getParamBoolean("autoHeat", action.db) == true) {
                    val formatDateMinute: DateFormat =
                        SimpleDateFormat("dd.MM.yyyy HH:mm")
                    val formatDate: DateFormat =
                        SimpleDateFormat("dd.MM.yyyy")
                    var nb = CommonFun.instance.getNastrBean(action.db)
                    var ds =  formatDateMinute.parse(formatDate.format(Date())+" "+nb
                        .t_start_autoheat
                        .replace("\n", ""))
                    var dp =  formatDateMinute.parse(formatDate.format(Date())+" "+nb
                        .t_stop_autoheat
                        .replace("\n", ""))
                    if (ds!!.after(dp!!)){
                        val cal = Calendar.getInstance()
                        cal.time = ds
                        cal.add(Calendar.DATE, 1)
                        ds = cal.time
                    }
                    var curr_d = Date()
                    if (curr_d.after(ds) && curr_d.before(dp)){
                        CommonFun.instance.setParam("t_start_heat_fact", formatDateMinute.format(ds!!)
                        +":00", action.db)
                        CommonFun.instance.setParamBoolean("heat10", true, action.db)
                        emit(MyGardenMainMVFlow.Mutation.ChangeHeat10(true))
                    } else {
                        CommonFun.instance.setParamBoolean("heat10", false, action.db)
                        emit(MyGardenMainMVFlow.Mutation.ChangeHeat10(false))
                        if (state.getBeanByName("TP1")!!.value.toInt() != 0) {
                            try {
                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService.heat0(),
                                            state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                        }
                    }

                }


                //подогрев 10
                if (CommonFun.instance.getParamBoolean("heat10", action.db) == true) {
                    var tstart = CommonFun.instance.getParam("t_start_heat_fact", action.db)
                    val formatFull: DateFormat =
                        SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                    var interval = getDateDiff(formatFull.parse(tstart)!!, Date(), TimeUnit.MINUTES)
                    try {
                        if ( (((interval/10).toInt()) % 2) == 0 ){
                            if (state.getBeanByName("TP1")!!.value.toInt() != 1) {
                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService.heat1(),
                                            state.value!!)
                                    )
                                )
                            }
                        } else {
                            if (state.getBeanByName("TP1")!!.value.toInt() != 0) {
                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient.apiService
                                            .heat0(), state.value!!)
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                    }
                }



                // АвтоПолив
                if (CommonFun.instance.getParamBoolean("autoWater", action.db) == true) {
                    val sStartL1 = CommonFun.instance.getParam("t_start_water_line1", action.db)
                    val sStopL1 = CommonFun.instance.getParam("t_stop_water_line1", action.db)
                    val sLastL1 = CommonFun.instance.getParam("p_last_line1", action.db)
                    val cherezL1 = CommonFun.instance.getParam("p_cherez_d_line1", action.db).toInt()

                    val curDate = Date()

                    val formatDOnly: Format = SimpleDateFormat("dd.MM.yyyy")
                    val sCurDateOnly = formatDOnly.format(curDate)
                    val formatFull: DateFormat =  SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                        var dStartL1 = formatFull.parse(sCurDateOnly + " " + sStartL1 + ":00")
                        var dStopL1 = formatFull.parse(sCurDateOnly + " " + sStopL1 + ":00")
                        var dLastL1 = formatFull.parse(sLastL1 + " " + sStartL1 + ":00")

                    val cal = Calendar.getInstance()
                    cal.time = curDate
                    cal.time = dLastL1!!
                    cal.add(Calendar.DATE, 1+cherezL1)
                    var dLastL1_tmp = cal.time
                    val vpl1 = state.getBeanByName("PL1")!!.value

                    if (curDate.after(dStartL1) && curDate.before(dStopL1) && if (!sLastL1.equals
                                (formatDOnly.format(curDate)))  curDate.after (dLastL1_tmp) else
                                    true
                    ) {

                        if (vpl1 != "1") {
                            try {
                                emit(
                                    MyGardenMainMVFlow.Mutation.ChangeLastWaterL1(
                                        CommonFun.instance.updateLastPolivL1
                                            (action.db)
                                    )
                                )
                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient
                                            .apiService.water1(), state.value!!)
                                    )
                                )
                                emit(
                                    MyGardenMainMVFlow.Mutation.ChangeLastWaterL1(
                                        CommonFun.instance.getParam(
                                            "p_last_line1"
                                            , action.db
                                        )
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                            //log("Отработал АвтоПолив: Команда вкл лин 1")
                        }
                    } else {

                        if (vpl1 != "0") {
                            try {
                                emit(
                                    MyGardenMainMVFlow.Mutation.ArdCommandQuery(
                                        CommonFun.instance.ardCommandHandler(ApiClient
                                            .apiService.water0(), state.value!!)
                                    )
                                )
                            } catch (e: Exception) {
                                effects.send(MyGardenMainMVFlow.Effect.ConnectLost(e.stackTraceToString()))
                            }
                            //log("Отработал АвтоПолив: Команда вЫкл лин 1")
                        }

                    }
                }

                //---autorobot
            }

        }
    }

}
