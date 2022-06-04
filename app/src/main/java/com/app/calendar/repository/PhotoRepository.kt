package com.app.calendar.repository

import androidx.annotation.WorkerThread
import com.app.calendar.dao.PhotoDao
import com.app.calendar.model.PhotoEntity

class PhotoRepository(private val photoDao: PhotoDao) {

    @WorkerThread
    suspend fun insert(photoEntityList: List<PhotoEntity>): List<Long> {
        return photoDao.insert(photoEntityList)
    }
}