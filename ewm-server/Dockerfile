FROM amazoncorretto:11-alpine-jdk
# debug
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/*.jar ewm-server.jar
ENTRYPOINT ["java","-jar","/ewm-server.jar"]