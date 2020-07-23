FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD "dolly-backend-app-nais/target/app-exec.jar" /app/app.jar
