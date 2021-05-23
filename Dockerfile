FROM adoptopenjdk/maven-openjdk11

WORKDIR /app

COPY . /app

RUN mvn clean package
RUN chmod 755 /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]