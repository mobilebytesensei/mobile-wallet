/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qrscan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.utils.QrCodeData

/**
 * MPay QR Code scanner screen.
 *
 * This screen handles the camera/scanner UI and emits decoded [QrCodeData]
 * via the [onQrCodeScanned] callback. Processing logic is handled externally.
 *
 * @param navigateBack Callback to navigate back
 * @param onQrCodeScanned Callback with decoded QR code data
 * @param viewModel The ViewModel for decoding QR codes
 */
@Composable
internal fun MpayQrScanScreen(
    navigateBack: () -> Unit,
    onQrCodeScanned: (QrCodeData) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MpayQrScanViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val qrData by viewModel.qrDataFlow.collectAsStateWithLifecycle()
    val error by viewModel.errorFlow.collectAsStateWithLifecycle()

    // Handle successful scan
    LaunchedEffect(qrData) {
        qrData?.let { data ->
            onQrCodeScanned(data)
        }
    }

    // Handle errors
    LaunchedEffect(error) {
        error?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            viewModel.clearError()
        }
    }

    MpayQrScanScreenContent(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onScanned = viewModel::onScanned,
        backPress = navigateBack,
    )
}

@Composable
fun MpayQrScanScreenContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onScanned: (String) -> Boolean,
    backPress: () -> Unit,
) {
    MifosScaffold(
        topBarTitle = null,
        backPress = backPress,
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            QrScannerWithPermissions(
                types = listOf(CodeType.QR),
                modifier = Modifier.padding(it),
                onScanned = onScanned,
            )
        }
    }
}
