package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FtagmentSignInBinding
import ru.netology.nmedia.viewmodel.SignViewModel

class SignInFragment : Fragment() {

    private val viewModel: SignViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FtagmentSignInBinding.inflate(inflater, container, false)

        binding.signIn.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()

            viewModel.updateUser(login, password)

            findNavController().navigateUp()
        }

        return binding.root
    }
}