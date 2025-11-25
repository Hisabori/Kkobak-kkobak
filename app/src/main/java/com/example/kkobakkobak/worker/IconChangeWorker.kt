package com.example.kkobakkobak.worker

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class IconChangeWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    companion object {
        const val ALIAS_NAME_KEY = "ALIAS_NAME"
    }

    override fun doWork(): Result {
        val aliasName = inputData.getString(ALIAS_NAME_KEY) ?: return Result.failure()
        val context = applicationContext
        val packageManager = context.packageManager
        val packageName = context.packageName

        // Disable all aliases first, then enable the target one.
        val componentToEnable = ComponentName(packageName, aliasName)
        val defaultComponent = ComponentName(packageName, "com.example.kkobakkobak.ui.main.MainActivity")
        val sadComponent = ComponentName(packageName, "com.example.kkobakkobak.ui.main.MainActivitySad")
        val angryComponent = ComponentName(packageName, "com.example.kkobakkobak.ui.main.MainActivityAngry")

        // Disable all components
        listOf(defaultComponent, sadComponent, angryComponent).forEach {
            if (it.className != componentToEnable.className) {
                 packageManager.setComponentEnabledSetting(
                    it, 
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
                    PackageManager.DONT_KILL_APP
                )
            }
        }
        
        // Enable the target component
        packageManager.setComponentEnabledSetting(
            componentToEnable, 
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
            PackageManager.DONT_KILL_APP
        )

        return Result.success()
    }
}
