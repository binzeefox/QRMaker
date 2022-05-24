package com.binzee.support.qr_maker

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.oned.CodaBarWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.lang.IllegalArgumentException
import java.util.*

/**
 * QRMaker
 *
 * 二维码生成器抽象
 * @since  2022/5/23 14:16
 * @author tong.xiwen
 */
interface QRMaker {
    enum class CodeType {
        QR,
        CODABAR
    }

    companion object {
        /**
         * 获取默认实现
         */
        fun getDefault(): QRMaker = QRMakerDefaultImpl()
    }

    /**
     * 生成Bitmap
     *
     * @param content   二维码内容
     * @param sizePx    图片宽高，单位px，应大于0
     * @param format    图像格式，默认QR码
     * @param charset   字符集，默认”UTF-8“
     * @param errorCorrection   容错等级，默认L
     * @param margin    留白，默认4px
     * @param colorDark 暗色色块色值
     * @param colorLight    亮色色块色值
     */
    fun createBitmap(
        content: String,
        sizePx: IntArray,
        format: CodeType = CodeType.QR,
        charset: String = "UTF-8",
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.L,
        margin: Long = 4,
        @ColorInt colorDark: Int = Color.parseColor("#000000"),
        @ColorInt colorLight: Int = Color.parseColor("#ffffff")
    ): Bitmap?
}

/**
 * 默认构造器实现
 */
class QRMakerDefaultImpl: QRMaker {
    companion object {
        private const val TAG = "QRMaker"
    }

    override fun createBitmap(
        content: String,
        sizePx: IntArray,
        format: QRMaker.CodeType,
        charset: String,
        errorCorrection: ErrorCorrectionLevel,
        margin: Long,
        colorDark: Int,
        colorLight: Int
    ): Bitmap? {
        if (content.isEmpty())
            throw IllegalArgumentException("content should not be null!!!")
        if (sizePx[0] < 0 || sizePx[1] < 0)
            throw IllegalArgumentException("invalid size input, check you input of argument 'sizePx'")

        try {
            val hints = Hashtable<EncodeHintType, Any>().also {
                it[EncodeHintType.CHARACTER_SET] = charset
                it[EncodeHintType.ERROR_CORRECTION] = errorCorrection
                it[EncodeHintType.MARGIN] = margin
            }

            val matrix = when (format) {
                QRMaker.CodeType.QR -> QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx[0], sizePx[1], hints)
                QRMaker.CodeType.CODABAR -> CodaBarWriter().encode(content, BarcodeFormat.CODABAR, sizePx[0], sizePx[1], hints)
            }


            // 创建像素数组
            val pixels = IntArray(sizePx[0] * sizePx[1])
            for (y in 0 until sizePx[1]) {
                for (x in 0 until sizePx[0]) {
                    // 若像素为1，则为深色色款，否则为浅色
                    pixels[y * sizePx[0] + x] =
                        if (matrix.get(x, y))
                            colorDark
                        else
                            colorLight
                }
            }

            // 根据像素创建bitmap
            val bitmap = Bitmap.createBitmap(sizePx[0], sizePx[1], Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, sizePx[0], 0, 0, sizePx[0], sizePx[1])
            return bitmap
        } catch (e: Throwable) {
            Log.e(TAG, "createBitmap: create failed", e)
            return null
        }
    }

}

