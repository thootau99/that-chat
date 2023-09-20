FROM amazoncorretto:20-alpine3.18-jdk
WORKDIR /src/app
COPY . .
ARG BOT_SECRET
ENV BOT_SECRET $BOT_SECRET

ARG REMOTE_API_HOST
ENV REMOTE_API_HOST $REMOTE_API_HOST
RUN ./mvnw install

ENTRYPOINT ["java","-jar","/src/app/target/that-chat.jar"]