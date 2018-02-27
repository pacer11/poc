FROM centos:latest
RUN yum update -y && yum upgrade -y
RUN yum install java-1.8.0-openjdk-devel
ADD /home/controller-user/flink-1.4.1 /flink-1.4.1
WORKDIR /flink-1.4.1

