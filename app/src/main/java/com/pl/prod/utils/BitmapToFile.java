package com.pl.prod.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by brander on 2017/11/23.
 */

public class BitmapToFile {

    public final static File convert(Context ctx, Bitmap bitmap, String fileName) throws IOException {

        //複製檔案到快取資料夾
        File tempFile = new File(ctx.getCacheDir(), fileName);

        try {
            tempFile.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//			bitmap.compress(CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos;
            fos = new FileOutputStream(tempFile);
            fos.write(bitmapdata);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return tempFile;
    }

    //add by Simon 20150610 特別供到店管理使用
    public final static File convertToAll(Context ctx, Bitmap bitmap, String fileName)
            throws IOException {

        // 複製檔案到快取資料夾
        File tempFile = new File(ctx.getCacheDir(), fileName);

        try {
            tempFile.createNewFile();
            // Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            //update by brander 改变图片质量的压缩为60% 值为40 2017-2-6
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos;
            fos = new FileOutputStream(tempFile);
            fos.write(bitmapdata);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return tempFile;
    }


}
