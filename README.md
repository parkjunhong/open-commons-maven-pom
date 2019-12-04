# open-commons-maven-pom
Maven POM file Generator

## How to build
일반적인 빌드 명령어는 mvn clean package -Dbuild.profile={원하는 프로파일} 이다.\
프로젝트를 빌드하는 경우 원하는 설정정보를 선택해서 빌드하는 것을 지원하기 위한 정보이며,\
프로파일명에 해당하는 디렉토리가 config/ 디렉토리 아래에 위치해야 한다.

__[config]__\
build.profile 에 해당하는 설정정보를 config/${build.profile} 형태로 찾아서 빌드를 실행한다.

__[빌드결과]__\
빌드 결과는 deploy/{build.profile} 형태로 생성된다.


