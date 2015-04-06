FROM java:8-jre
MAINTAINER https://m-ko-x.de Markus Kosmal <code@m-ko-x.de>

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_PERM_SIZE 256m

ENV TYPESAFE_SBT_VERSION 0.13.8

ENV APP_NAME lightlink
ENV APP_PORT 8080

ADD build.sbt /tmp/${APP_NAME}/build.sbt
ADD web.sbt /tmp/${APP_NAME}/web.sbt
ADD project/plugins.sbt /tmp/${APP_NAME}/project/plugins.sbt
ADD src /tmp/${APP_NAME}/src

WORKDIR /tmp/${APP_NAME}
RUN wget http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/${TYPESAFE_SBT_VERSION}/sbt-launch.jar -O sbt.jar && \
    java -XX:MaxPermSize=${JAVA_PERM_SIZE} -jar sbt.jar packArchive && \
    mkdir -p /opt/${APP_NAME} && \
    tar --strip-components=1 -C /opt/${APP_NAME} -xzf /tmp/${APP_NAME}/target/${APP_NAME}*.tar.gz && \
    rm -rf /root/.ivy /root/.sbt /tmp/${APP_NAME}

WORKDIR /opt/${APP_NAME}
EXPOSE $APP_PORT
ENTRYPOINT JVM_OPT=-Dcassandra.seeds.0=${CASSANDRA_PORT_9042_TCP_ADDR} /opt/${APP_NAME}/bin/${APP_NAME}
