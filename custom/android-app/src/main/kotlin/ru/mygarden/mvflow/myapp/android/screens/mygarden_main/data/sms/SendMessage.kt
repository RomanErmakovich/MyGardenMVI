package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.sms

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SendMessage {
    fun sendSMS(phoneNumber: String, message: String, context: Context) {
        val sentPI =
            PendingIntent.getBroadcast(context, 0, Intent("SENT_SMS_ACTION"), 0)
        val smsManager = SmsManager.getDefault()
        val mArray = smsManager.divideMessage(message)
        if (mArray.size > 1) {
            val sentArrayIntents =
                ArrayList<PendingIntent>()
            for (i in mArray.indices) sentArrayIntents.add(sentPI)
            smsManager.sendMultipartTextMessage(phoneNumber, null, mArray, sentArrayIntents, null)
        } else {
            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null)
        }
    }

}