package com.hampson.sharework.ui.worker.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hampson.sharework.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }
}