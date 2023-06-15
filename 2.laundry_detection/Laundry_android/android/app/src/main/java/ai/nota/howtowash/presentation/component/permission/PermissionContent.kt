package ai.nota.howtowash.presentation.component.permission

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

internal fun Context.checkPermission(permission: Array<String>): Boolean {
    return permission.all { ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED }
}

@Composable
fun rememberMultiplePermissionState(
    context: Context,
    permission: Array<String>,
    onPermissionResult: (isGranted: Boolean) -> Unit = {}
): MultiplePermissionState {

    val permissionState = remember {
        MultiplePermissionState(
            context = context,
            permissions = permission
        )
    }

    val activityResultLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isPermissionsGranted ->
            onPermissionResult(isPermissionsGranted.all { it.value })
        }

    DisposableEffect(key1 = permissionState) {
        permissionState.launcher = activityResultLauncher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

@Stable
class MultiplePermissionState(
    private val context: Context,
    private val permissions: Array<String>,
    internal var launcher: ActivityResultLauncher<Array<String>>? = null
) {
    fun launch() {
        if (!context.checkPermission(permissions))
            launcher?.launch(permissions)
    }
}