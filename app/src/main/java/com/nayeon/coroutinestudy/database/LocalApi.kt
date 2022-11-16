package com.nayeon.coroutinestudy.database

import android.content.Context
import com.nayeon.coroutinestudy.api.Item
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalApi @Inject constructor(@ApplicationContext val context: Context) {
    val database : AppDatabase? by lazy { AppDatabase.getInstance(context) }
    val starDao : StarDao? by lazy { database?.starDao() }

    fun addStar(item: Item) {
        val starItem = Star(
            uid = 0,
            item = item,
            starred = true
        )
        starDao?.insert(starItem)
    }

    fun deleteStar(star: Star) {
        starDao?.delete(star)
    }
}
