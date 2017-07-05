package parserspider;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class DownLoadFileV4 {

    /* ���� url ָ�����ҳ */
    public String downloadFile(String url) throws Exception {
        String filePath = null;
        // ��ʼ�����˴����캯������3.1�в�ͬ
        HttpClient httpclient = new DefaultHttpClient();
        //���ô���ͳ�ʱ��û��ʹ�ô���ʱע��
/*        HttpHost proxy = new HttpHost("172.16.91.109", 808);   
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);   
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);*/


        HttpHost targetHost = new HttpHost(url.replace("http://", ""));
        HttpGet httpget = new HttpGet("/");
        // �鿴Ĭ��requestͷ����Ϣ
        System.out.println("Accept-Charset:" + httpget.getFirstHeader("Accept-Charset"));
        // ��������������ӻᷢ������������Accept-CharsetΪgbk����utf-8��������Ĭ�Ϸ���gb2312���������google.cn��˵��
        httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
        // �ö��ŷָ���ʾ����ͬʱ���ܶ��ֱ���
        httpget.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        httpget.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        // ��֤ͷ����Ϣ������Ч
        System.out.println("Accept-Charset:" + httpget.getFirstHeader("Accept-Charset").getValue());

        // Execute HTTP request
        System.out.println("executing request " + httpget.getURI());

        HttpResponse response = null;
        try {
            response = httpclient.execute(targetHost, httpget);
        
        // HttpResponse response = httpclient.execute(httpget);

        System.out.println("----------------------------------------");
        System.out.println("Location: " + response.getLastHeader("Location"));
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getLastHeader("Content-Type"));
        System.out.println(response.getLastHeader("Content-Length"));
        System.out.println("----------------------------------------");

        // �ж�ҳ�淵��״̬�ж��Ƿ����ת��ץȡ������
        int statusCode = response.getStatusLine().getStatusCode();
        if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
                || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
            // �˴��ض����� �˴���δ��֤
            String newUri = response.getLastHeader("Location").getValue();
            httpclient = new DefaultHttpClient();
            httpget = new HttpGet(newUri);
            response = httpclient.execute(httpget);
        }

        // Get hold of the response entity
        HttpEntity entity = response.getEntity();

        // �鿴���з���ͷ����Ϣ
        Header headers[] = response.getAllHeaders();
        int ii = 0;
        while (ii < headers.length) {
            System.out.println(headers[ii].getName() + ": " + headers[ii].getValue());
            ++ii;
        }

        // If the response does not enclose an entity, there is no need
        // to bother about connection release
        if (entity != null) {
            // ��Դ����������һ��byte���鵱�У���Ϊ������Ҫ�����õ�������
            byte[] bytes = EntityUtils.toByteArray(entity);
            if(response.getLastHeader("Content-Type") != null){
                filePath = "D:\\spider\\" + getFileNameByUrl(url, response.getLastHeader("Content-Type").getValue());
            }else{
                filePath = "D:\\spider\\" + url.substring(url.lastIndexOf("/"), url.length());
            }
            saveToLocal(bytes, filePath);
            
            String charSet = "";

            // ���ͷ��Content-Type�а����˱�����Ϣ����ô���ǿ���ֱ���ڴ˴���ȡ
            charSet = EntityUtils.getContentCharSet(entity);

            System.out.println("In header: " + charSet);
            // ���ͷ����û�У���ô������Ҫ �鿴ҳ��Դ�룬���������Ȼ����˵��ȫ��ȷ����Ϊ��Щ�ֲڵ���ҳ������û����ҳ����дͷ��������Ϣ
            if (charSet == "") {
                String regEx = "(?=<meta).*?(?<=charset=[\\'|\\\"]?)([[a-z]|[A-Z]|[0-9]|-]*)";
                Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(new String(bytes)); // Ĭ�ϱ���ת���ַ�������Ϊ���ǵ�ƥ���������ģ����Դ��п��ܵ����������û��Ӱ��
                boolean result = m.find();
                if (m.groupCount() == 1) {
                    charSet = m.group(1);
                } else {
                    charSet = "";
                }
            }
            System.out.println("Last get: " + charSet);
            // ���ˣ����ǿ��Խ�ԭbyte���鰴����������ר���ַ������������ҵ��˱���Ļ���
            //System.out.println("Encoding string is: " + new String(bytes, charSet));
        }} catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            httpclient.getConnectionManager().shutdown();
            httpget.abort();
        }
        
        return filePath;
    }
    
    /**
     * ���� url ����ҳ����������Ҫ�������ҳ���ļ��� ȥ���� url �з��ļ����ַ�
     */
    public String getFileNameByUrl(String url, String contentType) {
        // remove http://
        url = url.substring(7);
        // text/html����
        if (contentType.indexOf("html") != -1&& url.indexOf(".jpg")!=-1&& url.indexOf(".gif")!=-1) {
            url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
            return url;
        }
        
        else if(url.indexOf(".jpg")!=-1|| url.indexOf(".gif")!=-1){
            url =url;
            return url;
        }// ��application/pdf����
        else {
            return url.replaceAll("[\\?/:*|<>\"]", "_") + "." + contentType.substring(contentType.lastIndexOf("/") + 1);
        }
    }

    /**
     * ������ҳ�ֽ����鵽�����ļ� filePath ΪҪ������ļ�����Ե�ַ
     */
    private void saveToLocal(byte[] data, String filePath) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++)
                out.write(data[i]);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}