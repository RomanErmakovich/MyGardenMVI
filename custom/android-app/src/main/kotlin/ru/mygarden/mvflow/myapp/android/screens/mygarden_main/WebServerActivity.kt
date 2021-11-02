package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.gson.GsonBuilder
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ArdBean
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.DB
//import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.DBComponent
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrBean
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


open class WebServerActivity:AppCompatActivity() {
    private val viewModel: MyGardenMainViewModel by viewModels()
    var androidWebServer : NanoHTTPD? = null
    var strAct: String = ""
    var fullState : HashSet<ArdBean>? = null
    var resAvailable: Boolean = false
    var nastrBean : NastrBean? = null
    var db: AppDatabase? =null
    var nastrAvailable: Boolean = false
    val actionQeue: Queue<MyGardenMainMVFlow.Action> = LinkedList<MyGardenMainMVFlow.Action>()

    private fun startAndroidWebServer(): Boolean {
        try{
            val port: Int = 8080
            androidWebServer = MGWebServer(port)
            androidWebServer!!.start()
            return true
        } catch (e: Exception) {
        }
        return false
    }
    private fun stopAndroidWebServer(): Boolean {
        if (androidWebServer != null) {
            androidWebServer!!.stop()
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "databasename4"
        )/*.addMigrations(MIGRATION_1_2)*/.allowMainThreadQueries().build()

        MyGardenMainMVFlow.db = db

        startAndroidWebServer()

        val view = object : MyGardenMainMVFlow.View {
            override fun actions(): Flow<MyGardenMainMVFlow.Action> = callbackFlow {
                val jobQueue = lifecycleScope.launch {
                    while (true) {
                        if (actionQeue.size > 0) {
                            offer(actionQeue.remove())
                        }
                        delay(2000)
                    }
                }
                jobQueue.start()

                val jobWebHandler = lifecycleScope.launch {
                    delay(5000)
                        while (true) {
                                when (strAct){
                                    "Open1Gr" -> actionQeue.add(MyGardenMainMVFlow.Action.Open1Gr(true))
                                    "Open12Gr" ->actionQeue.add(MyGardenMainMVFlow.Action.Open12Gr
                                        (true))
                                    "Open123Gr" -> actionQeue.add(MyGardenMainMVFlow.Action.Open123Gr(true))
                                        // (true))
                                    "CloserAllGr" -> actionQeue.add(MyGardenMainMVFlow.Action.CloserAllGr(true))
                                    "Water1On" -> actionQeue.add(MyGardenMainMVFlow.Action
                                        .Water1On(true))
                                    "Water1Off" -> actionQeue.add(MyGardenMainMVFlow.Action.Water1Off(true))
                                    "HeatOn" -> actionQeue.add(MyGardenMainMVFlow.Action.HeatOn(true))
                                    "Heat10On" -> offer(MyGardenMainMVFlow.Action.Heat10On(
                                        true))
                                    "HeatOff" -> actionQeue.add(MyGardenMainMVFlow.Action.HeatOff(true))
                                    "GetAllInfo" -> actionQeue.add(MyGardenMainMVFlow.Action.GetAllInfo(true))
                                    "GetNastrBean" -> {
                                        nastrBean = CommonFun.instance.getNastrBean(db)
                                        strAct = ""
                                        resAvailable = true
                                    }
                                    "AutoHeat" -> actionQeue.add(MyGardenMainMVFlow.Action.AutoHeat(true))
                                    "AutoWater" -> actionQeue.add(MyGardenMainMVFlow.Action.AutoWater(true))
                                    "AutoWind" -> actionQeue.add(MyGardenMainMVFlow.Action.AutoWind(true))
/*
                                    "saveNastr" -> {
                                        CommonFun.saveNastrBean()
                                    }
*/
                                }

                            if (strAct!=""){
                                resAvailable = false
                                strAct = ""
                            }
                            delay(1000)
                        }
                }
                jobWebHandler.start()

                awaitClose()
            }

            override fun render(state: MyGardenMainMVFlow.State) {

                if (state.value!=null) {
                    fullState = state.value.toHashSet()
                    fullState!!.add(ArdBean("autoWind", if (state.autoWind) "1" else "0"))
                    fullState!!.add(ArdBean("autoHeat", if (state.autoHeat) "1" else "0"))
                    fullState!!.add(ArdBean("autoWater", if (state.autoWater) "1" else "0"))
                    fullState!!.add(ArdBean("p_last_line1", state.lastWaterL1))
                    fullState!!.add(ArdBean("heat10", if (state.heat10) "1" else "0"))
                }

            }

        }



        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.takeView(this, view)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.observeEffects().collect {

                if (it is MyGardenMainMVFlow.Effect.DataRefreshed) {
                    resAvailable = true
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndroidWebServer()
    }

    inner class MGWebServer : NanoHTTPD {

        constructor(port: Int) : super(port) {}
        constructor(hostname: String?, port: Int) : super(hostname, port) {}

        override fun serve(session: IHTTPSession): Response {
            val method = session.method
            if (Method.POST.equals(method)){
                val map = HashMap<String, String>()
                session.parseBody(map)
                val json:String = map.get("postData")!!;
                val builder = GsonBuilder()
                val gson = builder.create()
                val nb = gson.fromJson<NastrBean>(json, NastrBean::class.java)
                CommonFun.instance.saveNastrBean(nb, db)
                val resp = "{name:\"result\", value:\"ok\"}"
                return newFixedLengthResponse(resp)
            } else {
                val parms = session.parms
                if (parms["strAction"] != null) {
                    resAvailable = false
                    strAct = parms["strAction"]!!
                    val strLastAct = parms["strAction"]!!

                    while (!resAvailable) {
                        Thread.currentThread().join(200)

                    }
                    resAvailable = false
                    val builder = GsonBuilder()
                    val gson = builder.create()

                    var resp: String
                    if (strLastAct.equals("GetNastrBean"))
                        resp = gson.toJson(nastrBean)
                    else
                        resp = gson.toJson(fullState)

                    return newFixedLengthResponse(resp)

                } else
                    return newFixedLengthResponse("No strAction")
            }
        }

    }

}