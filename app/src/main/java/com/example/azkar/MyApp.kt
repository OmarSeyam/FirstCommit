package com.example.azkar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi


class MyApp :Application() {
    companion object{
        val channel_ID = "FlashService"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        serviceManager()
    }

     @RequiresApi(Build.VERSION_CODES.O)
     private fun serviceManager(){
         if(Build.VERSION.SDK_INT>=26) {
             val serviceChannel = NotificationChannel(
                 channel_ID,
                 "First Service",
                 NotificationManager.IMPORTANCE_DEFAULT
             )
             val manager = getSystemService(NotificationManager::class.java)
             manager.createNotificationChannel(serviceChannel)
         }else{
             val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         }
     }

}