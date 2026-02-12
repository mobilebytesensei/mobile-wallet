/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.interbank.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.mifospay.feature.send.interbank.InterbankTransferFlowScreen

/**
 * Route for inter-bank transfer screen.
 *
 * @param returnDestination Where to navigate after transfer success
 * @param prefilledPhoneNumber Phone number from QR scan for participant lookup
 * @param prefilledRecipientName Display hint while looking up participant
 * @param prefilledAmount Pre-filled amount from QR code
 */
@Serializable
data class InterbankTransferRoute(
    val returnDestination: String = "home",
    val prefilledPhoneNumber: String? = null,
    val prefilledRecipientName: String? = null,
    val prefilledAmount: String? = null,
)

/**
 * Navigate to inter-bank transfer screen.
 *
 * @param returnDestination Where to navigate after transfer success
 * @param navOptions Navigation options
 */
fun NavController.navigateToInterbankTransfer(
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    this.navigate(InterbankTransferRoute(returnDestination = returnDestination), navOptions)
}

/**
 * Navigate to inter-bank transfer screen with pre-filled data from QR scan.
 *
 * @param phoneNumber Phone number for participant lookup (REQUIRED for QR-initiated transfers)
 * @param recipientName Display hint while looking up participant
 * @param amount Pre-filled amount from QR code
 * @param returnDestination Where to navigate after transfer success
 * @param navOptions Navigation options
 */
fun NavController.navigateToInterbankTransfer(
    phoneNumber: String,
    recipientName: String? = null,
    amount: String? = null,
    returnDestination: String = "home",
    navOptions: NavOptions? = null,
) {
    this.navigate(
        InterbankTransferRoute(
            returnDestination = returnDestination,
            prefilledPhoneNumber = phoneNumber,
            prefilledRecipientName = recipientName,
            prefilledAmount = amount,
        ),
        navOptions,
    )
}

fun NavGraphBuilder.interbankTransferScreen(
    onBackClick: () -> Unit,
    onTransferSuccess: () -> Unit,
) {
    composable<InterbankTransferRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InterbankTransferRoute>()
        InterbankTransferFlowScreen(
            onBackClick = onBackClick,
            onTransferSuccess = onTransferSuccess,
            prefilledPhoneNumber = route.prefilledPhoneNumber,
            prefilledRecipientName = route.prefilledRecipientName,
            prefilledAmount = route.prefilledAmount,
        )
    }
}
