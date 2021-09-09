package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.sms

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainActivity
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.MyGardenMainMVFlow


@Suppress("DEPRECATION")
class SMSReceiver : BroadcastReceiver() {

    @TargetApi(Build.VERSION_CODES.M)
    private fun getIncomingMessage(
        aObject: Any,
        bundle: Bundle
    ): SmsMessage? {
        var currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else {
            SmsMessage.createFromPdu(aObject as ByteArray)
        }

        return currentSMS
    }

val pdu_type = "pdus"

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val dao = (context as MyGardenMainActivity).db!!.paramDao()
        val remotePhoneNumber:String? = dao.getByMame("remote_phone")?.value_
        if (remotePhoneNumber == "") return

//---получить входящее SMS сообщение---
        val bundle = intent.extras
        var currentSMS: SmsMessage?
        var message = ""
        var senderNo = ""
        if (bundle != null) {
//---извлечь полученное SMS ---
            val pdu_Objects =
                bundle["pdus"] as Array<*>
            if (pdu_Objects.size>0) {
                for (aObject in pdu_Objects) {
                    currentSMS = getIncomingMessage(aObject!!, bundle)!!
                    senderNo = currentSMS.displayOriginatingAddress
                    message += currentSMS.displayMessageBody
                }
            }

            if (senderNo.endsWith(remotePhoneNumber!!, false)) {

                if (message.replace("\n", "").replace("\r", "") == "GET IP") {
                    context.actionQeue.add(MyGardenMainMVFlow.Action.GetIP)
                }

            }
        }
    }

}