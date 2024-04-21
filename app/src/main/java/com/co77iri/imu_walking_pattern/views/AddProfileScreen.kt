package com.co77iri.imu_walking_pattern.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.PROFILE
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    context: Context,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    var userName by remember { mutableStateOf(TextFieldValue()) } // 이름
    var phoneNumberInput by remember { mutableStateOf(TextFieldValue()) } // 전화번호
    var heightInput by remember { mutableStateOf(TextFieldValue()) } // 키
    var weightInput by remember { mutableStateOf(TextFieldValue()) }  // 몸무게
    var birthInput by remember { mutableStateOf(TextFieldValue()) }  // 생년월일

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

                // 이름
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min=45.dp),
                    value = userName,
                    singleLine = true, // 한 줄만 작성할 수 있도록
                    onValueChange = { userName = it }, // 유저가 입력한 값(it)을 remember에 저장
                    label = { Text("이름") },
                    placeholder = { Text("텍스트를 작성해주세요.") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // 전화번호
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min=45.dp),
                    value = phoneNumberInput,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = { phoneNumberInput = it },
                    label = { Text("전화번호") },
                    placeholder = { Text("010-1234-5678") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // 키
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min=45.dp),
                    value = heightInput,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = { heightInput = it },
                    label = { Text("키 (cm)") },
                    placeholder = { Text("175") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // 몸무게
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min=45.dp),
                    value = weightInput,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = { weightInput = it },
                    label = { Text("몸무게 (kg)") },
                    placeholder = { Text("70") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // 생년월일
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min=45.dp),
                    value = birthInput,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 키보드 타입 지정
                    onValueChange = { birthInput = it },
                    label = { Text("생년월일") },
                    placeholder = { Text("19880815") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE2E2E2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
            }


            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF424651)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        val newProfile = ProfileData(
                            name = userName.text,
                            phoneNumber = phoneNumberInput.text,
                            height = heightInput.text.toDouble(),
                            weight = weightInput.text.toDouble(),
                            hospital = "충남대학교",
                            birthDate = birthInput.text,
                            caliMinDistance = 10,
                            caliMaxValue = 40.0,
                            lastAccessTime = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()),
                            filename = ""
                        )

                        profileViewModel.saveProfile(context, newProfile) // 작성된 내용대로 프로필 생성
                        profileViewModel.loadProfiles(context)            // 안드로이드 내부 저장소에서 프로필 로드
                        navController.navigate(PROFILE)           // 다시 프로필 페이지로 전환
                    }

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
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
}