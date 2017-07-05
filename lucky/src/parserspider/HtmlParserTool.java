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
	    // 获取一个网站上的链接,filter 用来过滤链接
	    public static Set<String> extracLinks(String url, LinkFilter filter) {
	        Set<String> links = new HashSet<String>();
	        try {
	            Parser parser = new Parser();
	            // 设置代理，没有代理时注掉
//	            System.getProperties().put("proxySet", "true");
//	            System.getProperties().put("proxyHost", "172.16.91.109");
//	            System.getProperties().put("proxyPort", "808");
//	            Parser.getConnectionManager().setProxyHost("123");

	            parser.setURL(url);
	            parser.setEncoding("utf-8");
	            // 设置过滤图片
	            NodeFilter imgfil = new TagNameFilter("IMG");

	            // 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
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
	            // OrFilter 来设置过滤 <a> 标签，和 <frame> 标签
	            OrFilter lf = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
	            
	            // 得到所有经过过滤的标签
	            //NodeList list = parser.extractAllNodesThatMatch(lf);
	            NodeList list = parser.extractAllNodesThatMatch(imgfil);
	            for (int i = 0; i < list.size(); i++) {
	                Node tag = list.elementAt(i);
	                if (tag instanceof LinkTag)// <a> 标签
	                {
	                    if (tag instanceof ImageTag) {
	                        // 加入图片信息
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
	                } else// <frame> 标签
	                {
	                    if (tag instanceof ImageTag) {
	                        // 加入图片信息
	                        ImageTag link = (ImageTag) tag;
	                        String imageUrl = link.getImageURL();// url
	                        links.add(imageUrl);
	                        imageURLS.add(imageUrl);
	                        System.out.println(imageUrl);
	                    } else {
	                        // 提取 frame 里 src 属性的链接如 <frame src="test.html"/>
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
