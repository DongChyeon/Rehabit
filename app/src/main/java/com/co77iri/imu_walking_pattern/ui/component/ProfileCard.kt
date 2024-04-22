package com.co77iri.imu_walking_pattern.ui.component

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
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import com.co77iri.imu_walking_pattern.ui.profile.ProfileViewModel

@Composable
fun ProfileCard(
    user: ClinicalPatient,
    selectProfile: (ClinicalPatient) -> Unit,
    navigateToMenuSelect: () -> Unit
) {
    // profile
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
            .clickable {
                // 클릭한 카드의 프로필을 profile ViewModel의 selectedProfile에 저장
                selectProfile(user)
                navigateToMenuSelect()
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
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "emr 번호 : ${user.emrPatientNumber}",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 18.dp),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "출생연도 : ${user.birthYear}",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 18.dp)
                )
            }
        }
    }
}