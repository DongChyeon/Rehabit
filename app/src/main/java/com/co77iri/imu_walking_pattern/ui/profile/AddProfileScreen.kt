package com.co77iri.imu_walking_pattern.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.PROFILE
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.network.models.Gender
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    navController: NavController,
    viewModel: AddProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(true) {
        effectFlow.collect { effect ->
            when (effect) {
                is AddProfileContract.Effect.NavigateTo -> {
                    navController.navigate(effect.destination, effect.navOptions)
                }

                is AddProfileContract.Effect.NavigateUp -> {
                    navController.navigateUp()
                }

                is AddProfileContract.Effect.ShowSnackBar -> {
                    showSnackBar(snackbarHostState, effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Text(
                        "프로필 생성",
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
        }
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

                // EMR 번호
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 45.dp),
                    value = uiState.emrPatientNumber,
                    singleLine = true, // 한 줄만 작성할 수 있도록
                    onValueChange = viewModel::updateEmrPatientNumber, // 유저가 입력한 값(it)을 remember에 저장
                    label = { Text("EMR 번호") },
                    placeholder = { Text("EMR 번호를 입력해주세요.") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // 키
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 45.dp),
                    value = uiState.height,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = viewModel::updateHeight,
                    label = { Text("키 (cm)") },
                    placeholder = { Text("키를 입력해주세요.") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                // 출생년도
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 45.dp),
                    value = uiState.birthYear,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = viewModel::updateBirthYear,
                    label = { Text("출생연도") },
                    placeholder = { Text("출생연도를 입력해주세요.") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                Text("성별")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { viewModel.updateGender(Gender.M) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (uiState.gender == Gender.M) {
                                Color(0xFF424651)
                            } else {
                                Color.White
                            }
                        )
                    ) {
                        Text(
                            text = "남자",
                            color = if (uiState.gender == Gender.M) {
                                Color.White
                            } else {
                                Color(0xFF424651)
                            }
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { viewModel.updateGender(Gender.F) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (uiState.gender == Gender.F) {
                                Color(0xFF424651)
                            } else {
                                Color.White
                            },
                        )
                    ) {
                        Text(
                            text = "여자",
                            color = if (uiState.gender == Gender.F) {
                                Color.White
                            } else {
                                Color(0xFF424651)
                            }
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF424651),
                    contentColor = White
                ),
                contentPadding = PaddingValues(
                    all = 20.dp
                ),
                onClick = {
                    viewModel.addProfile()
                }
            ) {
                Text(
                    color = Color.White,
                    text = "프로필 생성하기",
                    fontSize = 18.sp,
                    modifier = Modifier
                )
            }
        }
    }
}
