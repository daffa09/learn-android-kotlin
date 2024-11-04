package com.dicoding.picodiploma.mycamera

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.camera.core.ImageProxy
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var maxResult: Int = 5,
    val modelName: String = "efficientdet_lite0_v1.tflite",
    val context: Context,
    val detectorListener: DetectorListener?
) {

    private var objectDetector: ObjectDetector? = null

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long
        )
    }

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupObjectDetector()
        }.addOnFailureListener {
            detectorListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
            Log.e(TAG, it.message.toString())
        }
    }

    private fun setupObjectDetector() {
        val optionBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)
        val baseOptionsBuilder = BaseOptions.builder()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                Log.d(TAG, "Using NNAPI delegate.")
                baseOptionsBuilder.useNnapi()
            } else {
                Log.d(TAG, "Using CPU with 4 threads.")
                baseOptionsBuilder.setNumThreads(4)
            }


        optionBuilder.setBaseOptions(baseOptionsBuilder.build())


        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun detectObject(image: ImageProxy) {

        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            detectorListener?.onError(errorMessage)
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitMap(image)))


        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        detectorListener?.onResults(
            results,
            inferenceTime,
            )
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when(rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    private fun toBitMap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}