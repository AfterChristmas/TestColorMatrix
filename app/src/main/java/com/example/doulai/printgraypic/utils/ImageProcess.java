package com.example.doulai.printgraypic.utils;

import android.graphics.Bitmap;

/**
 * @Description:
 * @Date: 2019/12/12 13:32
 * @Auther: wanyan
 */
public class ImageProcess {
    static {
        //ndk-build，这里对应 Android.mk 里的 LOCAL_MODULE := NDKSample
        //CMake，这里对应 CMakeLists.txt 里的 add_library NDKSample
        System.loadLibrary("X86Bridge");
    }

    //使用 native 关键字指示以原生代码形式实现的方法
    public static native Bitmap sauvola(Bitmap bitmap, int width, int window, double k);

    public static Bitmap sauvolaBitmap(Bitmap grayBitmap) {
        return sauvola(grayBitmap, 958, 31, 0.1);
    }
}
