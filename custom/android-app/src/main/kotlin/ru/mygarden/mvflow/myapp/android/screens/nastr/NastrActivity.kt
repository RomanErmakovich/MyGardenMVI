package ru.mygarden.mvflow.myapp.android.screens.nastr

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.mygarden.mvflow.myapp.android.databinding.MygardenNastrActivityBinding
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainActivity
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrMVFlow.Action
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrMVFlow.State
import java.text.*
import java.util.*

class NastrActivity : AppCompatActivity() {

    val viewModel: NastrViewModel by viewModels()

    lateinit var nastrBean: NastrBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MygardenNastrActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        title = "Настройки"

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "databasename4"
        )/*.addMigrations(MIGRATION_1_2)*/.allowMainThreadQueries().build()

        val dao = db.paramDao()
        dao.insertParams()

        val view = object : NastrMVFlow.View {

            override fun render(state: State) {

                if (state.value!=null) {
                    binding.etPhone.setText(nastrBean.remote_phone)
                    binding.etTGr1.text = nastrBean.t_gr1.toString()
                    binding.sbTGr1.progress = nastrBean.t_gr1
                    binding.etTGr2.text = nastrBean.t_gr2.toString()
                    binding.sbTGr2.progress = nastrBean.t_gr2
                    binding.etTGr3.text = nastrBean.t_gr3.toString()
                    binding.sbTGr3.progress = nastrBean.t_gr3
                    //binding.etFireT1Hot.text = nastrBean.t1_hot_fire.toString()
                    //binding.sbFireT1Hot.progress = nastrBean.t1_hot_fire

                    binding.etPlL1s.setText(nastrBean.t_start_water_line1)
                    binding.etPlL1p.setText(nastrBean.t_stop_water_line1)
                    binding.sbL1Cherez.progress = nastrBean.p_cherez_d_line1
                    binding.etL1Cherez.text = nastrBean.p_cherez_d_line1.toString()
                    binding.etPlL1cherez.setText(nastrBean.p_cherez_d_line1.toString())

                    binding.etHeatS.setText(nastrBean.t_start_autoheat)
                    binding.etHeatP.setText(nastrBean.t_stop_autoheat)

                }



            }


            override fun actions(): Flow<Action> = callbackFlow {

                binding.btnPlL1s.setOnClickListener{
                        showTimeDialog(binding.etPlL1s.text.toString(), binding.etPlL1s)
                    }

                binding.btnPlL1p.setOnClickListener{
                        showTimeDialog(binding.etPlL1p.text.toString(), binding.etPlL1p)
                    }

                binding.btnHeatS.setOnClickListener{
                    showTimeDialog(binding.etHeatS.text.toString(), binding.etHeatS)
                }

                binding.btnHeatP.setOnClickListener{
                    showTimeDialog(binding.etHeatP.text.toString(), binding.etHeatP)
                }

                binding.sbTGr1.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            binding.etTGr1.text = "Значение : " + Integer.toString(progress)+" градусов"
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    }
                )
                binding.sbTGr2.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            binding.etTGr2.text = "Значение : " + Integer.toString(progress)+" градусов"
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    }
                )

                binding.sbTGr3.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            binding.etTGr3.text = "Значение : " + Integer.toString(progress)+" градусов"
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    }
                )
                /*
                binding.sbFireT1Hot.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            binding.etFireT1Hot.text = "Значение : " + Integer.toString(progress)+" градусов"
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            binding.etTGr1.setTextColor(Color.DKGRAY)
                        }
                    }
                )
                 */
                binding.sbL1Cherez.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            binding.etL1Cherez.text = "Значение : " + Integer.toString(progress)
                            binding.etPlL1cherez.setText(Integer.toString(progress))
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    }
                )

                binding.btnCancel.setOnClickListener {
                    finish()
                    MyGardenMainActivity.launch(this@NastrActivity)
                }
                binding.btnSave.setOnClickListener {
                    nastrBean.t_gr1 = binding.sbTGr1.progress
                    nastrBean.t_gr2 = binding.sbTGr2.progress
                    nastrBean.t_gr3 = binding.sbTGr3.progress
                    //nastrBean.t1_hot_fire = binding.sbFireT1Hot.progress
                    nastrBean.p_cherez_d_line1 = binding.sbL1Cherez.progress
                    nastrBean.t_start_water_line1 = binding.etPlL1s.text.toString()
                    nastrBean.t_stop_water_line1 = binding.etPlL1p.text.toString()

                    nastrBean.t_start_autoheat = binding.etHeatS.text.toString()
                    nastrBean.t_stop_autoheat = binding.etHeatP.text.toString()

                    nastrBean.remote_phone = binding.etPhone.text.toString()
                    offer(Action.SaveNastrBean(nastrBean, db))
                }


                val jobGetNastr = lifecycleScope.launch { // launch a new coroutine and keep a
                    delay(70)
                    offer(Action.GetNastrBean(db))
                }
                jobGetNastr.start()

                awaitClose()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.takeView(this, view)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.observeEffects().collect {
                if (it is NastrMVFlow.Effect.GetNastrBean) {
                    nastrBean = it.nb
                }
                if (it is NastrMVFlow.Effect.Saved) {
                    Toast.makeText(this@NastrActivity, it.message, Toast.LENGTH_SHORT).show()
                    finish()
                    MyGardenMainActivity.launch(this@NastrActivity)
                }

            }

        }

    }



    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, NastrActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        MyGardenMainActivity.launch(this@NastrActivity)
    }

    fun showTimeDialog(dts: String?, et: EditText?) {
        val dateFormat = SimpleDateFormat("HH:mm")
        var date: Date?
        date = try {
            dateFormat.parse(dts!!)
        } catch (e: ParseException) {
            Toast.makeText(this@NastrActivity, "Неверный формат времени", Toast.LENGTH_SHORT).show()
            return
        }
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val myTimeListener = TimePickerDialog.OnTimeSetListener { viewDTP, hourOfDayDTP,
                                                                  minuteDTP ->
            if (viewDTP.isShown) {
                val calenar2 = Calendar.getInstance()
                calenar2[2019, 1, 1, hourOfDayDTP] = minuteDTP
                val dt2 = calenar2.time
                val formatter: Format = SimpleDateFormat("HH:mm")
                et!!.setText(formatter.format(dt2))
            }
        }
        val timePickerDialog = TimePickerDialog(
            this@NastrActivity,
            myTimeListener,
            hour,
            minute,
            true
        )
        timePickerDialog.setTitle("Выберите время:")
        timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }

}
