package com.co77iri.imu_walking_pattern.utils

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
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel

@Composable
fun ProfileCard(
    user: ProfileData,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    // profile
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
            .clickable {
//                showDialog = true
                // 클릭한 카드의 프로필을 profile ViewModel의 selectedProfile에 저장
                profileViewModel.selectedProfile = user
                navController.navigate("menu_select")
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .clip(CircleShape)
                    .background(color = Color(0xFF2F3239)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Add Profile",
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                        .fillMaxSize(),
                    tint = Color.White
                )
            }
            Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
//                    text = "홍길동 | 만 35세",
                    text = user.name + user.birthDate,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 18.dp),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = user.hospital + "병원",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 18.dp)
                )
            }
        }
    }
}