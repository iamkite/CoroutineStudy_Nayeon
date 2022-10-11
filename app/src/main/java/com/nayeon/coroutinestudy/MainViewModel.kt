package com.nayeon.coroutinestudy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nayeon.coroutinestudy.api.SearchApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchApi: SearchApi
) : ViewModel() {

    private val _link = MutableLiveData<String?>()
    val link : LiveData<String?> get() = _link

    init {
        getOneImageUrl("apple")
    }

    private fun getOneImageUrl(query: String?) {
        viewModelScope.launch {
            if (query.isNullOrEmpty()) {
                _link.postValue(null)
            } else {
                val imageSearchResponse = searchApi.searchOneImage(query = query)
                val link = imageSearchResponse.items[0].link
                _link.postValue(link)
            }
        }
    }
}