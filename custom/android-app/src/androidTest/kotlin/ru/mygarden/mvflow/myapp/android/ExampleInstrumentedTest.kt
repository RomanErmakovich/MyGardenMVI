package ru.mygarden.mvflow.myapp.android

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import org.junit.Test
import org.junit.runner.RunWith
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        //val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //assertEquals("net.pedroloureiro.mvflow.samples.android", appContext.packageName)


        fun getLocalIP():String?{
            try {
                val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf: NetworkInterface = en.nextElement()
                    val enumIpAddr: Enumeration<InetAddress> = intf.getInetAddresses()
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress: InetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }



        val ip = getLocalIP()
        println(ip)
    }
}
