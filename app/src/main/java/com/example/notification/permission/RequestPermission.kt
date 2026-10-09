package com.example.notification.permission

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermissionDialog() {
    val context = LocalContext.current

    // Trạng thái lưu xem đã có quyền hay chưa
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            // Dưới Android 13 không cần xin quyền runtime POST_NOTIFICATIONS
            mutableStateOf(true)
        }
    }

    // Launcher để bật popup xin quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            // Quyền đã được cấp, bạn có thể lấy FCM Token hoặc tiếp tục
        } else {
            // Người dùng từ chối quyền, hiển thị thông báo giải thích cho họ
        }
    }

    // Gọi yêu cầu quyền khi Composable hiển thị (nếu chưa có quyền và là Android 13+)
    LaunchedEffect(key1 = Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}