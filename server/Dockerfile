FROM tomcat:8.0.50-jre8

MAINTAINER s3rius <win10@list.ru>

EXPOSE 8080

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY ./tomcat-users.xml /usr/local/tomcat/conf/
COPY ./target/server.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]