#!/bin/bash

# Service names and paths
# Service names and paths
SERVICES=(
    "product-catalog:command-api"
    "product-catalog:command-handler"
    "product-catalog:event-handler"
    "product-catalog:query-api"
    "inventory:command-api"
    "inventory:command-handler"
    "inventory:event-handler"
    "inventory:query-api"
    "cart:command-api"
    "cart:command-handler"
    "cart:event-handler"
    "cart:query-api"
    "order:command-api"
    "order:command-handler"
    "order:event-handler"
    "order:query-api"
)
BASE_DIR=$(pwd)
LOG_DIR="$BASE_DIR/logs"
PID_DIR="$BASE_DIR/pids"

mkdir -p "$LOG_DIR"
mkdir -p "$PID_DIR"

get_safe_name() {
    echo "$1" | tr ':' '-'
}

start_service() {
    local service=$1
    local safe_name=$(get_safe_name "$service")
    local pid_file="$PID_DIR/$safe_name.pid"
    local log_file="$LOG_DIR/$safe_name.log"

    if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
        echo "Service $service is already running (PID: $(cat $pid_file))"
    else
        echo "Starting $service..."
        nohup ./gradlew :$service:bootRun > "$log_file" 2>&1 &
        echo $! > "$pid_file"
        echo "$service started with PID $(cat $pid_file). Logs: $log_file"
    fi
}

stop_service() {
    local service=$1
    local safe_name=$(get_safe_name "$service")
    local pid_file="$PID_DIR/$safe_name.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 $pid 2>/dev/null; then
            echo "Stopping $service (PID: $pid)..."
            kill $pid
            
            # Wait for it to die
            sleep 2
            if kill -0 $pid 2>/dev/null; then
                echo "Force killing $service..."
                kill -9 $pid
            fi
            rm "$pid_file"
            echo "$service stopped."
        else
            echo "Service $service is not running (PID file exists but process dead)."
            rm "$pid_file"
        fi
    else
        echo "Service $service is not running."
    fi
}

status_service() {
    local service=$1
    local safe_name=$(get_safe_name "$service")
    local pid_file="$PID_DIR/$safe_name.pid"

    if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
        echo "$service is RUNNING (PID: $(cat $pid_file))"
    else
        echo "$service is STOPPED"
    fi
}

case "$1" in
    start)
        echo "Starting all services..."
        for s in "${SERVICES[@]}"; do
            start_service "$s"
        done
        ;;
    stop)
        echo "Stopping all services..."
        for s in "${SERVICES[@]}"; do
            stop_service "$s"
        done
        # Also kill any lingering gradle daemons to unlock files
        echo "Stopping Gradle daemons..."
        ./gradlew --stop
        ;;
    restart)
        $0 stop
        sleep 5
        $0 start
        ;;
    status)
        echo "Service Status:"
        for s in "${SERVICES[@]}"; do
            status_service "$s"
        done
        ;;
    monitor)
        echo "Monitoring logs (Ctrl+C to exit)..."
        tail -f "$LOG_DIR"/*.log
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status|monitor}"
        exit 1
        ;;
esac
