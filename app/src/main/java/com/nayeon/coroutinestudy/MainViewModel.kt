package com.nayeon.coroutinestudy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.nayeon.coroutinestudy.api.Item
import com.nayeon.coroutinestudy.api.SearchApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchApi: SearchApi
) : ViewModel() {
    val searchText = mutableStateOf("")
    val query = MutableLiveData<String>()
    var selectedItem : Item? = null

    val imageFlow = query.switchMap {
        Pager(PagingConfig(pageSize = 10)) {
            ImageDataSource(searchApi, it)
        }.liveData.cachedIn(viewModelScope)
    }.asFlow()
}
