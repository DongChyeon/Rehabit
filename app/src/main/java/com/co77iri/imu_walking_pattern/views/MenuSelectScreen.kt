package com.co77iri.imu_walking_pattern.views

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.CSV_SELECT
import com.co77iri.imu_walking_pattern.SENSOR_SETTING
import com.co77iri.imu_walking_pattern.ui.profile.ProfileLegacyViewModel
import com.co77iri.imu_walking_pattern.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSelectScreen(
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    val selectedProfile = App.selectedProfile!!

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
                CenterAlignedTopAppBar (
                    title = {
                        Text(
                            selectedProfile.emrPatientNumber,
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
                                tint = Color.White
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
            verticalArrangement = Arrangement.Top,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // 프로필이미지
                Box(
                    modifier = Modifier
                        .size(120.dp, 120.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFE2E2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Add Profile",
                        modifier = Modifier.size(100.dp, 100.dp)
                    )
                }

                // 프로필 info
                Card(
                    shape = RoundedCornerShape(8.dp) ,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE2E2E2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(25.dp), // o
                        verticalArrangement = Arrangement.Center,
                    ) {
                        // 전화번호
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.Phone,
                            rowText = selectedProfile.emrPatientNumber
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.Create,
                            rowText = "${selectedProfile.height} cm"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.DateRange,
                            rowText = selectedProfile.birthYear
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp))
            {
                // 검사 결과 보기 버튼
                MenuSelectCustomBtn(
                    btnText = "검사 결과 보기",
                    navController = navController,
                    navDestination = CSV_SELECT
                )
                MenuSelectCustomBtn(
                    btnText = "보행 검사 시작",
                    navController = navController,
                    navDestination = SENSOR_SETTING
                )
            }
        }
    }
}

@Composable
fun MenuProfileRow(
    rowIcon: ImageVector,
    rowText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp, 30.dp)

        ) {
            Icon(
                imageVector = rowIcon,
                contentDescription = "Add Icon",
                modifier = Modifier.size(30.dp, 30.dp),
                tint = Color(0xFF2F3239)
            )
        }
        Text(
            color = Color(0xFF2F3239),
            text = rowText,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(start = 10.dp),
        )
    }
}

@Composable
fun MenuSelectCustomBtn(
    btnText: String,
    navController: NavController,
    navDestination: String
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF424651)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                navController.navigate(navDestination)
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                color = Color.White,
                text = btnText,
                fontSize = 18.sp,
                modifier = Modifier
            )
            Box(
                modifier = Modifier
                    .size(30.dp,30.dp)


            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Add Icon",
                    modifier = Modifier
                        .size(33.dp, 33.dp),
                    tint = Color.White
                )
            }
        }
    }
}