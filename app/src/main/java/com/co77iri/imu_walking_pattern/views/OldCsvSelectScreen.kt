package com.co77iri.imu_walking_pattern.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.CSV_RESULT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldCsvSelectScreen(
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

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
                        IconButton(onClick = { /*TODO*/ }) {
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
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp))
            {
                // TODO : forEach로
                CustomResultScreenCard(navController)
                CustomResultScreenCard(navController)
                CustomResultScreenCard(navController)
                CustomResultScreenCard(navController)
            }
//            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CustomResultScreenCard(
    navController: NavController
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
            .clickable {
//                showDialog = true
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
                    text = "2023.11.02",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "홍길동1",
                    fontSize = 16.sp,
                )
            }
            Box(
                modifier = Modifier
                    .size(25.dp, 25.dp)
                    .clip(CircleShape)
                    .background(color = Color(0xFF5C5F67)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Add Profile",
                    modifier = Modifier.size(20.dp, 20.dp)
                        .fillMaxSize(),
                    tint = Color.White
                )
            }
        }
    }
}