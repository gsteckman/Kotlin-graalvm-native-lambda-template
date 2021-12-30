FROM amazonlinux:2

RUN yum -y update \
    && yum install -y tar gzip bzip2-devel ed gcc gcc-c++ \
    less libcurl-devel openssl openssl-devel readline-devel xz-devel \
    zlib-devel glibc-static libcxx libcxx-devel llvm-toolset-7 zlib-static \
    && rm -rf /var/cache/yum

ENV GRAAL_VERSION 21.2.0
ENV GRAAL_FOLDERNAME graalvm-ce-java11-${GRAAL_VERSION}
ENV GRAAL_FILENAME graalvm-ce-java11-linux-amd64-${GRAAL_VERSION}.tar.gz
RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} | tar -xvz
RUN mv $GRAAL_FOLDERNAME /usr/lib/graalvm
RUN rm -rf $GRAAL_FOLDERNAME

RUN /usr/lib/graalvm/bin/gu install native-image
RUN ln -s /usr/lib/graalvm/bin/native-image /usr/bin/native-image

ENV JAVA_HOME /usr/lib/graalvm

WORKDIR /var/build/libs
ENTRYPOINT ["/usr/lib/graalvm/bin/native-image"]
