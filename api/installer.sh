#!/bin/bash

# Variables
arglen=$#
REPONAME="remote-monitoring-agent"
REPOURL="https://github.com/KeyMoad/remote-monitoring-agent.git"
SERVICE_NAME="remon-agent"
IP=$(hostname -i)
HOSTNAME=$(hostname -A)
PYTHON_V="python3.8"


function uninstall() {
    # Stop and disable service
    systemctl stop $SERVICE_NAME >/dev/null 2>&1
    systemctl disable $SERVICE_NAME >/dev/null 2>&1

    # Remove files
    rm -rf /opt/$REPONAME /etc/systemd/system/$SERVICE_NAME.service;

    # Reload daemon
    systemctl daemon-reload >/dev/null 2>&1
}


function install() {
    if [ -x "/opt/$REPONAME" ]; then
        uninstall;
    fi

    # Clone git repositorys
    git clone --quiet $REPOURL /opt/$REPONAME/ > /dev/null
    mv /opt/$REPONAME/api /opt/$REPONAME/ && rm -rf /opt/$REPONAME/app /opt/$REPONAME/api

    # Config .env
    mv /opt/$REPONAME/example.env /opt/$REPONAME/.env


    # Install python venv
    $PYTHON_V -m pip install --quiet -U virtualenv >/dev/null 2>&1
    $PYTHON_V -m venv /opt/$REPONAME/.venv >/dev/null 2>&1


    # Insatll requirements
    . /opt/$REPONAME/.venv/bin/activate > /dev/null
    /opt/$REPONAME/.venv/bin/python3 -m pip install --quiet -U pip > /dev/null
    /opt/$REPONAME/.venv/bin/python3 -m pip install --quiet -r /opt/$REPONAME/requirements.txt > /dev/null


    # Install service
    sed -i "s/pathtodir/opt\/$REPONAME/g" /opt/$REPONAME/$SERVICE_NAME.service /opt/$REPONAME/start.sh /opt/$REPONAME/.env > /dev/null
    sed -i "s/nametodir/$REPONAME/g" /opt/$REPONAME/.env > /dev/null
    mv /opt/$REPONAME/$SERVICE_NAME.service /etc/systemd/system/$SERVICE_NAME.service > /dev/null
    systemctl daemon-reload && systemctl enable $SERVICE_NAME >/dev/null 2>&1 && systemctl start $SERVICE_NAME > /dev/null
}


function help() {
    echo -e "use this script with options:\ninstall <token-name> <token>\nupdate <token-name> <token>\nuninstall\nhelp for this"
}

case "$1" in
    "help")
        help;
    ;;
    "install" | "update")
        echo "Installing started ..."
        install;
    ;;
    "uninstall")  echo  "Sending SIGINT signal"
        local DO_THE_UNINSTALL=""
        until [[ "$DO_THE_UNINSTALL" =~ ^(yes|YES|y|Y|no|NO|n|N)$ ]]; do
            read -e -p "Are you sure you want to uninstall RMAgent Service [yes/no]? " DO_THE_UNINSTALL
        done
        if [[ "$DO_THE_UNINSTALL" =~ ^(yes|YES|y|Y)$ ]]; then
            uninstall;
        fi
    ;;
    *)
        echo "unknown argument: $1"
        help;
    ;;
esac