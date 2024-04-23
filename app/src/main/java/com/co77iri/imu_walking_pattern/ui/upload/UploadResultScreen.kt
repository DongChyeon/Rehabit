//@file:JvmName("CsvSelectScreenKt")

package com.co77iri.imu_walking_pattern.ui.upload

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
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
import com.co77iri.imu_walking_pattern.UPLOAD_RESULT
import com.co77iri.imu_walking_pattern.models.CSVData
import com.co77iri.imu_walking_pattern.ui.component.SnackBar
import com.co77iri.imu_walking_pattern.ui.profile.showSnackBar
import com.co77iri.imu_walking_pattern.ui.upload.UploadResultContract.Companion.PLACEHOLDER_TEXT
import com.xsens.dot.android.sdk.models.XsensDotDevice
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadResultScreen(
    navController: NavController,
    l_csv: String,
    r_csv: String,
    viewModel: UploadResultViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    viewModel.updateCSVDataFromFile(l_csv)
    viewModel.updateCSVDataFromFile(r_csv)
    viewModel.updateTestDateAndTime(getCurrentDateTime())

    LaunchedEffect(true) {
        effectFlow.collect { effect ->
            when (effect) {
                is UploadResultContract.Effect.NavigateTo -> {
                    navController.navigate(effect.destination, effect.navOptions)
                }

                is UploadResultContract.Effect.NavigateUp -> {
                    navController.navigateUp()
                }

                is UploadResultContract.Effect.ShowSnackBar -> {
                    showSnackBar(snackbarHostState, effect.message)
                }
            }
        }
    }

    var parkinsonDropdownExpanded by remember { mutableStateOf(false) }

    // 23-10-04
    val leftFile = File(l_csv)

    val leftData: CSVData = viewModel.updateCSVDataFromFile(l_csv)

    val rightFile = File(r_csv)

    val rightData: CSVData = viewModel.updateCSVDataFromFile(r_csv)

    val firstCSVData = leftData
    val totalSteps = viewModel.getTotalStep(leftData, rightData)

    val totalTimeInSeconds = firstCSVData.getDataLength() / 60.toDouble()
    val cadence = (totalSteps.toDouble() / (firstCSVData.getDataLength() / 60)) * 60

    viewModel.updateTestDateAndTime(getCurrentDateTime())
    viewModel.updateTotalTimeInSeconds(totalTimeInSeconds)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackBar(snackbarHostState)
        },
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
                    ParkinsonStageDropDown(
                        expanded = parkinsonDropdownExpanded,
                        onExpandedChange = { parkinsonDropdownExpanded = it },
                        selectedValue = uiState.parkinsonStage,
                        onValueChange = viewModel::updateParkinsonStage
                    )

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
                                text = "${firstCSVData.getDataLength() / 60}초",
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

            Button(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF424651),
                    contentColor = White,
                    disabledContainerColor = Color(0xFFE2E2E2)
                ),
                contentPadding = PaddingValues(
                    vertical = 20.dp
                ),
                enabled = uiState.parkinsonStage != PLACEHOLDER_TEXT,
                onClick = {
                    viewModel.uploadCsvFiles(leftFile, rightFile)
                }
            ) {
                Text(
                    text = "검사 결과 업로드",
                    fontSize = 18.sp,
                    color = White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkinsonStageDropDown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "파킨슨 단계",
            fontSize = 18.sp,
            modifier = Modifier,
            fontWeight = FontWeight.SemiBold,
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = {
                onExpandedChange(it)
            }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = if (expanded) RoundedCornerShape(8.dp).copy(
                    bottomEnd = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                ) else RoundedCornerShape(8.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .background(Color(0xFFE2E2E2))
                    .fillMaxWidth(),
            ) {
                (1..5).forEach { value ->
                    DropdownMenuItem(
                        text = { Text(text = value.toString()) },
                        onClick = {
                            onExpandedChange(false)
                            onValueChange(value.toString())
                        },
                    )
                }
            }
        }
    }
}

private fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    return currentDateTime.format(formatter)
}

fun formatDate(inputDate: String): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        val date = LocalDateTime.parse(inputDate, inputFormat)
        date.format(outputFormat)
    } catch (e: Exception) {
        ""
    }
}