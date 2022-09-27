package com.zte.sdn.oscp.beauties;

/**
 * @author sunquan
 */
public class SougouImgApp {

    public static void main(String[] args) {
        final String url = "https://pic.sogou.com/napi/pc/searchList?mode=1&start=%s&xml_len=%s&query=%s";
//        final String searchPicName = "刘亦菲";
//        final String searchPicName = "刘浩存";
//        final String searchPicName = "杨颖";
//        final String searchPicName = "刘涛";
        final String searchPicName = "迪丽热巴";
        SougouImgFetcher fetcher = new SougouImgFetcher(url, searchPicName);

        // 定义爬取开始索引、每次爬取数量、总共爬取数量
        int start = 0, size = 50, limit = 1000;

        for (int i = start; i < start + limit; i += size) {
            fetcher.loadPicUrls(i, size);
        }
        fetcher.downloadImages();
    }
}
