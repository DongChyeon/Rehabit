package com.co77iri.imu_walking_pattern.ui.csv

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.UPLOAD_RESULT
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestData
import com.co77iri.imu_walking_pattern.ui.component.SnackBar
import com.co77iri.imu_walking_pattern.ui.profile.showSnackBar
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvSelectScreen(
    navController: NavController,
    viewModel: CsvSelectViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val results = uiState.results.collectAsLazyPagingItems()
    val resultsRefreshState = results.loadState.refresh

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(resultsRefreshState) {
        if (resultsRefreshState is LoadState.Error) {
            val errorMessage = resultsRefreshState.error.message ?: "네트워크 오류가 발생했습니다."
            showSnackBar(snackbarHostState, errorMessage)
        }
    }

    LaunchedEffect(true) {
        val selectedProfile = App.selectedProfile!!
        viewModel.getParkinsonTestDataList(selectedProfile.clinicalPatientId)

        effectFlow.collectLatest { effect ->
            when (effect) {
                is CsvSelectContract.Effect.NavigateTo -> {
                    navController.navigate(effect.destination, effect.navOptions)
                }

                is CsvSelectContract.Effect.NavigateUp -> {
                    navController.navigateUp()
                }

                is CsvSelectContract.Effect.ShowSnackBar -> {
                    showSnackBar(snackbarHostState, effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackBar(snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Text(
                        "검사결과 보기",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = White
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F3239))
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(
                    color = Color(0xFFF3F3F3)
                )
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = results.itemCount,
                    key = results.itemKey(),
                    contentType = results.itemContentType()
                ) { index ->
                    results[index]?.let {
                        ResultCard(
                            naviageToCsvResult = {
                                navController.navigate(UPLOAD_RESULT)
                            },
                            result = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    naviageToCsvResult: () -> Unit,
    result: ParkinsonTestData
) {
    val (formattedDate, formattedTime) = formatDateAndTime(result.testDateAndTime)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
            .clickable {
                naviageToCsvResult()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formattedTime,
                    fontSize = 16.sp,
                )
            }
        }
    }
}


fun formatDateAndTime(filename: String): Pair<String, String> {
    val cleanedFilename = filename.substringBeforeLast("_") // "_L.csv" 또는 "_R.csv" 제거
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH시 mm분 측정 결과", Locale.getDefault())

    return try {
        val date = originalFormat.parse(cleanedFilename)
        val formattedDate = dateFormat.format(date ?: return Pair("", ""))
        val formattedTime = timeFormat.format(date)

        Pair(formattedDate, formattedTime)
    } catch (e: Exception) {
        Log.e("CsvSelectScreen", "formatDateAndTime() - ${e.message}")
        Pair("", "")  // 형식이 맞지 않는 경우 빈 문자열 반환
    }
}