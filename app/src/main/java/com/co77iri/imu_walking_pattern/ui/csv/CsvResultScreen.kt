package com.co77iri.imu_walking_pattern.ui.csv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.ui.component.SnackBar
import com.co77iri.imu_walking_pattern.ui.profile.showSnackBar
import com.co77iri.imu_walking_pattern.ui.upload.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvResultScreen(
    navController: NavController,
    resultId: Int,
    viewModel: CsvResultViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    viewModel.getTestData(resultId)

    LaunchedEffect(true) {
        effectFlow.collect { effect ->
            when (effect) {
                is CsvResultContract.Effect.NavigateTo -> {
                    navController.navigate(effect.destination, effect.navOptions)
                }

                is CsvResultContract.Effect.NavigateUp -> {
                    navController.navigateUp()
                }

                is CsvResultContract.Effect.ShowSnackBar -> {
                    showSnackBar(snackbarHostState, effect.message)
                }
            }
        }
    }

    val totalSteps = viewModel.getTotalStep()
    val cadence = (totalSteps.toDouble() / (uiState.csvDataLeft.getDataLength() / 60)) * 60

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackBar(snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "검사결과",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(MENU_SELECT) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.White
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F3239))
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFF3F3F3)
                )
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
                    .verticalScroll(rememberScrollState()), // 스크롤 추가
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "검사결과 보기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    // 총 레코딩 시간
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "총 레코딩 시간",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${uiState.csvDataLeft.getDataLength() / 60}초",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "테스트 일시",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = formatDate(uiState.testDateAndTime),
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "파킨슨 단계",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${uiState.parkinsonStage}단계",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Cadence",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "분당 걸음 수",
                                    fontSize = 16.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81)
                                )
                            }

                            Text(
                                text = "${String.format("%.1f", cadence)} 걸음/분",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}