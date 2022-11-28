package com.nayeon.coroutinestudy

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.nayeon.coroutinestudy.api.*
import com.nayeon.coroutinestudy.database.LocalApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchApi: SearchApi,
    private val localApi: LocalApi,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application
) : AndroidViewModel(application) {
    val searchText = mutableStateOf("")
    val query = MutableLiveData<String>()
    var selectedItem: Item? = null

    val imageFlow = query.switchMap {
        Pager(PagingConfig(pageSize = 10)) {
            ImageDataSource(searchApi, it)
        }.liveData.cachedIn(viewModelScope)
    }.asFlow()

    private val starredItemList = MutableLiveData<List<Item>>()
    val starredItemListFlow = starredItemList.asFlow()

    private val starredLinkList = MutableLiveData<List<String>>()
    val starredLinkListFlow = starredLinkList.asFlow()

    init {
        viewModelScope.launch {
            updateStarredList()
        }
    }

    fun addOrDeleteStar(item: Item) {
        viewModelScope.launch {
            if (starredLinkList.value?.contains(item.link) == true) {
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
        val result = localApi.getAllStarredItem()
        val itemList = result.map { it.item }
        val linkList = result.map { it.urlString }
        starredItemList.postValue(itemList)
        starredLinkList.postValue(linkList)
    }

    fun download(imgUrl: String, title: String) {
        viewModelScope.launch(ioDispatcher) {
            DownloadModule
                .createDownloadRetrofit {
                    updateProgress(it)
                    Log.d("PROGRESS", "$it%")
                }
                .create(DownloadApi::class.java)
                .downloadImage(imgUrl).use {
                    DownloadUtils.download(
                        context = getApplication<Application?>().applicationContext,
                        name = title,
                        responseBody = it
                    )
                }
        }
    }

    private fun updateProgress(progress: Int) {
        // TODO : progress ui update
    }
}
