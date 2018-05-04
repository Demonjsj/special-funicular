package com.zd.sdtest;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MActivity";
    public static String EXTERNAL_FILE_PATH = "";
    public static String USB_STORAGE_PATH = "";

    public static final String ROOT_EXTERNAL_FILE_PATH = "/mnt/extsd";
    public static final String ROOT_USB_STORAGE_PATH = "/mnt/usbhost";
    public static final String INTERNAL_FILE_PATH = "/mnt/sdcard";

    private TextView tv_sd, tv_usb;
    private Button bt_sd, bt_usb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_sd = (TextView) findViewById(R.id.tv_sd);
        tv_usb = (TextView) findViewById(R.id.tv_usb);
        bt_sd = (Button) findViewById(R.id.bt_sd);
        bt_usb = (Button) findViewById(R.id.bt_usb);

        File a = Environment.getExternalStorageDirectory();
        Log.i("ssss","a = " + a.toString());
        String aaa = MainActivity.this.getExternalFilesDir(null).toString();
        Log.i("ssss","aaa = " + aaa);
        String eeeee = MainActivity.this.getExternalFilesDirs(null).toString();
        Log.i("ssss","aaa = " + eeeee);


        bt_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i(TAG, "--->OnClick");
//                boolean isExistSDCard = getSize(ROOT_EXTERNAL_FILE_PATH, 0) > 0;
//                if (isExistSDCard) {
//                    Log.i(TAG, "--->SDCard PATH = " + MainActivity.EXTERNAL_FILE_PATH);
//                    tv_sd.setText("SD PATH = " + MainActivity.EXTERNAL_FILE_PATH);
//                } else {
//                    Log.i(TAG, "--->NAND PATH = " + MainActivity.INTERNAL_FILE_PATH);
//                    tv_sd.setText("NAND PATH = " + MainActivity.INTERNAL_FILE_PATH);
//                }
            }
        });


        bt_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean isExistUSB = getSize(ROOT_USB_STORAGE_PATH, 0) > 0;
//                if (isExistUSB) {
//                    tv_usb.setText(MainActivity.USB_STORAGE_PATH);
//                }
            }
        });
    }

    public static long getSize(String path, int n) {
        Log.i(TAG, "--->path = " + path);
        Log.i(TAG, "--->n = " + n);
        if (n > 2) {
            Log.i(TAG, "---> n > 2 return 0");
            return 0;
        }
        List<String> list = getMountPathList();
        Log.e(TAG, "getSize: " + path);
        File[] files = new File(path).listFiles();
        Log.i(TAG, "--->files = " + files);
        if (path.equals(ROOT_EXTERNAL_FILE_PATH)) {
            for (File item : files) {
                if (getSize(item.getAbsolutePath(), n + 1) > 0) {
                    MainActivity.EXTERNAL_FILE_PATH = item.getAbsolutePath();
                    return 1;
                }
            }
        } else if (path.equals(ROOT_USB_STORAGE_PATH)) {
            for (File item : files) {
                Log.i(TAG, "--->item_usb = " + item.getAbsolutePath());
                if (getSize(item.getAbsolutePath(), n + 1) > 0) {
                    Log.i(TAG, "--->USB_STORAGE_PATH = " + MainActivity.USB_STORAGE_PATH);
                    MainActivity.USB_STORAGE_PATH = item.getAbsolutePath();
                    Log.i(TAG, "--->USB_STORAGE_PATH = " + MainActivity.USB_STORAGE_PATH);
                    return 1;
                }
            }
        }
        File file = new File(path);
        Log.i(TAG, "--->file = " + file);
        Log.i(TAG, "--->file.exists = " + file.exists());
        if (!list.contains(path))
            return 0;

        if (!file.exists()) {
            return 0;
        }

        long total = 0;
        Log.i(TAG, "--->total = " + total);
        try {
            StatFs statfs = new StatFs(path);
            Log.i(TAG, "--->statfs = " + statfs);
            long blocSize = statfs.getBlockSize();
            long totalBlocks = statfs.getBlockCount();
            total = totalBlocks * blocSize;
            Log.i(TAG, "--->total = " + total + "---blocSize = " + blocSize + "---totalBlocks = " + totalBlocks);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getSize: " + "string path err!");
        }
        if (total > 500000000 && total < 520000000) {
            return 0;
        }
        return total;
    }

    //获取已挂载路径
    public static List<String> getMountPathList() {
        List<String> pathList = new ArrayList<String>();
        final String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();//取得当前JVM的运行时环境
        try {
            Process p = run.exec(cmd);//执行命令
            BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (-1 != line.indexOf(" /mnt/") && -1 != line.indexOf(" vfat ")) {// 前后均有空格// 前面要有空格，以防断章取义
                    //输出信息内容： /data/media /storage/emulated/0 sdcardfs rw,nosuid,nodev,relatime,uid=1023,gid=1023 0 0
                    String[] temp = TextUtils.split(line, " ");
                    //分析内容可看出第二个空格后面是路径
                    String result = temp[1];
                    File file = new File(result);
                    //类型为目录、可读、可写，就算是一条挂载路径
                    if (file.isDirectory() && file.canRead() && file.canWrite()) {
                        pathList.add(result);
                    }
                    // 检查命令是否执行失败
                    if (p.waitFor() != 0 && p.exitValue() == 1) {
                        // p.exitValue()==0表示正常结束，1：非正常结束
                    }
                }
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            //命令执行异常，就添加默认的路径
            pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            e.printStackTrace();
        }
        return pathList;
    }
}
