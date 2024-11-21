package com.dicoding.learn

import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sp: SoundPool
    private var soundInt: Int = 0
    private var spLoaded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSound = findViewById<Button>(R.id.btn_sound_pool)

        sp = SoundPool.Builder()
            .setMaxStreams(10)
            .build()

        sp.setOnLoadCompleteListener{_,_,status ->
            if (status == 0) {
                spLoaded = true
                Log.d("SoundPool", "Load Success")
            } else {
                Log.d("SoundPool", "Load gagal")
                Toast.makeText(this@MainActivity, "Gagal Load", Toast.LENGTH_SHORT).show()
            }
        }

        soundInt = sp.load(this, R.raw.songs, 1)

        btnSound.setOnClickListener{
            if (spLoaded) {
                Log.d("SoundPool", "Setel Musik")
                sp.play(soundInt, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}