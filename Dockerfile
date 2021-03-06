FROM ubuntu:16.04

MAINTAINER Evgeniy Buevich

ENV PGVER 9.5

RUN apt-get -y update
RUN apt-get install -y openjdk-8-jdk-headless
RUN apt-get install -y maven
RUN apt-get install -y postgresql-$PGVER

USER postgres

RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -E UTF8 -T template0 -O docker docker &&\
    /etc/init.d/postgresql stop

RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf

EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

USER root

ENV WORK /opt/TP_DB_Project
ADD api_DB/ $WORK/api_DB/

WORKDIR $WORK/api_DB
RUN mvn package

EXPOSE 5000

CMD service postgresql start && java -Xmx300M -Xmx300M -jar $WORK/api_DB/target/DB_Project-1.0-SNAPSHOT.jar
