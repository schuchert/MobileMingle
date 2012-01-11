package com.tw.gaetestutil;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public abstract class GaeEnvironmentInitialization {
  private LocalServiceTestHelper gaeHelper;

  @Before
  public final void setupGaeEnvironment() {
    gaeHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    gaeHelper.setUp();
  }

  @After
  public final void tearDownGaeEnvironment() {
    gaeHelper.tearDown();
  }
}
