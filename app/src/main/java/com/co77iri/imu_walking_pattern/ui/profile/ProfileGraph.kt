package com.co77iri.imu_walking_pattern.ui.profile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.co77iri.imu_walking_pattern.ADD_PROFILE
import com.co77iri.imu_walking_pattern.PROFILE
import com.co77iri.imu_walking_pattern.PROFILE_ROUTE

fun NavGraphBuilder.profileGraph(
    navController: NavController
) {
    navigation(
        route = PROFILE_ROUTE,
        startDestination = PROFILE
    ) {
        composable(PROFILE) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(navController, profileViewModel)
        } // 프로필 선택 페이지 -> 프로필 선택하면 menu_select로 이동

        composable(ADD_PROFILE) {
            val addProfileViewModel: AddProfileViewModel = hiltViewModel()

            AddProfileScreen(navController, addProfileViewModel)
        } // 프로필 추가 페이지
    }
}