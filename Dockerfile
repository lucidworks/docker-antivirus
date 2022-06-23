FROM debian:bullseye

ADD . /app
WORKDIR /app

RUN apt update && \
    apt install -y gcc build-essential ruby clamav clamav-daemon ruby-dev podman && \
    apt-get clean && \
    groupadd jenkins -g 7000 && \
    useradd jenkins -d /home/jenkins -m -u 7000 -g docker -G docker -s /bin/bash && \
    freshclam

ADD registries.conf /etc/containers/registries.conf

RUN gem install bundler && bundle install

USER jenkins

