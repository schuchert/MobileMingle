package com.tw.mobilemingle.service;

import java.util.List;

import com.tw.mobilemingle.dao.ApplicationDao;
import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.domain.Journey;

public class ApplicationService {

  public void createApplicationNamed(String name) {
    Application a = new Application(name);
    new ApplicationDao().create(a);
  }

  public Application addJourneyTo(String applicationName, String journeyName, int points, int stories) {
    ApplicationDao dao = new ApplicationDao();
    Application a = dao.findByName(applicationName);
    Journey j = new Journey(journeyName, points, stories);
    a.add(j);
    dao.update(a);

    return a;
  }

  public Iterable<Journey> journeysFor(String applicationName) {
    ApplicationDao dao = new ApplicationDao();
    Application a = dao.findByName(applicationName);
    return a.getJourneys();
  }

  public void deleteApplicationNamed(String applicationName) {
    ApplicationDao dao = new ApplicationDao();
    dao.delete(applicationName);
  }

  public void deleteAllApplications() {
    new ApplicationDao().deleteAllApplications();
  }

  public void deleteJourneyFromApplication(String applicationName, String journeyName) {
    ApplicationDao dao = new ApplicationDao();
    Application a = dao.findByName(applicationName);
    a.removeJourneNamed(journeyName);
  }

  public List<Application> readAll() {
    return new ApplicationDao().readAll();
  }

  public boolean applicationExists(String applicationName) {
    return new ApplicationDao().applicationExists(applicationName);
  }
}
