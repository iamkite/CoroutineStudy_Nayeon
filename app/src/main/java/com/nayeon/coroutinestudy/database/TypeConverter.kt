package com.nayeon.coroutinestudy.database

import com.google.gson.GsonBuilder
import com.nayeon.coroutinestudy.api.Item

class TypeConverter {
    private val gson = GsonBuilder().create()

    @androidx.room.TypeConverter
    fun stringToItem(itemString: String?): Item? {
        itemString ?: return null
        return gson.fromJson(itemString, Item::class.java)
    }

    @androidx.room.TypeConverter
    fun itemToString(item: Item?): String? {
        item ?: return null
        return gson.toJson(item)
    }
}
