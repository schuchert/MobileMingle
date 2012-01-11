package com.tw.quote;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class JsonStockData extends HttpServlet {
  public static final Random RND = new Random();
  private static final long serialVersionUID = 1L;
  private static final double MAX_PRICE = 100.0;
  private static final double MAX_PRICE_CHANGE = 0.02;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    String[] stockSymbols = req.getParameter("q").trim().split("[ ,]+");
    List<Quote> quotes = quotesFor(stockSymbols);
    renderInto(out, quotes);
    out.flush();
  }

  private void renderInto(PrintWriter out, List<Quote> quotes) {
    QuoteResult quoteResult = new QuoteResult(1, 1, quotes.size(), quotes);
    out.println(new Gson().toJson(quoteResult));
  }

  private List<Quote> quotesFor(String[] stockSymbols) {
    int id = 0;
    LinkedList<Quote> result = new LinkedList<Quote>();
    for (String stockSymbol : stockSymbols) {
      double price = RND.nextDouble() * MAX_PRICE;
      double change = price * MAX_PRICE_CHANGE * (RND.nextDouble() * 2f - 1f);
      result.add(new Quote(++id, stockSymbol, price, change));
    }
    return result;
  }
}
