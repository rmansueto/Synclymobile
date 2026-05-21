package com.example.syncly.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// ── View ──────────────────────────────────────────────────────────────────────

fun View.show()      { visibility = View.VISIBLE }
fun View.hide()      { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.isVisible() = visibility == View.VISIBLE

// ── Keyboard ──────────────────────────────────────────────────────────────────

fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
}

fun EditText.onDone(action: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) { action(); true } else false
    }
}

// ── Toast ─────────────────────────────────────────────────────────────────────

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toastLong(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

// ── Validation ────────────────────────────────────────────────────────────────

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = length >= 6

fun String.isNotBlankOrEmpty(): Boolean = isNotBlank() && isNotEmpty()