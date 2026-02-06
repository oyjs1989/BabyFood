package com.example.babyfood.presentation.ui.inventory

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.animation.core.*
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.util.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryFormScreen(
    itemId: Long = 0L,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = itemId > 0L
    val context = LocalContext.current

    // 表单状态
    var foodId by remember { mutableStateOf(0L) }
    var foodName by remember { mutableStateOf("") }
    var foodImageUrl by remember { mutableStateOf<String?>(null) }
    var productionDate by remember { mutableStateOf(kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date) }
    var expiryDate by remember { mutableStateOf(kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date) }
    var storageMethod by remember { mutableStateOf(StorageMethod.REFRIGERATOR) }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // 未保存修改跟踪
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    // 日期选择器状态
    var showProductionDatePicker by remember { mutableStateOf(false) }
    var showExpiryDatePicker by remember { mutableStateOf(false) }

    // 图像识别相关状态
    var showImageRecognitionDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    val tempImageUri = remember { ImageUtils.createTempImageFile(context) }

    // 拍照启动器（先定义，因为 permissionLauncher 需要引用它）
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.recognizeFood(tempImageUri)
        }
    }

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("InventoryFormScreen", "✓ 相机权限已授予")
            cameraLauncher.launch(tempImageUri)
        } else {
            Log.w("InventoryFormScreen", "⚠️ 相机权限被拒绝")
            showPermissionDeniedDialog = true
        }
    }

    // 相册选择启动器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.recognizeFood(it)
        }
    }

    // 编辑模式：加载数据
    LaunchedEffect(itemId) {
        if (isEditing) {
            viewModel.loadInventoryItem(itemId)
        }
    }

    LaunchedEffect(uiState.selectedItem) {
        uiState.selectedItem?.let { item ->
            foodId = item.foodId
            foodName = item.foodName
            foodImageUrl = item.foodImageUrl
            productionDate = item.productionDate
            expiryDate = item.expiryDate
            storageMethod = item.storageMethod
            quantity = item.quantity.toString()
            unit = item.unit
            notes = item.notes ?: ""
        }
    }

    // 监听识别结果，自动填充表单
    LaunchedEffect(uiState.recognitionResult) {
        uiState.recognitionResult?.let { result ->
            foodName = result.foodName
            foodId = result.foodId
            foodImageUrl = result.foodImageUrl
            storageMethod = StorageMethod.valueOf(result.storageMethod)
            unit = result.defaultUnit
            quantity = result.quantity.toString()
            
            // 计算保质期
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val expiryEpochDay = today.toEpochDays() + result.estimatedShelfLife
            expiryDate = kotlinx.datetime.LocalDate.fromEpochDays(expiryEpochDay)
            
            // 填充备注（包含置信度）
            val notesBuilder = StringBuilder()
            if (result.notes != null) {
                notesBuilder.append(result.notes).append("\n")
            }
            notesBuilder.append("AI 识别置信度: ${(result.confidence * 100).toInt()}%")
            notes = notesBuilder.toString()
            
            viewModel.clearRecognitionResult()
        }
    }

    // 监听保存成功
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearSavedFlag()
            onSave()
        }
    }

    // 保存函数
    val saveInventoryItem = {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val item = InventoryItem(
            id = itemId,
            foodId = foodId,
            foodName = foodName,
            foodImageUrl = foodImageUrl,
            productionDate = productionDate,
            expiryDate = expiryDate,
            storageMethod = storageMethod,
            quantity = quantity.toFloatOrNull() ?: 0f,
            unit = unit,
            addedAt = if (isEditing) uiState.selectedItem?.addedAt ?: "" else currentTime.date.toString(),
            notes = notes.ifBlank { null }
        )
        viewModel.saveInventoryItem(item)
        hasUnsavedChanges = false
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Check,
                label = "保存",
                contentDescription = "保存食材",
                onClick = saveInventoryItem,
                enabled = foodName.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()
            )
        )
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 食材ID（暂时作为测试，实际应该从后端选择）
            OutlinedTextField(
                value = foodId.toString(),
                onValueChange = { foodId = it.toLongOrNull() ?: 0L },
                label = { Text("食材ID（测试用）") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 食材名称
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("食材名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showImageRecognitionDialog = true }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "拍照识别")
                    }
                }
            )

            // 生产日期
            OutlinedTextField(
                value = productionDate.toString(),
                onValueChange = { },
                label = { Text("生产日期") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { showProductionDatePicker = true }) {
                        Text("选择")
                    }
                }
            )

            // 保质期
            OutlinedTextField(
                value = expiryDate.toString(),
                onValueChange = { },
                label = { Text("保质期") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { showExpiryDatePicker = true }) {
                        Text("选择")
                    }
                }
            )

            // 保存方式
            Text(
                text = "保存方式",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            StorageMethod.values().forEach { method ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = storageMethod == method,
                        onClick = { storageMethod = method }
                    )
                    Text(
                        text = method.displayName,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // 数量和单位
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("数量") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("单位") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("如：g、kg、个") }
                )
            }

            // 备注
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 生产日期选择器
    if (showProductionDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = productionDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showProductionDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            productionDate = kotlinx.datetime.LocalDate.fromEpochDays((millis / (24 * 60 * 60 * 1000)).toInt())
                        }
                        showProductionDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProductionDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 保质期选择器
    if (showExpiryDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = expiryDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showExpiryDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            expiryDate = kotlinx.datetime.LocalDate.fromEpochDays((millis / (24 * 60 * 60 * 1000)).toInt())
                        }
                        showExpiryDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpiryDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 图像识别对话框
    if (showImageRecognitionDialog) {
        AlertDialog(
            onDismissRequest = { showImageRecognitionDialog = false },
            title = { Text("拍照识别食材") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            Log.d("InventoryFormScreen", "========== 检查相机权限 ==========")
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    Log.d("InventoryFormScreen", "✓ 相机权限已授予，直接启动相机")
                                    cameraLauncher.launch(tempImageUri)
                                }
                                else -> {
                                    Log.d("InventoryFormScreen", "⚠️ 请求相机权限")
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                            showImageRecognitionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "拍照")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("拍照")
                    }

                    Button(
                        onClick = {
                            Log.d("InventoryFormScreen", "✓ 从相册选择")
                            imagePickerLauncher.launch("image/*")
                            showImageRecognitionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "相册")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("从相册选择")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageRecognitionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 权限被拒绝对话框
    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("需要相机权限") },
            text = { Text("拍照功能需要相机权限。请在系统设置中开启权限后重试。") },
            confirmButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // AI 识别加载遮罩层 - 直接显示在页面上
    if (uiState.isRecognizing) {
        // 使用 LaunchedEffect 手动实现旋转动画
        var rotation by remember { mutableStateOf(0f) }
        var rotation2 by remember { mutableStateOf(0f) }
        var textAlpha by remember { mutableStateOf(1f) }
        var dotCount by remember { mutableStateOf(0) }
        
        // 旋转动画 - 外圈
        LaunchedEffect(Unit) {
            while (true) {
                rotation = (rotation + 10f) % 360f
                delay(30)
            }
        }
        
        // 旋转动画 - 内圈（反向）
        LaunchedEffect(Unit) {
            while (true) {
                rotation2 = (rotation2 - 15f) % 360f
                delay(25)
            }
        }
        
        // 文字透明度动画（闪烁效果）
        LaunchedEffect(Unit) {
            var fading = true
            while (true) {
                if (fading) {
                    textAlpha -= 0.1f
                    if (textAlpha <= 0.5f) fading = false
                } else {
                    textAlpha += 0.1f
                    if (textAlpha >= 1f) fading = true
                }
                delay(150)
            }
        }
        
        // 动态点动画
        LaunchedEffect(Unit) {
            while (true) {
                dotCount = (dotCount + 1) % 4
                delay(500)
            }
        }
        
        // 获取颜色
        val primaryColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
        val secondaryColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        val surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        val lightGrayColor = androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.3f)

        // 全屏遮罩层
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { }, // 拦截点击事件
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceColor,
                shadowElevation = 8.dp,
                modifier = Modifier.padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // 标题
                    Text(
                        text = "AI 识别中",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 显示正在识别的图片
                    uiState.recognizingImageUri?.let { imageUri ->
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "正在识别的食材图片",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 使用 Canvas 自定义绘制双圈旋转动画
                    Box(
                        modifier = Modifier.size(70.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 外圈 - 正向旋转
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 5f
                            val diameter = size.minDimension - strokeWidth
                            val radius = diameter / 2
                            val centerX = size.width / 2
                            val centerY = size.height / 2

                            // 外圈旋转圆弧
                            drawArc(
                                color = primaryColor,
                                startAngle = rotation - 90f,
                                sweepAngle = 80f,
                                useCenter = false,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - radius,
                                    centerY - radius
                                ),
                                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        
                        // 内圈 - 反向旋转
                        Canvas(modifier = Modifier.size(50.dp)) {
                            val strokeWidth = 5f
                            val diameter = size.minDimension - strokeWidth
                            val radius = diameter / 2
                            val centerX = size.width / 2
                            val centerY = size.height / 2

                            // 内圈旋转圆弧
                            drawArc(
                                color = secondaryColor,
                                startAngle = rotation2 - 90f,
                                sweepAngle = 100f,
                                useCenter = false,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - radius,
                                    centerY - radius
                                ),
                                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 动态文字 - 带闪烁效果和动态点
                    val dots = ".".repeat(dotCount)
                    Text(
                        text = "正在智能识别食材信息$dots",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.graphicsLayer { alpha = textAlpha }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 副标题
                    Text(
                        text = "AI 正在分析图片特征",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 识别错误对话框
    if (uiState.recognitionError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearRecognitionResult() },
            title = { Text("识别失败") },
            text = { Text(uiState.recognitionError ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearRecognitionResult() }) {
                    Text("确定")
                }
            }
        )
    }

    // 离开确认对话框
    if (showExitConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmationDialog = false },
            title = { Text("未保存的修改") },
            text = { Text("您有未保存的修改，是否要保存？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        saveInventoryItem()
                        showExitConfirmationDialog = false
                    },
                    enabled = foodName.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitConfirmationDialog = false
                        onBack()
                    }
                ) {
                    Text("放弃修改")
                }
            }
        )
    }
}