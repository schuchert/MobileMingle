package com.tw.mobilemingle.dao;

import java.util.Iterator;
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
  private static final String JOURNEY = "Journey";
  private static final String APPLICATION_FIELD = "application";
  private static final String STORY_COUNT_FIELD = "storyCount";
  private static final String APPLICATION = "Application";
  private static final String POINTS_FIELD = "points";
  private static final String NAME_FIELD = "name";

  public void create(Application application) {
    if (applicationNameAlreadyUsed(application.getName()))
      throw new DuplicatedApplicationException(application.getName());
  
    DatastoreService service = ds();
    Entity e = new Entity(APPLICATION);
    e.setProperty(NAME_FIELD, application.getName());
    service.put(e);
    for (Journey j : application.getJourneys()) 
      storeAJourney(service, e, j);
  }

  public Application findByName(String applicationName) {
    DatastoreService service = ds();
    PreparedQuery pq = allApplicationsQuery(applicationName);
  
    Iterator<Entity> iter = pq.asIterator();
  
    verifyNotFound(applicationName, iter);
  
    Application foundApplication = retrieveOneApplication(service, iter.next());
  
    verifyNotDuplicated(applicationName, iter);
  
    return foundApplication;
  }

  public ApplicationDao update(Application a) {
    delete(a.getName());
    create(a);
    return this;
  }

  public void delete(String applicationName) {
    DatastoreService service = ds();
    Query q = applicationQuery();
    q.addFilter(NAME_FIELD, FilterOperator.EQUAL, applicationName);
    PreparedQuery pq = service.prepare(q);
    List<Key> keys = removeAllFound(service, pq);
  
    for (Key current : keys) 
      deleteApplicationsJourneys(service, current);
  }

  public void deleteApplicationsJourneys(DatastoreService service, Key applicationKey) {
    Query jq = new Query(JOURNEY);
    jq.addFilter(APPLICATION_FIELD, FilterOperator.EQUAL, applicationKey);
    PreparedQuery pq = service.prepare(jq);
    removeAllFound(service, pq);
  }

  public void deleteAllApplications() {
    DatastoreService service = ds();
    Query q = applicationQuery();
    PreparedQuery pq = service.prepare(q);
    removeAllFound(service, pq);
    deleteAllJoureys();
  }

  public List<Application> readAll() {
    DatastoreService service = ds();
    PreparedQuery pq = service.prepare(new Query(APPLICATION));
  
    List<Application> applications = new LinkedList<Application>();
    for (Entity result : pq.asIterable())
      applications.add(retrieveOneApplication(service, result));
    return applications;
  }

  public boolean applicationExists(String applicationName) {
    return applicationCount(applicationName) > 0;
  }

  private boolean applicationNameAlreadyUsed(String name) {
    return applicationCount(name) != 0;
  }

  private void verifyNotDuplicated(String applicationName, Iterator<Entity> iter) {
    if (iter.hasNext())
      throw new DuplicatedApplicationException(applicationName);
  }

  private void verifyNotFound(String applicationName, Iterator<Entity> iter) {
    if (!iter.hasNext())
      throw new NoSuchApplicationException(applicationName);
  }

  private void storeAJourney(DatastoreService service, Entity e, Journey j) {
    Entity currentJourney = new Entity(JOURNEY);
    currentJourney.setProperty(NAME_FIELD, j.name);
    currentJourney.setProperty(STORY_COUNT_FIELD, j.storyCount);
    currentJourney.setProperty(POINTS_FIELD, j.points);
    currentJourney.setProperty(APPLICATION_FIELD, e.getKey());
    service.put(currentJourney);
  }

  private int applicationCount(String applicationName) {
    PreparedQuery pq = allApplicationsQuery(applicationName);
    int count = 0;
    for (Iterator<Entity> iter = pq.asIterator(); iter.hasNext(); iter.next())
      ++count;
    return count;
  }

  private PreparedQuery allApplicationsQuery(String applicationName) {
    Query q = applicationQuery().addFilter(NAME_FIELD, FilterOperator.EQUAL, applicationName);
    return ds().prepare(q);
  }

  private Query applicationQuery() {
    return new Query(APPLICATION);
  }

  private Query journeyQuery() {
    return new Query(JOURNEY);
  }

  private Application retrieveOneApplication(DatastoreService service, Entity result) {
    String name = result.getProperty(NAME_FIELD).toString();
    Application application = new Application(name);
    application.id = result.getKey();
    retrieveJourneysIntoApplication(service, application, result);
    return application;
  }

  private void retrieveJourneysIntoApplication(DatastoreService service, Application application, Entity applicationEntity) {
    Query jq = journeyQuery().addFilter(APPLICATION_FIELD, FilterOperator.EQUAL, applicationEntity.getKey());
    PreparedQuery pjq = service.prepare(jq);
    for (Entity journey : pjq.asIterable()) {
      String jName = journey.getProperty(NAME_FIELD).toString();
      int points = Integer.parseInt(journey.getProperty(POINTS_FIELD).toString());
      int stories = Integer.parseInt(journey.getProperty(STORY_COUNT_FIELD).toString());
      Journey j = new Journey(jName, points, stories);
      j.id = journey.getKey();
      application.add(j);
    }
  }

  private void deleteAllJoureys() {
    Query q = journeyQuery();
    DatastoreService service = ds();
    PreparedQuery pq = service.prepare(q);
    removeAllFound(service, pq);
  }

  private List<Key> removeAllFound(DatastoreService service, PreparedQuery pq) {
    List<Key> keys = new LinkedList<Key>();
    for (Entity result : pq.asIterable())
      keys.add(result.getKey());
    service.delete(keys);
    return keys;
  }
  
  protected DatastoreService ds() {
    return DatastoreServiceFactory.getDatastoreService();
  }
}
