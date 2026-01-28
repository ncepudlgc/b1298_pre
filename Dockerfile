FROM ubuntu:focal

ENV DEBIAN_FRONTEND noninteractive

RUN apt update -y

RUN apt install \
          openjdk-17-jdk \
	  git \
          --assume-yes

COPY . /code

WORKDIR /code

# REMOVED: Application should not be built during image creation
# RUN ./gradlew build
