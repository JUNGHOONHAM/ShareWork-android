package com.hampson.sharework.ui.worker.notice

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hampson.sharework.databinding.FragmentNoticeBinding

class NoticeFragment : Fragment() {

    private lateinit var binding: FragmentNoticeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNoticeBinding.inflate(inflater, container, false)

        return binding.root
    }
}