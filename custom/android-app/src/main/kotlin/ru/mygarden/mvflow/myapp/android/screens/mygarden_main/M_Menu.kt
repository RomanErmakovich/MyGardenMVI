package ru.mygarden.mvflow.myapp.android.screens.mygarden_main

import android.app.AlertDialog
import android.content.Context
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db.AppDatabase
import javax.inject.Inject

object M_Menu {

    var mMenuListener:M_MenuListener? = null
    val m_menu =
        arrayOf("Проветривание",  /*"Освещение", "Вентиляторы",*/"Подогрев", "Полив")

    val provetr_gr = arrayOf(
        "Форточка 1 открыта",
        "Форточки 1 и 2 открыты",
        "Форточки 1,2,3 открыты",
        "Все форточки закрыты"
    )

    val dif_lines_gr = arrayOf("Линия полива 1" /*, "Линия полива 2"*/)
    val line_act = arrayOf("Включить", "ВЫключить")
    val com_lines_gr = arrayOf(
        "Подогрев 1 включить",
        "Подогрев 1 (чередование 10 мин.) включить",
        "Подогрев 1 вЫключить"
    )


    var builder_m_memu: AlertDialog.Builder? = null
    var builder_provetr_gr: AlertDialog.Builder? = null

    //AlertDialog.Builder builder_provetr_actions;
    var builder_dif_lines_gr: AlertDialog.Builder? = null
    var builder_com_lines_gr: AlertDialog.Builder? = null
    var builder_lines_actions: AlertDialog.Builder? = null

    private var m_menu_act: String? = null
    private var sub_menu_act: String? = null
    private var sub_sub_menu_act: String? = null


    fun show(context: Context) {
        builder_m_memu = AlertDialog.Builder(context)
        builder_m_memu!!.setTitle("Ручное управление")
            .setItems(m_menu) { _, item ->
                m_menu_act = m_menu[item]

                //////////////////////////////////////////////////////////////////////////////
                builder_provetr_gr = AlertDialog.Builder(context)
                builder_provetr_gr!!.setTitle(m_menu_act)
                    .setItems(provetr_gr) { _, itemG ->
                        sub_menu_act = provetr_gr[itemG]
                        when (m_menu_act) {
                            "Проветривание" -> when (sub_menu_act) {
                                "Форточка 1 открыта" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .Open1Gr(false))
                                }
                                "Форточки 1 и 2 открыты" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .Open12Gr(false))
                                }
                                "Форточки 1,2,3 открыты" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    //sendBT("3")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .Open123Gr(false))
                                }
                                "Все форточки закрыты" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .CloserAllGr(false))
                                }
                            }
                        }
                    }
                builder_provetr_gr!!.setCancelable(true)

                if (m_menu_act == "Проветривание") builder_provetr_gr!!.show()

                ///////////////////////////////////////////////////////////////////////////////
                builder_dif_lines_gr = AlertDialog.Builder(context)
                builder_dif_lines_gr!!.setTitle(m_menu_act)
                    .setItems(dif_lines_gr) { _, itemG ->
                        sub_menu_act = dif_lines_gr[itemG]
                        builder_lines_actions = AlertDialog.Builder(context)
                        builder_lines_actions!!.setTitle("Действие")
                            .setItems(line_act) { _, itemL ->
                                sub_sub_menu_act = line_act[itemL]
                                when (m_menu_act) {
                                    "Полив" -> when (sub_menu_act) {
                                        "Линия полива 1" -> when (sub_sub_menu_act) {
                                            "Включить" -> {
                                                /*
                                                log(
                                                    "ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act " + line_act[item]
                                                )
                                       */
                                                mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow
                                                    .Action
                                                    .Water1On(
                                                        false))
                                            }
                                            "ВЫключить" -> {
                                                /*
                                                log(
                                                    "ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act " + line_act[item]
                                                )
                                                 */
                                                mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow
                                                    .Action
                                                    .Water1Off(false))
                                            }
                                        }
                                        "Линия2" -> when (sub_sub_menu_act) {
                                            "Включить" -> {
                                                /*
                                                log(
                                                    "ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act " + line_act[item]
                                                )
                                                 */
                                                //sendBT("m")
                                            }
                                            "ВЫключить" -> {
                                                /*
                                                log(
                                                    "ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act " + line_act[item]
                                                )
                                                 */
                                                //sendBT("n")
                                            }
                                        }
                                    }
                                }
                            }
                        builder_lines_actions!!.setCancelable(true)
                        builder_lines_actions!!.show()
                    }
                builder_dif_lines_gr!!.setCancelable(true)
                if (m_menu_act == "Полив" || m_menu_act == "Освещение") builder_dif_lines_gr!!.show()


                ///////////////////////////////////////////////////////////////////////////////

                //"Линия 1 включена", "Линии 1 и 2 включены", "Все линии вЫключены"
                builder_com_lines_gr = AlertDialog.Builder(context)
                builder_com_lines_gr!!.setTitle(m_menu_act)
                    .setItems(com_lines_gr) { _, itemC ->
                        sub_menu_act = com_lines_gr[itemC]
                        when (m_menu_act) {
                            "Подогрев" -> when (sub_menu_act) {
                                "Подогрев 1 включить" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .HeatOn(false))
                                }
                                "Подогрев 1 (чередование 10 мин.) включить" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .Heat10On(false))
                                }
                                "Подогрев 1 вЫключить" -> {
                                    //log("ЛОКАЛЬНО Команда меню : $m_menu_act $sub_menu_act")
                                    mMenuListener!!.onM_MenuAction(MyGardenMainMVFlow.Action
                                        .HeatOff( false))
                                }
                            }
                        }
                    }

                builder_com_lines_gr!!.setCancelable(true)
                if (m_menu_act == "Вентиляторы" || m_menu_act == "Подогрев") builder_com_lines_gr!!.show()
            }

        builder_m_memu!!.setCancelable(true)
        builder_m_memu!!.show()


    }

}