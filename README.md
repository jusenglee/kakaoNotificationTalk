# kakaoNotificationTalk

카카오 알림톡 API TEST 코드.

전송에 필요한 다양한 템플릿을 선택 할 수 있게 하기 위해 따로
MessageTemplate을 생성하여 추가적인 템플릿 작성 및 설정 할 수 있게 하려 한다.


![image](https://github.com/jusenglee/kakaoNotificationTalk/assets/85321903/8c3ab57a-93cc-48fe-987c-1907b5c271ec)

API 실행을 위한 데이터는 유동적으로 전달을 위해 Map 형태를 사용한다.

사용자에게 띄워줄 템플릿에 데이터 세팅을 위한 templateValues 맵과
API 서버로의 전송을 위한 prepareFormData 맵 두가지가 있다.
