package com.co77iri.imu_walking_pattern.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnackBar(snackBarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackBarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(
                    bottom = 50.dp,
                    start = 20.dp,
                    end = 20.dp
                )
            ) {
                Text(text = data.message)
            }
        }
    )
}
