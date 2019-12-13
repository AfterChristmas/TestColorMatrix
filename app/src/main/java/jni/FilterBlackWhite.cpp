//
// Created by wy on 2019/12/11.
//
#include <stdlib.h>
#include <math.h>
#include <__clang_cuda_device_functions.h>

void sauvola(unsigned char * grayImage,unsigned char * biImage,int w,int h,int k,int windowSize)
{
    int whalf = windowSize >> 1;

    int i,j;
    int IMAGE_WIDTH = w;
    int IMAGE_HEIGHT = h;
    // create the integral image
    unsigned long * integralImg = (unsigned long*)malloc(IMAGE_WIDTH*IMAGE_HEIGHT*sizeof(unsigned long*));
    unsigned long * integralImgSqrt = (unsigned long*)malloc(IMAGE_WIDTH*IMAGE_HEIGHT*sizeof(unsigned long*));
    int sum = 0;
    int sqrtsum = 0;
    int index;
    for (i=0; i<IMAGE_HEIGHT; i++)
    {
        // reset this column sum
        sum = 0;
        sqrtsum = 0;

        for (j=0; j<IMAGE_WIDTH; j++)
        {
            index = i*IMAGE_WIDTH+j;

            sum += grayImage[index];
            sqrtsum += grayImage[index] * grayImage[index];

            if (i==0)
            {
                integralImg[index] = sum;
                integralImgSqrt[index] = sqrtsum;
            }
            else
            {
                integralImgSqrt[index] = integralImgSqrt[(i-1)*IMAGE_WIDTH+j] + sqrtsum;
                integralImg[index] = integralImg[(i-1)*IMAGE_WIDTH+j] + sum;
            }
        }
    }

    //Calculate the mean and standard deviation using the integral image
    int xmin,ymin,xmax,ymax;
    double mean,std,threshold;
    double diagsum,idiagsum,diff,sqdiagsum,sqidiagsum,sqdiff,area;

    for (i=0; i<IMAGE_WIDTH; i++){
        for (j=0; j<IMAGE_HEIGHT; j++){
            xmin = max(0,i - whalf);
            ymin = max(0,j - whalf);
            xmax = min(IMAGE_WIDTH-1,i+whalf);
            ymax = min(IMAGE_HEIGHT-1,j+whalf);

            area = (xmax - xmin + 1) * (ymax - ymin + 1);
            if(area <= 0)
            {
                biImage[i * IMAGE_WIDTH + j] = 255;
                continue;
            }

            if(xmin == 0 && ymin == 0){
                diff = integralImg[ymax * IMAGE_WIDTH + xmax];
                sqdiff = integralImgSqrt[ymax * IMAGE_WIDTH + xmax];
            }else if(xmin > 0 && ymin == 0){
                diff = integralImg[ymax * IMAGE_WIDTH + xmax] - integralImg[ymax * IMAGE_WIDTH + xmin - 1];
                sqdiff = integralImgSqrt[ymax * IMAGE_WIDTH + xmax] - integralImgSqrt[ymax * IMAGE_WIDTH + xmin - 1];
            }else if(xmin == 0 && ymin > 0){
                diff = integralImg[ymax * IMAGE_WIDTH + xmax] - integralImg[(ymin - 1) * IMAGE_WIDTH + xmax];
                sqdiff = integralImgSqrt[ymax * IMAGE_WIDTH + xmax] - integralImgSqrt[(ymin - 1) * IMAGE_WIDTH + xmax];;
            }else{
                diagsum = integralImg[ymax * IMAGE_WIDTH + xmax] + integralImg[(ymin - 1) * IMAGE_WIDTH + xmin - 1];
                idiagsum = integralImg[(ymin - 1) * IMAGE_WIDTH + xmax] + integralImg[ymax * IMAGE_WIDTH + xmin - 1];
                diff = diagsum - idiagsum;

                sqdiagsum = integralImgSqrt[ymax * IMAGE_WIDTH + xmax] + integralImgSqrt[(ymin - 1) * IMAGE_WIDTH + xmin - 1];
                sqidiagsum = integralImgSqrt[(ymin - 1) * IMAGE_WIDTH + xmax] + integralImgSqrt[ymax * IMAGE_WIDTH + xmin - 1];
                sqdiff = sqdiagsum - sqidiagsum;
            }

            mean = diff/area;
            std  = sqrt((sqdiff - diff*diff/area)/(area-1));
            threshold = mean*(1+k*((std/128)-1));
            if(grayImage[j*IMAGE_WIDTH + i] < threshold)
                biImage[j*IMAGE_WIDTH + i] = 0;
            else
                biImage[j*IMAGE_WIDTH + i] = 255;
        }
    }

    free(integralImg);
    free(integralImgSqrt);
}