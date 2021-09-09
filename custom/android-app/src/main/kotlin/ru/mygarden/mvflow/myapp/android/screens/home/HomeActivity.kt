package ru.mygarden.mvflow.myapp.android.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.mygarden.mvflow.myapp.android.databinding.HomeActivityBinding
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainActivity
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase


/*
private typealias State = Unit
private typealias Mutation = Nothing

private sealed class Action {
    object OpenCountersExample : Action()
    object OpenLifecycleExample : Action()
}
*/
/**
 * This is a very simple scree without any state so this is not the typical approach for a MVFlow.
 *
 * In this case, there is not state, which implies there are no mutations either, so we use
 * Unit instead for those classes.
 *
 * In normal cases you might prefer to have the MVFlow object code outside the activity. And you can do the same with
 * the view code too.
 */
class HomeActivity : AppCompatActivity() {

    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 123

    private fun checkAndRequestPermissions(save: Boolean): Boolean {
        val permissionSendMessage = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )
        val receiveSMS = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        )
        val readSMS = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        )
        val write_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val blueTOOTH = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_WIFI_STATE
        )
        val blueTOOTHadmin = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CHANGE_WIFI_STATE
        )
        val netState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        val wAKE_LOCK = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WAKE_LOCK
        )
        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS)
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS)
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (write_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (read_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (blueTOOTH != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE)
        }
        if (blueTOOTHadmin != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CHANGE_WIFI_STATE)
        }
        if (netState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE)
        }
        if (wAKE_LOCK != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (save) ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            //Toast.makeText(LoadActivity.this, "aaaaaaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
            if (checkAndRequestPermissions(true)) {
                //finish()
                //startActivity(Intent(this@LoadActivity, MainActivity::class.java))

            }
        }
    }


    val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Подключение"

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "databasename4"
        )/*.addMigrations(MIGRATION_1_2)*/.allowMainThreadQueries().build()

        val dao = db.paramDao()
        dao.insertParams()

        val view = object : HomeMVFlow.View {

            override fun render(state: HomeMVFlow.State) {
                //binding.counterField.text = state.value?.get(0)?.name + state.value?.get(0)?.value
                /*
                binding.counterField.text = state.value.toString()
                 */
                binding.progressBar.visibility =
                    if (state.showProgress) View.VISIBLE else View.INVISIBLE

                binding.tvMessage.text = state.value.toString()


            }


            override fun actions(): Flow<HomeMVFlow.Action> = callbackFlow {

                val job = lifecycleScope.launch { // launch a new coroutine and keep a reference
                    // to its Job
                    val wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    while (true) {
                        offer(HomeMVFlow.Action.GetState(wifiMgr))
                        delay(20000)
                    }
                }
                job.join()

                awaitClose()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.takeView(this, view)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.mvFlow.observeEffects().collect {
                if (it is HomeMVFlow.Effect.StartMainActivity) {
                    finish()
                    MyGardenMainActivity.launch(this@HomeActivity)
                }
            }

        }

    }



    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }


}
