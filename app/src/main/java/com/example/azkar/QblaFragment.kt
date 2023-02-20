package com.example.azkar

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.azkar.databinding.FragmentQblaBinding


lateinit var d: MainActivity
 var lastAccelerometer: FloatArray= FloatArray(3)
 var lastMagnetic: FloatArray= FloatArray(3)
var rotationMatrix: FloatArray= FloatArray(9)
 var orientation: FloatArray= FloatArray(9)
var isLastAccelerometerArrayCopied = false
var isLastMagneticArrayCopied = false
var lastUpdateTime:Long = 0
var currentDegree: Float = 0f

class QblaFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentQblaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQblaBinding.inflate(inflater, container, false)
        return binding.root
    }
    lateinit var sensorManager:SensorManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d = (activity as MainActivity)
        sensorManager = d.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(
            this,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(p0.values, 0, lastAccelerometer, 0, p0.values.size)
            isLastAccelerometerArrayCopied = true
        } else if (p0!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(p0.values, 0, lastMagnetic, 0, p0.values.size)
            isLastMagneticArrayCopied = true
        }
        if (isLastAccelerometerArrayCopied && isLastMagneticArrayCopied && System.currentTimeMillis() - lastUpdateTime > 250) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetic)
            SensorManager.getOrientation(rotationMatrix, orientation)
            var animRadians = orientation[0]
            var animDegree = Math.toDegrees(animRadians.toDouble()).toFloat()
            val anim = RotateAnimation(
                currentDegree,
                -animDegree,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration=250
            anim.fillAfter = true
            binding.qiblaImg.startAnimation(anim)
            currentDegree = -animDegree
            lastUpdateTime= System.currentTimeMillis()
            binding.tvDegree.setText(animDegree.toInt().toString()+"°")
            if(animDegree.toInt()==152){
                val v = d.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v!!.vibrate(
                        VibrationEffect.createOneShot(
                            500,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    //deprecated in API 26
                    v!!.vibrate(500)
                }
            }

        }
//
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}