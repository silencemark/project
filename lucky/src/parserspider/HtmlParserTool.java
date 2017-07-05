package parserspider;

import java.util.*;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParserTool {
	 public static List<String>imageURLS = new ArrayList<String>();
	    // ��ȡһ����վ�ϵ�����,filter ������������
	    public static Set<String> extracLinks(String url, LinkFilter filter) {
	        Set<String> links = new HashSet<String>();
	        try {
	            Parser parser = new Parser();
	            // ���ô���û�д���ʱע��
//	            System.getProperties().put("proxySet", "true");
//	            System.getProperties().put("proxyHost", "172.16.91.109");
//	            System.getProperties().put("proxyPort", "808");
//	            Parser.getConnectionManager().setProxyHost("123");

	            parser.setURL(url);
	            parser.setEncoding("utf-8");
	            // ���ù���ͼƬ
	            NodeFilter imgfil = new TagNameFilter("IMG");

	            // ���� <frame >��ǩ�� filter��������ȡ frame ��ǩ��� src ��������ʾ������
	            NodeFilter frameFilter = new NodeFilter() {
	                private static final long serialVersionUID = -6464506837817768182L;

	                public boolean accept(Node node) {
	                    if (node.getText().startsWith("frame src=")) {
	                        return true;
	                    } else {
	                        return false;
	                    }
	                }
	            };
	            // OrFilter �����ù��� <a> ��ǩ���� <frame> ��ǩ
	            OrFilter lf = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
	            
	            // �õ����о������˵ı�ǩ
	            //NodeList list = parser.extractAllNodesThatMatch(lf);
	            NodeList list = parser.extractAllNodesThatMatch(imgfil);
	            for (int i = 0; i < list.size(); i++) {
	                Node tag = list.elementAt(i);
	                if (tag instanceof LinkTag)// <a> ��ǩ
	                {
	                    if (tag instanceof ImageTag) {
	                        // ����ͼƬ��Ϣ
	                        ImageTag link = (ImageTag) tag;
	                        String imageUrl = link.getImageURL();// url
	                        links.add(imageUrl);
	                        imageURLS.add(imageUrl);
	                        System.out.println(imageUrl);
	                    }else{
	                        LinkTag link = (LinkTag) tag;
	                        String linkUrl = link.getLink();// url
	                        if (filter.accept(linkUrl))
	                        links.add(linkUrl);
	                    }
	                } else// <frame> ��ǩ
	                {
	                    if (tag instanceof ImageTag) {
	                        // ����ͼƬ��Ϣ
	                        ImageTag link = (ImageTag) tag;
	                        String imageUrl = link.getImageURL();// url
	                        links.add(imageUrl);
	                        imageURLS.add(imageUrl);
	                        System.out.println(imageUrl);
	                    } else {
	                        // ��ȡ frame �� src ���Ե������� <frame src="test.html"/>
	                        String frame = tag.getText();
	                        int start = frame.indexOf("src=");
	                        frame = frame.substring(start);
	                        int end = frame.indexOf(" ");
	                        if (end == -1)
	                            end = frame.indexOf(">");
	                        String frameUrl = frame.substring(5, end - 1);
	                        if (filter.accept(frameUrl))
	                            links.add(frameUrl);
	                    }
	                }
	            }
	        } catch (ParserException e) {
	            e.printStackTrace();
	        }
	        return links;
	    }
}
