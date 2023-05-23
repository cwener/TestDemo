package com.example.test.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.test.R

/**
 * @author chengwen
 * @createTime 2023/5/23
 **/
class MaskOverLayImageView: AppCompatImageView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        post {
            val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.cover_mini)
            val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.circlemask) 
            val bitmap3 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) 
            val canvas = Canvas(bitmap3) 
            canvas.drawBitmap(bitmap2, 0f, 0f, null)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap1, 0f, 0f, paint)
            setImageBitmap(bitmap3)
        }
    }
}