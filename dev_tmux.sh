#!/bin/bash
########################################################################
# A helper script to start jetty and GWT codeserver in 2 separate panes
# in tmux. It's a life saver for me. I do not have to start the processes
# mistakenly in duplicate terminals.
#
#  Usage: dev_tmux.sh {start|stop|status|attach}
#
# - First cut: Google AI Gemini Pro 
# - Fixed deprecated tmux flags by Claude AI Sonnet 4.5 Jan-04-2026 
# - pass JAVA_HOME and PATH to tmux session Jan-07-2026 
# Update: remove exporting PATH, it breaks the script Jan-21-2026 
########################################################################
SESSION="gwt-dev"
MYDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

check_java_version() {
    java --version
    if (( $? != 0 )); then
        echo ""
        echo "**** JDK is old, does not support --version. aborting ..."
        echo "**** JAVA_HOME must be set accordingly ***"
        echo ""
        exit 1
    fi
    if [[ -z ${JAVA_HOME} ]]; then
        echo "JAVA_HOME env var not exit, aborting ..."
        exit 1
    fi
}
check_tmux() {
    if ! tmux -V >/dev/null 2>&1
    then
        echo "Please install tmux first ..., aborting ..."
        exit 1
    fi
}

start() {
    # --- 1. PRE-FLIGHT CHECKS (Only needed for start) ---
    check_java_version
    check_tmux

    tmux has-session -t $SESSION 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "⚠️  Session '$SESSION' is already running."
        echo "   Run '$0 attach' to connect, or '$0 stop' to kill it."
        exit 0
    fi

    echo "🚀 Starting GWT Dev Environment..."

    # --- 2. START SESSION ---
    tmux new-session -d -s $SESSION -n "GWT-Dev"

    # --- 3. CONFIGURE KILL SWITCH ---
    tmux bind-key K kill-session -t $SESSION

    # --- 4. PANE 1 (Top): JETTY ---
    tmux send-keys -t $SESSION "trap 'tmux kill-session -t $SESSION' EXIT" C-m

    # [FIX] Explicitly export the JAVA_HOME and path from the parent shell to this pane
    tmux send-keys -t $SESSION "export JAVA_HOME='$JAVA_HOME'" C-m

    tmux send-keys -t $SESSION "cd $MYDIR" C-m
    tmux send-keys -t $SESSION "./dev_run_jetty.sh || read" C-m

    # --- 5. SPLIT ---
    tmux split-window -v -t $SESSION

    # --- 6. PANE 2 (Bottom): CODESERVER ---
    tmux send-keys -t $SESSION "trap 'tmux kill-session -t $SESSION' EXIT" C-m

    # [FIX] Do the same for the CodeServer pane just in case
    tmux send-keys -t $SESSION "export JAVA_HOME='$JAVA_HOME'" C-m

    tmux send-keys -t $SESSION "cd $MYDIR" C-m
    tmux send-keys -t $SESSION "sleep 2; ./dev_run_codeserver.sh || read" C-m

    # --- 7. ATTACH ---
    tmux select-pane -t $SESSION -U
    tmux attach -t $SESSION
}


stop() {
    check_tmux
    # Simple, brutal kill. Does not depend on JDK.
    tmux kill-session -t $SESSION 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ Session '$SESSION' killed."
    else
        echo "⚠️  Session '$SESSION' not found."
    fi
}

status() {
    check_tmux
    tmux has-session -t $SESSION 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "🟢 Session '$SESSION' is RUNNING."
        tmux list-windows -t $SESSION
    else
        echo "🔴 Session '$SESSION' is NOT running."
    fi
}

dev_compile() {
    ${MYDIR}/compile.sh dev
    if (( $? != 0 )) ; then
        echo "❌ Dev Compilation failed..."
        echo ""
        exit 1
    fi
    echo ""
    echo "✅ Dev compilation successfull!"
}

production_compile() {
    ${MYDIR}/compile.sh production
    if (( $? != 0 )) ; then
        echo "❌ Production Compilation failed..."
        echo ""
        exit 1
    fi
    echo ""
    echo "✅ Production compilation successfull!"
    echo "🔴 This compilation cannot be used for GWT super dev mode"
}

usage() {
    echo " $0 [commands]
Where the commands are:
    dev_compile             - Compile in dev mode and exit
    x|dev_compile_and_start - Compile in dev mode and start GWT super dev mode
    production_compile      - Compile in production mode and exit
    start                   - Start super dev mode (requires dev compilation)
    stop                    - Stop tmux sessions if running
    attach                  - Attach to running super dev mode
    status                  - Check status of dev mode
To detach: CTRL+b d
To exit sessions: CTRL+b x in each pane

"
exit 0
}


case "$1" in
    dev_compile)
        dev_compile
        ;;
    production_compile)
        production_compile
        ;;
    dev_compile_and_start|x)
        dev_compile
        echo ""
        read -p "Press ENTER to start GWT super dev mode..."
        start
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    attach)
        check_tmux
        tmux attach -t $SESSION
        ;;
    *) 
        usage
        ;;
esac
