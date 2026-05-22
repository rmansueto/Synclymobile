package com.example.syncly.screens.availability

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.syncly.R
import com.example.syncly.data.Availability
import com.example.syncly.data.SessionManager
import com.example.syncly.databinding.ActivityAvailabilityBinding
import com.google.android.material.button.MaterialButton

class AvailabilityActivity : AppCompatActivity(), AvailabilityContract.View {

    private lateinit var binding: ActivityAvailabilityBinding
    private lateinit var presenter: AvailabilityPresenter

    // Holds the current weekly state in memory — mirrors your web app's `weekly` state
    private val weekly = mutableMapOf<Int, MutableList<Availability>>()

    private val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvailabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init empty slots for all 7 days
        for (i in 0..6) weekly[i] = mutableListOf()

        presenter = AvailabilityPresenter(
            view    = this,
            model   = AvailabilityModel(),
            session = SessionManager(this)
        )

        binding.btnBack.setOnClickListener { presenter.onBackClicked() }

        binding.btnSave.setOnClickListener {
            presenter.saveAvailability(weekly)
        }

        presenter.loadAvailability()
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
    }

    override fun displayAvailability(weekly: Map<Int, List<Availability>>) {
        // Sync into local mutable map
        for ((day, slots) in weekly) {
            this.weekly[day] = slots.toMutableList()
        }
        renderWeekly()
    }

    // Renders all 7 days dynamically — mirrors your web app's days list
    private fun renderWeekly() {
        binding.containerDays.removeAllViews()

        for (day in 0..6) {
            val slots = weekly[day] ?: mutableListOf()
            val dayView = LayoutInflater.from(this)
                .inflate(R.layout.item_availability_day, binding.containerDays, false)

            val tvDay     = dayView.findViewById<TextView>(R.id.tvDayName)
            val container = dayView.findViewById<LinearLayout>(R.id.containerSlots)
            val btnAdd    = dayView.findViewById<MaterialButton>(R.id.btnAddSlot)

            tvDay.text = dayNames[day]

            // Render existing slots
            slots.forEachIndexed { index, slot ->
                addSlotView(container, day, index, slot)
            }

            // Add slot button
            btnAdd.setOnClickListener {
                val newSlot = Availability(
                    dayOfWeek = day,
                    startTime = "09:00",
                    endTime   = "17:00",
                    timezone  = "UTC"
                )
                weekly[day]?.add(newSlot)
                addSlotView(container, day, (weekly[day]?.size ?: 1) - 1, newSlot)
            }

            binding.containerDays.addView(dayView)
        }
    }

    // Adds a single time slot row with start/end time and remove button
    private fun addSlotView(
        container: LinearLayout,
        day: Int,
        index: Int,
        slot: Availability
    ) {
        val slotView = LayoutInflater.from(this)
            .inflate(R.layout.item_time_slot, container, false)

        val tpStart   = slotView.findViewById<TimePicker>(R.id.tpStart)
        val tpEnd     = slotView.findViewById<TimePicker>(R.id.tpEnd)
        val btnRemove = slotView.findViewById<ImageButton>(R.id.btnRemoveSlot)

        tpStart.setIs24HourView(true)
        tpEnd.setIs24HourView(true)

        // Parse "HH:mm" or "HH:mm:ss" from Spring Boot LocalTime
        val (startH, startM) = parseTime(slot.startTime)
        val (endH, endM)     = parseTime(slot.endTime)

        tpStart.hour   = startH
        tpStart.minute = startM
        tpEnd.hour     = endH
        tpEnd.minute   = endM

        // Update weekly state when time changes
        tpStart.setOnTimeChangedListener { _, h, m ->
            weekly[day]?.getOrNull(index)?.let {
                weekly[day]!![index] = it.copy(startTime = "%02d:%02d".format(h, m))
            }
        }
        tpEnd.setOnTimeChangedListener { _, h, m ->
            weekly[day]?.getOrNull(index)?.let {
                weekly[day]!![index] = it.copy(endTime = "%02d:%02d".format(h, m))
            }
        }

        btnRemove.setOnClickListener {
            weekly[day]?.removeAt(index)
            container.removeView(slotView)
        }

        container.addView(slotView)
    }

    // Handles "09:00", "09:00:00" formats from Spring Boot LocalTime
    private fun parseTime(time: String?): Pair<Int, Int> {
        if (time.isNullOrBlank()) return Pair(9, 0)
        val parts = time.split(":")
        return Pair(
            parts.getOrNull(0)?.toIntOrNull() ?: 9,
            parts.getOrNull(1)?.toIntOrNull() ?: 0
        )
    }

    override fun onSaveSuccess() {
        Toast.makeText(this, "Availability saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onError(message: String) {
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