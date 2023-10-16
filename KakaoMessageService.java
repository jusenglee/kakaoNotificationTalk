import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KakaoMessageService {
    private static final Logger logger = LoggerFactory.getLogger(KakaoMessageService.class);
    private static final String API_ENDPOINT = System.getenv("KAKAO_API_ENDPOINT"); // 전송 할 엔드포인트
    private static final String API_KEY = System.getenv("KAKAO_API_KEY"); // 발급받은 API 키

  
  
    //템플릿 내의 플레이스홀더를 실제 값으로 치환하는 메서드
    private String applyTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("#{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
  
    
    private Request buildRequest(Map<String, String> formData, MessageTemplate templateType, Map<String, String> templateValues) {
      MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
      for (Map.Entry<String, String> entry : formData.entrySet()) {
          bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
      }
      
      String contents = applyTemplate(templateType.getTemplate(), templateValues);
      bodyBuilder.addFormDataPart("contents", contents);
      
      return new Request.Builder()
          .url(API_ENDPOINT)
          .method("POST", bodyBuilder.build())
          .addHeader("Content-Type", "multipart/form-data")
          .addHeader("sejongApiKey", API_KEY)
          .build();
  }
    
    
    public String kakaoMessageTest(OkHttpClient client, Map<String, String> formData, MessageTemplate templateType, Map<String, String> templateValues) throws IOException {
      Request request = buildRequest(formData, templateType, templateValues);

      try (Response response = client.newCall(request).execute()) {
          if (response.isSuccessful()) {
              logger.info("Response is successful");
              return response.body().string();
          } else {
              logger.error("Unexpected response code: {}", response.code());
              throw new IOException("Unexpected response code " + response.code());
          }
      }
  }
}