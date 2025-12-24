package gcfv2;

import java.io.BufferedWriter;

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
    response.appendHeader("Content-Type", "application/json");
    final BufferedWriter writer = response.getWriter();
    writer.write(gson.toJson(new ResponseMessage("Hello world!")));
  }
}
