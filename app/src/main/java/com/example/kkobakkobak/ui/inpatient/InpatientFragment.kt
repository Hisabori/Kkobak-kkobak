package com.example.kkobakkobak.ui.inpatient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.databinding.FragmentInpatientBinding
import com.example.kkobakkobak.data.database.InpatientEntity
import com.example.kkobakkobak.data.model.InpatientEntry as ModelEntry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class InpatientFragment : Fragment() {
    private var _binding: FragmentInpatientBinding? = null
    private val binding get() = _binding!!

    // 명시적으로 타입을 지정해주면 에러를 잡기 쉬워
    private val vm: InpatientViewModel by viewModels()

    // 어댑터 초기화 시 빈 리스트 전달
    private val adapter = InpatientAdapter(emptyList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInpatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@InpatientFragment.adapter // 중복 이름 방지
        }

        binding.swipe.setOnRefreshListener { vm.load() }

        // 수집(Collect) 로직
        viewLifecycleOwner.lifecycleScope.launch {
            vm.items.collectLatest { list ->
                binding.swipe.isRefreshing = false
                // map을 통해 Entity를 UI용 Model로 변환
                adapter.submit(list.map { it.toModel() })
            }
        }

        // 초기 로드
        vm.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 확장 함수: Entity -> Model 변환
    private fun InpatientEntity.toModel(): ModelEntry {
        // DB의 date 형식이 "yyyy-MM-dd"라고 가정
        val parsed = try { LocalDate.parse(this.date) } catch (e: Exception) { LocalDate.now() }
        val weekday = parsed.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        return ModelEntry(
            date = parsed,
            count = this.count, // Entity에 count 필드가 있는지 확인!
            weekday = weekday
        )
    }
}