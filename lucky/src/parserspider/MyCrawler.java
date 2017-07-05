package parserspider;

import java.util.Set;

public class MyCrawler {
	 /**
     * ʹ�����ӳ�ʼ�� URL ����
     * 
     * @return
     * @param seeds
     *            ����URL
     */
    private void initCrawlerWithSeeds(String[] seeds) {
        for (int i = 0; i < seeds.length; i++)
            LinkQueue.addUnvisitedUrl(seeds[i]);
    }

    /**
     * ץȡ����
     * 
     * @return
     * @param seeds
     * @throws Exception 
     */
    public void crawling(String[] seeds) throws Exception { 
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.contains("csdn"))
                    return true;
                else
                    return false;
            }
        };
        // ��ʼ�� URL ����
        initCrawlerWithSeeds(seeds);
        // ѭ����������ץȡ�����Ӳ�����ץȡ����ҳ������1000
        while (!LinkQueue.unVisitedUrlsEmpty() && LinkQueue.getVisitedUrlNum() <= 1000) {
            // ��ͷURL������
            String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
            if (visitUrl == null)
                continue;
            DownLoadFileV4 downLoader = new DownLoadFileV4();
            // ������ҳ
            try {
                //downLoader.downloadFile(visitUrl);
                // ֻ����ͼƬ����������ҳ
                if(HtmlParserTool.imageURLS.contains(visitUrl)){
                    downLoader.downloadFile(visitUrl);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println();            
            // �� url ���뵽�ѷ��ʵ� URL ��
            LinkQueue.addVisitedUrl(visitUrl);
            // ��ȡ��������ҳ�е� URL
            Set<String> links = HtmlParserTool.extracLinks(visitUrl, filter);
            // �µ�δ���ʵ� URL ���
            for (String link : links) {
                LinkQueue.addUnvisitedUrl(link);
            }
        }
    }

    // main �������
    public static void main(String[] args) throws Exception {
        MyCrawler crawler = new MyCrawler();
        crawler.crawling(new String[] {"http://www.csdn.com"});
    }
}
