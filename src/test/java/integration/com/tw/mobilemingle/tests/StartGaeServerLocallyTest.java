package integration.com.tw.mobilemingle.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StartGaeServerLocallyTest {
  StartGaeServerLocally gaeServer;


  @Before
  public void startServer() throws Exception {
    gaeServer = new StartGaeServerLocally("../MobileMingle/war");
    gaeServer.start();
  }

  @After
  public void stopServer() {
    gaeServer.shutdown();
  }

  @Test
  public void doesItStopNicely() throws Exception {
  }

  @Test
  public void doesItStopMultipleTimes() throws Exception {
    gaeServer.shutdown();
    gaeServer.start();
  }
}
