#!/bin/bash

# Variables
REPONAME="remote-monitoring-agent"
REPOURL="https://github.com/KeyMoad/remote-monitoring-agent.git"
SERVICE_NAME="remon-agent"
PYTHON_V="python3.8"


function uninstall() {
    # Stop and disable service
    systemctl stop $SERVICE_NAME >/dev/null 2>&1
    systemctl disable $SERVICE_NAME >/dev/null 2>&1

    # Remove files
    rm -rf /opt/$REPONAME /etc/systemd/system/$SERVICE_NAME.service

    # Reload daemon
    systemctl daemon-reload >/dev/null 2>&1
}


function install() {
    if [ -x "/opt/$REPONAME" ]; then
        uninstall;
    fi

    # Clone git repositorys
    echo -e "Downloading latest version of agent ... \n"
    git clone --quiet $REPOURL /opt/$REPONAME/ > /dev/null && \
    cd /opt/$REPONAME/ && \
    git checkout main > /dev/null && \
    rm -rf ./app && \
    mv ./api/* ./api/.pass ./ && \
    rm -rf ./api && \
    cd - >/dev/null 2>&1

    # Install python venv
    echo -e "Installing virtual environment ... \n"
    $PYTHON_V -m pip install --quiet -U virtualenv || exit 1
    $PYTHON_V -m venv /opt/$REPONAME/.venv || exit 1


    # Config .env
    echo -e "Configuring environments ... \n"
    mv /opt/$REPONAME/example.env /opt/$REPONAME/.env

    # Insatll requirements
    echo -e "Install and initialize requirements ... \n"
    . /opt/$REPONAME/.venv/bin/activate > /dev/null
    /opt/$REPONAME/.venv/bin/python3 -m pip install --quiet -U pip > /dev/null
    /opt/$REPONAME/.venv/bin/python3 -m pip install --quiet -r /opt/$REPONAME/requirements.txt > /dev/null


    # Install service
    echo -e "Setup $SERVICE_NAME.service ... \n"
    sed -i "s/pathtodir/opt\/$REPONAME/g" /opt/$REPONAME/$SERVICE_NAME.service /opt/$REPONAME/starter.sh /opt/$REPONAME/.env > /dev/null
    sed -i "s/nametodir/$REPONAME/g" /opt/$REPONAME/.env > /dev/null
    mv /opt/$REPONAME/$SERVICE_NAME.service /etc/systemd/system/$SERVICE_NAME.service > /dev/null
    systemctl daemon-reload && systemctl enable $SERVICE_NAME >/dev/null 2>&1 && systemctl start $SERVICE_NAME > /dev/null
}


function help() {
    echo "Installer 1.0.0 (Installer for Remote Monitoring App)"
    printf "Usage:\n %s \n\n" "$0 [arg]"
    echo "Options:"
    printf "help          %s \n\n" "      Show list of arguments and usage informations."
    printf "install       %s \n\n" "      Install the RMAgent on your mechine."
    printf "update        %s \n\n" "      Update RMAgent. Please use it if your agent version is not latest!"
    printf "uninstall     %s \n\n" "      Uninstall the RMAgent service and all dependencies from your mechine."
}

case "$1" in
    "install" | "update")
        echo "Installing started ..."
        install;
    ;;
    "uninstall")
        local DO_THE_UNINSTALL=""
        until [[ "$DO_THE_UNINSTALL" =~ ^(yes|YES|y|Y|no|NO|n|N)$ ]]; do
            read -e -p "Are you sure you want to uninstall RMAgent Service [yes/no]? " DO_THE_UNINSTALL
        done
        if [[ "$DO_THE_UNINSTALL" =~ ^(yes|YES|y|Y)$ ]]; then
            uninstall;
        fi
    ;;
    "help")
        help;
    ;;
    *)
        if [[ $# == 0 ]]; then
            echo -e "Please use one of the arguments below \n"
            sleep 1
        else
            echo -e "Unknown argument: $1 \n"
            sleep 1
        fi

        help;
    ;;
esac