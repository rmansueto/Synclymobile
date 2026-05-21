package com.example.syncly.screens.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.syncly.data.SessionManager
import com.example.syncly.data.User
import com.example.syncly.databinding.ActivityRegisterBinding
import com.example.syncly.screens.home.HomeActivity
import com.example.syncly.screens.login.LoginActivity

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = RegisterPresenter(
            view    = this,
            model   = RegisterModel(),
            session = SessionManager(this)
        )

        binding.btnRegister.setOnClickListener {
            presenter.register(
                email           = binding.etEmail.text.toString(),
                password        = binding.etPassword.text.toString(),
                confirmPassword = binding.etConfirmPassword.text.toString(),
                fullName        = binding.etFullName.text.toString()
            )
        }

        binding.tvLogin.setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
    }

    override fun onRegisterSuccess(user: User) {
        Toast.makeText(
            this,
            "Welcome to Syncly, ${user.fullName ?: user.email}!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRegisterError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}