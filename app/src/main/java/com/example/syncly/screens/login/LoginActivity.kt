package com.example.syncly.screens.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.syncly.data.SessionManager
import com.example.syncly.data.User
import com.example.syncly.databinding.ActivityLoginBinding
import com.example.syncly.screens.home.HomeActivity
import com.example.syncly.screens.register.RegisterActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        // Auto-navigate if already logged in
        if (session.isLoggedIn()) {
            navigateToHome()
            return
        }

        presenter = LoginPresenter(
            view    = this,
            model   = LoginModel(),
            session = session
        )

        binding.btnLogin.setOnClickListener {
            presenter.login(
                email    = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString()
            )
        }

        binding.tvRegister.setOnClickListener {
            presenter.onRegisterClicked()
        }
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    override fun onLoginSuccess(user: User) {
        Toast.makeText(
            this,
            "Welcome back, ${user.fullName ?: user.email}!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onLoginError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}