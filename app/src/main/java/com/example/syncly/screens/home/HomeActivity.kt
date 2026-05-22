package com.example.syncly.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.syncly.data.SessionManager
import com.example.syncly.data.User
import com.example.syncly.databinding.ActivityHomeBinding
import com.example.syncly.screens.availability.AvailabilityActivity
import com.example.syncly.screens.login.LoginActivity
import com.example.syncly.screens.profile.ProfileActivity

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

        binding.btnLogout.setOnClickListener { presenter.logout() }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnAvailability.setOnClickListener {
            startActivity(Intent(this, AvailabilityActivity::class.java))
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
        binding.tvWelcome.text = "Welcome, ${user.fullName ?: "User"}!"
        binding.tvEmail.text   = user.email ?: ""

        if (!user.photoUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(user.photoUrl)
                .circleCrop()
                .into(binding.ivAvatar)  // add this ImageView to activity_home.xml
        }
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