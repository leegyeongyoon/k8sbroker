FROM harbor.okestro.cld/library/paas-broker-jdk11:0.1
LABEL maintainer="jj.kim"
RUN mkdir -p /app/paas-broker
ADD ./target/PaaSBroker-0.1.jar /app/paas-broker
WORKDIR /app/paas-broker
EXPOSE 11001
ENTRYPOINT ["java","-jar","PaaSBroker-0.1.jar"]
