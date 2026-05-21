package com.example.syncly.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.syncly.data.SessionManager
import com.example.syncly.data.User
import com.example.syncly.databinding.ActivityHomeBinding
import com.example.syncly.screens.login.LoginActivity

class HomeActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = HomePresenter(
            view    = this,
            model   = HomeModel(),
            session = SessionManager(this)
        )

        binding.btnLogout.setOnClickListener {
            presenter.logout()
        }

        presenter.loadUserData()
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun displayUserInfo(user: User) {
        binding.tvWelcome.text  = "Welcome, ${user.fullName ?: "User"}!"
        binding.tvEmail.text    = user.email ?: ""
        // photoUrl ready to use — load with Glide/Picasso when you add it:
        // Glide.with(this).load(user.photoUrl).into(binding.ivAvatar)
    }

    override fun onLoadError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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