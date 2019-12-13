package com.afterchristmas.testcolormatrix;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.recognizer.utils.image.IntegralImageSlicer;
import com.android.recognizer.utils.image.SauvolaImageProcessor;

import net.sourceforge.javaocr.ocr.PixelImage;

import java.io.ByteArrayOutputStream;

public class Main3Activity extends AppCompatActivity {
    private PixelImage processImage;
    private Bitmap backBuffer;
    private SauvolaImageProcessor imageProcessor;
    private IntegralImageSlicer slicer;
    private ImageView iv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        // and now create byte image
        // image to hold copy to be processed  - allow for borders
        Bitmap originImg = BitmapFactory.decodeResource(getResources(), R.drawable.gray_test);
        int bitmapW = originImg.getWidth();
        int bitmapH = originImg.getHeight();
        int[] pixelsImagePix = new int[bitmapW*bitmapH];
        originImg.getPixels(pixelsImagePix, 0, bitmapW, 0, 0, bitmapW, bitmapH);


        imageProcessor = new SauvolaImageProcessor(bitmapW, bitmapH, bitmapW, bitmapH, 0, 1);
        PixelImage pixelImage = imageProcessor.prepareImage(pixelsImagePix, 1, 1);
        // slicer receivers template image which will hold integral image copy

        final Bitmap temp = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.RGB_565);
        temp.setPixels(pixelImage.pixels, 0, bitmapW, 0, 0, bitmapW, bitmapH);
        iv_show = findViewById(R.id.iv_show);
        iv_show.setImageBitmap(temp);
    }
    /**
     * 把Bitmap转Byte
     * @Author HEH
     * @EditTime 2010-07-19 上午11:45:56
     */
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
