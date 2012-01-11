package com.tw.mobilemingle.domain;

import com.google.appengine.api.datastore.Key;

public class Journey {
  public Key id;
  public String name;
  public int storyCount;
  public int points;
  Application application;

  public Journey(String name, int points, int storyCount) {
    this.name = name;
    this.points = points;
    this.storyCount = storyCount;
  }

  public void setApplication(Application application) {
    this.application = application;
  }
}
