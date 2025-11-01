package com.localpulse.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRCodeUtils {
    
    fun generateQRCodeBitmap(
        content: String,
        width: Int = 512,
        height: Int = 512
    ): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            
            bitmap
        } catch (e: Exception) {
            null
        }
    }
    
    fun validateQRCode(content: String): Boolean {
        return content.startsWith("LP_") && content.length > 10
    }
    
    fun extractTicketId(qrCode: String): String? {
        return if (validateQRCode(qrCode)) {
            qrCode.split("_").getOrNull(1)
        } else {
            null
        }
    }
}
