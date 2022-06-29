FROM quay.io/podman/stable:v4.1.1

ADD . /app
WORKDIR /app

ADD groupcfg.sh groupcfg.sh

RUN yum upgrade -y && \
    yum install -y make gcc-c++ coreutils clamav clamav-update ruby-devel python-pip && \
    groupadd jenkins -g 7000 && \
    useradd jenkins -d /home/jenkins -m -u 7000 -g 7000 -s /bin/bash && \
    pip install PyYAML && \
    bash groupcfg.sh && \
    freshclam

ADD registries.conf /etc/containers/registries.conf

USER 7000

RUN sudo gem install bundler && \
    sudo bundle install
