package com.app.body_manage.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import com.app.body_manage.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.lang.Double.parseDouble
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import timber.log.Timber


class MeasureCameraActivity : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var cameraSelector: CameraSelector

    private lateinit var photoUri: Uri
    private lateinit var cameraExecutor: ExecutorService
    private val recognitionMap = mutableMapOf<Double, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_preview_with_number)
        initCamera()
        // バックグラウンドのエグゼキュータ
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionCheck()
        }
        val nextButton = findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            // 撮影した結果を返却
            if (this@MeasureCameraActivity::photoUri.isInitialized) {
                intent.putExtra(INTENT_KEY_PHOTO_URI, photoUri)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
        val backButton = findViewById<Button>(R.id.back_from_camera_btn)
        backButton.setOnClickListener {
            finish()
        }
        val switchCameraButton = findViewById<Button>(R.id.switch_camera)
        switchCameraButton.setOnClickListener {
            lensFacing = when (lensFacing) {
                CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
                CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
                else -> CameraSelector.LENS_FACING_BACK
            }
            initCamera()
            startCamera()
        }
        val takePhotoButton = findViewById<Button>(R.id.shutter_btn)
        takePhotoButton.setOnClickListener {
            val photoOutputFilePath = createFile(it.context)
            val metadata = Metadata().apply {
                // インカメの場合は写真を反転する
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(photoOutputFilePath.toFile())
                .setMetadata(metadata)
                .build()

            val imageSavedCapture: ImageCapture.OnImageSavedCallback =
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // TODO: シャッター音をならす
                        Handler(Looper.getMainLooper()).post {
                            // 古い写真を削除
                            if (this@MeasureCameraActivity::photoUri.isInitialized) photoUri.toFile()
                                .delete()
                            photoUri = checkNotNull(outputFileResults.savedUri)
                            val imageView = findViewById<ImageView>(R.id.captured_img)
                            imageView?.setImageURI(photoUri)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // TODO: シャッター音をならす
                        Handler(Looper.getMainLooper()).post {
                            Timber.e(exception)
                            Toast.makeText(applicationContext, "写真の撮影に失敗しました。", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            imageCapture.takePicture(outputOptions, cameraExecutor, imageSavedCapture)
        }
    }

    private fun initCamera() {
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    /**
     * カメラ起動
     */
    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val previewView = findViewById<PreviewView>(R.id.camera_preview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case to display camera preview.
            val preview = Preview.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                // enable the following line if RGBA output is needed.
                // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it ->
                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            val recognizer = TextRecognition.getClient()
                            recognizer.process(image)
                                .addOnSuccessListener { result ->
                                    if (result.textBlocks.isNotEmpty()) {
                                        val textList = result.textBlocks
                                            .map {
                                                it.text.replace("S", "5")
                                                    .replace("s", "5")
                                                    .replace("o", "0")
                                                    .replace("O", "0")
                                                    .replace("H", "4")
                                            }.filter {
                                                if (it.contains("E") ||
                                                    it.contains("e") ||
                                                    it.contains("D")
                                                ) return@filter false

                                                var numCheck = false
                                                try {
                                                    val parsed = parseDouble(it)
                                                    numCheck = parsed != 0.0
                                                } catch (e: Throwable) {
                                                }
                                                return@filter numCheck
                                            }.map {
                                                parseDouble(it)
                                            }.map {
                                                when (it) {
                                                    in 10.0..99.9 -> {
                                                        it
                                                    }
                                                    in 100.0..999.9 -> {
                                                        it / 10
                                                    }
                                                    else -> {
                                                        it / 100
                                                    }
                                                }
                                            }
                                            .toList()
                                        textList.forEach {
                                            if (recognitionMap.containsKey(it)) {
                                                recognitionMap[it] =
                                                    checkNotNull(recognitionMap[it]) + 1
                                            } else {
                                                recognitionMap[it] = 1
                                            }
                                        }

                                        val recognitionResultView =
                                            findViewById<TextView>(R.id.recognized)
                                        recognitionResultView.text =
                                            recognitionMap.map {
                                                "${it.key}kg -> ${it.value}回"
                                            }.toList().joinToString("\r\n")
                                    }
                                    imageProxy.close()
                                }.addOnFailureListener {
                                    it.printStackTrace()
                                    imageProxy.close()
                                }
                        }

//                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
//                        // insert your code here.
//                        // after done, release the ImageProxy object
//                        imageProxy.close()
                    })
                }

            // Set up the capture use case to allow users to take photos.
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()

            // Attach use cases to the camera with the same lifecycle owner
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
            )

            // Connect the preview use case to the previewView
            preview.setSurfaceProvider(
                previewView.surfaceProvider
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun permissionCheck() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (i in grantResults.indices) {
                val checkResult = grantResults[i] == PackageManager.PERMISSION_GRANTED
                if (!checkResult) {
                    finish()
                }
            }
            startCamera()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PHOTO_EXTENSION = ".jpg"
        private const val FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSSS"
        const val INTENT_KEY_PHOTO_URI = "PHOTO_URI"
        fun createCameraActivityIntent(context: Context): Intent {
            return Intent(context.applicationContext, MeasureCameraActivity::class.java)
        }

        private fun createFile(context: Context): Path {
            val fileName = SimpleDateFormat(
                FILE_FORMAT,
                Locale.JAPAN
            ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
            return context.filesDir.toPath().resolve(fileName)
        }
    }
}