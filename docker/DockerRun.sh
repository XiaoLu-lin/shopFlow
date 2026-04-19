#=====================================================================================
#=================================1、定义初始化变量======================================
#=====================================================================================

#操作/项目路径(Dockerfile存放的路径)
BASE_PATH=/opt/projects/litemall-plus

#项目名称
SERVER_NAME=litemall-plus

#获此项目的取容器id
CID=$(docker ps -a | grep -w "$SERVER_NAME" | awk '{print $1}')

#获取此项目的镜像id
IID=$(docker images | grep -w "$SERVER_NAME" | awk '{print $3}')

#=====================================================================================
#=========================2、移动jenkins已经构建好的jar包到Dockerfile所在目录=========================
#=====================================================================================

# 查找源jar文件名，进行重命名，最后将源文件移动到Dockerfile文件所在目录
function transfer(){
    echo "=========================>>>>>>>移动jenkins构建好的jar到Dockerfile所在目录，并重命名为order-meal.jar"

        mv /root/.jenkins/workspace/litemall-plus/litemall-all/target/litemall-all-0.1.0-exec.jar /opt/projects/litemall-plus/litemall-plus.jar

    echo "=========================>>>>>>>迁移完成Success"

}

#=====================================================================================
#==================================3、构建最新镜像=======================================
#=====================================================================================

# 构建docker镜像
function build(){

    #无论镜像存在与否，都停止原容器服务，并移除原容器服务
    echo "=========================>>>>>>>停止$SERVER_NAME容器，CID=$CID"
    docker stop $CID

    echo "=========================>>>>>>>移除$SERVER_NAME容器，CID=$CID"
    docker rm $CID

    #无论如何，都去构建新的镜像
    #构建新的镜像之前，移除旧的镜像
    if [ -n "$IID" ]; then
        echo "=========================>>>>>>>存在$SERVER_NAME镜像，IID=$IID"


        echo "=========================>>>>>>>移除老的$SERVER_NAME镜像，IID=$IID"
        docker rmi $IID

        echo "=========================>>>>>>>构建新的$SERVER_NAME镜像，开始---->"
        cd $BASE_PATH
	docker build -t $SERVER_NAME .
	echo "=========================>>>>>>>构建新的$SERVER_NAME镜像，完成---->"

    else
        echo "=========================>>>>>>>不存在$SERVER_NAME镜像，构建新的镜像，开始--->"
        cd $BASE_PATH
        docker build -t $SERVER_NAME .
        echo "=========================>>>>>>>构建新的$SERVER_NAME镜像，结束--->"
    fi
}

#=====================================================================================
#==============================4、最后运行docker容器，启动服务===============================
#=====================================================================================

# 运行docker容器
# 先备份老的jar包
# 再移动新的jar包到Dockerfile文件所在目录
# 接着，构建新的镜像
# 最后运行最新容器，启动服务
function run(){
    transfer
    build

    docker run -p 6914:6914 --restart=always --name $SERVER_NAME \
	-v /opt/projects/litemall-plus/logs:/logs \
	-v /opt/projects/litemall-plus/backup:/backup \
	-v /opt/projects/litemall-plus/storage:/storage \
	-v /opt/projects/litemall-plus/cert:/opt/projects/litemall-plus/cert \
	-d $SERVER_NAME

}

#入口
run

