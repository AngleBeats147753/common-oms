FROM openjdk:17
COPY ./application.jar /application.jar
ENTRYPOINT ["java", "-jar", "/application.jar", \
    "--spring.profiles.active=${PROFILES}", \
    "--spring.config.import=configserver:${CONFIG_URL}", \
    "--spring.cloud.config.enabled=true", \
    "--spring.cloud.config.username=${CONFIG_USERNAME}", \
    "--spring.cloud.config.password=${CONFIG_PASSWORD}"]