package ai.nota.howtowash.presentation.navigation.result

import ai.nota.howtowash.presentation.detector.Recognition
import ai.nota.howtowash.presentation.feature.result.ResultScreen
import ai.nota.howtowash.presentation.navigation.base.BaseDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import java.net.URLDecoder

object ResultDestination : BaseDestination {
    override val route: String = "result?imagePath={imagePath}&objects={objects}&latency={latency}&fps={fps}"
    override val destination: String = "result"
}

fun NavGraphBuilder.installResultDestination() {
    composable(
        route = ResultDestination.route,
        arguments = listOf(
            navArgument("imagePath") { defaultValue = "" },
            navArgument("objects") { defaultValue = "" },
            navArgument("latency") { defaultValue = 0L },
            navArgument("fps") { defaultValue = 0f }
        )
    ) {
        val imagePath = URLDecoder.decode(it.arguments?.getString("imagePath") ?: "", "utf8")
        val objects = Gson().fromJson(it.arguments?.getString("objects"), Array<Recognition>::class.java).toList()
        val latency = it.arguments?.getLong("latency") ?: 0L
        val fps = it.arguments?.getFloat("fps") ?: 0f
        ResultScreen(imagePath = imagePath, objects = objects, latency = latency, fps = fps)
    }
}