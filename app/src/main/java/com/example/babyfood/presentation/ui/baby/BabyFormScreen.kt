package com.example.babyfood.presentation.ui.baby

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import coil.compose.AsyncImage
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
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
    onNavigateToHealthRecords: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var allergyItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var preferenceItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var chewingAbility by remember { mutableStateOf("NORMAL") }
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
            onSave()
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
                chewingAbility = it.chewingAbility ?: "NORMAL"
                avatarUrl = it.avatarUrl
            }
        } else {
            // 默认值为当天
            birthDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            chewingAbility = "NORMAL"
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
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 顶部导航栏
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Baby Details" else "Add Baby",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // 底部操作按钮
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 编辑资料按钮
                    Button(
                        onClick = { /* 进入编辑模式 */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Profile",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    // 健康记录按钮
                    OutlinedButton(
                        onClick = onNavigateToHealthRecords,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Health Records",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                // 新增模式下的保存按钮
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
                            chewingAbility = chewingAbility,
                            avatarUrl = avatarUrl
                        )
                        viewModel.saveBaby(baby)
                        onSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = name.isNotBlank() && birthDate != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 头像区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 外圈装饰
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 头像图片或占位符
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "宝宝头像",
                                modifier = Modifier
                                    .size(132.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(132.dp)
                                    .clip(CircleShape)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.firstOrNull()?.toString() ?: "?",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 编辑头像按钮
                    FloatingActionButton(
                        onClick = { showImagePickerDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "上传头像",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // 姓名和年龄
                Spacer(modifier = Modifier.height(16.dp))
                if (isEditing) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val ageInMonths = birthDate?.let {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        (now.year - it.year) * 12 + (now.monthNumber - it.monthNumber)
                    } ?: 0
                    Text(
                        text = "$ageInMonths Months old",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
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
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showImagePickerDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 编辑模式下的表单字段
            if (!isEditing) {
                // 姓名
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("宝宝姓名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // 出生日期
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
            }

            // 生长曲线卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // 标题行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Growth Curve",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = onNavigateToGrowth,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "View Details",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer { rotationZ = 180f }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 身高体重数据
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 身高
                        GrowthDataCard(
                            label = "Height",
                            value = if (height.isNotBlank()) "${height.toDoubleOrNull()?.toInt() ?: 0}" else "72",
                            unit = "cm",
                            modifier = Modifier.weight(1f)
                        )
                        // 体重
                        GrowthDataCard(
                            label = "Weight",
                            value = if (weight.isNotBlank()) weight else "9.5",
                            unit = "kg",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 营养目标卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // 标题行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Nutrition Goals",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { /* 编辑营养目标 */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 卡路里进度
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calories",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "850 kcal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 营养指标
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutritionItem(label = "Protein", value = "15g")
                        NutritionItem(label = "Calcium", value = "260mg")
                        NutritionItem(label = "Iron", value = "11mg")
                    }
                }
            }

            // 过敏食材卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Allergies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAllergyDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Manage",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (allergyItems.isEmpty()) {
                        Text(
                            text = "No allergies recorded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allergyItems.forEach { item ->
                                AllergyChip(label = item)
                            }
                        }
                    }
                }
            }

            // 偏好食材卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showPreferenceDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Manage",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (preferenceItems.isEmpty()) {
                        Text(
                            text = "No preferences recorded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            preferenceItems.forEach { item ->
                                PreferenceChip(label = item)
                            }
                        }
                    }
                }
            }

            // 咀嚼能力（仅在编辑模式下显示）
            if (!isEditing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "咀嚼能力",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "NORMAL" to "正常",
                                "STRONG" to "较强",
                                "WEAK" to "较弱"
                            ).forEach { (value, label) ->
                                FilterChip(
                                    selected = chewingAbility == value,
                                    onClick = { chewingAbility = value },
                                    label = { Text(label) },
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "选择宝宝的咀嚼能力，帮助系统推荐适合的质地",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
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
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
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

@Composable
private fun GrowthDataCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AllergyChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PreferenceChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
