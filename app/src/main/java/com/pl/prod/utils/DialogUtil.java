package com.pl.prod.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

/**
 * Created by brander on 2017/9/25.
 * 对话框工具
 */

public class DialogUtil {
    private static AlertDialog alertDialog;
    private static AlertDialog.Builder builder;

    public static void alertDialog(Context context, String title, String message) {

    }

    public static void updateApk(final String updateUrl, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择:")
                .setMessage("有新版本是否更新")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //下载
                        try {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(updateUrl);
                            intent.setData(content_url);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
