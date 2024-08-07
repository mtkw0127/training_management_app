package com.app.body_manage.ui.measure.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.app.body_manage.R
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.BodyPhoto
import com.app.body_manage.data.repository.LogRepository
import com.app.body_manage.dialog.FloatNumberPickerDialog
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.camera.CameraActivity
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_ADD
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_DELETE
import com.app.body_manage.ui.measure.list.MeasureListActivity.Companion.RESULT_CODE_EDIT
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import java.time.LocalDate
import java.time.LocalDateTime

class MeasureFormActivity : AppCompatActivity() {
    // 更新前の測定日時
    private val captureDateTime: LocalDateTime by lazy {
        intent.getSerializableExtra(
            KEY_CAPTURE_TIME
        ) as? LocalDateTime ?: LocalDateTime.now() // ランチャーの場合は今日
    }

    private val formType: FormType by lazy {
        intent.getSerializableExtra(FORM_TYPE) as? FormType ?: FormType.ADD // ランチャーの場合は追加
    }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val photoList = CameraActivity.photoList.toList()
            val photoModels =
                photoList.map { uri -> BodyPhoto(uri = uri) }.toList()
            viewModel.addPhotos(photoModels)
        }
    }

    // 写真詳細への遷移
    private val photoDetailLauncher = registerForActivityResult(StartActivityForResult()) {}

    private lateinit var viewModel: BodyMeasureEditFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setUp()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            BodyMeasureFormScreen(
                uiState = uiState,
                onClickNextDay = {
                    viewModel.setNextDay()
                },
                onClickPreviousDay = {
                    viewModel.setPreviousDay()
                },
                onClickTall = {
                    val tall =
                        (uiState as FormState.HasData).model.tall ?: return@BodyMeasureFormScreen
                    FloatNumberPickerDialog.createDialog(
                        label = getString(R.string.tall),
                        number = tall,
                        unit = getString(R.string.unit_cm),
                        supportOneHundred = true,
                    ) {
                        viewModel.setTall(it)
                    }.show(supportFragmentManager, null)
                },
                onClickDelete = {
                    viewModel.deleteBodyMeasure()
                    setResult(RESULT_CODE_DELETE)
                    finish()
                },
                onClickBackPress = { finish() },
                onClickTakePhoto = {
                    LogRepository().sendLog(
                        context = this,
                        key = LogRepository.KEY_OPEN_MEASURE_CAMERA,
                    )
                    cameraActivityLauncher.launch(CameraActivity.createCameraActivityIntent(this))
                },
                onClickSave = {
                    viewModel.save()
                    when (formType) {
                        FormType.ADD -> {
                            setResult(RESULT_CODE_ADD)
                            LogRepository().sendLog(
                                context = this,
                                key = LogRepository.KEY_MEASURE_ADD,
                            )
                        }

                        FormType.EDIT -> {
                            setResult(RESULT_CODE_EDIT)
                            LogRepository().sendLog(
                                context = this,
                                key = LogRepository.KEY_MEASURE_EDIT,
                            )
                        }
                    }
                    finish()
                },
                onClickPhotoDetail = {
                    photoDetailLauncher.launch(
                        PhotoDetailActivity.createIntent(
                            this,
                            it.uri
                        )
                    )
                },
                onClickDeletePhoto = {
                    viewModel.deletePhoto(it)
                },
                onClickTime = {
                    val time = (uiState as FormState.HasData).model.time
                    TimePickerDialog.createTimePickerDialog(time) { hour, minute ->
                        viewModel.setTime(
                            LocalDateTime.of(
                                time.year,
                                time.monthValue,
                                time.dayOfMonth,
                                hour,
                                minute
                            )
                        )
                    }.show(supportFragmentManager, null)
                },
                onChangeWeightDialog = {
                    FloatNumberPickerDialog.createDialog(
                        getString(R.string.weight),
                        (uiState as FormState.HasData).model.weight,
                        getString(R.string.unit_kg)
                    ) {
                        viewModel.setWeight(it)
                    }.show(supportFragmentManager, null)
                },
                onChangeMemo = {
                    viewModel.updateMemo(it)
                },
                onChangeFatDialog = {
                    FloatNumberPickerDialog.createDialog(
                        getString(R.string.fat),
                        (uiState as FormState.HasData).model.fat,
                        getString(R.string.unit_percent)
                    ) {
                        viewModel.setFat(it)
                    }.show(supportFragmentManager, null)
                }
            )
        }
    }

    private fun setUp() {
        viewModel = BodyMeasureEditFormViewModel(UserPreferenceRepository(this), application)
        when (formType) {
            FormType.ADD -> {
                viewModel.setType(FormViewModelState.Type.Add)
                val measureDate =
                    checkNotNull(intent.getSerializableExtra(KEY_CAPTURE_TIME) as? LocalDate)
                viewModel.setMeasureDate(measureDate)
                viewModel.loadLatestMeasure()
            }

            FormType.EDIT -> {
                viewModel.loadBodyMeasure(captureDateTime)
                viewModel.setType(FormViewModelState.Type.Edit)
                viewModel.setMeasureDate(captureDateTime.toLocalDate())
            }
        }
    }

    companion object {
        private const val FORM_TYPE = "FORM_TYPE"
        private const val KEY_CAPTURE_TIME = "KEY_CAPTURE_TIME"

        enum class FormType {
            ADD, EDIT
        }

        fun createMeasureEditIntent(
            context: Context,
            measureTime: LocalDateTime,
        ): Intent {
            return Intent(context, MeasureFormActivity::class.java).apply {
                putExtra(FORM_TYPE, FormType.EDIT)
                putExtra(KEY_CAPTURE_TIME, measureTime)
            }
        }

        fun createMeasureFormIntent(context: Context, measureDate: LocalDate): Intent {
            return Intent(context, MeasureFormActivity::class.java).apply {
                putExtra(FORM_TYPE, FormType.ADD)
                putExtra(KEY_CAPTURE_TIME, measureDate)
            }
        }
    }
}
