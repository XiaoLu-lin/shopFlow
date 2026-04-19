#1.指定基础镜像，并且必须是第一条指令
FROM java:8

#2.指明该镜像的作者和其电子邮件
MAINTAINER "927069313@qq.com"

#3.添加时区
ENV TZ=Asia/Shanghai

#添加jar
ADD litemall-plus.jar litemall-plus.jar
EXPOSE 6914
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./litemall-plus.jar"]
