package com.co77iri.imu_walking_pattern.views.old

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.R
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.ui.profile.ProfileLegacyViewModel
import com.co77iri.imu_walking_pattern.ui.theme.PrimaryBlue
import com.co77iri.imu_walking_pattern.ui.theme.SecondaryGray
import com.co77iri.imu_walking_pattern.ui.profile.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldProfileScreen(
    context: Context,
    navController: NavController,
    profileViewModel: ProfileLegacyViewModel
    ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar (
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
                title = {
                    Text(
                        "보행검사",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
//                actions = {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            imageVector = Icons.Filled.Menu,
//                            contentDescription = "메뉴"
//                        )
//                    }
//                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {  innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(25.dp),
//                .size(boxWidth.dp, boxHeight.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    Text("사용자 프로필", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 프로필 추가 카드
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SecondaryGray
                            ),
                            modifier = Modifier
                                .size(width = 240.dp, height = 120.dp)
                                .clickable {
                                    showDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp, 48.dp),
//                                .background(color = PrimaryBlue),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add Icon",
                                        modifier = Modifier.size(48.dp, 48.dp)
                                    )
                                }
                                Text(
                                    text = "프로필 추가   ",
                                    fontSize = 18.sp
                                )
                            }
                        }
                        
                        profileViewModel.profiles.forEach { profile ->
                            customProfileCard(profile, profileViewModel, navController)
                        }
                    }
                }
            }
        }
    }

    ProfileCreateDialog(
        context,
        profileViewModel = profileViewModel,
        showDialog = showDialog,
        onClose = { showDialog = false }
    )

    SelectDialog(
        profileViewModel = profileViewModel,
        navController = navController,
        onClose = {profileViewModel.selectedProfile = null}
    )
}

@Composable
fun customProfileCard(
    user: ProfileData,
    profileViewModel: ProfileLegacyViewModel,
    navController: NavController
) {
    val profileIcon = painterResource(id = R.drawable.icon_profile_user)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = SecondaryGray
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 120.dp)
            .clickable(
                onClick = {
                    profileViewModel.selectedProfile = user

//                    ! navController.navigate("sensor_conn_screen")
                }
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp, 48.dp)
                        .background(color = Color.Gray),
                ) {
                    Image(
                        painter = profileIcon,
                        contentDescription = "프로필 아이콘",
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(user.hospital, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                        user.phoneNumber + "\n"
                        + user.birthDate
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDialog(
    profileViewModel: ProfileLegacyViewModel,
    navController: NavController,
    onClose: () -> Unit
) {
    if( profileViewModel.selectedProfile != null ) {
        Dialog( onDismissRequest = onClose ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(Color.White)
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "보행검사 방법 선택",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color(0xFFBDC0F8)
                            ),
                            navigationIcon = {
                                IconButton(onClick = { onClose() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                                }
                            }
                        )
                    }
                ) {innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
//                        Spacer(modifier = Modifier.height(20.dp))
                        Divider()
                        // 1. 저장된 파일 분석
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            onClick = {
                                // TODO : ~~
//                            navController.navigate("pta_test_screen")
                            }
                        ) {
                            Text(
                                "저장된 파일 분석",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider()

                        // 2. 센서를 이용해 보행검사 실시
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            onClick = {
                                navController.navigate("sensor_conn_screen")
                            }
                        ) {
                            Text(
                                "센서를 이용해 보행검사 실시",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCreateDialog(
    context: Context,
    profileViewModel: ProfileLegacyViewModel,
    showDialog: Boolean,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    if( showDialog ) {
        Dialog( onDismissRequest = onClose ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onClose ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
//                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "프로필 생성",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            PrimaryBlue,
                                            SecondaryGray
//                                    MaterialTheme.colors.primary,
//                                    MaterialTheme.colors.primaryVariant
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_profile_user),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "내원 병원 : 충남대병원", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    InputField(hint = "이름(홍길동)", value = name, onValueChange = { name = it })
                    InputField(hint = "전화번호(010-1234-5678)", value = phoneNumber, onValueChange = { phoneNumber = it })
                    InputField(hint = "키(cm)", value = height, onValueChange = { height = it })
                    InputField(hint = "몸무게(kg)", value = weight, onValueChange = { weight = it })
                    InputField(hint = "생년월일(19880815)", value = birthDate, onValueChange = { birthDate = it })

                    // DatePicker and Dropdown for 날짜 and 성별 will go here
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            val newProfile = ProfileData(
                                name =  name,
                                phoneNumber = phoneNumber,
                                height = height.toDouble(),
                                weight = weight.toDouble(),
                                hospital = "충남대학교",
                                birthDate = birthDate,
                                caliMinDistance = 10,
                                caliMaxValue = 40.0,
                                lastAccessTime = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()),
                                filename = ""
                            )

                            profileViewModel.saveProfile(context, newProfile)
                            profileViewModel.loadProfiles(context)
                            onClose()
                        }) {
                            Text("생성하기")
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    hint: String,
    value: String,
    onValueChange: (String) -> Unit
) {
//    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldColors = TextFieldDefaults.textFieldColors(
//        textColor = Color.Black,
        disabledTextColor = Color.Gray,
        focusedIndicatorColor = Color.Blue,
        unfocusedIndicatorColor = Color.Gray,
        disabledIndicatorColor = Color.Gray,
        cursorColor = Color.Black,
        errorCursorColor = Color.Red,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Gray,
        errorLabelColor = Color.Red,
        errorLeadingIconColor = Color.Red,
        errorTrailingIconColor = Color.Red
    )

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = hint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = textFieldColors
    )
}
