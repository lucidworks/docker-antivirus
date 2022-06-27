FROM quay.io/podman/stable:v4.1.1

ADD . /app
WORKDIR /app

ADD groupcfg.sh groupcfg.sh

RUN echo "zchunk = False" >> /etc/dnf/dnf.conf && \
    yum upgrade -y && \
    yum install -y make gcc-c++ coreutils clamav ruby-devel && \
    groupadd jenkins -g 7000 && \
    useradd jenkins -d /home/jenkins -m -u 7000 -g 7000 -s /bin/bash && \
    bash groupcfg.sh

ADD registries.conf /etc/containers/registries.conf

RUN gem install bundler && bundle install

USER 7000
