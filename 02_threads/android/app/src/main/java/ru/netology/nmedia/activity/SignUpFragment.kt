package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signUn.setOnClickListener {
            if (binding.password.text.toString() != binding.confirmPassword.text.toString()) {
                Snackbar.make(binding.root, R.string.password_error, Snackbar.LENGTH_LONG).show()
            } else {
                viewModel.registerUser(
                    login = binding.login.text.toString(),
                    password = binding.password.text.toString(),
                    name = binding.name.text.toString()
                )
                findNavController().navigateUp()
            }
        }

        return binding.root
    }
}