package com.myway.crawler.downloader;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.myway.crawler.bean.WechatMsg;
import com.myway.util.common.JSONUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * 
 * 公众号下载
 * 
 * @author zhangy
 * @version 2018年9月18日
 */
@Service
@Scope("prototype")
public class WeChatDownloader extends AbstractCustomDownloader {

    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Map<String, CloseableHttpClient> httpClients = new HashMap<>();
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    public WeChatDownloader() {}

    private CloseableHttpClient getHttpClient(Site site, Proxy proxy) {
        if (site == null) {
            return this.httpClientGenerator.getClient((Site) null);
        } else {
            String domain = site.getDomain();
            CloseableHttpClient httpClient = (CloseableHttpClient) this.httpClients.get(domain);
            if (httpClient == null) {
                synchronized (this) {
                    httpClient = (CloseableHttpClient) this.httpClients.get(domain);
                    if (httpClient == null) {
                        httpClient = this.httpClientGenerator.getClient(site);
                        this.httpClients.put(domain, httpClient);
                    }
                }
            }

            return httpClient;
        }
    }


    /**
     * 执行下载
     */
    @Override
    public Page download(Request request, Task task) {
        // 获取站点
        Site site = task.getSite();
        WechatMsg wechatMsg = null;
        if (request.getUrl().indexOf("http://mp.weixin.qq.com/s?__biz=") == 0
                || request.getUrl().indexOf("http://mp.weixin.qq.com/s?") == 0) {
            // 内容页
            String url = request.getUrl();
            if (request.getUrl().indexOf("预处理") > 0) {
                String[] urls = request.getUrl().split("预处理");
                url = urls[0];
                request.setUrl(url);
                wechatMsg = JSONUtil.toObject(urls[1], WechatMsg.class);
                log.debug("=====预处理========{}", wechatMsg);
            }
        }
        String charset = site.getCharset();
        Map<String, String> headers = site.getHeaders();
        Set<Integer> acceptStatCode = site.getAcceptStatCode();
        log.info("downloading page {}", request.getUrl());
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;

        Page page;
        try {
            HttpUriRequest pageRequest = this.getHttpUriRequest(request, site, headers, null);
            httpResponse = this.getHttpClient(site, null).execute(pageRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra("statusCode", Integer.valueOf(statusCode));
            if (!this.statusAccept(acceptStatCode, statusCode)) {
                log.warn("get page {} error, status code {} ", request.getUrl(),
                        Integer.valueOf(statusCode));
                return null;
            }
            page = this.handleResponse(request, charset, httpResponse, task);
            this.onSuccess(request);
        } catch (IOException e) {
            log.error("download page {} error", request.getUrl(), e);
            if (site.getCycleRetryTimes() > 0) {
                return this.addToCycleRetry(request, site);
            }
            this.onError(request);
            return null;
        } finally {
            request.putExtra("statusCode", Integer.valueOf(statusCode));
            if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
                site.returnHttpProxyToPool((HttpHost) request.getExtra("proxy"),
                        ((Integer) request.getExtra("statusCode")).intValue());
            }
            try {
                if (httpResponse != null) {
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                log.error("close response fail", e);
            }
        }
        if (wechatMsg != null) {
            page.putField("wechatMsg", wechatMsg);
        }
        return page;
    }

    @Override
    public void setThread(int thread) {
        this.httpClientGenerator.setPoolSize(thread);
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(Integer.valueOf(statusCode));
    }

    protected HttpUriRequest getHttpUriRequest(Request request, Site site,
            Map<String, String> headers, HttpHost proxy) {
        RequestBuilder requestBuilder = this.selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            Iterator<Entry<String, String>> requestConfigBuilder = headers.entrySet().iterator();
            while (requestConfigBuilder.hasNext()) {
                Entry<String, String> headerEntry = requestConfigBuilder.next();
                requestBuilder.addHeader((String) headerEntry.getKey(),
                        (String) headerEntry.getValue());
            }
        }

        Builder requestConfigBuilder1 = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getTimeOut()).setCookieSpec("best-match");
        if (proxy != null) {
            requestConfigBuilder1.setProxy(proxy);
            request.putExtra("proxy", proxy);
        }

        requestBuilder.setConfig(requestConfigBuilder1.build());
        return requestBuilder.build();
    }

    protected RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method != null && !method.equalsIgnoreCase("GET")) {
            if (method.equalsIgnoreCase("POST")) {
                RequestBuilder requestBuilder = RequestBuilder.post();
                NameValuePair[] nameValuePair =
                        (NameValuePair[]) ((NameValuePair[]) request.getExtra("nameValuePair"));
                if (nameValuePair != null && nameValuePair.length > 0) {
                    requestBuilder.addParameters(nameValuePair);
                }

                return requestBuilder;
            } else if (method.equalsIgnoreCase("HEAD")) {
                return RequestBuilder.head();
            } else if (method.equalsIgnoreCase("PUT")) {
                return RequestBuilder.put();
            } else if (method.equalsIgnoreCase("DELETE")) {
                return RequestBuilder.delete();
            } else if (method.equalsIgnoreCase("TRACE")) {
                return RequestBuilder.trace();
            } else {
                throw new IllegalArgumentException("Illegal HTTP Method " + method);
            }
        } else {
            return RequestBuilder.get();
        }
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse,
            Task task) throws IOException {
        String content = this.getContent(charset, httpResponse);
        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
    }

    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = this.getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                log.warn(
                        "Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()",
                        Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes)
            throws IOException {
        String value = httpResponse.getEntity().getContentType().getValue();
        String charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            log.debug("Auto get charset: {}", charset);
            return charset;
        } else {
            Charset defaultCharset = Charset.defaultCharset();
            String content = new String(contentBytes, defaultCharset.name());
            if (StringUtils.isNotEmpty(content)) {
                Document document = Jsoup.parse(content);
                Elements links = document.select("meta");
                Iterator<Element> var9 = links.iterator();

                while (var9.hasNext()) {
                    Element link = (Element) var9.next();
                    String metaContent = link.attr("content");
                    String metaCharset = link.attr("charset");
                    if (metaContent.indexOf("charset") != -1) {
                        metaContent = metaContent.substring(metaContent.indexOf("charset"),
                                metaContent.length());
                        charset = metaContent.split("=")[1];
                        break;
                    }

                    if (StringUtils.isNotEmpty(metaCharset)) {
                        charset = metaCharset;
                        break;
                    }
                }
            }

            log.debug("Auto get charset: {}", charset);
            return charset;
        }
    }
}
