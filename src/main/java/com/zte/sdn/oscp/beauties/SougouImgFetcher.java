package com.zte.sdn.oscp.beauties;

import com.alibaba.fastjson.JSONObject;
import com.zte.sdn.oscp.beauties.utils.HttpClientUtils;
import com.zte.sdn.oscp.beauties.utils.ImgDownloadUtil;

import java.util.ArrayList;
import java.util.List;

public class SougouImgFetcher {

    private String url;
    private String word;

    private String imageStoreDir;

    private List<JSONObject> dataList;
    private List<String> urlList;

    public SougouImgFetcher(String url, String word, String imageStoreDir) {
        this.url = url;
        this.word = word;
        this.dataList = new ArrayList<>();
        this.urlList = new ArrayList<>();
        this.imageStoreDir = imageStoreDir;
    }

    public SougouImgFetcher(String url, String word) {
        this(url, word, "E:/pipeline/sougou");
    }

    public void loadPicUrls(int idx, int size) {
        String url = String.format(this.url, idx, size, this.word);
        System.out.println(url);
        String res = HttpClientUtils.get(url);
        JSONObject object = JSONObject.parseObject(res);
        JSONObject dataJsonObj = (JSONObject) object.get("data");
        if (dataJsonObj == null || !dataJsonObj.containsKey("items")) {
            return;
        }
        List<JSONObject> items = (List<JSONObject>) dataJsonObj.get("items");
        for (JSONObject item : items) {
            this.urlList.add(item.getString("picUrl"));
        }
        this.dataList.addAll(items);
    }

    // 下载
    public void downloadImages() {
        try {
            ImgDownloadUtil.doDownload(this.urlList, this.word, this.imageStoreDir);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}