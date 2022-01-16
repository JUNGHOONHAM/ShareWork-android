package com.hampson.sharework.ui.worker.chat

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hampson.sharework.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }
}