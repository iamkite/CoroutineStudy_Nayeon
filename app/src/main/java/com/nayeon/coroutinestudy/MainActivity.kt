package com.nayeon.coroutinestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.nayeon.coroutinestudy.api.Item
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
                        composable(
                            route = "${DETAIL_SCREEN_KEY}/{${IMAGE_ITEM_KEY}}",
                            arguments = listOf(navArgument(IMAGE_ITEM_KEY) { type = NavType.ParcelableType(Item::class.java) })
                        ) { backStackEntry ->
                            DetailScreen(navController = navController, item = backStackEntry.arguments?.getParcelable(IMAGE_ITEM_KEY) ?: return@composable)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchScreen(navController: NavController) {
        Column {
            SearchBar()
            SearchImage(navController = navController)
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
                onValueChange = { searchText = it },
                placeholder = { Text("검색어를 입력하세요") }
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
                                navController.navigate("${DETAIL_SCREEN_KEY}/$item")
                            }
                    )
                }
            }
        }
    }

    @Composable
    fun DetailScreen(navController: NavController, item: Item) {
        DetailImage(navController = navController, thumbnailLink = item.thumbnail)
        DetailText(title = item.title, width = item.sizeWidth, height = item.sizeHeight)
    }

    @Composable
    fun DetailImage(navController: NavController, thumbnailLink: String) {
        GlideImage(
            imageModel = thumbnailLink,
            modifier = Modifier
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

    companion object {
        const val SEARCH_SCREEN_KEY = "searchScreen"
        const val DETAIL_SCREEN_KEY = "detailScreen"
        const val IMAGE_ITEM_KEY = "imageItem"
    }
}