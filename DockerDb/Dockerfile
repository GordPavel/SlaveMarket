FROM postgres:9.4
MAINTAINER s3rius <win10@list.ru>

RUN localedef -i ru_RU -c -f UTF-8 -A /usr/share/locale/locale.alias ru_RU.UTF-8

ENV POSTGRES_USER s3rius
ENV POSTGRES_PASSWORD 19216211
ENV POSTGRES_DB slaveMarket

COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d