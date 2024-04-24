package com.co77iri.imu_walking_pattern.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.co77iri.imu_walking_pattern.ADD_PROFILE
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.ui.component.ProfileCard
import com.co77iri.imu_walking_pattern.ui.component.SearchTextField
import com.co77iri.imu_walking_pattern.ui.component.SnackBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val profiles = uiState.profiles.collectAsLazyPagingItems()
    val profilesRefreshState = profiles.loadState.refresh

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(profilesRefreshState) {
        if (profilesRefreshState is LoadState.Error) {
            val errorMessage = profilesRefreshState.error.message ?: "네트워크 오류가 발생했습니다."
            showSnackBar(snackbarHostState, errorMessage)
        }
    }

    LaunchedEffect(true) {
        viewModel.getProfiles()

        effectFlow.collectLatest { effect ->
            when (effect) {
                is ProfileContract.Effect.NavigateTo -> {
                    navController.navigate(effect.destination, effect.navOptions)
                }

                is ProfileContract.Effect.ShowSnackBar -> {
                    showSnackBar(snackbarHostState, effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackBar(snackbarHostState)
        },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "PROFILE",
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(
                            0xFF2F3239
                        )
                    )
                )

                SearchTextField(
                    modifier = Modifier.padding(
                        top = 10.dp,
                        start = 20.dp,
                        end = 20.dp
                    ),
                    value = uiState.searchWord,
                    onValueChange = viewModel::updateSearchWord,
                    placeholderText = uiState.placeholderText,
                    onSearch = { viewModel.searchProfile() }
                )
            }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = profiles.itemCount,
                    key = profiles.itemKey(),
                    contentType = profiles.itemContentType()
                ) { index ->
                    profiles[index]?.let {
                        ProfileCard(
                            user = it,
                            selectProfile = { profile ->
                                App.selectedProfile = profile
                            },
                            navigateToMenuSelect = {
                                navController.navigate(MENU_SELECT)
                            }
                        )
                    }
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
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF424651),
            contentColor = White
        ),
        contentPadding = PaddingValues(
            all = 20.dp
        ),
        onClick = {
            navigateToAddProfile()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(33.dp, 33.dp)
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
                fontSize = 18.sp
            )
        }
    }
}

suspend fun showSnackBar(
    snackbarHostState: androidx.compose.material.SnackbarHostState,
    message: String
) {
    snackbarHostState.showSnackbar(message)
}