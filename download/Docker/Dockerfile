FROM ubuntu:20.04

ENV USER root

RUN apt-get update && \
    apt-get -y upgrade && \
	apt-get install -y openjdk-8-jdk && \
	apt-get install -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;

RUN apt-get update && \
	apt-get install -y ca-certificates-java && \
	apt-get clean && \
	update-ca-certificates -f && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;

RUN rm /etc/localtime && \
    ln -s /usr/share/zoneinfo/America/Mexico_City /etc/localtime;

ADD ./download/target/*.jar /app.jar

ENV HOME=/root
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENV LANG en_US.UTF-8
ENV JAVA_OPTS=""
WORKDIR /root
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]
