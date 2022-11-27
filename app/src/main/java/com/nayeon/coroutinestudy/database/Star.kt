package com.nayeon.coroutinestudy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nayeon.coroutinestudy.api.Item

@Entity
data class Star(
    @PrimaryKey @ColumnInfo(name = "url_string") val urlString: String,
    @ColumnInfo(name = "item") val item: Item,
)
