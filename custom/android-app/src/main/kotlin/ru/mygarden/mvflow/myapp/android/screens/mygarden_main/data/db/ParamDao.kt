package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
public interface ParamDao {

    @Query("SELECT * FROM parambean WHERE name_ = :pname_")
    fun getByMame(pname_: String): ParamBean?

    @Query("SELECT * FROM parambean")
    fun getAll(): List<ParamBean>?

    @Query("SELECT count(*) FROM parambean")
    fun getCount(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg param_bean: ParamBean)

    @Update
    fun update(vararg param_bean: ParamBean)

    @Delete
    fun delete(param_bean: ParamBean)

    fun insertParams(){
        if (getCount()<7){
            insert(ParamBean("remote_phone",""))
//            insert(ParamBean("fl_log","0"))
            insert(ParamBean("fl_krit","0"))
//            insert(ParamBean("need_t","22"))
//            insert(ParamBean("hot_t","50"))
//            insert(ParamBean("low_t","0"))
            insert(ParamBean("t_gr1","50"))
            insert(ParamBean("t_gr2","50"))
            insert(ParamBean("t_gr3","50"))
//            insert(ParamBean("dt1_vent","50"))
//            insert(ParamBean("dt2_vent","50"))
//            insert(ParamBean("t1_hot_fire","50"))
//            insert(ParamBean("t2_low_fire","50"))
/*
            insert(ParamBean("t_start_sv_line1","12:00"))
            insert(ParamBean("t_stop_sv_line1","12:00"))
            insert(ParamBean("t_start_sv_line2","12:00"))
            insert(ParamBean("t_stop_sv_line2","12:00"))

 */
            insert(ParamBean("t_start_water_line1","12:00"))
            insert(ParamBean("t_stop_water_line1","12:00"))
            insert(ParamBean("t_start_water_line2","12:00"))
            insert(ParamBean("t_stop_water_line2","12:00"))
            insert(ParamBean("p_last_line1","26.02.2019"))
            insert(ParamBean("p_cherez_d_line1","0"))
            insert(ParamBean("p_last_line2","26.02.2019"))
            insert(ParamBean("p_cherez_d_line2","0"))
//            insert(ParamBean("autoLamp","0"))
            insert(ParamBean("autoWind","0"))
//            insert(ParamBean("autoVent","0"))
            insert(ParamBean("autoHeat","0"))
            insert(ParamBean("autoWater","0"))
//            insert(ParamBean("last_power_status","wire"))
//            insert(ParamBean("last_bak_status","full"))
            insert(ParamBean("heat10","0"))
            insert(ParamBean( "t_start_heat_fact", "26.02.2019"))

            insert(ParamBean("t_start_autoheat","12:00"))
            insert(ParamBean("t_stop_autoheat","12:00"))
        }
    }

}