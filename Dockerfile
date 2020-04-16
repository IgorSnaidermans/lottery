FROM openjdk:13
RUN mkdir webapps
COPY /target/lottery.war webapps
COPY ./src/main/webapp/ webapps
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar webapps/lottery.war" ]

