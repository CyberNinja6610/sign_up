package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpViewModel

@ExperimentalCoroutinesApi
class SignUpFragment : Fragment() {
    private val viewModel: SignUpViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

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

        binding.confirmPassword.editText?.apply {
            addTextChangedListener {
                viewModel.editConfirmPassword(it.toString())
            }

            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel.validateConfirmPassword()
                }
            }
        }

        binding.name.editText?.apply {
            addTextChangedListener {
                viewModel.editName(it.toString())
            }

            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel.validateName()
                }
            }
        }

        binding.submit.setOnClickListener {
            viewModel.submit();
        }



        viewModel.state.observe(viewLifecycleOwner) {
            binding.login.error = it.loginError
            binding.password.error = it.passwordError
            binding.confirmPassword.error = it.loginError
            binding.name.error = it.passwordError
        }

        viewModel.login.observe(viewLifecycleOwner) {
            binding.login.editText?.setText(it)
            binding.login.editText?.setSelection(it.length)
        }

        viewModel.password.observe(viewLifecycleOwner) {
            binding.password.editText?.setText(it)
            binding.password.editText?.setSelection(it.length)
        }

        viewModel.confirmPassword.observe(viewLifecycleOwner) {
            binding.confirmPassword.editText?.setText(it)
            binding.confirmPassword.editText?.setSelection(it.length)
        }

        viewModel.name.observe(viewLifecycleOwner) {
            binding.name.editText?.setText(it)
            binding.name.editText?.setSelection(it.length)
        }

        viewModel.token.observe(viewLifecycleOwner) {
            it?.let {
                AppAuth.getInstance().setAuth(it.id, it.token)
                viewModel.reset();
                findNavController().navigateUp()
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> viewModel.changePhoto(it.data?.data)
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.removePhoto.visibility = View.GONE
                return@observe
            }

            binding.removePhoto.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }





        return binding.root
    }

}