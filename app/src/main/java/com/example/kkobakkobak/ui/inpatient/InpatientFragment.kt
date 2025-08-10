package com.example.kkobakkobak.ui.inpatient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.db.InpatientEntity
import com.example.kkobakkobak.databinding.FragmentInpatientBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.kkobakkobak.data.model.InpatientEntry as ModelEntry

class InpatientFragment : Fragment() {
    private var _binding: FragmentInpatientBinding? = null
    private val binding get() = _binding!!
    private val vm: InpatientViewModel by viewModels()

    private val adapter = InpatientAdapter(emptyList())
    private val emojis = listOf("💊","🌧","🌥","🏥","😢")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInpatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter
        binding.tvTitle.text = getString(R.string.inpatient_title)

        binding.swipe.setOnRefreshListener { vm.load() }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.items.collectLatest { list ->
                binding.swipe.isRefreshing = false

                // Adapter가 기대하는 data.model.InpatientEntry(LocalDate 포함)로 매핑
                adapter.submit(list.map { it.toModel() })

                if (list.isEmpty()) {
                    binding.tvUpdated.text = "🥺 ${getString(R.string.inpatient_load_failed)}"
                    binding.tvAvg.text = ""
                    binding.tvMin.text = ""
                    binding.tvMax.text = ""
                    return@collectLatest
                }

                val avg = list.map { it.count }.average().toInt()
                val min = list.minByOrNull { it.count }!!
                val max = list.maxByOrNull { it.count }!!

                binding.tvUpdated.text =
                    "${emojis.random()} ${getString(R.string.inpatient_last_updated_prefix)} ${list.last().date}"

                binding.tvAvg.text = getString(R.string.inpatient_avg_format, avg)
                binding.tvMin.text = getString(R.string.inpatient_min_format, min.count, min.date)
                binding.tvMax.text = getString(R.string.inpatient_max_format, max.count, max.date)
            }
        }

        vm.load()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

/** Entity(String 날짜) → data.model.InpatientEntry(LocalDate 날짜) */
private fun InpatientEntity.toModel(): ModelEntry {
    val raw = date.toString()
    val normalized = if (raw.contains('.')) raw.replace('.', '-') else raw

    val parsed: LocalDate = try {
        LocalDate.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
        // 파싱 실패 시 오늘 날짜로 대체(앱이 죽지 않게)
        LocalDate.now()
    }

    val weekday = parsed.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN) // "월","화"...

    return ModelEntry(
        date = parsed,     // <- LocalDate
        count = count,
        weekday = weekday
    )
}
