package com.example.kkobakkobak.ui.medication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.model.MedicationIntake
import com.example.kkobakkobak.util.DateUtils

class MedicationHistoryAdapter(private val intakes: List<MedicationIntake>) :
    RecyclerView.Adapter<MedicationHistoryAdapter.IntakeViewHolder>() {

    class IntakeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medName: TextView = view.findViewById(R.id.tv_med_name)
        val time: TextView = view.findViewById(R.id.tv_med_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication_intake, parent, false)
        return IntakeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntakeViewHolder, position: Int) {
        // 1. 리스트(intakes)에서 하나를 꺼내 'intake'라고 이름 짓기
        val intake = intakes[position]

        // 2. 필드명 맞추기 (medicineName, date, time)
        holder.medName.text = intake.medicineName
        holder.time.text = "${intake.date} ${intake.time}"
    }

    override fun getItemCount(): Int = intakes.size
}
