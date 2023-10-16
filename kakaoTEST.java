import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class kakaoTEST {

    private KakaoMessageService service;
    private OkHttpClient mockClient;
    private Call mockCall;
    private Response mockResponse;

    String plusFriendId = "@acoms_plus";
    String senderKey = System.getenv("KAKAO_SENDER_KEY");;
    String title = "Acoms+ 알림톡";
    String userKey = "0102";
    String templateCode = "SJB1132";
    String receiverTelNo = "12341234";
    
    // 테스트 케이스 활용을 위한 템플릿 설정 값
    private Map<String, String> prepareTemplateValues() {
      Map<String, String> templateValues = new HashMap<>();
      templateValues.put("회원명", "홍길동");
      templateValues.put("학술지명", "JISTAP2");
      templateValues.put("논문명", "테스트 논문");
      templateValues.put("투고일자", "10/20 19:00");
      templateValues.put("투고결과", "게재 가");
      return templateValues;
    }

    //테스트 케이스 활용을 위한 API 전송 값
    private Map<String, String> prepareFormData() {
      Map<String, String> formData = new HashMap<>();
      formData.put("plusFriendId", plusFriendId);
      formData.put("senderKey", senderKey);
      formData.put("title", title);
      formData.put("userKey", userKey);
      formData.put("templateCode", templateCode);
      formData.put("receiverTelNo", receiverTelNo);
      return formData;
    }

    

    @BeforeEach
    public void setup() {
        service = new KakaoMessageService();

        mockClient = Mockito.mock(OkHttpClient.class);
        mockCall = Mockito.mock(Call.class);
        mockResponse = Mockito.mock(Response.class);

        Mockito.when(mockClient.newCall(Mockito.any())).thenReturn(mockCall);
    }

    @Test
    void testSuccessfulResponse() throws IOException { // API 응답이 성공적인 경우, 해당 응답을 제대로 처리하는지 확인
        
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        Mockito.when(mockResponse.isSuccessful()).thenReturn(true);
        Mockito.when(mockResponse.body()).thenReturn(ResponseBody.create("Success", MediaType.get("text/plain")));
       
        Map<String, String> templateValues = prepareTemplateValues();//값 세팅
        Map<String, String> formData = prepareFormData();//값 세팅
        
        String result = service.kakaoMessageTest(mockClient,formData,MessageTemplate.KAKAO_심사,templateValues);
        assertEquals("Success", result);
    }

    @Test
    void testFailureResponse() throws IOException {
        
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        Mockito.when(mockResponse.isSuccessful()).thenReturn(false);
        Mockito.when(mockResponse.toString()).thenReturn("Failure");
        
        Map<String, String> templateValues = prepareTemplateValues();//값 세팅
        Map<String, String> formData = prepareFormData();//값 세팅
        
        assertThrows(IOException.class, () -> kakaoMessageTestWithRetry(mockClient,formData,MessageTemplate.KAKAO_심사,templateValues,3));
    }
    
    //실패시 처리 할 재전송 메소드.
    public String kakaoMessageTestWithRetry(OkHttpClient client, Map<String, String> formData, MessageTemplate template, Map<String, String> templateValues, int maxRetries) throws IOException {
      int attempt = 0;
      IOException lastException = null;

      while (attempt < maxRetries) {
          try {
              return service.kakaoMessageTest(client, formData, template, templateValues);
          } catch (IOException e) {
              lastException = e;
              attempt++;

              // 지수 백오프 전략: 각 재시도 사이에 대기시간이 지수적으로 증가.
              try {
                  Thread.sleep((long) (Math.pow(2, attempt) * 1000)); // 2^attempt * 1000ms
              } catch (InterruptedException ignored) {}
          }
      }

      // 모든 재시도가 실패한 경우
      throw lastException; 
  }
}
