/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qrscan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.mpay.qrscan.MpayQrScanScreen

const val MPAY_QR_SCAN_ROUTE = "mpay_qr_scan_route"

fun NavController.navigateToMpayQrScan(navOptions: NavOptions? = null) =
    navigate(MPAY_QR_SCAN_ROUTE, navOptions)

/**
 * Registers the MPay QR scan screen in the navigation graph.
 *
 * This is a simplified scanner that only decodes QR codes and emits [QrCodeData].
 * Processing logic should be handled by the caller (typically fast-mpay module).
 *
 * @param navigateBack Callback to navigate back
 * @param onQrCodeScanned Callback invoked with the decoded QR code data
 */
fun NavGraphBuilder.mpayQrScanScreen(
    navigateBack: () -> Unit,
    onQrCodeScanned: (QrCodeData) -> Unit,
) {
    composableWithSlideTransitions(route = MPAY_QR_SCAN_ROUTE) {
        MpayQrScanScreen(
            navigateBack = navigateBack,
            onQrCodeScanned = onQrCodeScanned,
        )
    }
}
