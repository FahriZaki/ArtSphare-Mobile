package com.bangkit.capstone.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityRegisterBinding
import com.bangkit.capstone.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regisMail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val email = editable.toString()
                if (viewModel.isEmailValid(email)) {
                    binding.emailAlert.text = ""
                } else {
                    binding.emailAlert.text = resources.getString(R.string.email_false)
                }
            }

        })
        binding.regisPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.regisPass.errorCode == 1) {
                    binding.passwordWarning.text = binding.regisPass.errorMessage
                    binding.passwordWarning.visibility = TextView.VISIBLE
                } else {
                    binding.passwordWarning.visibility = TextView.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.regisMail.text.toString()
            val password = binding.regisPass.text.toString()

            viewModel.registerUser(name, email, password)
            viewModel.isSuccess.observe(this) { isSuccess ->
                showRegisterResponse(isSuccess)
            }
        }
    }

    private fun showRegisterResponse(isSuccess: Boolean) {
        if (isSuccess) {
            binding.passwordWarning.apply {
                visibility = View.VISIBLE
                setTextColor(Color.BLUE)
                text = resources.getString(R.string.regis_true)
            }
        } else {
            binding.passwordWarning.apply {
                visibility = View.INVISIBLE
                setTextColor(Color.RED)
                text = resources.getString(R.string.regis_false)
            }
        }
    }
}
