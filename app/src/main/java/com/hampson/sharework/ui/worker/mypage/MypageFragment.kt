package com.hampson.sharework.ui.worker.mypage

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hampson.sharework.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMypageBinding.inflate(inflater, container, false)

        return binding.root
    }
}