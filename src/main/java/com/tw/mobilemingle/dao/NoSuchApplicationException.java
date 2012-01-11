package com.tw.mobilemingle.dao;

public class NoSuchApplicationException extends RuntimeException {
  public final String applicationName;

  public NoSuchApplicationException(String applicationName) {
    this.applicationName = applicationName;
  }

  private static final long serialVersionUID = 1L;

}
