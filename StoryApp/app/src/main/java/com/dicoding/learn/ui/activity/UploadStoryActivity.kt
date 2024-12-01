package com.dicoding.learn.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.learn.data.model.AddStoryResponse
import com.dicoding.learn.databinding.ActivityUploadStoryBinding
import com.dicoding.learn.helpers.Result
import com.dicoding.learn.helpers.getImageUri
import com.dicoding.learn.helpers.reduceFileImage
import com.dicoding.learn.helpers.showMaterialDialog
import com.dicoding.learn.helpers.uriToFile
import com.dicoding.learn.ui.viewmodels.UploadStoryViewModel
import com.dicoding.learn.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUploadStoryBinding
    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var lat : Float? = null
    private var lon : Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        viewModel.curImage.observe(this) {image ->
            binding.imagePreview.setImageURI(image)
        }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonUpload.setOnClickListener { uploadStory() }
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri : Uri? ->
        if(uri!= null) {
            binding.imagePreview.setImageURI(uri)
            viewModel.setCurImage(uri)
        }
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if(it) {
            binding.imagePreview.setImageURI(viewModel.curImage.value)
        } else {
            viewModel.setCurImage(null)
        }
    }

    private fun startGallery() = launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    private fun startCamera() {
        viewModel.setCurImage(getImageUri(this))
        launchIntentCamera.launch(viewModel.curImage.value as Uri)
    }

    private fun uploadStory() {
        val uri = viewModel.curImage.value ?: return
        lifecycleScope.launch {
            try {
                showProgress(true)

                val image = withContext(Dispatchers.IO) {
                    uriToFile(uri, this@UploadStoryActivity).reduceFileImage()
                }
                Log.d("path", image.path)

                val desc = binding.editTextDescription.text.toString()


                viewModel.uploadStory(image, desc, lat, lon).observe(this@UploadStoryActivity) { res ->
                    handleUploadResult(res)
                }
            } catch (e: Exception) {
                showProgress(false)
                Log.e("UploadStory", "Error during image upload", e)
                showMaterialDialog(this@UploadStoryActivity, "Error", "Unexpected error occurred", "Retry")
            }
        }
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressUpload.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun handleUploadResult(res: Result<AddStoryResponse>) {
        when (res) {
            is Result.Loading -> showProgress(true)
            is Result.Error -> {
                showProgress(false)
                showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.error}", "Retry")
                Log.d("UploadStoryError", res.error)
            }
            is Result.Success -> {
                showProgress(false)
                if (res.data.error == true) {
                    showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.data.message}", "Retry")
                } else {
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@UploadStoryActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}