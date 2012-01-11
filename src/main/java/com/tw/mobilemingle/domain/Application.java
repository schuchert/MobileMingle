package com.tw.mobilemingle.domain;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class Application {
  public Key id;
  private String name;
  private List<Journey> journeys;

  public Application(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private List<Journey> getJourneyCollection() {
    if (journeys == null)
      journeys = new LinkedList<Journey>();
    return journeys;
  }

  public void add(Journey j) {
    getJourneyCollection().add(j);
    j.setApplication(this);
  }

  public Iterable<Journey> getJourneys() {
    return getJourneyCollection();
  }

  public int storyCount() {
    int total = 0;
    for (Journey j : getJourneys())
      total += j.storyCount;
    return total;
  }

  public int totalPoints() {
    int total = 0;
    for (Journey j : getJourneys())
      total += j.points;
    return total;
  }

  public void removeJourneNamed(String journeyName) {
    for (Journey j : getJourneyCollection())
      if (j.name.equals(journeyName)) {
        getJourneyCollection().remove(j);
        return;
      }
  }

  public int journeyCount() {
    return getJourneyCollection().size();
  }
}
