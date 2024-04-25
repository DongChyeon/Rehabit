package com.co77iri.imu_walking_pattern.ui.csv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.PROFILE
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
                actions = {
                    IconButton(onClick = {
                        navController.navigate(PROFILE) {
                            popUpTo(PROFILE) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Share",
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
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // 스크롤 추가
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp),
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

                    // 총 걸음 수
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 20.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "총 걸음 수",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = totalSteps.toString(),
                                fontSize = 18.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 20.dp, bottom = 10.dp),
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

                        // 라인
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 1.dp)
                                .background(color = Color(0xFFC8C8C8))
                        )

                        // 왼발
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "왼발",
                                fontSize = 16.sp,
                                modifier = Modifier,
                                color = Color(0xFF7D7E81)
                            )
                            Text(
                                text = uiState.leftSteps.toString(),
                                fontSize = 16.sp,
                                modifier = Modifier,
                                color = Color(0xFF7D7E81)
                            )
                        }

                        // 오른발
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "오른발",
                                fontSize = 16.sp,
                                modifier = Modifier,
                                color = Color(0xFF7D7E81)
                            )
                            Text(
                                text = uiState.rightSteps.toString(),
                                fontSize = 16.sp,
                                modifier = Modifier,
                                color = Color(0xFF7D7E81)
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // 분당 걸음 수
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
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
                                Text(
                                    text = "${String.format("%.1f", cadence)}걸음/분",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // 10.dp 간격을 추가

                        // 걸음 거리
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = "Stride length",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "걸음 거리",
                                    fontSize = 16.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81)
                                )
                                Text(
                                    text = "${String.format("%.1f", uiState.strideLength)}cm",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }



                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // 신장 비례 걸음 거리 비율
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = "Stride length/Height",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "신장 비례 걸음 비율",
                                    fontSize = 16.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81)
                                )
                                Text(
                                    text = "${String.format("%.1f", uiState.strideLength / App.selectedProfile?.height!!.toDouble())}",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // 10.dp 간격을 추가

                        // Step length
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = "Step length",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "한 발을 내디뎌 반대쪽 발이 땅에 닿을 때까지의 거리",
                                    fontSize = 14.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81),
                                    lineHeight = 15.sp
                                )
                                Text(
                                    text = "${String.format("%.1f",  uiState.stepLength)}cm",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // 평균 속도
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = "Avg Speed",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "평균 속도",
                                    fontSize = 16.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81)
                                )
                                Text(
                                    text = "${String.format("%.1f", uiState.averageSpeed)}cm/s",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // 10.dp 간격을 추가

                        // 걸음 거리
                        Card(
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE2E2E2)
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = "Gait cycleduration",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "보행 주기",
                                    fontSize = 14.sp,
                                    modifier = Modifier,
                                    color = Color(0xFF7D7E81),
                                )
                                Text(
                                    text = "${String.format("%.1f", uiState.gaitCycle)}초",
                                    fontSize = 18.sp,
                                    modifier = Modifier,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}