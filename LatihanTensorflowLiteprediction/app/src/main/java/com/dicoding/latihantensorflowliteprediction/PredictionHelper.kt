package com.dicoding.latihantensorflowliteprediction

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PredictionHelper(
    private val modelName: String = "rice_stock.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onDownloadSuccess: () -> Unit
) {
    private var interpreter: InterpreterApi? = null
    private var isGPUSupported: Boolean = false

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).addOnSuccessListener { isAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (isAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
                isGPUSupported = true
            }
            TfLite.initialize(context, optionsBuilder.build())
                .addOnSuccessListener {
                    Log.d(TAG, "TensorFlow Lite successfully initialized. in init")
                    // loadLocalModel()
                    downloadModel()
                }
                .addOnFailureListener { exception ->
                    val errorMsg = "Failed to initialize TensorFlow Lite: ${exception.message}"
                    Log.e(TAG, errorMsg)
                    onError(errorMsg)
                }
        }.addOnFailureListener {
            val errorMsg = context.getString(R.string.tflite_is_not_initialized_yet)
            Log.e(TAG, "GPU delegate check failed: $errorMsg")
            onError(errorMsg)
        }
    }

    @Synchronized
    private fun downloadModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Rice-Stock", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                try {
                    onDownloadSuccess()
                    initializeInterpreter(model)
                } catch (e: IOException) {
                    onError(e.message.toString())
                }
            }
            .addOnFailureListener { e: Exception? ->
                onError(context.getString(R.string.firebaseml_model_download_failed))
            }
    }

    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            if (isGPUSupported) {
                options.addDelegateFactory(GpuDelegateFactory())
                Log.d(TAG, "GPU delegate is enabled.")
            }
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
                Log.d(TAG, "Interpreter initialized successfully. in byteBuffer")
            } else if (model is CustomModel) {
                model.file?.let {
                    interpreter = InterpreterApi.create(it, options)
                    Log.d(TAG, "Interpreter initialized successfully. in customModel")
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Error initializing interpreter: ${e.message}"
            Log.e(TAG, errorMsg)
            onError(errorMsg)
        }
    }

    fun close() {
        interpreter?.close()
        Log.d(TAG, "Interpreter closed.")
    }

    fun predict(inputString: String) {
        if (interpreter == null) {
            val errorMsg = "Interpreter is not initialized yet."
            Log.e(TAG, errorMsg)
            onError(errorMsg)
            return
        }

        try {
            val inputArray = FloatArray(1)
            inputArray[0] = inputString.toFloat()
            val outputArray = Array(1) { FloatArray(1) }

            interpreter?.run(inputArray, outputArray)
            val result = outputArray[0][0].toString()
            Log.d(TAG, "Prediction result: $result")
            onResult(result)
        } catch (e: Exception) {
            val errorMsg = "Error during prediction: ${e.message}"
            Log.e(TAG, errorMsg)
            onError(errorMsg)
        }
    }


    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    companion object {
        private const val TAG = "PredictionHelper"
    }
}
