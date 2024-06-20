package com.bangkit.capstone.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityLoginBinding
import com.bangkit.capstone.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginMail.addTextChangedListener(object : TextWatcher {
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

        binding.loginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.loginPassword.errorCode == 1) {
                    binding.passAlert.text = binding.loginPassword.errorMessage
                    binding.passAlert.visibility = TextView.VISIBLE
                } else {
                    binding.passAlert.visibility = TextView.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnLogin.setOnClickListener {
            val emailOrUsername = binding.loginMail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(emailOrUsername, password)
            } else {
                Toast.makeText(
                    this,
                    "Email atau Username dan Password tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.regisButton.setOnClickListener {
            navigateToRegister()
        }

        val isLoggedIn = getSharedPreferences("LoginSession", Context.MODE_PRIVATE).getBoolean(
            "isLoggedIn",
            false
        )
        if (isLoggedIn) {
            toMainActivity()
        }

        viewModel.isLoading.observe(this) { isLoading -> showLoading(isLoading) }
        viewModel.isSuccess.observe(this) { isSuccess -> showLoginResponse(isSuccess) }
        viewModel.token.observe(this) { token -> saveSession(token) }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@LoginActivity as Activity,
                Pair(binding.img, "profile"),
                Pair(binding.loginMail, "email"),
                Pair(binding.loginPassword, "password"),
                Pair(binding.regisButton, "regbutton"),
            )
        startActivity(intent, optionsCompat.toBundle())
    }

    private fun saveSession(token: String?) {
        val sharedPreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("token", token)
        editor.apply()
        toMainActivity()
    }

    private fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            ObjectAnimator.ofFloat(binding.layoutLoginInfo, View.ALPHA, 0.2f).start()
            binding.loadLogin.visibility = View.VISIBLE
        } else {
            ObjectAnimator.ofFloat(binding.layoutLoginInfo, View.ALPHA, 1f).start()
            binding.loadLogin.visibility = View.GONE
        }
    }

    private fun showLoginResponse(isSuccess: Boolean) {
        if (isSuccess) {
            Toast.makeText(this@LoginActivity, "Login success", Toast.LENGTH_SHORT).show()
            saveUserDetails() // Save user details in SharedPreferences
        } else {
            binding.passAlert.visibility = View.VISIBLE
            binding.passAlert.text = resources.getString(R.string.login_false)
        }
    }

    private fun saveUserDetails() {
        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val email = binding.loginMail.text.toString()
        editor.putString("email", email)
        editor.apply()
    }
}
