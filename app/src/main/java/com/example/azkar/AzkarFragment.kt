package com.example.azkar

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.azkar.databinding.FragmentAzkarBinding
import com.example.azkar.databinding.FragmentQblaBinding
import java.util.*

class AzkarFragment : Fragment() {
    private var _binding: FragmentAzkarBinding? = null
    private val binding get() = _binding!!
    lateinit var d:MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAzkarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d=(activity as MainActivity)

        val sharedP = requireActivity().getSharedPreferences(
            "MyPref",
            Context.MODE_PRIVATE
        )
        var edit = sharedP!!.edit()
        binding.mAzkar.setHint("Morning Azkar :  ${sharedP.getString("mAzkarh","7")}:${sharedP.getString("mAzkarm","00 am")} " )
        binding.eAzkar.setHint("Evinig Azksr :  ${sharedP.getString("eAzkarh","10")}:${sharedP.getString("eAzkarm","00 pm")}")
        binding.fm.setHint("For Morning :  ${sharedP.getString("m","20")}")
        binding.fe.setHint("For Evning :  ${sharedP.getString("e","15")}")
        binding.imageView.setOnClickListener {
            d.makeCurrentFragment(QblaFragment())
        }
        var mAzkarh="7"
        var mAzkarm="00"
        var eAzkarh="22"
        var eAzkarm="00"
        binding.mAzkar.setOnClickListener {
            val ct = Calendar.getInstance()
            val hour = ct.get(Calendar.HOUR_OF_DAY)
            val minute = ct.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog(
                d,
                { timepicker, h, m ->       mAzkarh="$h"
                    mAzkarm="$m"
                    binding.mAzkar.setText("$h:$m") },
                hour,
                minute,
                true
            )

            timePicker.show()

        }
        binding.eAzkar.setOnClickListener {
            val ct = Calendar.getInstance()
            val hour = ct.get(Calendar.HOUR_OF_DAY)
            val minute = ct.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog(
                d,
                { timepicker, h, m ->   eAzkarh="$h"
                    eAzkarm="$m"
                    binding.eAzkar.setText("$h:$m")},
                hour,
                minute,
                true
            )

            timePicker.show()
        }
        binding.button.setOnClickListener {
           edit.putString("mAzkarh",mAzkarh)
           edit.putString("mAzkarm",mAzkarm)
           edit.putString("eAzkarh",eAzkarh)
           edit.putString("eAzkarm",eAzkarm)
            val m =binding.fm.text?.toString()
            val e=binding.fe.text?.toString()
            if(m!!.isNotEmpty()){
                edit.putString("m",m)
            }else{
                edit.putString("m","20")
            }
            if(e!!.isNotEmpty()){
                edit.putString("e",e)
            }else{
                edit.putString("e","15")
            }
            binding.fm.text.clear()
            binding.fe.text.clear()
            binding.mAzkar.text.clear()
            binding.eAzkar.text.clear()
            if(edit.commit()){
                Toast.makeText(d,"Done Successfuly!",Toast.LENGTH_SHORT).show()
            }
            binding.mAzkar.setHint("Morning Azkar :  ${sharedP.getString("mAzkarh","7")}:${sharedP.getString("mAzkarm","00 am")} " )
            binding.eAzkar.setHint("Evinig Azksr :   ${sharedP.getString("eAzkarh","10")}:${sharedP.getString("eAzkarm","00 pm")}")
            binding.fm.setHint("For Morning :  ${sharedP.getString("m","20")}")
            binding.fe.setHint("For Evning :  ${sharedP.getString("e","15")}")
        }
        binding.btnEnd.setOnClickListener {
            val i=Intent(d,MyService::class.java)
            d.stopService(i)
        }
    }
}