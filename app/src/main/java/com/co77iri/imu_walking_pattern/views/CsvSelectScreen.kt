package com.co77iri.imu_walking_pattern.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.CSV_RESULT
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvSelectScreen(
    navController: NavController,
    resultViewModel: ResultViewModel
) {
    val context = LocalContext.current
    val csvDir = File(context.filesDir, "not_uploaded")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    LaunchedEffect(Unit) {
        resultViewModel.loadcsvFiles(csvDir)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    IconButton(onClick = { navController.popBackStack() }) {
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp))
            {
                // TODO : forEach로
                resultViewModel.csvFiles.value.forEach { session ->
                    ResultCard(navController, resultViewModel, session)
                }
//                tmpCard()
//                tmpCard()
//                tmpCard()
//                tmpCard()
            }
//            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ResultCard(
    navController: NavController,
    resultViewModel: ResultViewModel,
    session: ResultViewModel.CsvSession
) {
    val (formattedDate, formattedTime) = formatDateAndTime(session.leftFile.name)
    val (titleDate, titleTime) = formatTitle(session.leftFile.name)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
            .clickable {
                resultViewModel.uploadTitle.value = titleDate + "_" + titleTime
                resultViewModel.updateCSVDataFromFile(session.leftFile.path)
                resultViewModel.updateCSVDataFromFile(session.rightFile.path)

                navController.navigate(CSV_RESULT)
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
//                            horizontalAlignment = Alignment.CenterHorizontally,
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
    val originalFormat = SimpleDateFormat("yyMMdd_HHmm", Locale.getDefault())
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

fun formatTitle(filename: String): Pair<String, String> {
    val cleanedFilename = filename.substringBeforeLast("_") // "_L.csv" 또는 "_R.csv" 제거
    val originalFormat = SimpleDateFormat("yyMMdd_HHmm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())

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
