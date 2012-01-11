package integration.com.tw.mobilemingle.tests;

import java.io.File;

import com.google.appengine.tools.KickStart;
import com.google.appengine.tools.development.DevAppServer;
import com.google.appengine.tools.development.DevAppServerMain;

public class StartGaeServerLocally {
  private Thread gaeServerThread;
  private final String webDirectory;

  public StartGaeServerLocally(String webDirectory) {
    this.webDirectory = webDirectory;
  }

  public void start() {
    gaeServerThread = new Thread(new Runnable() {
      public void run() {
        File dir = new File(webDirectory);
        KickStart.main(new String[] { DevAppServerMain.class.getName(), dir.getAbsolutePath() });
      }
    });
    
    gaeServerThread.start();

    for (int i = 0; i < 10; ++i) {
      try {
        Thread.sleep(500);
        new UrlReader().getContent(String.format("http://localhost:%d/application", DevAppServer.DEFAULT_HTTP_PORT));
        return;
      } catch (Exception e) {
      }
    }
  }

  public void shutdown() {
    gaeServerThread.interrupt();
    try {
      gaeServerThread.join(2000);
    } catch (InterruptedException e) {
    }
    gaeServerThread = null;
  }
}
