package com.example.syncly.screens.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.syncly.data.SessionManager
import com.example.syncly.data.User
import com.example.syncly.databinding.ActivityProfileBinding
import java.io.File
import java.io.FileOutputStream
import com.example.syncly.R

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var presenter: ProfilePresenter
    private var selectedPhotoFile: File? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ProfilePresenter(
            view    = this,
            model   = ProfileModel(),
            session = SessionManager(this)
        )

        binding.btnBack.setOnClickListener { presenter.onBackClicked() }

        binding.btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnSave.setOnClickListener {
            presenter.updateProfile(
                fullName        = binding.etFullName.text.toString(),
                newPassword     = binding.etNewPassword.text.toString().takeIf { it.isNotBlank() },
                confirmPassword = binding.etConfirmPassword.text.toString().takeIf { it.isNotBlank() },
                photoFile       = selectedPhotoFile
            )
        }

        binding.btnCancel.setOnClickListener { presenter.onBackClicked() }

        presenter.loadProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            selectedPhotoFile = uriToFile(uri)
            binding.ivAvatar.setImageURI(uri)
        }
    }

    // Convert URI to a temp File so we can send it as multipart
    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(tempFile).use { output ->
            inputStream?.copyTo(output)
        }
        return tempFile
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
    }

    override fun displayUser(user: User) {
        binding.etFullName.setText(user.fullName ?: "")
        binding.tvEmail.setText(user.email ?: "")

        // Load profile photo from Supabase storage URL via Glide
        if (!user.photoUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.mipmap.ic_launcher)   // shows while loading
                .error(R.mipmap.ic_launcher)          // shows if URL fails
                .circleCrop()                         // circular like your web app
                .into(binding.ivAvatar)
        }
    }

    override fun onUpdateSuccess(user: User) {
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        displayUser(user)
    }

    override fun onUpdateError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateBack() {
        finish()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}