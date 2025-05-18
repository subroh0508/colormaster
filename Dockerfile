# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY . /home/gradle/app/
WORKDIR /home/gradle/app
RUN gradle clean build -i --stacktrace

# Stage 2: Build Application
FROM gradle:latest AS build

# Install Litestream CLI
ENV LITESTREAM_VERSION=v0.3.13

ADD https://github.com/benbjohnson/litestream/releases/download/$LITESTREAM_VERSION/litestream-$LITESTREAM_VERSION-linux-amd64.tar.gz /tmp/litestream.tar.gz
RUN tar -C /usr/local/bin -xzf /tmp/litestream.tar.gz

COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Build the fat JAR, Gradle also supports shadow
# and boot JAR by default.
RUN gradle backend:server:buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:22 AS runtime

EXPOSE 8080
RUN mkdir /app
COPY --from=build /usr/local/bin/litestream /usr/local/bin/litestream
COPY --from=build /home/gradle/src/backend/server/build/libs/*.jar /app/colormaster.jar

COPY litestream.yml /etc/litestream.yml
COPY run.sh /app/run.sh

ENTRYPOINT ["app/run.sh"]
