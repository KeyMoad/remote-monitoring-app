#!/usr/bin/env bash

#set -e

# Variables
arglen=$#
GITTOKENNAME=$2
GITTOKEN=$3
REPONAME=""
REPOURL=""
SERVICE_NAME="remon-agent"
PANEL_IP=
IP=$(hostname -i)
HOSTNAME=$(hostname -A)
PYTHON_V="python3.8"


function uninstall() {
    # Stop and disable service
    systemctl stop $SERVICE_NAME >/dev/null 2>&1
    systemctl disable $SERVICE_NAME >/dev/null 2>&1

    # Remove files
    rm -rf /opt/$REPONAME /etc/systemd/system/$SERVICE_NAME.service /var/log/$SERVICE_NAME.log;

    # Reload daemon
    systemctl daemon-reload >/dev/null 2>&1
}


function install() {
    if [ -x "/opt/$REPONAME" ]; then
        uninstall;
    fi


    # Clone git repositorys
    git clone --quiet $REPOURL /opt/$REPONAME/ > /dev/null


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


if [ $arglen -eq 0 ]; then
    help;
elif [ $arglen -eq 1 ]; then
    if [ $1 == "uninstall" ]; then
        uninstall;
    elif [ $1 == "help" ]; then
        help;
    elif [ $1 == "install" ] || [ $1 == "update" ];then
        echo "lost token name and token for this option $1"
        help;
    else
        echo "unknown argument: $1"
        help;
    fi
elif [ $arglen -eq 3 ]; then
    if [ $1 == "install" ] || [ $1 == "update" ]; then
        install;
    else
        echo "unknown argument: $1"
        help;
    fi
else
    echo "unknown argument: $1"
    help;
fi
