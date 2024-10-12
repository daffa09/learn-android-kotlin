package com.learn.intent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvResult: TextView

    // Activity Result Launcher to handle activity result
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleActivityResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
    }

    // Initialize all the views and set click listeners
    private fun initViews() {
        findViewById<Button>(R.id.btn_move_activity).setOnClickListener(this)
        findViewById<Button>(R.id.btn_move_activity_data).setOnClickListener(this)
        findViewById<Button>(R.id.btn_move_activity_object).setOnClickListener(this)
        findViewById<Button>(R.id.btn_dial_number).setOnClickListener(this)
        findViewById<Button>(R.id.btn_move_for_result).setOnClickListener(this)
        tvResult = findViewById(R.id.tv_result)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_move_activity -> moveToActivity(MoveActivity::class.java)
            R.id.btn_move_activity_data -> moveToActivityWithData()
            R.id.btn_move_activity_object -> moveToActivityWithObject()
            R.id.btn_dial_number -> dialPhoneNumber("085157441749")
            R.id.btn_move_for_result -> moveForResult()
        }
    }

    // Navigate to an activity without passing data
    private fun moveToActivity(activityClass: Class<*>) {
        val intent = Intent(this@MainActivity, activityClass)
        startActivity(intent)
    }

    // Navigate to MoveWithDataActivity with additional data
    private fun moveToActivityWithData() {
        val intent = Intent(this@MainActivity, MoveWithDataActivity::class.java).apply {
            putExtra(MoveWithDataActivity.EXTRA_NAME, "Fanthom")
            putExtra(MoveWithDataActivity.EXTRA_AGE, 20)
        }
        startActivity(intent)
    }

    // Navigate to MoveWithObjectActivity with a Person object
    private fun moveToActivityWithObject() {
        val person = Person("Fanthom", 20, "daffa@gmail.com", "Depok")
        val intent = Intent(this@MainActivity, MoveWithObjectActivity::class.java).apply {
            putExtra(MoveWithObjectActivity.EXTRA_PERSON, person)
        }
        startActivity(intent)
    }

    // Dial a phone number using Intent.ACTION_DIAL
    private fun dialPhoneNumber(phoneNumber: String) {
        val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(dialPhoneIntent)
    }

    // Launch activity for result
    private fun moveForResult() {
        val intent = Intent(this@MainActivity, MoveForResultActivity::class.java)
        resultLauncher.launch(intent)
    }

    // Handle the result from the activity launched for result
    private fun handleActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == MoveForResultActivity.RESULT_CODE && data != null) {
            val selectedValue = data.getIntExtra(MoveForResultActivity.EXTRA_SELECTED_VALUE, 0)
            tvResult.text = "Hasil : $selectedValue"
        }
    }
}
