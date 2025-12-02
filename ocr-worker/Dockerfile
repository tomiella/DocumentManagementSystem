FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .

RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre-noble
WORKDIR /app

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    libtesseract-dev \
    ghostscript \
    tesseract-ocr-eng \
    tesseract-ocr-deu \
    poppler-utils \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
