package com.nayeon.coroutinestudy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(star: Star)

    @Delete
    fun delete(star: Star)

    @Query("SELECT url_string FROM star")
    fun getAllStarredItem() : List<String>
}
