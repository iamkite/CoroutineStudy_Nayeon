package com.nayeon.coroutinestudy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StarDao {
    @Insert
    fun insert(star: Star)

    @Delete
    fun delete(star: Star)
}
