package com.dicoding.latihantensorflowliteprediction

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.latihantensorflowliteprediction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var predictionHelper: PredictionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnPredict.isEnabled = false
        predictionHelper = PredictionHelper(
            context = this,
            onResult = {result ->
                binding.tvResult.text = result
            },
            onError = {errorMessage ->
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            },
            onDownloadSuccess = {
                binding.btnPredict.isEnabled = true
            }
        )

        binding.btnPredict.setOnClickListener {
            val inputString = binding.edSales.text.toString()
            predictionHelper.predict(inputString)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        predictionHelper.close()
    }
}