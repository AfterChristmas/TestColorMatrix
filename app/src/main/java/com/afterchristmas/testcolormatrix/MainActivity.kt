package com.afterchristmas.testcolormatrix

import android.graphics.*
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 把彩色图片换成灰色
        // 方法1：
//        val matrix = ColorMatrix()
//        matrix.setSaturation(0f)
//        val filter = ColorMatrixColorFilter(matrix)
//        imageView1.colorFilter = filter

        // 方法2：
        val bmp = BitmapFactory.decodeResource(resources,
                R.drawable.test9)
//                image2!!.setImageBitmap(threshedBitmap(bmp));
//        image2!!.setImageBitmap(convertToBlackWhite(bmp));
//        lineGrey(bmp)
//        convertGreyImgByFloyd(bmp)
//        imageView2!!.setImageBitmap(sauva(threshedBitmap(bmp),0.3,31));
        imageView2!!.setImageBitmap(threshedBitmap(bmp));
//        imageView2!!.setImageBitmap(sauva(threshedBitmap(bmp),100));

       /* Thread{
            kotlin.run {
                val bitmap2 = sauva(threshedBitmap(bmp),30)
                runOnUiThread {
                    image2!!.setImageBitmap(bitmap2);
                }
            }
        }.start()*/
//                image2!!.setImageBitmap(lineGrey(bmp));
//        switchBlackNWhiteColor(bmp)
//        convertGreyImgByFloyd(bmp)
//        lineGrey(bmp)

        // 图片变暗
        // 方法1
