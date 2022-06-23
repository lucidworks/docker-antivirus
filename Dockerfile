FROM quay.io/podman/stable

ADD . /app
WORKDIR /app

RUN yum install -y make gcc-c++ coreutils clamav ruby-devel slirp4netns fuse-overlayfs

ADD registries.conf /etc/containers/registries.conf

RUN gem install bundler && bundle install
