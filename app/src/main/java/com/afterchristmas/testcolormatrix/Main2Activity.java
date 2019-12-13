package com.afterchristmas.testcolormatrix;

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

import androidx.appcompat.app.AppCompatActivity;
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
        Bitmap originImg = BitmapFactory.decodeResource(getResources(), R.drawable.gray_test);
//        binarization(originImg);
        Sauvola(originImg);

     /*   Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(), originImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayImg);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(originImg,0,0,paint);
        iv_show.setImageBitmap(grayImg);*/
        for (int i = 0; i < 10; i++) {
            
        }

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

    /**
     实现Sauvola算法实现图像二值化

     @param bin_image 用于存储二值化完成的图像
     @param gray_image 用于存储等待二值化完成的灰度图像
     */
    public  void Sauvola(Bitmap grayBitmap)
    {

        //以当前像素点为中心的邻域的宽度
        int w = 40;

        //使用者自定义的修正系数
        double k = 0.3;

        //邻域边界距离中心点的距离
        int whalf = w >> 1;
        int MAXVAL = 256;

        int image_width = grayBitmap.getWidth();
        int image_height = grayBitmap.getHeight();
        int[][] bin_image = new int[image_width][image_height];
        int[][] gray_image = new int[image_width][image_height];



        for (int i = 0; i < image_width; i++) {
            for (int j = 0; j < image_height; j++) {
                int pixel = grayBitmap.getPixel(i, j);
                bin_image[i][j] = (byte) (grayBitmap.getPixel(i, j)&0xff);
                System.err.println("bin_image=="+bin_image[i][j]);
            }
        }




        int[][] integral_image = new int[image_width][image_height];
        int[][] integral_sqimg = new int[image_width][image_height];
        int[][] rowsum_image = new int[image_width][image_height];
        int[][] rowsum_sqimg = new int[image_width][image_height];


        int xmin,ymin,xmax,ymax;
        double diagsum,idiagsum,diff,sqdiagsum,sqidiagsum,sqdiff,area;
        double mean,std,threshold;

        for (int j = 0; j < image_height; j++)
        {
            rowsum_image[0][j] = gray_image[0][j];
            rowsum_sqimg[0][j] = gray_image[0][j] * gray_image[0][j];
        }
        for (int i = 1; i < image_width; i++)
        {
            for (int j = 0; j < image_height; j++)
            {
                //计算图像范围内任意宽度窗口(邻域)的灰度值之和
                rowsum_image[i][j] = rowsum_image[i - 1][j] + gray_image[i][j];

                //计算图像范围内任意宽度窗口(邻域)的灰度值平方之和
                rowsum_sqimg[i][j] = rowsum_sqimg[i - 1][j] + gray_image[i][j] * gray_image[i][j];
            }
        }

        for (int i = 0; i < image_width; i++)
        {
            integral_image[i][0] = rowsum_image[i][0];
            integral_sqimg[i][0] = rowsum_sqimg[i][0];
        }
        for (int i = 0; i < image_width; i++)
        {
            for (int j = 1; j < image_height; j++)
            {
                //计算图像范围内任意宽度窗口(邻域)的灰度值的积分
                integral_image[i][j] = integral_image[i][j - 1] + rowsum_image[i][j];

                //计算图像范围内任意宽度窗口(邻域)的灰度值平方的积分
                integral_sqimg[i][j] = integral_sqimg[i][j - 1] + rowsum_sqimg[i][j];
            }
        }

        //Calculate the mean and standard deviation using the integral image

        for(int i=0; i<image_width; i++)
        {
            for(int j=0; j<image_height; j++)
            {
                xmin = Math.max(0,i-whalf);
                ymin = Math.max(0, j - whalf);
                xmax = Math.min(image_width - 1, i + whalf);
                ymax = Math.min(image_height - 1, j + whalf);
                area = (xmax-xmin+1)*(ymax-ymin+1);
                // area can't be 0 here
                // proof (assuming whalf >= 0):
                // we'll prove that (xmax-xmin+1) > 0,
                // (ymax-ymin+1) is analogous
                // It's the same as to prove: xmax >= xmin
                // image_width - 1 >= 0         since image_width > i >= 0
                // i + whalf >= 0               since i >= 0, whalf >= 0
                // i + whalf >= i - whalf       since whalf >= 0
                // image_width - 1 >= i - whalf since image_width > i
                // --IM
                if (area <= 0)
                {
                    throw new RuntimeException("Binarize: area can't be 0 here");
                }
                if (xmin == 0 && ymin == 0)
                { // Point at origin
                    diff = integral_image[xmax][ymax];
                    sqdiff = integral_sqimg[xmax][ymax];
                }
                else if (xmin == 0 && ymin > 0)
                { // first column
                    diff = integral_image[xmax][ymax] - integral_image[xmax][ymin - 1];
                    sqdiff = integral_sqimg[xmax][ymax] - integral_sqimg[xmax][ymin - 1];
                }
                else if (xmin > 0 && ymin == 0)
                { // first row
                    diff = integral_image[xmax][ymax] - integral_image[xmin - 1][ymax];
                    sqdiff = integral_sqimg[xmax][ymax] - integral_sqimg[xmin - 1][ymax];
                }
                else
                { // rest of the image
                    diagsum = integral_image[xmax][ymax] + integral_image[xmin - 1][ymin - 1];
                    idiagsum = integral_image[xmax][ymin - 1] + integral_image[xmin - 1][ymax];
                    //以(i,j)为中心点的w邻域内灰度值的积分
                    diff = diagsum - idiagsum;

                    sqdiagsum = integral_sqimg[xmax][ymax] + integral_sqimg[xmin - 1][ymin - 1];
                    sqidiagsum = integral_sqimg[xmax][ymin - 1] + integral_sqimg[xmin - 1][ymax];
                    //以(i,j)为中心点的w邻域内灰度值平方的积分
                    sqdiff = sqdiagsum - sqidiagsum;
                }

                //以(i,j)为中心点的w邻域内的灰度均值
                mean = diff/area;

                //以(i,j)为中心点的w邻域内的标准方差
                std = Math.sqrt((sqdiff - diff*diff/area)/(area-1));

                //根据Sauvola计算公式和以(i,j)为中心点的w邻域内的灰度均值与标准方差来计算当前点(i,j)的二值化阈值
                threshold = mean*(1+k*((std/128)-1));
                System.err.println("threshold==std "+std+"  mean="+mean+"  threshold="+threshold+"  width="+width+"/i="+i+"/"+j);
                System.err.println("threshold==gray_image "+gray_image[i][j]);

                //根据当前点的阈值对当前像素点进行二值化
                if(gray_image[i][j] < threshold)
                {
                    bin_image[i][j] = 0;
                }
                else
                {
                    bin_image[i][j] = (byte)(MAXVAL-1);
                }
            }
        }





        int  binIndex =0;
        int[] _bin_image = new int[image_width*image_height];
        for (int i = 0; i < image_width; i++) {
            for (int j = 0; j < image_height; j++) {
                _bin_image[binIndex]= bin_image[i][j] ;
                binIndex++;
            }
        }
        final Bitmap temp = Bitmap.createBitmap(image_width, image_height, Bitmap.Config.RGB_565);
        temp.setPixels(_bin_image, 0, image_width, 0, 0, image_width, image_height);
        iv_show.setImageBitmap(temp);
    }
}
