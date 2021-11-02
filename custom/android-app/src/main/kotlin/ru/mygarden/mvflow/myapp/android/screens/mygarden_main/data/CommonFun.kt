package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data

import android.app.Application
import android.graphics.Color
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainMVFlow
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ArdBean
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.ParamBean
import ru.mygarden.mvflow.myapp.android.screens.nastr.NastrBean
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CommonFun {

/*
     constructor (){
          (MGApplication).component!!.inject(this)
     }
*/

     private object HOLDER {
          val INSTANCE = CommonFun()
     }

     companion object {
          val instance: CommonFun by lazy { HOLDER.INSTANCE }
     }


/*
     var db: AppDatabase? = null
     @Inject set
*/

     val hostIP = "192.168.0.210"

     var formatDOnly: Format = SimpleDateFormat("dd.MM.yyyy")



     fun ardCommandHandler(bean: ArdBean, beanList: List<ArdBean>):List<ArdBean>{
          var ret = ArrayList<ArdBean>()

               when (bean.name) {
                    "GR" -> when (bean.value){
                         "CCC" -> {
                              ret.add(ArdBean("GR1", "C"))
                              ret.add(ArdBean("GR2", "C"))
                              ret.add(ArdBean("GR3", "C"))
                         }
                         "OCC" -> {
                              ret.add(ArdBean("GR1", "O"))
                              ret.add(ArdBean("GR2", "C"))
                              ret.add(ArdBean("GR3", "C"))
                         }
                         "OOC" -> {
                              ret.add(ArdBean("GR1", "O"))
                              ret.add(ArdBean("GR2", "O"))
                              ret.add(ArdBean("GR3", "C"))
                         }
                         "OOO" -> {
                              ret.add(ArdBean("GR1", "O"))
                              ret.add(ArdBean("GR2", "O"))
                              ret.add(ArdBean("GR3", "O"))
                         }
                    }
                    "PL1" -> when (bean.value){
                         "1" -> ret.add(ArdBean("PL1", "1"))
                         "0" -> ret.add(ArdBean("PL1", "0"))
                    }
                    "TP1" -> when (bean.value){
                         "1" -> ret.add(ArdBean("TP1", "1"))
                         "0" -> ret.add(ArdBean("TP1", "0"))
                    }

               }

          for (bM: ArdBean in beanList) {
               var ff = false
               for (bR: ArdBean in ret) {
                    if(bM.name.equals(bR.name, ignoreCase = true)) ff=true
               }
               if (!ff) ret.add(bM)
          }

          return ret
     }


     fun getBeanStr(bean: ArdBean, db:AppDatabase?): String {
          return when (bean.name) {
               "H1V" -> "Влажность в тепл. : " + bean.value
               "T1V" -> "Темпер. в тепл. : " + bean.value
               "T1O" -> "Темпер. наружная : " + bean.value
               "VP1" -> "Влажность почвы : " + bean.value
               "GR1" -> "Форт1 : " + when (bean.value) {
                    "O" -> "Откр"
                    "C" -> "Закр"
                    else -> "Н/О"
               }
               "GR2" -> "Форт2 : " + when (bean.value) {
                    "O" -> "Откр"
                    "C" -> "Закр"
                    else -> "Н/О"
               }
               "GR3" -> "Форт3 : " + when (bean.value) {
                    "O" -> "Откр"
                    "C" -> "Закр"
                    else -> "Н/О"
               }
               "PL1" -> "Полив : " + when (bean.value) {
                    "0" -> "выкл"
                    else -> "Вкл"
               }
               "TP1" -> "Подогр : " + if (getParamBoolean("heat10", db)!!) {
                    "(чередов.10 мин) вент " + when (bean.value) {
                         "0" -> "выкл"
                         else -> "Вкл"
                    }
               } else {
                    when (bean.value) {
                         "0" -> "выкл"
                         else -> "Вкл"
                    }
               }
               else -> "???"
          }

     }

     fun getBeanStrColor(bean: ArdBean, db:AppDatabase?): Int {
          return when (bean.name) {
               "H1V" -> Color.BLACK
               "T1V" -> Color.BLACK
               "T1O" -> Color.BLACK
               "VP1" -> Color.BLACK
               "GR1" -> when (bean.value) {
                    "O" -> Color.RED
                    "C" -> Color.BLACK
                    else -> Color.RED
               }
               "GR2" -> when (bean.value) {
                    "O" -> Color.RED
                    "C" -> Color.BLACK
                    else -> Color.RED
               }
               "GR3" -> when (bean.value) {
                    "O" -> Color.RED
                    "C" -> Color.BLACK
                    else -> Color.RED
               }
               "PL1" -> when (bean.value) {
                    "0" -> Color.BLACK
                    else -> Color.RED
               }
               "TP1" -> if (getParamBoolean("heat10", db)!!) Color.RED else {
                    when (bean.value) {
                         "0" -> Color.BLACK
                         else -> Color.RED
                    }
               }
               else -> Color.BLACK
          }

     }

     fun getBeanByName(beanName: String, beanList: List<ArdBean>): ArdBean? {
          for (b: ArdBean in beanList) {
               if (b.name.equals(beanName, ignoreCase = true)) {
                    return b
               }
          }
          return null
     }

     fun setParam(name: String, value: String, db:AppDatabase?) {
          db!!.paramDao().insert(ParamBean(name, value))
     }

     fun getParam(name: String, db:AppDatabase?): String {
          return db!!.paramDao().getByMame(name)!!.value_!!
     }

     fun setParamInt(name: String, value: Int, db:AppDatabase?) {
          db!!.paramDao().insert(ParamBean(name, value.toString()))
     }

     fun getParamInt(name: String, db:AppDatabase?): Int {
          return (db!!.paramDao().getByMame(name)!!.value_!!).toInt()
     }

     fun setParamBoolean(name: String, value: Boolean, db:AppDatabase?) {
          if (value) setParamInt(name, 1, db)
          else setParamInt(name, 0, db)
     }

     fun getParamBoolean(name: String, db:AppDatabase?): Boolean? {
          if (getParamInt(name, db) == 1)
               return true
          else if (getParamInt(name, db) == 0)
               return false
          else return null
     }


     fun getNastrBean(db:AppDatabase?): NastrBean {

          val l = db!!.paramDao().getAll()
          val m = l!!.associate({ Pair(it.name_, it.value_) })
          val nb = NastrBean()
          nb.remote_phone = m.get("remote_phone")!!
          nb.t_start_water_line1 = m.get("t_start_water_line1")!!
          nb.t_stop_water_line1 = m.get("t_stop_water_line1")!!
          nb.t_start_water_line2 = m.get("t_start_water_line2")!!
          nb.t_stop_water_line2 = m.get("t_stop_water_line2")!!
          nb.t_gr1 = m.get("t_gr1")!!.toInt()
          nb.t_gr2 = m.get("t_gr2")!!.toInt()
          nb.t_gr3 = m.get("t_gr3")!!.toInt()
          //nb.t1_hot_fire =m.get("t1_hot_fire")!!.toInt()
          nb.p_cherez_d_line1 = m.get("p_cherez_d_line1")!!.toInt()
          nb.p_cherez_d_line2 = m.get("p_cherez_d_line2")!!.toInt()
          //NastrBean.fl_krit = getParamBoolean("fl_krit")!!
          nb.t_start_autoheat = m.get("t_start_autoheat")!!
          nb.t_stop_autoheat = m.get("t_stop_autoheat")!!

          return nb
     }

     fun saveNastrBean(nastrBean: NastrBean, db:AppDatabase?) {
          setParam("remote_phone", nastrBean.remote_phone, db)
          setParam("t_start_water_line1", nastrBean.t_start_water_line1, db)
          setParam("t_stop_water_line1", nastrBean.t_stop_water_line1, db)
          setParam("t_start_water_line2", nastrBean.t_start_water_line2, db)
          setParam("t_stop_water_line2", nastrBean.t_stop_water_line2, db)
          setParamInt("t_gr1", nastrBean.t_gr1, db)
          setParamInt("t_gr2", nastrBean.t_gr2, db)
          setParamInt("t_gr3", nastrBean.t_gr3, db)
          //setParamInt("t1_hot_fire", nastrBean.t1_hot_fire, db)
          setParamInt("p_cherez_d_line1", nastrBean.p_cherez_d_line1, db)
          setParamInt("p_cherez_d_line2", nastrBean.p_cherez_d_line2, db)
          //setParamBoolean("fl_krit", nastrBean.fl_krit, db)
          setParam("t_start_autoheat", nastrBean.t_start_autoheat, db)
          setParam("t_stop_autoheat", nastrBean.t_stop_autoheat, db)
     }

     fun updateLastPolivL1(db:AppDatabase?): String {

          var res = formatDOnly.format(Date())
          setParam("p_last_line1", res, db)
          return res
     }


     fun getLocalIpAddress(): String? {
          try {
               val en = NetworkInterface.getNetworkInterfaces()
               while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                         val inetAddress = enumIpAddr.nextElement()
                         if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                              return inetAddress.getHostAddress()
                         }
                    }
               }
          } catch (ex: SocketException) {
               ex.printStackTrace()

          }
          return null
          //throw IllegalArgumentException()
     }

}