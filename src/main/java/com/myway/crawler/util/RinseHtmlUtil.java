package com.myway.crawler.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myway.crawler.config.RinseRule;
import com.myway.usf.common.oss.OssManager;
import com.myway.util.common.StringUtil;
import com.myway.util.common.TimeUtil;

/**
 * 
 * @author zhangy
 *
 */
public class RinseHtmlUtil {
    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Document rinseHtml(Document document, List<RinseRule> rinseRules) {
        for (RinseRule rinseRule : rinseRules) {
            document = rinseDocument(document, rinseRule);
        }
        return document;
    }

    public static String rinseHtml(String html, List<RinseRule> rinseRules) {
        Document document = Jsoup.parse(html);
        for (RinseRule rinseRule : rinseRules) {
            document = rinseDocument(document, rinseRule);
        }
        return document.body().children().outerHtml();
    }

    /**
     * 
     * 替换微信公众号图片地址
     * 
     * @param html
     * @return
     */
    public static String imgHandleOss(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("img");
        for (Element element : elements) {
            String imgsrc = element.attr("data-src");
            try {
                String osssrc = uploadImgToOss(imgsrc);
                if (osssrc != null) {
                    element.attr("src", osssrc);
                }
            } catch (Exception e) {
                log.error("error while find img:{}", imgsrc);
                log.error("exception:", e);
            }
        }
        Elements backImgs = document.select("[style*=mmbiz.qpic.cn]");
        for (Element element : backImgs) {
            String fullstyle = element.attr("style");
            try {
                // 分割多个style
                String[] styles = fullstyle.split(";");
                for (String style : styles) {
                    // 检查style
                    if (!StringUtil.isEmpty(style) && style.contains("mmbiz.qpic.cn")) {
                        String imgsrc =
                                style.substring(style.indexOf("http"), style.indexOf("\")"));
                        String osssrc = uploadImgToOss(imgsrc);
                        if (osssrc != null) {
                            String newStyle = fullstyle.replace(imgsrc, osssrc);
                            log.info("find newStyle:{}", newStyle);
                            element.attr("style", newStyle);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("error while find backgroud style:{}", fullstyle);
                log.error("exception:", e);
            }
        }
        Elements videos = document.select(".video_iframe");
        for (Element video : videos) {
            String src = video.attr("data-src");
            if (!StringUtil.isEmpty(src)) {
                log.info(" check video : {}", src);
                video.attr("width", "100%");
                if (src.contains("height=")) {
                    String height = src.split("height=")[1].split("&")[0];
                    video.attr("height", height);
                } else {
                    video.attr("height", "auto");
                }
                if (src.contains("vid=")) {
                    String vid = src.split("vid=")[1].split("&")[0];
                    video.attr("src",
                            "//v.qq.com/txp/iframe/player.html?vid=##&autoplay=false&full=false&show1080p=true&isDebugIframe=false"
                                    .replace("##", vid));
                } else {
                    video.attr("src", src);
                }
            }
        }
        return document.body().children().outerHtml();
    }

    /**
     * 
     * 上传图片到Oss
     * 
     * @param imgUrl
     * @return
     */
    public static String uploadImgToOss(String imgUrl) {
        if (StringUtil.isEmpty(imgUrl)) {
            return null;
        }
        // 默认jpg
        String suffix = ".jpg";
        if (imgUrl.contains("wx_fmt=")) {
            String format = imgUrl.split("wx_fmt=")[1];
            if (!StringUtil.isEmpty(format)) {
                switch (format.toLowerCase()) {
                    case "png":
                        suffix = ".png";
                        break;
                    case "gif":
                        suffix = ".gif";
                        break;
                    default:
                        break;
                }
            }
        } else {
            log.warn("unsupported url pattern:{}", imgUrl);
            log.info("use default format jpg to upload===");
        }
        StringBuilder path = new StringBuilder();
        path.append("wechat/");
        path.append(TimeUtil.getFormatDate(new Date(), "yyyy/MM/dd"));
        path.append("/");
        String ossImg = path.toString() + UUID.randomUUID() + suffix;
        try {
            OssManager.uploadStream(getWechatImage(imgUrl), ossImg);
            return "http:" + OssManager.getOssConfig().getDomainUrl() + ossImg;
        } catch (Exception e) {
            log.error("upload to oss error", e);
            return null;
        }
    }

    /**
     * 
     * 下载图片
     * 
     * @param imgUrl
     * @return
     * @throws Exception
     */
    public static byte[] getWechatImage(String imgUrl) throws Exception {
        URL obj = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setReadTimeout(5000);
        conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        conn.addRequestProperty("Accept", "image/webp,image/*,*/*;q=0.8");
        conn.addRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        conn.addRequestProperty("Referer",
                "http://mp.weixin.qq.com/s?timestamp=1487925204&src=3&ver=1&signature=qoDKjm5Udr8iZ*a591eYZywUYURtYJeoerXNa*DZZtQVrZUReulnX3Z2pnciMZY-QiFrI5p67snT9RdazeFOkLF85Iv9mTvKrFDsJkMhc4x4ERG3fuF8rTMzELD69vGVtEp1P2F-gmHKlDv6Nsr9U8Ay9vDTS4f5fUzEIx7l8Ps=");
        conn.addRequestProperty("Host", "mp.weixin.qq.com");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = conn.getInputStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            log.error("Failed while reading bytes", e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return baos.toByteArray();
    }

    public static Document rinseDocument(Document document, RinseRule rinseRule) {
        switch (rinseRule.getAction()) {
            case "replace":
                return replace(document, rinseRule);
            case "fullreplace":
                return fullReplace(document, rinseRule);
            case "add":
                return add(document, rinseRule);
            case "delete":
                return delete(document, rinseRule);
            case "copy":
                return copy(document, rinseRule);
            default:
                return document;
        }
    }

    public static Document replace(Document document, RinseRule rinseRule) {
        Elements elements = document.select(rinseRule.getCssQuery());
        switch (rinseRule.getType()) {
            case "attr":
                for (Element element : elements) {
                    String attrVlue = element.attr(rinseRule.getName());
                    attrVlue = attrVlue.replaceAll(rinseRule.getSource(), rinseRule.getTarget());
                    element.attr(rinseRule.getName(), attrVlue);
                }
                break;
            case "text":
                for (Element element : elements) {
                    String text = element.text();
                    text = text.replaceAll(rinseRule.getSource(), rinseRule.getTarget());
                    element.text(text);
                }
                break;
            case "node":
                for (Element element : elements) {
                    String nodestr = element.outerHtml().replaceAll(rinseRule.getSource(),
                            rinseRule.getTarget());
                    Document node = Jsoup.parse(nodestr);
                    element.replaceWith(node.body().children().first());
                }
                break;
            case "style":
                for (Element element : elements) {
                    replaceStyle(element, rinseRule);
                }
                break;
            default:
                break;
        }
        return document;
    }

    public static Document fullReplace(Document document, RinseRule rinseRule) {
        String html = document.body().children().outerHtml();
        html = html.replaceAll(rinseRule.getSource(), rinseRule.getTarget());
        return Jsoup.parse(html);
    }

    public static Document add(Document document, RinseRule rinseRule) {
        Elements elements = document.select(rinseRule.getCssQuery());
        switch (rinseRule.getType()) {
            case "attr":
                elements.attr(rinseRule.getName(), rinseRule.getValue());
                break;
            case "text":
                for (Element element : elements) {
                    element.appendText(element.text() + rinseRule.getValue());
                }
                break;
            case "node":
                Node node = Jsoup.parse(rinseRule.getValue());
                for (Element element : elements) {
                    element.appendChild(node);
                }
                break;
            case "style":
                for (Element element : elements) {
                    addStyle(element, rinseRule);
                }
                break;
            default:
                break;
        }
        return document;
    }

    public static Document delete(Document document, RinseRule rinseRule) {
        log.info("delete:{}", rinseRule.getCssQuery());
        Elements elements = document.select(rinseRule.getCssQuery());
        log.info("find element:{}", elements);
        switch (rinseRule.getType()) {
            case "attr":
                elements.removeAttr(rinseRule.getName());
                break;
            case "text":
                for (Element element : elements) {
                    element.text("");
                }
                break;
            case "node":
                elements.remove();
                break;
            case "style":
                for (Element element : elements) {
                    deleteStyle(element, rinseRule);
                }
                break;
            default:
                break;
        }
        return document;
    }

    public static void deleteStyle(Element element, RinseRule rinseRule) {
        String attrVlue = element.attr("style");
        String[] keys = rinseRule.getName().split(":");
        String[] csss = attrVlue.split(";");
        Map<String, String> cssMap = new HashMap<>();
        for (String css : csss) {
            if (StringUtils.isNotEmpty(css)) {
                String[] cssValue = css.split(":");
                if (cssValue.length >= 2) {
                    cssMap.put(cssValue[0].trim(), cssValue[1]);
                }
            }
        }
        for (String key : keys) {
            if (StringUtils.isNotEmpty(key) && cssMap.containsKey(key)) {
                cssMap.remove(key);
            }
        }
        StringBuffer sb = new StringBuffer();
        Set<String> keysets = cssMap.keySet();
        for (String key : keysets) {
            String stylevalue = cssMap.get(key);
            sb.append(key).append(":").append(stylevalue).append(";");
        }
        if (sb.length() > 0) {
            element.attr("style", sb.toString());
        } else {
            element.removeAttr("style");
        }
    }

    public static void addStyle(Element element, RinseRule rinseRule) {
        String attrVlue = element.attr("style");
        String[] csss = attrVlue.split(";");
        Map<String, String> cssMap = new HashMap<>();
        for (String css : csss) {
            if (StringUtils.isNotEmpty(css)) {
                String[] cssValue = css.split(":");
                if (cssValue.length >= 2) {
                    cssMap.put(cssValue[0].trim(), cssValue[1]);
                }
            }
        }
        cssMap.put(rinseRule.getName(), rinseRule.getValue());
        StringBuffer sb = new StringBuffer();
        Set<String> keysets = cssMap.keySet();
        for (String key : keysets) {
            String stylevalue = cssMap.get(key);
            sb.append(key).append(":").append(stylevalue).append(";");
        }
        if (sb.length() > 0) {
            element.attr("style", sb.toString());
        } else {
            element.removeAttr("style");
        }
    }

    public static void replaceStyle(Element element, RinseRule rinseRule) {
        String attrVlue = element.attr("style");
        String[] csss = attrVlue.split(";");
        Map<String, String> cssMap = new HashMap<>();
        for (String css : csss) {
            if (StringUtils.isNotEmpty(css)) {
                String[] cssValue = css.split(":");
                if (cssValue.length >= 2) {
                    if (cssValue[0].trim().equals(rinseRule.getName())) {
                        cssMap.put(cssValue[0].trim(), cssValue[1].replaceAll(rinseRule.getSource(),
                                rinseRule.getTarget()));
                    } else {
                        cssMap.put(cssValue[0].trim(), cssValue[1]);
                    }
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        Set<String> keysets = cssMap.keySet();
        for (String key : keysets) {
            String stylevalue = cssMap.get(key);
            sb.append(key).append(": ").append(stylevalue).append("; ");
        }
        if (sb.length() > 0) {
            element.attr("style", sb.toString());
        } else {
            element.removeAttr("style");
        }
    }

    public static Document copy(Document document, RinseRule rinseRule) {
        Elements elements = document.select(rinseRule.getCssQuery());
        switch (rinseRule.getType()) {
            case "attr":
                for (Element element : elements) {
                    String attrVlue = element.attr(rinseRule.getSource());
                    element.attr(rinseRule.getTarget(), attrVlue);
                }
                break;
            default:
                break;
        }
        return document;
    }

}
