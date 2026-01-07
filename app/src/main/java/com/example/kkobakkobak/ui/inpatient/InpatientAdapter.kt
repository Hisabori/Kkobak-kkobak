package com.example.kkobakkobak.ui.inpatient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.data.model.InpatientEntry
import com.example.kkobakkobak.databinding.ItemInpatientBinding
import java.time.format.DateTimeFormatter

class InpatientAdapter :
    ListAdapter<InpatientEntry, InpatientAdapter.VH>(InpatientDiffCallback()) {

    inner class VH(val binding: ItemInpatientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemInpatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.tvDate.text = item.date.format(DateTimeFormatter.ofPattern("M월 d일"))
        holder.binding.tvWeekday.text = item.weekday
        holder.binding.tvCount.text = "${item.count}회"
    }

    class InpatientDiffCallback : DiffUtil.ItemCallback<InpatientEntry>() {
        override fun areItemsTheSame(oldItem: InpatientEntry, newItem: InpatientEntry): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: InpatientEntry, newItem: InpatientEntry): Boolean {
            return oldItem == newItem
        }
    }
}
