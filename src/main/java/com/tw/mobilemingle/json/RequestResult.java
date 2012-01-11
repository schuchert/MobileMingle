package com.tw.mobilemingle.json;

public class RequestResult {
    public final boolean success;
    public final String action;
    public final Object result;
    public RequestResult(boolean success, String action, Object result) {
      this.success = success;
      this.action = action;
      this.result = result;
    }
}
