package com.afterchristmas.testcolormatrix;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

public class Main2Activity extends AppCompatActivity {

    private SeekBar sb_red, sb_green, sb_blue, sb_alpha;
    private ImageView iv_show;
    private Bitmap afterBitmap;
    private Paint paint;
    private Canvas canvas;
    private Bitmap baseBitmap;
    private int width;
    private int height;
    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        iv_show = (ImageView) findViewById(R.id.iv_show);
        sb_red = (SeekBar) findViewById(R.id.sb_red);
        sb_green = (SeekBar) findViewById(R.id.sb_green);
        sb_blue = (SeekBar) findViewById(R.id.sb_blue);
        sb_alpha = (SeekBar) findViewById(R.id.sb_alpha);

        sb_red.setOnSeekBarChangeListener(seekBarChange);
        sb_green.setOnSeekBarChangeListener(seekBarChange);
        sb_blue.setOnSeekBarChangeListener(seekBarChange);
        sb_alpha.setOnSeekBarChangeListener(seekBarChange);

     /*   // 从资源文件中获取图片
        baseBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.test9);
        // 获取一个与baseBitmap大小一致的可编辑的空图片
        afterBitmap = Bitmap.createBitmap(baseBitmap.getWidth(),
                baseBitmap.getHeight(), baseBitmap.getConfig());
        canvas = new Canvas(afterBitmap);
        paint = new Paint();*/
        Bitmap originImg = BitmapFactory.decodeResource(getResources(), R.drawable.test9);
        binarization(originImg);
     /*   Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(), originImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayImg);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(originImg,0,0,paint);
        iv_show.setImageBitmap(grayImg);*/

    }

    private SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 获取每个SeekBar当前的值
            float progressR = sb_red.getProgress() / 128f;
            float progressG = sb_green.getProgress() / 128f;
            float progressB = sb_blue.getProgress() / 128f;
            float progressA = sb_alpha.getProgress() / 128f;
            Log.i("main", "R：G：B=" + progressR + "：" + progressG + "：" + progressB);
            // 根据SeekBar定义RGBA的矩阵
            float[] src = new float[]{
                    progressR, 0, 0, 0, 0,
                    0, progressG, 0, 0, 0,
                    0, 0, progressB, 0, 0,
                    0, 0, 0, progressA, 0};
            // 定义ColorMatrix，并指定RGBA矩阵
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(src);
            // 设置Paint的颜色
            paint.setColorFilter(new ColorMatrixColorFilter(src));
            // 通过指定了RGBA矩阵的Paint把原图画到空白图片上
            canvas.drawBitmap(baseBitmap, new Matrix(), paint);
            iv_show.setImageBitmap(afterBitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }
    };

    public void binarization(final Bitmap img) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                width = img.getWidth();
                height = img.getHeight();
                int area = width * height;
                int gray[][] = new int[width][height];
                int average = 0;// 灰度平均值
                int graysum = 0;
                int graymean = 0;
                int grayfrontmean = 0;
                int graybackmean = 0;
                int pixelGray;
                int front = 0;
                int back = 0;
                int[] pix = new int[width * height];
                img.getPixels(pix, 0, width, 0, 0, width, height);
                for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
                    for (int j = 1; j < height; j++) {
                        int x = j * width + i;
                        int r = (pix[x] >> 16) & 0xff;
                        int g = (pix[x] >> 8) & 0xff;
                        int b = pix[x] & 0xff;
                        pixelGray = (int) (0.3 * r + 0.59 * g + 0.11 * b);// 计算每个坐标点的灰度
                        gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
                        graysum += pixelGray;
                    }
                }
                graymean = (int) (graysum / area);// 整个图的灰度平均值
                average = graymean;
                Log.i(TAG,"Average:"+average);
                for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
                {
                    for (int j = 0; j < height; j++) {
                        if (((gray[i][j]) & (0x0000ff)) < graymean) {
                            graybackmean += ((gray[i][j]) & (0x0000ff));
                            back++;
                        } else {
                            grayfrontmean += ((gray[i][j]) & (0x0000ff));
                            front++;
                        }
                    }
                }
                int frontvalue = (int) (grayfrontmean / front);// 前景中心
                int backvalue = (int) (graybackmean / back);// 背景中心
                float G[] = new float[frontvalue - backvalue + 1];// 方差数组
                int s = 0;
                Log.i(TAG,"Front:"+front+"**Frontvalue:"+frontvalue+"**Backvalue:"+backvalue);
                for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法（OTSU算法）
                {
                    back = 0;
                    front = 0;
                    grayfrontmean = 0;
                    graybackmean = 0;
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
                                graybackmean += ((gray[i][j]) & (0x0000ff));
                                back++;
                            } else {
                                grayfrontmean += ((gray[i][j]) & (0x0000ff));
                                front++;
                            }
                        }
                    }
                    grayfrontmean = (int) (grayfrontmean / front);
                    graybackmean = (int) (graybackmean / back);
                    G[s] = (((float) back / area) * (graybackmean - average)
                            * (graybackmean - average) + ((float) front / area)
                            * (grayfrontmean - average) * (grayfrontmean - average));
                    s++;
                }
                float max = G[0];
                int index = 0;
                for (int i = 1; i < frontvalue - backvalue + 1; i++) {
                    if (max < G[i]) {
                        max = G[i];
                        index = i;
                    }
                }

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int in = j * width + i;
                        if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
                            pix[in] = Color.rgb(0, 0, 0);
                        } else {
                            pix[in] = Color.rgb(255, 255, 255);
                        }
                    }
                }

                final Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                temp.setPixels(pix, 0, width, 0, 0, width, height);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_show.setImageBitmap(temp);
                    }
                });
            }
        }.start();

    }
}
