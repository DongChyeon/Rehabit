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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.CSV_SELECT
import com.co77iri.imu_walking_pattern.SENSOR_SETTING
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSelectScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
                CenterAlignedTopAppBar (
                    title = {
                        Text(
                            // selectedProfile이 무조건 있다고 가정하고 !! 붙이기 (selectedProfile은 nullable 변수)
                            profileViewModel.selectedProfile!!.name,
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
//                        .padding(20.dp) 추가x
                        .clickable {
//                            showDialog = true
                        }

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(25.dp), // o
                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 전화번호
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.Phone,
                            rowText = profileViewModel.selectedProfile!!.phoneNumber
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.Create,
                            rowText = profileViewModel.selectedProfile!!.height.toString()
                                        + "cm, "
                                        + profileViewModel.selectedProfile!!.weight.toString()
                                        + "kg"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuProfileRow(
                            rowIcon = Icons.Rounded.DateRange,
                            rowText = profileViewModel.selectedProfile!!.birthDate
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

//            Spacer(modifier = Modifier.height(20.dp))
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
//                            .background(color = Color.White)

        ) {
            Icon(
//                imageVector = Icons.Rounded.Phone,
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