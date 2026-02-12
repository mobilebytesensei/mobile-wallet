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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.mifospay.core.data.util.MpayQrCodeProcessor
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrCodeType

/**
 * ViewModel for MPay QR code scanning.
 *
 * This is a simplified ViewModel that only decodes QR codes and emits [QrCodeData].
 * All processing logic has been moved to the fast-mpay module.
 */
class MpayQrScanViewModel : ViewModel() {

    private val _qrDataFlow = MutableStateFlow<QrCodeData?>(null)
    val qrDataFlow = _qrDataFlow.asStateFlow()

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow = _errorFlow.asStateFlow()

    /**
     * Process scanned QR code data.
     *
     * @param data Raw QR code data string
     * @return true if QR was successfully decoded, false otherwise
     */
    fun onScanned(data: String): Boolean {
        return try {
            // Try MPay/UPI format first
            val qrData = MpayQrCodeProcessor.decodeMpayString(data)
            _qrDataFlow.update { qrData }
            _errorFlow.update { null }
            true
        } catch (e: Exception) {
            // Try legacy Beneficiary JSON format
            handleLegacyBeneficiaryJson(data)
        }
    }

    /**
     * Handles legacy Beneficiary JSON format QR codes.
     * Converts to QrCodeData with BENEFICIARY type.
     */
    private fun handleLegacyBeneficiaryJson(data: String): Boolean {
        val trimmedData = data.trim()

        if (!trimmedData.startsWith("{") || !trimmedData.endsWith("}")) {
            _errorFlow.update { "Invalid QR code format" }
            return false
        }

        return try {
            val beneficiary = Json.decodeFromString<Beneficiary>(trimmedData)

            // Convert Beneficiary to QrCodeData
            // Note: clientId and accountId are not available in legacy format
            val qrData = QrCodeData(
                type = QrCodeType.BENEFICIARY,
                clientId = 0L,
                clientName = beneficiary.clientName ?: beneficiary.name ?: "",
                accountNo = beneficiary.accountNumber ?: "",
                amount = "",
                accountId = 0L,
                accountTypeId = beneficiary.accountType?.id?.toLong() ?: QrCodeData.ACCOUNT_TYPE_ID,
            )

            _qrDataFlow.update { qrData }
            _errorFlow.update { null }
            true
        } catch (e: Exception) {
            _errorFlow.update { "Scan a valid QR code" }
            false
        }
    }

    /**
     * Clears the current result to allow re-scanning.
     */
    fun clearResult() {
        _qrDataFlow.update { null }
        _errorFlow.update { null }
    }

    /**
     * Clears only the error to allow re-scanning after an error.
     */
    fun clearError() {
        _errorFlow.update { null }
    }
}
