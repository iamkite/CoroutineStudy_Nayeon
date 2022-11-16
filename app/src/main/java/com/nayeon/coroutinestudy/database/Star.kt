package com.nayeon.coroutinestudy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nayeon.coroutinestudy.api.Item

@Entity
data class Star(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "item") val item: Item,
    @ColumnInfo(name = "starred") val starred: Boolean
)
