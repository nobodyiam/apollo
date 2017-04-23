package com.ctrip.framework.apollo.util.http;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.exceptions.ApolloConfigStatusCodeException;
import com.ctrip.framework.apollo.util.ConfigUtil;

import org.unidal.helper.Files;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class HttpUtil {
  private ConfigUtil m_configUtil;
  private Gson gson;
  private String basicAuth;

  /**
   * Constructor.
   */
  public HttpUtil() {
    m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    gson = new Gson();
    try {
      basicAuth = "Basic " + BaseEncoding.base64().encode("user:".getBytes("UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Do get operation for the http request.
   *
   * @param httpRequest  the request
   * @param responseType the response type
   * @return the response
   * @throws ApolloConfigException if any error happened or response code is neither 200 nor 304
   */
  public <T> HttpResponse<T> doGet(HttpRequest httpRequest, final Class<T> responseType) {
    Function<String, T> convertResponse = new Function<String, T>() {
      @Override
      public T apply(String input) {
        return gson.fromJson(input, responseType);
      }
    };

    return doGetWithSerializeFunction(httpRequest, convertResponse);
  }

  /**
   * Do get operation for the http request.
   *
   * @param httpRequest  the request
   * @param responseType the response type
   * @return the response
   * @throws ApolloConfigException if any error happened or response code is neither 200 nor 304
   */
  public <T> HttpResponse<T> doGet(HttpRequest httpRequest, final Type responseType) {
    Function<String, T> convertResponse = new Function<String, T>() {
      @Override
      public T apply(String input) {
        return gson.fromJson(input, responseType);
      }
    };

    return doGetWithSerializeFunction(httpRequest, convertResponse);
  }

  private <T> HttpResponse<T> doGetWithSerializeFunction(HttpRequest httpRequest,
                                                         Function<String, T> serializeFunction) {
    InputStream is = null;
    int statusCode;
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(httpRequest.getUrl()).openConnection();

      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", basicAuth);

      int connectTimeout = httpRequest.getConnectTimeout();
      if (connectTimeout < 0) {
        connectTimeout = m_configUtil.getConnectTimeout();
      }

      int readTimeout = httpRequest.getReadTimeout();
      if (readTimeout < 0) {
        readTimeout = m_configUtil.getReadTimeout();
      }

      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);

      conn.connect();

      statusCode = conn.getResponseCode();

      if (statusCode == 200) {
        is = conn.getInputStream();
        String content = Files.IO.INSTANCE.readFrom(is, Charsets.UTF_8.name());
        return new HttpResponse<>(statusCode, serializeFunction.apply(content));
      }

      if (statusCode == 304) {
        return new HttpResponse<>(statusCode, null);
      }

    } catch (Throwable ex) {
      throw new ApolloConfigException("Could not complete get operation", ex);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ex) {
          // ignore
        }
      }
    }

    throw new ApolloConfigStatusCodeException(statusCode,
        String.format("Get operation failed for %s", httpRequest.getUrl()));
  }

}
