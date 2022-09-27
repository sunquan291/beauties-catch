package com.zte.sdn.oscp.beauties.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author sunquan
 */
public class ImgDownloadUtil {
    private static final ExecutorService EXE = Executors.newFixedThreadPool(4);
    private static volatile AtomicInteger suc = new AtomicInteger();
    private static volatile AtomicInteger fails = new AtomicInteger();

    private static DecimalFormat decimalFormat = new DecimalFormat("0000");

    /**
     * @param storeDir 文件下载目录
     * @param url      文件下载地址
     * @param cate     搜索的图片名称与storeDir构成完整目录
     * @param name     图片新名称eg.00010.jpg
     * @throws Exception
     */
    private static void downloadImg(String storeDir, String url, String cate, String name) throws Exception {
        String path = storeDir + "/" + cate + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 获取扩展名
        String realExt = url.substring(url.lastIndexOf("."));
        String fileName = name + realExt;
        fileName = fileName.replace("-", "");
        String filePath = path + fileName;
        File img = new File(filePath);
        // 若文件之前已经下载过，则跳过
        if (img.exists()) {
            System.out.println(String.format("文件%s已存在本地目录", fileName));
            return;
        }

        URLConnection con = new URL(url).openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        InputStream inputStream = con.getInputStream();
        byte[] bs = new byte[1024];

        File file = new File(filePath);
        FileOutputStream os = new FileOutputStream(file, true);
        // 开始读取 写入
        int len;
        while ((len = inputStream.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        System.out.println("picUrl: " + url);
        System.out.println(String.format("正在下载第%s张图片", suc.getAndIncrement()));

        os.close();
        inputStream.close();
    }


    public static void doDownload(List<String> data, String word, String storeDir) throws InterruptedException {
        long start = System.currentTimeMillis();
        //1.数据预处理
        List<String> picUrls = data.stream().filter(Objects::nonNull).filter(picUrl ->
                        picUrl.endsWith(".jpg")
                                || picUrl.endsWith(".png")
                                || picUrl.endsWith(".jpeg")
                )
                .collect(Collectors.toList());

        //2.图片下载
        for (int i = 0; i < picUrls.size(); i++) {
            String picUrl = picUrls.get(i);
            //0001.jpg
            String picName = decimalFormat.format(i);
            EXE.execute(() -> {
                try {
                    downloadImg(storeDir, picUrl, word, picName);
                } catch (Exception e) {
                    fails.incrementAndGet();
                }
            });
        }
        EXE.shutdown();
        EXE.awaitTermination(60, TimeUnit.SECONDS);
        //2.详情打印
        System.out.println("=======Finished========");
        System.out.println("共有有效URL: " + picUrls.size());
        System.out.println("下载成功: " + suc);
        System.out.println("下载失败: " + fails);

        File dir = new File(storeDir + "/" + word + "/");
        int len = Objects.requireNonNull(dir.list()).length;
        System.out.println("当前共有文件： " + len);

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) / 1000.0 + "秒");

        //恢复计数器
        suc.set(0);
        fails.set(0);
    }


    private static String getPicName(int index) {
        String name = "";
        if (index < 10) {
            name = "000" + index;
        } else if (index < 100) {
            name = "00" + index;
        } else if (index < 1000) {
            name = "0" + index;
        } else {
            name = String.valueOf(index);
        }
        return name;
    }

}