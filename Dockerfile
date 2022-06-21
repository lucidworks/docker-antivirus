FROM debian:stretch

ADD . /app
WORKDIR /app

RUN apt update && \
    apt install -y gcc build-essential ruby build-essential clamav clamav-daemon ruby-dev && \
    freshclam

RUN gem install bundler && bundle install

