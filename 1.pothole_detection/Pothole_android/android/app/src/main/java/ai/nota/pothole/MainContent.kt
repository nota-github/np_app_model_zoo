package ai.nota.pothole

import ai.nota.pothole.component.permission.rememberMultiplePermissionState
import ai.nota.pothole.component.toast.rememberToastState
import ai.nota.pothole.navigation.detection.DetectionDestination
import ai.nota.pothole.navigation.detection.installDetectionDestination
import ai.nota.pothole.navigation.home.HomeDestination
import ai.nota.pothole.navigation.home.installHomeDestination
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.launch

@Composable
fun MainContent(
    appState: AppState = rememberAppState(),
    onClickFinishActivity: () -> Unit
) {
    val context: Context = LocalContext.current
    val snackBarState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    val toastState = rememberToastState()
    val permissionState = rememberMultiplePermissionState(
        context = context,
        permission = arrayOf(
            Manifest.permission.CAMERA,
        ),
        onPermissionResult = { isGranted ->
            if (isGranted) {
                toastState.show("앱 내 권한이 허용되었습니다.")
            } else {
                coroutineScope.launch {
                    snackBarState.showSnackbar(
                        "앱 내 모든 권한을 앱 설정 페이지에서 허용해주세요.",
                        "허용하기",
                        true
                    ).let {
                        when (it) {
                            SnackbarResult.ActionPerformed -> {
                                context.startActivity(Intent().apply {
                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    data = Uri.fromParts("package", context.packageName, null)
                                })
                            }
                            SnackbarResult.Dismissed -> {
                                toastState.show("권한이 허용되지 않으면 앱을 사용할 수 없습니다. 앱을 종료합니다.")
                                onClickFinishActivity()
                            }
                        }
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionState.launch()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = appState.navController,
            startDestination = HomeDestination.route
        ) {
            installHomeDestination { modelType ->
                appState.navigate("${DetectionDestination.destination}?modelType=$modelType")
            }
            installDetectionDestination()
        }
    }
}