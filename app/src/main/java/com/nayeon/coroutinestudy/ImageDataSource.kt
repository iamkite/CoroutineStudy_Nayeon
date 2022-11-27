package com.nayeon.coroutinestudy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nayeon.coroutinestudy.api.Item
import com.nayeon.coroutinestudy.api.SearchApi

class ImageDataSource(
    private val searchApi: SearchApi,
    private val query: String
) : PagingSource<Int, Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val nextPageNum = params.key ?: 0
            val response = searchApi.searchImage(query = query, start = nextPageNum * 10 + 1)

            LoadResult.Page(
                data = response.items,
                prevKey = if (nextPageNum == 0) null else nextPageNum - 1,
                nextKey = nextPageNum + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
