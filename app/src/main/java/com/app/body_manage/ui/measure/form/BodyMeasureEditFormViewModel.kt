package com.app.body_manage.ui.measure.form

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.PhotoEntity
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.PhotoRepository
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.launch
import timber.log.Timber

class BodyMeasureEditFormViewModel : ViewModel() {

    enum class PhotoType {
        Saved, ADDED
    }

    // 撮影した写真データはここに保存する
    data class PhotoModel(val id: Int = -1, val uri: Uri, val photoType: PhotoType)

    private val _photoList = MutableLiveData<MutableList<PhotoModel>>(mutableListOf())
    val photoList: LiveData<MutableList<PhotoModel>> = _photoList

    fun addPhotos(photoList: List<PhotoModel>) {
        _photoList.value?.addAll(photoList)
        _photoList.value = _photoList.value
    }

    fun deletePhoto(position: Int) {
        _photoList.value?.removeAt(position)
        _photoList.value = _photoList.value
    }

    var application: Application? = null
    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).bodyMeasureRepository
    }
    private val photoRepository: PhotoRepository by lazy {
        (application as TrainingApplication).photoRepository
    }

    lateinit var intent: Intent

    // 測定日時
    val captureDate: LocalDate by lazy {
        intent.getSerializableExtra(BodyMeasureEditFormActivity.KEY_CAPTURE_DATE) as LocalDate
    }

    // 更新後の測定日時
    lateinit var measureTime: LocalDateTime
    var measureWeight = 50F
    var measureFat = 20.0F

    // coroutineによるローディング取得
    lateinit var bodyMeasureEntity: BodyMeasureEntity
    var loadedBodyMeasure = MutableLiveData(false)
    private var loadingBodyMeasure = MutableLiveData(false)
    fun loadBodyMeasure() {
        // ロード中はロードしない
        if (loadingBodyMeasure.value == true) return
        loadingBodyMeasure.value = true
        // 対象の日付に紐づくデータが存在すれば取得する.
        viewModelScope.launch {
            runCatching { bodyMeasureRepository.getEntityByCaptureTime(measureTime) }
                .onFailure { e ->
                    Toast.makeText(
                        application,
                        "読み込みに失敗しました。",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
                .onSuccess { res ->
                    val it = res[0]
                    bodyMeasureEntity = it
                    measureTime = it.capturedTime
                    measureWeight = it.weight
                    measureFat = it.fatRate
                    loadedBodyMeasure.value = true
                }.also {
                    // ロード中終了
                    loadingBodyMeasure.value = false
                }
        }
    }

    private val loadingPhoto = MutableLiveData(false)
    fun loadPhotos() {
        if (loadingPhoto.value == true) return
        loadingPhoto.value = true
        viewModelScope.launch {
            runCatching { photoRepository.selectPhotos(bodyMeasureId = bodyMeasureEntity.ui) }
                .onFailure { e ->
                    e.printStackTrace()
                    Timber.e(e)
                    Toast.makeText(
                        application,
                        "写真の読み込みに失敗しました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onSuccess { photos ->
                    if (photos.isNotEmpty()) {
                        _photoList.value =
                            photos.map {
                                PhotoModel(it.ui, it.photoUri.toUri(), PhotoType.Saved)
                            }.toList().toMutableList()
                    }
                }
                .also {
                    loadingPhoto.value = false
                }
        }
    }

    /**
     * 写真を登録
     */
    fun addPhoto(saveModel: BodyMeasureEntity) {
        viewModelScope.launch {
            runCatching {
                // 最新の写真をサムネイルに設定
                if (_photoList.value?.isNotEmpty() == true) {
                    saveModel.photoUri = checkNotNull(_photoList.value).last().uri.toString()
                }
                val id = bodyMeasureRepository.insert(saveModel)
                if (_photoList.value?.isNotEmpty() == true) {
                    photoRepository.insert(
                        createPhotoModels(id.toInt())
                    )
                }
            }.onFailure { it.printStackTrace() }
        }
    }

    /**
     * 写真を更新
     */
    fun editPhoto(saveModel: BodyMeasureEntity) {
        viewModelScope.launch {
            saveModel.ui = bodyMeasureEntity.ui
            // 紐づく写真を全件->再登録して更新
            runCatching {
                photoRepository.deletePhotos(bodyMeasureEntity.ui)
                if (_photoList.value?.isNotEmpty() == true) {
                    photoRepository.insert(
                        createPhotoModels(bodyMeasureEntity.ui)
                    )
                    // 最新の写真をサムネイルに設定
                    saveModel.photoUri = checkNotNull(_photoList.value).last().uri.toString()
                }
                bodyMeasureRepository.update(saveModel)
            }.onFailure { it.printStackTrace() }
        }
    }

    /**
     * 写真のモデル生成
     */
    private fun createPhotoModels(id: Int): List<PhotoEntity> {
        val photoList = checkNotNull(_photoList.value)
        return photoList.map {
            PhotoEntity(
                ui = 0,
                bodyMeasureId = id,
                photoUri = it.uri.toString()
            )
        }
    }
}