package com.robot.webparser;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;

/**
 * Created by terry on 15/11/2.
 */
public class DataAnalyzer {

    String inputHTML;

    public DataAnalyzer() {

    }

    public void set_resource(String a_inputHTML) {
        inputHTML = a_inputHTML;
    }

    public ArrayList<String> getContentDatas() {
        ArrayList<String> content_datas = new ArrayList<String>();
        try {
            Parser parser = Parser.createParser(inputHTML, "UTF-8");
            NodeList contentList = parser.parse(null);
            for (int i = 0; i < contentList.size(); i ++) {
                Node node = contentList.elementAt(i);
                String content_data = node.toPlainTextString();
                if (content_data != null && content_data.length() > 0) {
                    content_datas.add(content_data);
                }
            }
        } catch (ParserException e) {
//            System.err.printf("getContentDatas err : %s\n", e);
//            e.printStackTrace();
            return content_datas;
        }
        return content_datas;
    }

    public ArrayList<String> getLinks() {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Parser parser = Parser.createParser(inputHTML, "UTF-8");
            NodeClassFilter linkFilter = new NodeClassFilter(LinkTag.class);
            NodeList linkList = parser.parse(linkFilter);
            for (int i = 0; i < linkList.size(); i ++) {
                LinkTag node = (LinkTag)linkList.elementAt(i);
                String url = node.getLink();
                String contain = " '\"";
                if (url.startsWith("http") && !url.contains(contain) && checkUrl(url)) {
                    urls.add(url);
                }
            }
        } catch (ParserException e) {
//            System.err.printf("getLinks err : %s\n", e);
//            e.printStackTrace();
            return urls;
        }
        return urls;
    }

    public static boolean checkUrl(String url) {
        return url.matches("^((https|http|ftp|rtsp|mms)?://)"
                + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
                + "|"
                + "([0-9a-z_!~*'()-]+\\.)*"
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."
                + "[a-z]{2,6})"
                + "(:[0-9]{1,4})?"
                + "((/?)|"
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
    }
}
