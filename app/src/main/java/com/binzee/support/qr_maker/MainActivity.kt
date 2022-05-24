package com.binzee.support.qr_maker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.textView).setOnClickListener {
            findViewById<ImageView>(R.id.iv_display).also {
                val bitmap = QRMaker.getDefault()
                    .createBitmap(
                        "112345889",
                        intArrayOf(500, 500),
                        charset = "ISO-8859-1",
                        format = QRMaker.CodeType.CODABAR
                    )
//                    .createBitmap("HELLO WORLD !!!", intArrayOf(500, 500))
                it.setImageBitmap(bitmap)
            }
        }
    }
}