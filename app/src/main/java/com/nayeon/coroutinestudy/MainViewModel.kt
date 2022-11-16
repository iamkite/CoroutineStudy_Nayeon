package com.nayeon.coroutinestudy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.*
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

    private val viewModelJob = SupervisorJob()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun addStar(item: Item) {
        uiScope.launch {
            saveStar(item)
        }
    }

    private suspend fun saveStar(item: Item) = withContext(Dispatchers.Default) {
        localApi.addStar(item)
    }

    fun deleteStar(star: Star) {
        localApi.deleteStar(star)
    }
}
