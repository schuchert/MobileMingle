package com.tw.mobilemingle.dao;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.domain.Journey;

public class ApplicationDao {
  private int applicationCount(String applicationName) {
    PreparedQuery pq = allApplicationsQuery(applicationName);
    int count = 0;
    for (@SuppressWarnings("unused")
    Entity current : pq.asIterable())
      ++count;
    return count;
  }

  private PreparedQuery allApplicationsQuery(String applicationName) {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Query q = applicationQuery();
    q.addFilter("name", FilterOperator.EQUAL, applicationName);
    PreparedQuery pq = service.prepare(q);
    return pq;
  }

  private Query applicationQuery() {
    Query q = new Query("Application");
    return q;
  }

  public ApplicationDao create(Application a) {
    if (applicationCount(a.getName()) != 0)
      throw new DuplicatedApplicationException(a.getName());

    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Entity e = new Entity("Application");
    e.setProperty("name", a.getName());
    service.put(e);
    for (Journey j : a.getJourneys()) {
      Entity currentJourney = new Entity("Journey");
      currentJourney.setProperty("name", j.name);
      currentJourney.setProperty("storyCount", j.storyCount);
      currentJourney.setProperty("points", j.points);
      currentJourney.setProperty("application", e.getKey());
      service.put(currentJourney);
    }
    return this;
  }

  public Application findByName(String applicationName) {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery pq = allApplicationsQuery(applicationName);

    List<Application> applications = new LinkedList<Application>();
    for (Entity result : pq.asIterable())
      applications.add(retrieveOneApplication(service, result));

    if (applications.size() == 0)
      throw new NoSuchApplicationException(applicationName);
    if (applications.size() > 1)
      throw new DuplicatedApplicationException(applicationName);

    return applications.get(0);
  }

  private Application retrieveOneApplication(DatastoreService service, Entity result) {
    String name = result.getProperty("name").toString();
    Application application = new Application(name);
    appendJourneys(service, application, result);
    return application;
  }

  private void appendJourneys(DatastoreService service, Application application, Entity applicationEntity) {
    Query jq = new Query("Journey");
    jq.addFilter("application", FilterOperator.EQUAL, applicationEntity.getKey());
    PreparedQuery pjq = service.prepare(jq);
    for (Entity journey : pjq.asIterable()) {
      String jName = journey.getProperty("name").toString();
      int points = Integer.parseInt(journey.getProperty("points").toString());
      int stories = Integer.parseInt(journey.getProperty("storyCount").toString());
      application.add(new Journey(jName, points, stories));
    }
  }

  public ApplicationDao update(Application a) {
    delete(a.getName());
    create(a);
    return this;
  }

  public ApplicationDao delete(String applicationName) {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Query q = applicationQuery();
    q.addFilter("name", FilterOperator.EQUAL, applicationName);
    PreparedQuery pq = service.prepare(q);
    List<Key> keys = new LinkedList<Key>();
    for (Entity result : pq.asIterable()) {
      keys.add(result.getKey());
    }
    service.delete(keys);

    for (Entity result : pq.asIterable()) {
      Query jq = new Query("Journey");
      jq.addFilter("application", FilterOperator.EQUAL, result.getKey());
      pq = service.prepare(jq);
      List<Key> journeyKeys = new LinkedList<Key>();
      for (Entity journey : pq.asIterable())
        journeyKeys.add(journey.getKey());
      service.delete(journeyKeys);
    }

    return this;
  }

  public ApplicationDao deleteAllApplications() {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Query q = applicationQuery();
    PreparedQuery pq = service.prepare(q);
    List<Key> keys = new LinkedList<Key>();
    for (Entity result : pq.asIterable())
      keys.add(result.getKey());
    service.delete(keys);
    deleteAllJoureys();
    return this;
  }

  private void deleteAllJoureys() {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Query q = new com.google.appengine.api.datastore.Query("Journey");
    PreparedQuery pq = service.prepare(q);
    List<Key> keys = new LinkedList<Key>();
    for (Entity result : pq.asIterable())
      keys.add(result.getKey());
    service.delete(keys);
  }

  public List<Application> readAll() {
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery pq = service.prepare(new Query("Application"));

    List<Application> applications = new LinkedList<Application>();
    for (Entity result : pq.asIterable())
      applications.add(retrieveOneApplication(service, result));
    return applications;
  }

  public boolean applicationExists(String applicationName) {
    return applicationCount(applicationName) > 0;
  }

}
