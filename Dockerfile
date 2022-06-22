FROM debian:bullseye

ADD . /app
WORKDIR /app

RUN apt update && \
    apt install -y gcc build-essential ruby clamav clamav-daemon ruby-dev podman && \
    freshclam

ADD registries.conf /etc/containers/registries.conf

RUN gem install bundler && bundle install



