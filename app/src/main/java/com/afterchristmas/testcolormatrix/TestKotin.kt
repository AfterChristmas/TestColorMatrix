package com.afterchristmas.testcolormatrix

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @Description:
 * @Date: 2019/12/13 11:33
 * @Auther: wanyan
 */
class TestKotin {
    fun lineGrey(image: Bitmap) {
        var red = 12
        var green = 12
        var blue = 12
        var alpha = 12
        val newColor = alpha or (red shl 16) or (green shl 8) or blue
    }
}