package gcfv2;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

public class HelloHttpFunction implements HttpFunction {

  static class ResponseMessage {
    String message;
    ResponseMessage(String message) { this.message = message; }
  }

  private static final Gson gson = new Gson();

  public void service(final HttpRequest request, final HttpResponse response) throws Exception {

    String token = null;
    Map<String, java.util.List<String>> headers = request.getHeaders();
    if (headers != null) {
      List<String> authHeaders = headers.get("Authorization");
      if (authHeaders == null || authHeaders.isEmpty()) {
        authHeaders = headers.get("authorization");
      }
      if (authHeaders != null && !authHeaders.isEmpty()) {
        String header = authHeaders.get(0).trim();
        if (header.toLowerCase().startsWith("bearer ")) {
          token = header.substring(7).trim();
        } else {
          token = header;
        }
      }
    }

    response.appendHeader("Content-Type", "application/json");
    final BufferedWriter writer = response.getWriter();

    if (token == null || token.isEmpty()) {
      response.setStatusCode(401);
      writer.write(gson.toJson(new ResponseMessage("missing token")));
      return;
    }

    String last8;
    if (token.length() <= 8) {
      last8 = token;
    } else {
      last8 = token.substring(token.length() - 8);
    }

    writer.write(gson.toJson(new ResponseMessage("Hello world! token: " + last8)));
  }
}
