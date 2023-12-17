FROM eclipse-temurin:17
COPY ./build/libs/scraper*.jar np-electoral-roll.jar
EXPOSE 8080
ENV PARALLELISM=10
CMD java -Dcom.sun.management.jmxremote ${JAVA_OPTS} -Djava.util.concurrent.ForkJoinPool.common.parallelism=${PARALLELISM} -jar np-electoral-roll.jar