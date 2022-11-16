package com.nayeon.coroutinestudy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.room.Room
import com.nayeon.coroutinestudy.api.Item
import com.nayeon.coroutinestudy.api.SearchApi
import com.nayeon.coroutinestudy.database.LocalApi
import com.nayeon.coroutinestudy.database.Star
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchApi: SearchApi,
    private val localApi: LocalApi
) : ViewModel() {
    val searchText = mutableStateOf("")
    val query = MutableLiveData<String>()
    var selectedItem : Item? = null

    val imageFlow = query.switchMap {
        Pager(PagingConfig(pageSize = 10)) {
            ImageDataSource(searchApi, it)
        }.liveData.cachedIn(viewModelScope)
    }.asFlow()

    fun addStar(item: Item) {
        localApi.addStar(item)
    }

    fun deleteStar(star: Star) {
        localApi.deleteStar(star)
    }
}
