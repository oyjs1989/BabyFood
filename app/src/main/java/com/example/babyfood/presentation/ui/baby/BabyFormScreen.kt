package com.example.babyfood.presentation.ui.baby

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import coil.compose.AsyncImage
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import android.net.Uri
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BabyFormScreen(
    babyId: Long = 0,
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var allergyItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var preferenceItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAllergyDialog by remember { mutableStateOf(false) }
    var showPreferenceDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var newAllergy by remember { mutableStateOf("") }
    var newPreference by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val isEditing = babyId > 0
    val context = LocalContext.current

    // 图片选择启动器（从相册选择）
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            avatarUrl = it.toString()
            Log.d("BabyFormScreen", "图片已选择: $it")
        }
    }

    // 拍照启动器
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d("BabyFormScreen", "拍照成功")
        }
    }

    // 监听保存成功状态
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearSavedFlag()
            onBack()
        }
    }

    LaunchedEffect(babyId) {
        if (isEditing) {
            val baby = uiState.babies.find { it.id == babyId }
            baby?.let {
                name = it.name
                birthDate = it.birthDate
                weight = it.weight?.toString() ?: ""
                height = it.height?.toString() ?: ""
                allergyItems = it.getEffectiveAllergies()
                preferenceItems = it.getEffectivePreferences()
                avatarUrl = it.avatarUrl
            }
        } else {
            // 默认值为当天
            birthDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
    }

    // 添加过敏食材
    fun addAllergy() {
        if (newAllergy.isNotBlank() && newAllergy !in allergyItems) {
            allergyItems = allergyItems + newAllergy
            newAllergy = ""
        }
    }

    // 移除过敏食材
    fun removeAllergy(item: String) {
        allergyItems = allergyItems - item
    }

    // 添加偏好食材
    fun addPreference() {
        if (newPreference.isNotBlank() && newPreference !in preferenceItems) {
            preferenceItems = preferenceItems + newPreference
            newPreference = ""
        }
    }

    // 移除偏好食材
    fun removePreference(item: String) {
        preferenceItems = preferenceItems - item
    }

    // 日期选择器对话框
    val datePickerState = rememberDatePickerState()
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            birthDate = kotlinx.datetime.LocalDate(
                                year = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).year,
                                monthNumber = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
                                dayOfMonth = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth
                            )
                            showDatePicker = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑宝宝" else "添加宝宝") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 头像上传区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 头像图片或占位符
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "宝宝头像",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    // 编辑头像按钮
                    FloatingActionButton(
                        onClick = { showImagePickerDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.BottomEnd),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "上传头像",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 图片选择对话框
            if (showImagePickerDialog) {
                AlertDialog(
                    onDismissRequest = { showImagePickerDialog = false },
                    title = { Text("选择头像") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 从相册选择
                            Button(
                                onClick = {
                                    imagePickerLauncher.launch("image/*")
                                    showImagePickerDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "从相册选择",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("从相册选择")
                            }

                            // 拍照
                            Button(
                                onClick = {
                                    // TODO: 创建临时文件用于拍照
                                    showImagePickerDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "拍照",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("拍照")
                            }

                            // 帮助信息
                            TextButton(
                                onClick = { showImagePickerDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = "帮助",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("帮助")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showImagePickerDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 姓名
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("宝宝姓名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 出生日期
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "出生日期",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = birthDate?.toString() ?: "选择日期",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "选择日期"
                        )
                    }
                }
            }

            // 过敏食材
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "过敏食材",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { showAllergyDialog = true },
                        enabled = allergyItems.size < 10
                    ) {
                        Text("+ 添加")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (allergyItems.isEmpty()) {
                    Text(
                        text = "暂无过敏食材",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allergyItems.forEach { item ->
                            FilterChip(
                                selected = true,
                                onClick = { removeAllergy(item) },
                                label = { Text(item) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "移除"
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 偏好食材
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "偏好食材",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { showPreferenceDialog = true },
                        enabled = preferenceItems.size < 10
                    ) {
                        Text("+ 添加")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (preferenceItems.isEmpty()) {
                    Text(
                        text = "暂无偏好食材",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        preferenceItems.forEach { item ->
                            FilterChip(
                                selected = true,
                                onClick = { removePreference(item) },
                                label = { Text(item) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "移除"
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(
                onClick = {
                    val baby = com.example.babyfood.domain.model.Baby(
                        id = babyId,
                        name = name,
                        birthDate = birthDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        weight = null,
                        height = null,
                        allergies = allergyItems.map { com.example.babyfood.domain.model.AllergyItem(it) },
                        preferences = preferenceItems.map { com.example.babyfood.domain.model.PreferenceItem(it) },
                        avatarUrl = avatarUrl
                    )
                    viewModel.saveBaby(baby)
                    onSave()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && birthDate != null
            ) {
                Text("保存")
            }

            // 过敏食材添加对话框
            if (showAllergyDialog) {
                AlertDialog(
                    onDismissRequest = { showAllergyDialog = false },
                    title = { Text("添加过敏食材") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newAllergy,
                                onValueChange = { newAllergy = it },
                                label = { Text("食材名称") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                addAllergy()
                                showAllergyDialog = false
                            },
                            enabled = newAllergy.isNotBlank()
                        ) {
                            Text("添加")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAllergyDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 偏好食材添加对话框
            if (showPreferenceDialog) {
                AlertDialog(
                    onDismissRequest = { showPreferenceDialog = false },
                    title = { Text("添加偏好食材") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newPreference,
                                onValueChange = { newPreference = it },
                                label = { Text("食材名称") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                addPreference()
                                showPreferenceDialog = false
                            },
                            enabled = newPreference.isNotBlank()
                        ) {
                            Text("添加")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPreferenceDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            if (uiState.error != null) {
                Text(
                    text = "错误: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}