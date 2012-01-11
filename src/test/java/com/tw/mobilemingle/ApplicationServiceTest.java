package com.tw.mobilemingle;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tw.gaetestutil.GaeEnvironmentInitialization;
import com.tw.mobilemingle.dao.ApplicationDao;
import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.service.ApplicationService;

public class ApplicationServiceTest extends GaeEnvironmentInitialization {
  private static final String TEST_APP = "test_app";

  ApplicationService service;

  @Before
  public void init() {
    service = new ApplicationService();
  }

  @After
  public void deleteAllApplications() {
    service.deleteAllApplications();
  }

  @Test
  public void creatingApplication() {
    service.createApplicationNamed(TEST_APP);
    Application found = new ApplicationDao().findByName(TEST_APP);
    assertNotNull(found);
  }

  @Test
  public void addJourneyToApplication() {
    service.createApplicationNamed(TEST_APP);
    service.addJourneyTo(TEST_APP, "j", 12, 14);
    Application found = new ApplicationDao().findByName(TEST_APP);
    assertTrue(found.getJourneys().iterator().hasNext());
  }
}