package com.tw.mobilemingle.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tw.mobilemingle.dao.NoSuchApplicationException;
import com.tw.mobilemingle.domain.Application;
import com.tw.mobilemingle.domain.Journey;
import com.tw.mobilemingle.json.RequestResult;
import com.tw.mobilemingle.service.ApplicationService;

public class ApplicationJsonServlet extends HttpServlet {
  private static final String READ = "read";
  private static final String DELETE_ALL = "deleteAll";
  private static final String READ_ALL = "readAll";
  private static final String DELETE_JOURNEY = "deleteJourney";
  private static final String ADD_JOURNEY = "addJourney";
  private static final String DELETE = "delete";
  private static final String CREATE = "create";
  private static final String ACTIONS = CREATE + ", " + READ + ", " + "DELETE" + ", " + ADD_JOURNEY + ", "
      + DELETE_JOURNEY + ", " + DELETE_ALL + ", " + READ_ALL;
  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String action = getAction(req);
    String applicationName = getApplicationName(req);

    ApplicationService service = new ApplicationService();
    PrintWriter out = resp.getWriter();

    if (READ.equals(action)) {
      retrieveApplication(service, action, applicationName, req, out);
    } else if (CREATE.equals(action)) {
      createApplication(service, action, applicationName, req, out);
    } else if (DELETE.equals(action)) {
      deleteApplication(service, action, applicationName, req, out);
    } else if (ADD_JOURNEY.equals(action)) {
      addJourneyToApplication(service, action, applicationName, req, out);
    } else if (DELETE_JOURNEY.equals(action)) {
      deleteJourneyFromApplication(service, action, applicationName, req, out);
    } else if (DELETE_ALL.equals(action)) {
      deleteAllApplications(service, action, applicationName, req, out);
    } else if (READ_ALL.equals(action)) {
      readAllApplications(service, action, applicationName, req, out);
    } else {
      streamResult(false, action, out, "unknown action. available: " + ACTIONS);
    }
  }

  private void readAllApplications(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    List<Application> allApps = service.readAll();
    streamResult(true, action, out, allApps);
  }

  private void deleteAllApplications(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    service.deleteAllApplications();
    streamResult(true, action, out, applicationName);
    waitForApplicationEffect(service, false, applicationName);
  }

  private void deleteJourneyFromApplication(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    String journeyName = req.getParameter("journeyName");
    service.deleteJourneyFromApplication(applicationName, journeyName);
    streamResult(true, action, out, applicationName);
    waitForApplicationEffect(service, true, applicationName);
  }

  private void addJourneyToApplication(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    String journeyName = req.getParameter("journeyName");
    int points = Integer.parseInt(req.getParameter("points"));
    int stories = Integer.parseInt(req.getParameter("stories"));
    service.addJourneyTo(applicationName, journeyName, points, stories);
    streamResult(true, action, out, applicationName);
    waitForApplicationEffect(service, true, applicationName);
  }

  private void deleteApplication(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    service.deleteApplicationNamed(applicationName);
    streamResult(true, action, out, applicationName);
    waitForApplicationEffect(service, false, applicationName);
  }

  private void createApplication(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    service.createApplicationNamed(applicationName);
    streamResult(true, action, out, applicationName);
    waitForApplicationEffect(service, true, applicationName);
  }

  private void retrieveApplication(ApplicationService service, String action, String applicationName,
      HttpServletRequest req, PrintWriter out) {
    Iterable<Journey> journeys = getJourneys(service, applicationName);
    streamResult(true, action, out, journeys);
  }

  private String getAction(HttpServletRequest req) {
    String action = req.getParameter("action");
    if (action == null || action.trim().length() == 0)
      action = READ;
    return action;
  }

  private String getApplicationName(HttpServletRequest req) {
    String name = req.getParameter("applicationName");
    name = name != null ? name.trim() : "default";
    return name;
  }

  private Iterable<Journey> getJourneys(ApplicationService service, String name) {
    try {
      return service.journeysFor(name);
    } catch (NoSuchApplicationException e) {
      if ("default".equals(name)) {
        service.createApplicationNamed("default");
        return getJourneys(service, name);
      }
      throw e;
    }
  }

  private void streamResult(boolean result, String action, PrintWriter out, Object returnObject) {
    Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }

      public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().equals("application");
      }
    }).setPrettyPrinting().create();
    out.println(gson.toJson(new RequestResult(result, action, returnObject)));
    out.flush();
  }

  private void waitForApplicationEffect(ApplicationService service, boolean shouldBeThere, String applicationName) {
    while (service.applicationExists(applicationName) != shouldBeThere)
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
  }
}
