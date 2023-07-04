# KidsQuiz

## 📂 프로젝트 소개
- 팝업 식으로 한글, 수학, 창의력에 대한 4지 선다 퀴즈가 나옴으로서 **영유아 교육 및 스마트폰 사용 관리**를 목적으로 하는 서비스입니다. <br>
- 최근 맞벌이 가정이 증가함에 따라 육아 중 영유아에 대한 스마트폰 노출이 불가피한 상황입니다. <br>
  이에 영유아의 퀴즈를 통해 영유아의 주의를 환기시켜 부모님들의 염려와 스마트폰 사용에 대한 부정적인 영향을 최소화하기 위한 목적으로 기획하게 되었습니다. <br>

### 📆 개발 인원 및 기간
- 개발기간 : 2020/04 ~ 2021/01
- 개발 인원 : 안드로이드 화면 작성 3명, 안드로이드 전반 2명

## 🛠 사용 기술 및 구현 기능
### 사용 기술 및 tools
> - Android-Appplication : <img src = "https://img.shields.io/badge/android-3DDC84?style=for-the-badge&logo=android&logoColor=white">&nbsp; <img src = "https://img.shields.io/badge/androidstudio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white">&nbsp; <img src = "https://img.shields.io/badge/java-4B4B77?style=for-the-badge">&nbsp;

> - Server : <img src = "https://img.shields.io/badge/firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white">&nbsp; <img src = "https://img.shields.io/badge/json-000000?style=for-the-badge">&nbsp;

### 구현 기능
#### User
- Firebase Authentication를 활용한 회원가입 및 로그인 기능 
- 회원 정보 수정(프로필 이미지, 소개, 닉네임) 기능
- 회원 탈퇴 기능

#### Quiz
- 연령, 출제 간격 설정
- 퀴즈 출제 버튼을 클릭하면 설정한 출제 간격마다 팝업식으로 4시선다 문제 등장
- 퀴즈 종료 버튼을 누를 때까지 무한 반복

#### Statistics
- 전체 정답률, 과목 별 정답률 조회 기능
- 다른 영유아 사용자들의 정오답 데이터를 기반으로 한 백분위 조회 기능
