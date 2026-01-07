package com.example.kkobakkobak.ui.inpatient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.data.database.InpatientEntity
import com.example.kkobakkobak.data.model.InpatientEntry
import com.example.kkobakkobak.databinding.FragmentInpatientBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class InpatientFragment : Fragment() {
    private var _binding: FragmentInpatientBinding? = null
    private val binding get() = _binding!!

    private val vm: InpatientViewModel by viewModels()
    private lateinit var mAdapter: InpatientAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInpatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = InpatientAdapter()

        binding.rvServey.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        binding.swipe.setOnRefreshListener { vm.load() }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.items.collectLatest { list ->
                binding.swipe.isRefreshing = false
                mAdapter.submitList(list.map { it.toModel() })
            }
        }

        vm.load()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun InpatientEntity.toModel(): InpatientEntry {
        val parsed = try { LocalDate.parse(this.date) } catch (e: Exception) { LocalDate.now() }
        val weekday = parsed.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        return InpatientEntry(
            date = parsed,
            count = this.count,
            weekday = weekday
        )
    }
}
