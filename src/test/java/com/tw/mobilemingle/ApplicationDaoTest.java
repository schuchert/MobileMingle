package com.tw.mobilemingle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tw.gaetestutil.GaeEnvironmentInitialization;
import com.tw.mobilemingle.dao.ApplicationDao;
import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.domain.Journey;

public class ApplicationDaoTest extends GaeEnvironmentInitialization {
  private static final String TEST_APP = "test_app";

  ApplicationDao dao = new ApplicationDao();

  @Before
  public void init() {
    dao = new ApplicationDao();
    dao.create(new Application(TEST_APP));
  }

  @After
  public void tearDown() {
    dao.deleteAllApplications();
  }

  @Test
  public void canFindCreatedApplication() {
    Application found = dao.findByName(TEST_APP);
    assertNotNull(found);
  }

  @Test
  public void canAddJoruinesToApplication() {
    Application found = dao.findByName(TEST_APP);
    Journey j = new Journey("j", 12, 4);
    found.add(j);
    dao.update(found);
    Application updated = dao.findByName(TEST_APP);
    assertTrue(updated.getJourneys().iterator().hasNext());
  }

  @Test
  public void canRemoveJourneyFromApplication() {
    Application found = dao.findByName(TEST_APP);
    found.add(new Journey("j1", 12, 4));
    found.add(new Journey("j2", 12, 4));
    dao.update(found);

    Application toDeleteFrom = dao.findByName(TEST_APP);
    toDeleteFrom.removeJourneNamed("j2");
    dao.update(found);
  }
  
  @Test
  public void removingJourneysFromOneApplicaionDoesNotRemoveFromTheOther() {
    Application a = new Application("second");
    a.add(new Journey("j1", 2, 3));
    a.add(new Journey("j2", 2, 3));
    dao.create(a);
    Application found = dao.findByName(TEST_APP);
    found.add(new Journey("j1", 2, 3));
    found.add(new Journey("j2", 2, 3));
    dao.update(found);
    Application found2 = dao.findByName("second");
    found2.removeJourneNamed("j2");
    dao.update(found2);
    
    found = dao.findByName(TEST_APP);
    found2 = dao.findByName("second");
    assertEquals(2, found.journeyCount());
    assertEquals(1, found2.journeyCount());
  }
}