package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

@ExperimentalCoroutinesApi
class SignInFragment : Fragment() {
    private val viewModel: SignInViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.login.editText?.apply {
            addTextChangedListener {
                viewModel.editLogin(it.toString())
            }

            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel.validateLogin()
                }
            }
        }

        binding.password.editText?.apply {
            addTextChangedListener {
                viewModel.editPassword(it.toString())
            }

            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel.validatePassword()
                }
            }
        }

        binding.submit.setOnClickListener {
            viewModel.submit();
        }

        viewModel.state.observe(viewLifecycleOwner) {
            binding.login.error = it.loginError
            binding.password.error = it.passwordError
        }
        viewModel.login.observe(viewLifecycleOwner) {
            binding.login.editText?.setText(it)
            binding.login.editText?.setSelection(it.length)
        }
        viewModel.password.observe(viewLifecycleOwner) {
            binding.password.editText?.setText(it)
            binding.password.editText?.setSelection(it.length)
        }

        viewModel.token.observe(viewLifecycleOwner) {
            it?.let {
                AppAuth.getInstance().setAuth(it.id, it.token)
                viewModel.reset();
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

}