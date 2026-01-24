#!/bin/bash

# Service names and paths
SERVICES=("product-catalog:command-api" "product-catalog:command-handler" "product-catalog:event-handler" "product-catalog:query-api")
BASE_DIR=$(pwd)
LOG_DIR="$BASE_DIR/logs"
PID_DIR="$BASE_DIR/pids"

mkdir -p "$LOG_DIR"
mkdir -p "$PID_DIR"

start_service() {
    local service=$1
    local service_name=$(echo $service | cut -d':' -f2)
    local pid_file="$PID_DIR/$service_name.pid"
    local log_file="$LOG_DIR/$service_name.log"

    if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
        echo "Service $service_name is already running (PID: $(cat $pid_file))"
    else
        echo "Starting $service_name..."
        nohup ./gradlew :product-catalog:$service_name:bootRun > "$log_file" 2>&1 &
        echo $! > "$pid_file"
        echo "$service_name started with PID $(cat $pid_file). Logs: $log_file"
    fi
}

stop_service() {
    local service=$1
    local service_name=$(echo $service | cut -d':' -f2)
    local pid_file="$PID_DIR/$service_name.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 $pid 2>/dev/null; then
            echo "Stopping $service_name (PID: $pid)..."
            # Since bootRun spawns a child process, we might need to be more aggressive or kill the gradle daemon carefully
            # Ideally, killing the gradle wrapper process should propagate if using --no-daemon, otherwise it kills the client only.
            # A more robust way for bootRun is checking jps or using kill tree.
            # Simplicity: Kill the PID recorded (likely the gradlew wrapper shell or java process).
            kill $pid
            
            # Wait for it to die
            sleep 2
            if kill -0 $pid 2>/dev/null; then
                echo "Force killing $service_name..."
                kill -9 $pid
            fi
            rm "$pid_file"
            echo "$service_name stopped."
        else
            echo "Service $service_name is not running (PID file exists but process dead)."
            rm "$pid_file"
        fi
    else
        echo "Service $service_name is not running."
    fi
}

status_service() {
    local service=$1
    local service_name=$(echo $service | cut -d':' -f2)
    local pid_file="$PID_DIR/$service_name.pid"

    if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
        echo "$service_name is RUNNING (PID: $(cat $pid_file))"
    else
        echo "$service_name is STOPPED"
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
