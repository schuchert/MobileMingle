package com.tw.quote;

import java.util.List;

public class QuoteResult {
  public final int page;
  public final int total;
  public final int records;
  public final List<Quote> rows;

  public QuoteResult(int page, int total, int records, List<Quote> rows) {
    this.page = page;
    this.total = total;
    this.records = records;
    this.rows = rows;
  }
}
