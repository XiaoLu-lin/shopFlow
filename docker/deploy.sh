#!/bin/bash
# ShopFlow 服务器端部署脚本（非 Docker）

set -e

APP_DIR=${APP_DIR:-/opt/shopflow}
JAR_NAME=shopflow.jar
DIST_DIR=$APP_DIR/dist
PORT=6914
LOG_FILE=$APP_DIR/logs/start.log

echo "========================================================"
echo "  ShopFlow Deploy - $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================================"

echo "[1/4] Ensuring directories exist ..."
mkdir -p $APP_DIR/logs $APP_DIR/backups $DIST_DIR

echo "[2/4] Backing up old jar ..."
if [ -f "$APP_DIR/$JAR_NAME" ]; then
    BACKUP_NAME="${JAR_NAME%.jar}_$(date +%Y%m%d%H%M%S).jar.bak"
    cp $APP_DIR/$JAR_NAME $APP_DIR/backups/$BACKUP_NAME || true
    ls -t $APP_DIR/backups/*.jar.bak 2>/dev/null | tail -n +4 | xargs rm -f || true
fi

echo "[3/4] Restarting backend process ..."
OLD_PID=$(lsof -ti tcp:$PORT || true)
if [ -n "$OLD_PID" ]; then
    kill -9 $OLD_PID
    echo "      Killed PID: $OLD_PID"
    sleep 2
fi

cd $APP_DIR
nohup java \
    -Dfile.encoding=UTF-8 \
    -Djava.security.egd=file:/dev/./urandom \
    -jar $JAR_NAME \
    > $LOG_FILE 2>&1 &

NEW_PID=$!
echo "      Started PID: $NEW_PID"
sleep 15
if ! kill -0 $NEW_PID 2>/dev/null; then
    echo "ERROR: Backend failed to start. Check $LOG_FILE"
    exit 1
fi

echo "[4/4] Reloading Nginx ..."
if command -v nginx >/dev/null 2>&1; then
    nginx -s reload || true
    echo "      Nginx reloaded by 'nginx -s reload'."
elif [ -x /www/server/nginx/sbin/nginx ]; then
    /www/server/nginx/sbin/nginx -s reload || true
    echo "      Nginx reloaded by BaoTa binary path."
elif command -v bt >/dev/null 2>&1; then
    bt reload || true
    echo "      Nginx reloaded by 'bt reload'."
elif [ -x /etc/init.d/nginx ]; then
    /etc/init.d/nginx reload || true
    echo "      Nginx reloaded by init script."
else
    echo "      WARNING: nginx reload command not found. Reload it manually if needed."
fi

echo "========================================================"
echo "  Deploy finished at $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================================"
