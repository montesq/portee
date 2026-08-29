package com.portee.app

import android.app.Application
import android.content.Intent
import android.os.Process
import kotlin.system.exitProcess

class PorteeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val intent = Intent(this, CrashActivity::class.java).apply {
                    putExtra(CrashActivity.EXTRA_TRACE, throwable.stackTraceToString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            } finally {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
    }
}
