package com.dicoding.learn.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.learn.databinding.ActivityMainBinding
import com.dicoding.learn.R
import com.dicoding.learn.helpers.showMaterialDialog
import com.dicoding.learn.ui.adapter.LoadingStateAdapter
import com.dicoding.learn.ui.adapter.StoryListAdapter
import com.dicoding.learn.ui.viewmodels.MainViewModel
import com.dicoding.learn.ui.viewmodels.ViewModelFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val storyAdapter : StoryListAdapter = StoryListAdapter()
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addStory.setOnClickListener {
            startActivity(Intent(this, UploadStoryActivity::class.java))
        }

        viewModel.getSession().observe(this) {user ->
            if(!user.isLoading) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                setupRv()
                setupData()
            }
        }
    }

    private fun setupRv() {
        binding.recyclerView.apply {
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupData() {
        viewModel.story.observe(this) {data ->
            storyAdapter.submitData(lifecycle, data)
            binding.loadStory.visibility = View.GONE
        }
    }

    @Suppress("DEPRECATION")
    private fun changeAppLanguage(code : String) {
        val locale = Locale(code)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        recreate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                showMaterialDialog(
                    this@MainActivity,
                    "Logout",
                    "Are you sure you want to logout?",
                    "Yes",
                    "No",
                    onPositiveClick = {
                        viewModel.logout()
                    }
                )
                true
            }

            R.id.language_english -> {
                changeAppLanguage("en")
                true
            }
            R.id.language_indonesian -> {
                changeAppLanguage("id")
                true
            }
            R.id.language_japanese -> {
                changeAppLanguage("ja")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}