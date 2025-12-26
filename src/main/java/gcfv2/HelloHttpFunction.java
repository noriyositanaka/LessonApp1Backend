package gcfv2;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class HelloHttpFunction implements HttpFunction {

  static class ResponseMessage {
    String message;
    ResponseMessage(String message) { this.message = message; }
  }

  private static final Gson gson = new Gson();

  public void service(final HttpRequest request, final HttpResponse response) throws Exception {

    // ヘッダーからtokenを読み出す
    String token = null;
    Map<String, java.util.List<String>> headers = request.getHeaders();

    // コンテンツタイプと writer を先に準備しておく（早期リターンで使うため）
    response.appendHeader("Content-Type", "application/json");
    final BufferedWriter writer = response.getWriter();

    // Authorization ヘッダーがなければすぐ 401 を返す
    if (headers == null) {
      response.setStatusCode(401);
      writer.write(gson.toJson(new ResponseMessage("missing token")));
      return;
    }

    List<String> authHeaders = headers.get("Authorization");
    if (authHeaders == null || authHeaders.isEmpty()) {
      authHeaders = headers.get("authorization");
    }
    if (authHeaders == null || authHeaders.isEmpty()) {
      response.setStatusCode(401);
      writer.write(gson.toJson(new ResponseMessage("missing token")));
      return;
    }

    String header = authHeaders.get(0).trim();
    if (header.toLowerCase().startsWith("bearer ")) {
      token = header.substring(7).trim();
    } else {
      token = header;
    }

    if (token == null || token.isEmpty()) {
      response.setStatusCode(401);
      writer.write(gson.toJson(new ResponseMessage("missing token")));
      return;
    }

    String uid = null;
    String userName = null;
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseOptions options = FirebaseOptions.builder()
          .setProjectId("lessonapp1-4843e")
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();
        FirebaseApp.initializeApp(options);
      }
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
      uid = decodedToken.getUid();
      userName = decodedToken.getName();
      // 必要なら decodedToken を使って uid 等を参照できます
    } catch (Exception e) {
      response.setStatusCode(401);
      writer.write(gson.toJson(new ResponseMessage("invalid token")));
      return;
    }
    // 正常系レスポンス
    // response.setStatusCode(200);
    User user = new User(uid, userName);
    String userJson = gson.toJson(user);    
    writer.write(userJson);
  }
}
