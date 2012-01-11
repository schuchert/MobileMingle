package com.tw.mobilemingle.dao;

public class DuplicatedApplicationException extends RuntimeException {
  public final String applicationName;

  public DuplicatedApplicationException(String applicationName) {
    this.applicationName = applicationName;
  }

  private static final long serialVersionUID = 1L;
}
