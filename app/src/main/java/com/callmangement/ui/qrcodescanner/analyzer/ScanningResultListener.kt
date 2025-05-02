package com.callmangement.ui.qrcodescanner.analyzer

interface ScanningResultListener {
    fun onScanned(result: String)
}