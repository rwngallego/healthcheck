FROM adoptopenjdk:11-jre-hotspot

ENV PACKAGE healthcheck-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME /app

EXPOSE 8888

COPY ./build/libs/$PACKAGE $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $PACKAGE"]
