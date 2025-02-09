package com.nayeon.coroutinestudy

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = SEARCH_SCREEN_KEY) {
                        composable(SEARCH_SCREEN_KEY) { SearchScreen(navController = navController) }
                        composable(DETAIL_SCREEN_KEY) { DetailScreen(navController = navController) }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchScreen(navController: NavController) {
        val searchText: String by remember { viewModel.searchText }
        Column {
            SearchBar()
            if (searchText.isEmpty()) {
                StarredImage()
            } else {
                SearchImage(navController = navController)
            }
        }
    }

    @Preview
    @Composable
    fun SearchBar() {
        var searchText: String by remember { viewModel.searchText }

        Row(
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
        ) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    if (it.isEmpty()) {
                        viewModel.query.value = it
                    }
                },
                placeholder = { Text("검색어를 입력하세요") },
                singleLine = true
            )
            Button(onClick = {
                viewModel.query.value = searchText
            }) {
                Text("검색")
            }
        }
    }

    @Composable
    fun SearchImage(navController: NavController) {
        val imageList = viewModel.imageFlow.collectAsLazyPagingItems()
        val starredLinkList = viewModel.starredLinkListFlow.collectAsState(initial = listOf())

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(imageList.itemCount) { index ->
                imageList[index]?.let { item ->
                    GlideImage(
                        imageModel = item.link,
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                            .fillMaxWidth()
                            .aspectRatio(1.0f)
                            .clickable {
                                viewModel.selectedItem = item
                                navController.navigate(DETAIL_SCREEN_KEY)
                            }
                    )
                    Box {
                        Icon(
                            painter = painterResource(
                                id = if (starredLinkList.value.contains(item.link)) {
                                    R.drawable.ic_baseline_star_24
                                } else {
                                    R.drawable.ic_baseline_star_border_24
                                }
                            ),
                            contentDescription = "star border",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .clickable {
                                    viewModel.addOrDeleteStar(item)
                                }
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun StarredImage() {
        val starredItemList = viewModel.starredItemListFlow.collectAsState(initial = listOf())
        val starredLinkList = viewModel.starredLinkListFlow.collectAsState(initial = listOf())

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(starredItemList.value.size) { index ->
                val item = starredItemList.value[index]
                GlideImage(
                    imageModel = item.link,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .fillMaxWidth()
                        .aspectRatio(1.0f)
                )
                Box {
                    Icon(
                        painter = painterResource(
                            id = if (starredLinkList.value.contains(item.link)) {
                                R.drawable.ic_baseline_star_24
                            } else {
                                R.drawable.ic_baseline_star_border_24
                            }
                        ),
                        contentDescription = "star border",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .clickable {
                                viewModel.addOrDeleteStar(item)
                            }
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }

    @Composable
    fun DetailScreen(navController: NavController) {
        val item = viewModel.selectedItem ?: run {
            navController.navigateUp()
            return
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            DetailImage(navController = navController, thumbnailLink = item.thumbnail)
            DetailText(title = item.title, width = item.sizeWidth, height = item.sizeHeight)
            DownloadButton(imgUrl = item.link, title = item.title)
            DownloadProgress(imgUrl = item.link)
        }
    }

    @Composable
    fun DetailImage(navController: NavController, thumbnailLink: String) {
        GlideImage(
            imageModel = thumbnailLink,
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .clickable {
                    navController.navigateUp()
                }
        )
    }

    @Composable
    fun DetailText(title: String, width: Int, height: Int) {
        Text(text = title)
        Text("width : $width, height : $height")
    }

    @Composable
    fun DownloadButton(imgUrl: String, title: String) {
        OutlinedButton(onClick = { viewModel.download(imgUrl, title) }) {
            Text(text = "Download")
        }
    }

    @Composable
    fun DownloadProgress(imgUrl: String) {
        val downloadProgress = viewModel.downloadProgressFlow.collectAsState(initial = null)
        if (downloadProgress.value != null && imgUrl == viewModel.selectedItem?.link) {
            Text(text = "download : " + downloadProgress.value.toString() + "%")
        }
        if (downloadProgress.value == 100) {
            Toast.makeText(this, "Download Complete!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val SEARCH_SCREEN_KEY = "searchScreen"
        const val DETAIL_SCREEN_KEY = "detailScreen"
        const val IMAGE_ITEM_KEY = "imageItem"
    }
}
