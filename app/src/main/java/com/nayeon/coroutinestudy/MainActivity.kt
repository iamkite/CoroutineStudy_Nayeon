package com.nayeon.coroutinestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nayeon.coroutinestudy.ui.theme.CoroutineStudyTheme
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutineStudyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        SearchBar()
                        SearchImage()
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun SearchBar() {
        var searchText : String by remember { viewModel.searchText }

        Row(
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("검색어를 입력하세요") }
            )
            Button(onClick = { viewModel.getOneImageUrl(searchText) }) {
                Text("검색")
            }
        }
    }

    @Preview
    @Composable
    fun SearchImage() {
        val link = viewModel.link.observeAsState()

        GlideImage(
            imageModel = link.value,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}