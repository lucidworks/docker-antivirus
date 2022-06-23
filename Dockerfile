FROM quay.io/podman/stable:v4.1.1

ADD . /app
WORKDIR /app

RUN yum upgrade -y && \
    yum install -y make gcc-c++ coreutils clamav ruby-devel

ADD registries.conf /etc/containers/registries.conf

RUN gem install bundler && bundle install
