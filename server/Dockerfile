FROM tomcat:8.0.50-jre8

MAINTAINER s3rius <win10@list.ru>

EXPOSE 8080

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh

RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./tomcat-users.xml /usr/local/tomcat/conf/
COPY ./target/server.war /usr/local/tomcat/webapps/ROOT.war

ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"
EXPOSE 8000

CMD ["catalina.sh", "jpda", "run"]