//        val drawable = resources.getDrawable(R.drawable.baby)
//        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
//        imageView3.setImageDrawable(drawable)

        // 方法2：没布局文件里的image4，要显示的图片作为background,把变暗的图片或颜色设为src,就可以实现变暗的效果
        // ImageView image4 = (ImageView) findViewById(R.id.imageView4);
    }

    /**
     * 将彩色图转换为纯黑白二色
     *
     * @param 位图
     * @return 返回转换好的位图
     */
    private fun convertToBlackWhite(bmp: Bitmap): Bitmap {
        val width = bmp.width // 获取位图的宽
        val height = bmp.height // 获取位图的高
        val pixels = IntArray(width * height) // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height)
        val alpha = 0xFF shl 24
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                // 分离三原色
                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                // 转化成灰度像素
                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }

        // 新建图片
        val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)

        //        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
        return newBmp
    }

    fun switchBlackNWhiteColor(switchBitmap: Bitmap): Bitmap? {
        object : Thread() {
            override fun run() {
                val width = switchBitmap.width
                val height = switchBitmap.height

                // Turn the picture black and white
                val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBitmap)
                canvas.drawBitmap(switchBitmap, Matrix(), Paint())

                var current_color: Int
                var red: Int
                var green: Int
                var blue: Int
                var alpha: Int
                var avg = 0
                for (i in 0 until width) {
                    for (j in 0 until height) {
                        //get the color of each bit
                        current_color = switchBitmap.getPixel(i, j)
                        //achieve  three-primary color
                        red = Color.red(current_color)
                        green = Color.green(current_color)
                        blue = Color.blue(current_color)
                        alpha = Color.alpha(current_color)
                        avg = (red + green + blue) / 3// RGB average


                        if (avg >= 126) {
                            newBitmap.setPixel(i, j, Color.argb(alpha, 255, 255, 255))// white
                        } else if (avg < 126 && avg >= 115) {
                            newBitmap.setPixel(i, j, Color.argb(alpha, 108, 108, 108))//grey
                        } else {
                            newBitmap.setPixel(i, j, Color.argb(alpha, 0, 0, 0))// black
                        }


                    }
                }

                runOnUiThread { imageView2.setImageBitmap(newBitmap) }
            }
        }.start()

        return null
    }

    //抖动算法来对图像进行二值化处理
    private fun convertGreyImgByFloyd(img: Bitmap): Bitmap? {
        object : Thread() {
            override fun run() {
                val width = img.width         //获取位图的宽
                val height = img.height       //获取位图的高
                val pixels = IntArray(width * height) //通过位图的大小创建像素点数组
                img.getPixels(pixels, 0, width, 0, 0, width, height)
                val gray = IntArray(height * width)
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val grey = pixels[width * i + j]
                        val red = grey and 0x00FF0000 shr 16
                        gray[width * i + j] = red
                    }
                }
                var e = 0
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val g = gray[width * i + j]
                        if (g >= 128) {
                            pixels[width * i + j] = -0x1
                            e = g - 255

                        } else {
                            pixels[width * i + j] = -0x1000000
                            e = g - 0
                        }
                        if (j < width - 1 && i < height - 1) {
                            //右边像素处理
                            gray[width * i + j + 1] += 3 * e / 8
                            //下
                            gray[width * (i + 1) + j] += 3 * e / 8
                            //右下
                            gray[width * (i + 1) + j + 1] += e / 4
                        } else if (j == width - 1 && i < height - 1) {//靠右或靠下边的像素的情况
                            //下方像素处理
                            gray[width * (i + 1) + j] += 3 * e / 8
                        } else if (j < width - 1 && i == height - 1) {
                            //右边像素处理
                            gray[width * i + j + 1] += e / 4
                        }
                    }
                }
                val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                runOnUiThread { imageView2.setImageBitmap(mBitmap) }
            }
        }.start()
        return null
    }

    fun lineGrey(image: Bitmap) {
        object : Thread() {
            override fun run() {
                //得到图像的宽度和长度
                val width = image.width
                val height = image.height
                //创建线性拉升灰度图像
                var linegray: Bitmap? = null
                linegray = image.copy(Bitmap.Config.ARGB_8888, true)
                //依次循环对图像的像素进行处理
                for (i in 0 until width) {
                    for (j in 0 until height) {
                        //得到每点的像素值
                        val col = image.getPixel(i, j)
                        val alpha = col and -0x1000000
                        var red = col and 0x00FF0000 shr 16
                        var green = col and 0x0000FF00 shr 8
                        var blue = col and 0x000000FF
                        // 增加了图像的亮度
                        red = (1.1 * red + 30).toInt()
                        green = (1.1 * green + 30).toInt()
                        blue = (1.1 * blue + 30).toInt()
                        //对图像像素越界进行处理
                        if (red >= 255) {
                            red = 255
                        }

                        if (green >= 255) {
                            green = 255
                        }

                        if (blue >= 255) {
                            blue = 255
                        }
                        // 新的ARGB
                        val newColor = alpha or (red shl 16) or (green shl 8) or blue
                        System.err.println("grayImagePixels=="+newColor)
                        //设置新图像的RGB值
                        linegray!!.setPixel(i, j, newColor)
                    }
                }
                runOnUiThread { imageView2!!.setImageBitmap(linegray) }
            }
        }.start()
    }


    //matrix that changes picture into gray scale
    fun createGreyMatrix(): ColorMatrix {
        return ColorMatrix(floatArrayOf(0.2989f, 0.5870f, 0.1140f, 0f, 0f, 0.2989f, 0.5870f, 0.1140f, 0f, 0f, 0.2989f, 0.5870f, 0.1140f, 0f, 0f, 0f, 0f, 0f, 1f, 0f))
    }

    // matrix that changes gray scale picture into black and white at given threshold.
    // It works this way:
    // The matrix after multiplying returns negative values for colors darker than threshold
    // and values bigger than 255 for the ones higher.
    // Because the final result is always trimed to bounds (0..255) it will result in bitmap made of black and white pixels only
    fun createThresholdMatrix(threshold: Int): ColorMatrix {
        return ColorMatrix(floatArrayOf(
                85f, 85f, 85f, 0f, -255f * threshold,
                85f, 85f, 85f, 0f, -255f * threshold,
                85f, 85f, 85f, 0f, -255f * threshold,
                0f, 0f, 0f, 1f, 0f))
    }

    fun threshedBitmap(srcBitmap: Bitmap): Bitmap? {
        var result = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(result)

        val bitmapPaint = Paint()
        bitmapPaint.colorFilter = ColorMatrixColorFilter(createGreyMatrix())
        c.drawBitmap(srcBitmap, 0f, 0f, bitmapPaint)

//        bitmapPaint.colorFilter = ColorMatrixColorFilter(createThresholdMatrix(110))
//        c.drawBitmap(result, 0f, 0f, bitmapPaint)

        bitmapPaint.colorFilter = null

        saveImageToGallery(result)

        return result;
    }
    fun saveImageToGallery(bmp: Bitmap) {
        // 首先保存图片
        val appDir = File(Environment.getExternalStorageDirectory().path + "/高考必备笔记1")
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + "wy.jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            //            XyToast.makeText("文件未发现");
            e.printStackTrace()
        } catch (e: IOException) {
            //            XyToast.makeText("保存出错了...");
            e.printStackTrace()
        } catch (e: Exception) {
            //            XyToast.makeText("保存出错了...");
            e.printStackTrace()
        }

    }
    fun replaceColor(src: Bitmap?, fromColor: Int, targetColor: Int): Bitmap? {
        if (src == null) {
            return null
        }
        // Source image size
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (x in pixels.indices) {
            pixels[x] = if (pixels[x] == fromColor) targetColor else pixels[x]
        }
        // create result bitmap output
        val result = Bitmap.createBitmap(width, height, src.config)
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height)

        return result
    }

    fun sauva(src: Bitmap?, k: Double, windowSize: Int): Bitmap? {
        if (src == null) {
            return null
        }
        // Source image size

        val whalf = windowSize shr 1
        val width = src.width
        val height = src.height

        // create the integral image
        val integralImg = LongArray(width * height)
        val integralImgSqrt = LongArray(width * height)

        val grayImagePixels = IntArray(width * height)
        var grayImageIndex = 0
//        src.getPixels(grayImagePixels, 0, width, 0, 0, width, height)
        for (i in 0 until width) {
            for (j in 0 until height) {
                //get the color of each bit
               val  current_color = src.getPixel(i, j)
                //achieve  three-primary color
                val red = Color.red(current_color)
                System.err.println("threshold red==" + red)
                grayImagePixels[grayImageIndex] = red
            }
        }

        val biImagePixels = IntArray(width * height)
        val result = Bitmap.createBitmap(width, height, src.config)
        result.getPixels(biImagePixels, 0, width, 0, 0, width, height)

        //处理像素   二值化处理
        var sum: Long = 0
        var sqrtsum: Long = 0
        var index: Int
        for (i in 0 .. (height-1)) {
            sum = 0
            sqrtsum = 0
            for (j in 0 .. (width-1)) {
                index = i * width + j
                sum += grayImagePixels [index]
                sqrtsum  +=  grayImagePixels[index] * grayImagePixels[index]
                if (i == 0) {
                    integralImg[index] = sum;
                    integralImgSqrt[index] = sqrtsum;
                } else {
                    integralImgSqrt[index] = integralImgSqrt[(i - 1) * width + j] + sqrtsum;
                    integralImg[index] = integralImg[(i - 1) * height + j] + sum;
                }
            }
        }

        var xmin: Int
        var ymin: Int
        var xmax: Int
        var ymax: Int
        var mean: Double
        var std: Double
        var threshold: Double
        var diagsum: Double
        var idiagsum: Double
        var diff: Double
        var sqdiagsum: Double
        var sqidiagsum: Double
        var sqdiff: Double
        var area: Double

        for (i in 0 .. (width-1)) {
            for (j in 0 .. (height-1)) {
                xmin = max(0, i - whalf);
                ymin = max(0, j - whalf);
                xmax = min(width - 1, i + whalf);
                ymax = min(height - 1, j + whalf);

                area = ((xmax - xmin + 1) * (ymax - ymin + 1)).toDouble();
                if (area <= 0) {
                    biImagePixels[i * width + j] = 255;
                    continue;
                }

                if (xmin == 0 && ymin == 0) {
                    diff = integralImg[ymax * width + xmax].toDouble();
                    sqdiff = integralImgSqrt[ymax * width + xmax].toDouble();
                } else if (xmin > 0 && ymin == 0) {
                    diff = (integralImg[ymax * width + xmax] - integralImg[ymax * width + xmin - 1]).toDouble();
                    sqdiff = (integralImgSqrt[ymax * width + xmax] - integralImgSqrt[ymax * width + xmin - 1]).toDouble();
                } else if (xmin == 0 && ymin > 0) {
                    diff = (integralImg[ymax * width + xmax] - integralImg[(ymin - 1) * width + xmax]).toDouble();
                    sqdiff = (integralImgSqrt[ymax * width + xmax] - integralImgSqrt[(ymin - 1) * width + xmax]).toDouble()
                } else {
                    diagsum = (integralImg[ymax * width + xmax] + integralImg[(ymin - 1) * width + xmin - 1]).toDouble();
                    idiagsum = (integralImg[(ymin - 1) * width + xmax] + integralImg[ymax * width + xmin - 1]).toDouble();
                    diff = diagsum - idiagsum;

                    sqdiagsum = (integralImgSqrt[ymax * width + xmax] + integralImgSqrt[(ymin - 1) * width + xmin - 1]).toDouble();
                    sqidiagsum = (integralImgSqrt[(ymin - 1) * width + xmax] + integralImgSqrt[ymax * width + xmin - 1]).toDouble();
                    sqdiff = sqdiagsum - sqidiagsum;
                }

                mean = diff / area;
                std = sqrt((sqdiff - diff * diff / area) / (area - 1));
                threshold = mean * (1 + k * ((std / 128) - 1));
                System.err.println("threshold=="+threshold)
                if (grayImagePixels[j * width + i] < threshold)
                    biImagePixels[j * width + i] = 0;
                else
                    biImagePixels[j * width + i] = 255;

            }
        }
        for (i in 0..(width - 1)) {
            for (j in 0..(height - 1)) {

            }
        }


        // create result bitmap output
//        val result = Bitmap.createBitmap(width, height, src.config)
        //set pixels
        result.setPixels(biImagePixels, 0, width, 0, 0, width, height)
        return result
    }
}
