package com.example.azkar

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.azkar.MyApp.Companion.channel_ID
import java.util.*

class MyService : Service(), SensorEventListener {
    var isZ: Int = 0
    var w1: Int = 0
    var w2: Int = 0
    var x1=0
    var x2=0
    lateinit var sensorManager: SensorManager
    lateinit var mediaPlayer1: MediaPlayer
    lateinit var mediaPlayer2: MediaPlayer

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mediaPlayer1 = MediaPlayer.create(this, R.raw.sbah)
        mediaPlayer2 = MediaPlayer.create(this, R.raw.msaa)

        azkarWithLight()
        stopAzkarInProximity()
        stopAzkarInY_axis()
        startForeground(1, notiBulder())
        return super.onStartCommand(intent, flags, startId)
    }

    fun azkarWithLight() {
        val s = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL)
    }



    fun stopAzkarInY_axis() {
        val s1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, s1, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopAzkarInProximity() {
        val s2 = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorManager.registerListener(this, s2, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun notiBulder(): Notification {
        val i = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(this, 0, i, 0)
        val notification = NotificationCompat.Builder(this, channel_ID)
            .setContentTitle("Azkar")
            .setContentText("Click To Stop Service!")
            .setSmallIcon(R.drawable.notifications)
            .setContentIntent(pending)
            .setAutoCancel(false)
            .build()
        return notification
    }
    lateinit var ct:Calendar
     var hour:Int=0
     var minute :Int=0

    override fun onSensorChanged(p0: SensorEvent?) {

        val sharedP = getSharedPreferences(
            "MyPref",
            Context.MODE_PRIVATE
        )
         ct = Calendar.getInstance()
         hour = ct.get(Calendar.HOUR_OF_DAY)
        minute = ct.get(Calendar.MINUTE)
        val h = sharedP.getString("mAzkarh", "0")
        val m = sharedP.getString("mAzkarm", "0")
        val he = sharedP.getString("eAzkarh", "0")
        val me = sharedP.getString("eAzkarm", "0")
        val lightM = sharedP.getString("m", "0")
        val lightE = sharedP.getString("e", "0")
        if (p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = p0.values[0].toInt()
            val y = p0.values[1].toInt()
            val z = p0.values[2].toInt()
            if (z < 0) {
                isZ = 1
            }
            if (y < 0) {
                if (mediaPlayer1.isPlaying) {
                    mediaPlayer1.stop()
                    w1=1
                    w2=0
                }
                if (mediaPlayer2.isPlaying) {
                    mediaPlayer2.stop()
                    w2=1
                    w1=0
                }
            }
        }
        if (isZ == 1) {
            if (h!!.toInt() == hour && m!!.toInt() == minute&& w1==0&&x1==0) {
                mediaPlayer1.start()
            } else if (he!!.toInt() == hour && me!!.toInt() == minute&& w2==0&&x2==0) {
                mediaPlayer2.start()
            }
        } else {
            if (p0!!.sensor.type == Sensor.TYPE_LIGHT) {
                val light = p0.values[0].toInt()
                if (light > lightM!!.toInt() && h!!.toInt() == hour && m!!.toInt() == minute&& w1==0&&x1==0) {
                    mediaPlayer1.start()
                } else {
                    if (light < lightE!!.toInt() && he!!.toInt() == hour && me!!.toInt() == minute&& w2==0&&x2==0)
                        mediaPlayer2.start()
                }
            }
        }
        if (p0!!.sensor.type == Sensor.TYPE_PROXIMITY) {
            val x=p0.values[0].toInt()


            if(x==0){
                if (mediaPlayer1.isPlaying){
                    mediaPlayer1.pause()
                    x1=1
                }
                if (mediaPlayer2.isPlaying){
                    mediaPlayer2.pause()
                    x2=1
                }

            }else{
                if (x1==1){
                    mediaPlayer1.start()
                    x1=0
                }
                if (x2==1){
                    mediaPlayer2.start()
                    x2=0
                }
            }

        }



    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}