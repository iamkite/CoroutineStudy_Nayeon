package com.nayeon.coroutinestudy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.room.Room
import com.nayeon.coroutinestudy.api.IODispatcher
import com.nayeon.coroutinestudy.api.Item
import com.nayeon.coroutinestudy.api.SearchApi
import com.nayeon.coroutinestudy.database.LocalApi
import com.nayeon.coroutinestudy.database.Star
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchApi: SearchApi,
    private val localApi: LocalApi,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val searchText = mutableStateOf("")
    val query = MutableLiveData<String>()
    var selectedItem : Item? = null

    val imageFlow = query.switchMap {
        Pager(PagingConfig(pageSize = 10)) {
            ImageDataSource(searchApi, it)
        }.liveData.cachedIn(viewModelScope)
    }.asFlow()

    val starredList = MutableLiveData<List<String>>()
    val starredListFlow = starredList.asFlow()

    fun addOrDeleteStar(item: Item) {
        viewModelScope.launch {
            if (starredList.value?.contains(item.link) == true) {
                deleteStar(item)
            } else {
                saveStar(item)
            }
            updateStarredList()
        }
    }

    private suspend fun saveStar(item: Item) = withContext(ioDispatcher) {
        localApi.addStar(item)
    }

    private suspend fun deleteStar(item: Item) = withContext(ioDispatcher) {
        localApi.deleteStar(item)
    }

    private suspend fun updateStarredList() = withContext(ioDispatcher) {
        starredList.postValue(localApi.isStarredItem())
    }
}
