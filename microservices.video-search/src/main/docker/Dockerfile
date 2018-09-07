FROM docker.boonlogic.io/java:8u171-alpine

ENV JVM_MEM_OPTS_SUPPORT_CONTAINER="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap" \
    JAVA_OPTS="-Djava.security.egd=file:/dev/urandom"

COPY app.jar /

CMD ["java -XX:+PrintFlagsFinal $JVM_MEM_OPTS_SUPPORT_CONTAINER $JAVA_OPTS -jar app.jar"]
