FROM amazoncorretto:20-alpine3.18-jdk
WORKDIR /src/app
COPY . .
RUN ./mvnw install

ARG BOT_SECRET
ENV BOT_SECRET $BOT_SECRET

ARG REMOTE_API_HOST
ENV REMOTE_API_HOST $REMOTE_API_HOST

ENTRYPOINT ["java","-jar","/src/app/target/that-chat.jar"]