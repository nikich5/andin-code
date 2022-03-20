package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R

import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.StringArg

private const val BASE_URL = "http://192.168.0.11:9999"

@AndroidEntryPoint
class ImageFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentImageBinding.inflate(inflater, container, false)

        arguments?.textArg.let {
            Glide.with(binding.image)
                .load("$BASE_URL/media/${it}")
                .error(R.drawable.ic_baseline_error_outline_24)
                .fitCenter()
                .centerCrop()
                .timeout(10_000)
                .into(binding.image)
        }

        binding.back.setOnClickListener { findNavController().navigateUp() }

        return binding.root
    }
}