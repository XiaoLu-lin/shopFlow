#!/bin/bash
BUILD_ID=DONTKILLME
echo "=========================>>>>>>>移动jenkins构建好的jar到Dockerfile所在目录，并重命名为shopflow.jar"

mv /root/.jenkins/workspace/shopflow/shopflow-all/target/shopflow-all-0.1.0-exec.jar /opt/projects/shopflow/shopflow.jar

echo "=========================>>>>>>>迁移完成Success"

echo "=========================>>>>>>>启动项目"
name=$(lsof -i:6914|tail -1|awk '"$1"!=""{print $2}')
if [ -z $name ]
then
	echo "=======================启动shopflow========================="
	cd /opt/projects/shopflow/
	nohup java -Dfile.encoding=UTF-8 -jar shopflow.jar > logs/start.log &
	exit 0
fi
id=$(lsof -i:6914|tail -1|awk '"$1"!=""{print $2}')
kill -9 $id
echo "==============================6914以关闭  kid:$id  启动shopflow===================="
cd /opt/projects/shopflow/
nohup java -Dfile.encoding=UTF-8 -jar shopflow.jar > logs/start.log &

exit 0 
