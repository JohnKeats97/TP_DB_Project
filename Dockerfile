FROM ubuntu:16.04

MAINTAINER Evgeniy Buevich

RUN apt-get -y update


ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER

RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN pip3 install --upgrade pip
RUN pip3 install pytz
RUN pip3 install psycopg2
RUN pip3 install gunicorn
RUN pip3 install flask
RUN pip3 install ujson

USER postgres

RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -E UTF8 -T template0 -O docker docker &&\
    /etc/init.d/postgresql stop

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit=off" >> /etc/postgresql/$PGVER/main/postgresql.conf

EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

USER root

ENV WORK /opt/forum_db
ADD DataBase/ $WORK/DataBase/
ADD appconfig.py $WORK/appconfig.py
ADD route.py $WORK/route.py
ADD main.py $WORK/main.py
ADD schema_DB.sql $WORK/schema_DB.sql

EXPOSE 5000

ENV PGPASSWORD docker
CMD service postgresql start &&\
    cd $WORK/ &&\
    psql -h localhost -U docker -d docker -f schema_DB.sql &&\
    gunicorn -w 4 -b :5000 main:app
