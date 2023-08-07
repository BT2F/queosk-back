예시를 든 것이니 이를 수정하여 반영하시면 됩니다.

이슈 제목 - bug : InvalidLoginException 미 반환되는 버그 

### 버그

`UserService`에서 비밀번호 검증( 로그인 , 고객정보수정, 고객 비밀번호 변경) 시 

비밀번호가 일치하지 않을경우 정상적인 예외 `InvalidLoginException`가 발생하지 않고

`internal server error 500` 가 반환됨. 

### 영향

로그인, 고객정보수정, 고객 비밀번호 변경과 같은 기능에서 잘못된 비밀번호를 입력하는 경우 

비정상적인 메시지 `Internal Server Error`가 반환되고,이로인해 예외발생 원인 파악이 어려울 수 있음.

### 재현 방법

UserService에서 잘못된 비밀번호를 입력하여 

`login()`,`editUserInformation()`,`changeUserPassword()` 호출 

### 해결 방안

디버깅을 통하여 원인을 확인하고 해당 버그 수정 필요