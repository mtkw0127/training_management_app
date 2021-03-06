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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.app.body_manage.R
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import timber.log.Timber

class CameraActivity : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var cameraSelector: CameraSelector

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_preview)
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
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        // バックの場合、撮影した撮影した撮影を除去
        val backButton = findViewById<Button>(R.id.back_from_camera_btn)
        backButton.setOnClickListener {
            photoList.clear()
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
                            val photoUri = checkNotNull(outputFileResults.savedUri)
                            // Prevに追加
                            val imageView = findViewById<ImageView>(R.id.captured_img)
                            imageView?.setImageURI(photoUri)
                            // 一覧に追加
                            photoList.add(photoUri)
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
    private fun startCamera() {
        val previewView = findViewById<PreviewView>(R.id.camera_preview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case to display camera preview.
            val preview = Preview.Builder().build()

            // Set up the capture use case to allow users to take photos.
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()

            // Attach use cases to the camera with the same lifecycle owner
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture
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

        val photoList: MutableList<Uri> = mutableListOf()

        fun createCameraActivityIntent(context: Context): Intent {
            photoList.clear()
            return Intent(context.applicationContext, CameraActivity::class.java)
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