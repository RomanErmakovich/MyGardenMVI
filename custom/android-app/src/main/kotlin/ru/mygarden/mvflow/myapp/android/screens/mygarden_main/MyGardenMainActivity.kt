package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.mygarden.mvflow.myapp.android.R
import ru.mygarden.mvflow.myapp.android.databinding.MygardenMainActivityBinding
import ru.mygarden.mvflow.myapp.android.screens.home.HomeActivity
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainMVFlow.Action
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainMVFlow.State
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.ParamDao
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.sms.SMSReceiver
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.sms.SendMessage
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrActivity
import java.util.*


class MyGardenMainActivity : WebServerActivity(), M_MenuListener {


    val viewModel: MyGardenMainViewModel by viewModels()

    var mySMSReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = MygardenMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Программа МОЙ САД"

        val dao: ParamDao = db!!.paramDao()
        dao.insertParams()

        mySMSReceiver = SMSReceiver()
        val filterSMS = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(mySMSReceiver, filterSMS)

        val view = object : MyGardenMainMVFlow.View {

            override fun render(state: State) {

                binding.mainLL.setBackgroundColor(
                    if (state.showProgress) Color.RED else Color.parseColor(
                        "#CCFFCC"
                    )
                )

                if (state.value != null) {
                    binding.tvTN.text = CommonFun.instance.getBeanStr(state.getBeanByName("T1O")!!, db)
                    binding.tvTVV.text = CommonFun.instance.getBeanStr(state.getBeanByName("T1V")!!, db)
                    binding.tvVVV.text = CommonFun.instance.getBeanStr(state.getBeanByName("H1V")!!, db)
                    binding.tvVP.text = CommonFun.instance.getBeanStr(state.getBeanByName("VP1")!!, db)
                    binding.tvLastWater.text = "Посл. полив : " + state.lastWaterL1

                    val GR1 = state.getBeanByName("GR1")!!
                    binding.txGR1.text = CommonFun.instance.getBeanStr(GR1, db)
                    binding.txGR1.setTextColor(CommonFun.instance.getBeanStrColor(GR1, db))

                    val GR2 = state.getBeanByName("GR2")!!
                    binding.txGR2.text = CommonFun.instance.getBeanStr(GR2, db)
                    binding.txGR2.setTextColor(CommonFun.instance.getBeanStrColor(GR2, db))

                    val GR3 = state.getBeanByName("GR3")!!
                    binding.txGR3.text = CommonFun.instance.getBeanStr(GR3, db)
                    binding.txGR3.setTextColor(CommonFun.instance.getBeanStrColor(GR3, db))

                    val PL1 = state.getBeanByName("PL1")!!
                    binding.txWater1.text = CommonFun.instance.getBeanStr(PL1, db)
                    binding.txWater1.setTextColor(CommonFun.instance.getBeanStrColor(PL1, db))

                    val TP1 = state.getBeanByName("TP1")!!
                    binding.txTP1.text = CommonFun.instance.getBeanStr(TP1, db)
                    binding.txTP1.setTextColor(CommonFun.instance.getBeanStrColor(TP1, db))



                    if (state.autoWater){
                        binding.ibAutoWater.setBackgroundColor(Color.RED)
                        binding.tvAutoWater.text = "Автополив : Вкл"
                        binding.tvAutoWater.setTextColor(Color.RED)
                    } else {
                        binding.ibAutoWater.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MyGardenMainActivity,
                                R.color.colorBtnGray1
                            )
                        )
                        binding.tvAutoWater.text = "Автополив : выкл"
                        binding.tvAutoWater.setTextColor(Color.BLACK)
                    }

                    if (state.autoHeat) {
                        binding.ibAutoHeat.setBackgroundColor(Color.RED)
                        binding.tvAutoHeat.text = "Автоподогр : Вкл"
                        binding.tvAutoHeat.setTextColor(Color.RED)
                    } else {
                        binding.ibAutoHeat.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MyGardenMainActivity,
                                R.color.colorBtnGray1
                            )
                        )
                        binding.tvAutoHeat.text = "Автоподогр : выкл"
                        binding.tvAutoHeat.setTextColor(Color.BLACK)
                    }


                    if (state.autoWind){
                        binding.ibAutoWind.setBackgroundColor(Color.RED)
                        binding.tvAutoWind.text = "Автопров : Вкл"
                        binding.tvAutoWind.setTextColor(Color.RED)
                    } else {
                        binding.ibAutoWind.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MyGardenMainActivity,
                                R.color.colorBtnGray1
                            )
                        )
                        binding.tvAutoWind.text = "Автопров : выкл"
                        binding.tvAutoWind.setTextColor(Color.BLACK)
                    }

                }

            }


            override fun actions(): Flow<Action> = callbackFlow {
                M_Menu.mMenuListener = this@MyGardenMainActivity
                binding.ibMMenu.setOnClickListener {
                    M_Menu.show(this@MyGardenMainActivity)
                }
                binding.ibSettings.setOnClickListener {
                    finish()
                    NastrActivity.launch(this@MyGardenMainActivity)
                }


                binding.ibAutoWater.setOnClickListener {
                    actionQeue.add(Action.AutoWater(false))
                }
                binding.ibAutoHeat.setOnClickListener {
                    actionQeue.add(Action.AutoHeat(false))
                }
                binding.ibAutoWind.setOnClickListener {
                    actionQeue.add(Action.AutoWind(false))
                }





                val jobGetAllAutoState = lifecycleScope.launch {
                    delay(1000)
                    actionQeue.add(Action.GetAllAutoState(false))
                }
                jobGetAllAutoState.start()

                val jobInfo = lifecycleScope.launch {
                    while (true) {
                        delay(1000)
                        actionQeue.add(Action.GetAllInfo(false))
                        delay(30000)
                    }
                }
                jobInfo.start()


                val jobRobotOthers = lifecycleScope.launch {
                    delay(7000)
                    while (true) {
                        actionQeue.add(Action.AutoRobotOthers)
                        delay(60000)
                    }
                }
                jobRobotOthers.start()



                val jobRobotWind = lifecycleScope.launch {
                    delay(5000)
                    while (true) {
                        actionQeue.add(Action.AutoRobotWind)
                        delay(30 * 60000)
                    }
                }
                jobRobotWind.start()

                awaitClose()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.takeView(this, view)
        }
        lifecycleScope.launchWhenStarted {

            //viewModel.mvFlow.addExternalActions()

            viewModel.mvFlow.observeEffects().collect {
                if (it is MyGardenMainMVFlow.Effect.ShowToast) {
                    Toast.makeText(this@MyGardenMainActivity, it.message, Toast.LENGTH_SHORT).show()
                }

                if (it is MyGardenMainMVFlow.Effect.ConnectLost) {
                    Toast.makeText(
                        this@MyGardenMainActivity, /*it.message*/ "Потеряна связь с " +
                                "блоком !", Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                    HomeActivity.launch(this@MyGardenMainActivity)
                }

                if (it is MyGardenMainMVFlow.Effect.RemoteIP) {
                    //println(it.ip + " *******************************************")
                    SendMessage().sendSMS(
                        "8" + dao.getByMame("remote_phone")!!.value_!!, it.ip,
                        this@MyGardenMainActivity
                    )
                }

            }

        }

    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MyGardenMainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onM_MenuAction(action: Action) {
            actionQeue.add(action)
    }


    override fun onDestroy() {
        try {
            unregisterReceiver(mySMSReceiver)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }


}
