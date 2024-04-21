package com.co77iri.imu_walking_pattern.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ADD_PROFILE
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.ui.component.ProfileCard
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
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
                        "PROFILE",
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F3239))
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF3F3F3)
                )
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                profileViewModel.profiles.forEach { profile ->
                    ProfileCard(
                        user = profile,
                        profileViewModel = profileViewModel,
                        navigateToMenuSelect = {
                            navController.navigate(MENU_SELECT)
                        }
                    )
                }
            }

            AddProfileCard(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                navController.navigate(ADD_PROFILE)
            }
        }
    }
}

@Composable
fun AddProfileCard(
    modifier: Modifier = Modifier,
    navigateToAddProfile: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF424651)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                navigateToAddProfile()
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(33.dp, 33.dp)

            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(33.dp, 33.dp),
                    tint = White
                )
            }
            Text(
                color = White,
                text = "프로필 추가",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 18.dp)
            )
        }
    }
}