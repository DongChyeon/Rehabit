//@file:JvmName("CsvSelectScreenKt")

package com.co77iri.imu_walking_pattern.ui.upload

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.models.CSVData
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadResultScreen(
    navController: NavController,
    l_csv: String,
    r_csv: String,
    resultViewModel: ResultViewModel
) {
    resultViewModel.updateCSVDataFromFile(l_csv)
    resultViewModel.updateCSVDataFromFile(r_csv)

    val context = LocalContext.current

    val selectedProfile = App.selectedProfile!!

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )
    var showDialog by remember { mutableStateOf(false) }

    // 23-10-04
    val leftData: CSVData = resultViewModel.updateCSVDataFromFile(l_csv)
    val leftSteps = resultViewModel.getStep(leftData)

    val rightData: CSVData = resultViewModel.updateCSVDataFromFile(r_csv)
    val rightSteps = resultViewModel.getStep(rightData)

    val firstCSVData = leftData
    val totalSteps = resultViewModel.getTotalStep(leftData, rightData)

    val totalTimeInSeconds = firstCSVData.getDataLength() / 60.toDouble()
    val cadence = (totalSteps.toDouble() / (firstCSVData.getDataLength() / 60)) * 60

    val totalWalkingDistance = resultViewModel.calculateTotalWalkingDistance(leftData, rightData)
    val avgWalkingDistance: Double = totalWalkingDistance / totalSteps
    val avgDistanceDivHeight: Double = avgWalkingDistance / selectedProfile.height.toDouble()

    val avgSpeed = totalWalkingDistance / totalTimeInSeconds
    val gaitCycleDuration = totalTimeInSeconds / totalSteps.toDouble()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Text(
                        "측정 결과",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(MENU_SELECT) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val csv_l = File(l_csv)
                        val csv_r = File(r_csv)

                        resultViewModel.uploadCsvFiles(
                            getCurrentDateTime(),
                            totalTimeInSeconds,
                            1,
                            csv_l,
                            csv_r
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
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
                .padding(top = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 스크롤 추가

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "검사결과 보기",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 20.dp),
                    fontWeight = FontWeight.SemiBold,

                    )

                // 총 레코딩 시간
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE2E2E2)
                    ),

                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable {
                            showDialog = true
                        }

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                            text = "${firstCSVData.getDataLength()/60}초",
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
                    ),

                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable {
                            showDialog = true
                        }

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
                            text = leftSteps.toString(),
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
                            text = rightSteps.toString(),
                            fontSize = 16.sp,
                            modifier = Modifier,
                            color = Color(0xFF7D7E81)
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
                            .weight(1f)
                            .padding(start = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                            .weight(1f)
                            .padding(end = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                                text = "${String.format("%.1f",avgWalkingDistance)}cm",
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
                            .weight(1f)
                            .padding(start = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                                text = "${String.format("%.1f", avgDistanceDivHeight)}",
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
                            .weight(1f)
                            .padding(end = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                                text = "${String.format("%.1f",avgWalkingDistance*2)}cm",
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
                            .weight(1f)
                            .padding(start = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                                text = "${String.format("%.1f", avgSpeed)}cm/s",
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
                            .weight(1f)
                            .padding(end = 20.dp)
                            .clickable {
                                showDialog = true
                            },
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
                                text = "${String.format("%.1f", gaitCycleDuration)}초",
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


private fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}