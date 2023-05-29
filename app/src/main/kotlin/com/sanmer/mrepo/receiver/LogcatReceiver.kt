package com.sanmer.mrepo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sanmer.mrepo.utils.expansion.deleteLog
import timber.log.Timber

class LogcatReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                context.deleteLog("app")
                Timber.i("boot-complete triggered")
            }
        }
    }
}