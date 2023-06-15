package ai.nota.pothole.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ModelType {
    Nano,
    Nano70,
    Medium60
}


@Composable
fun HomeContent(
    navigateToDetectionScreen: (modelType: String) -> Unit
) {
    var selectedModelType by rememberSaveable { mutableStateOf(ModelType.Nano) }

    fun isSelectedType(checkableType: ModelType): Boolean = selectedModelType == checkableType
    fun onChangeState(newType: ModelType) {
        selectedModelType = newType
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Column {
                ModelType.values().forEach {
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = isSelectedType(it),
                                onClick = {
                                    onChangeState(it)
                                }
                            )
                            .padding(end = 10.dp)
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(end = 10.dp),
                            selected = isSelectedType(it),
                            onClick = {
                                onChangeState(it)
                            }
                        )

                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = it.name
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                content = { Text("Detection") },
                onClick = {
                    navigateToDetectionScreen(selectedModelType.name)
                },
            )
        }
    }
}
