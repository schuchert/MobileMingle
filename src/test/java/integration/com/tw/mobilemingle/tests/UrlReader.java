package integration.com.tw.mobilemingle.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class UrlReader {
  public String getContent(String url) {
    StringBuffer result = new StringBuffer();
    try {
      HttpClient httpclient = new DefaultHttpClient();
      HttpGet httpget = new HttpGet(url);
      HttpResponse response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        InputStreamReader irs = new InputStreamReader(instream);
        BufferedReader br = new BufferedReader(irs);
        String l;
        while ((l = br.readLine()) != null) {
          result.append(l);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return result.toString();
  }
}
