package com.tw.quote;

public class Quote {
  public final int id;
  public final String symbol;
  public final double price;
  public final double change;

  public Quote(int id, String symbol, double price, double change) {
    this.id = id;
    this.symbol = symbol;
    this.price = price;
    this.change = change;
  }
}
