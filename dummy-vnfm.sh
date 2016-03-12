#!/bin/bash

_openbaton_base="/opt/openbaton"
_dummy_vnfm_base="${_openbaton_base}/dummy-vnfm-amqp/"
source ${_dummy_vnfm_base}/gradle.properties

_version=${version}

_openbaton_config_file=/etc/openbaton/openbaton.properties


function check_rabbitmq {
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
	ps -aux | grep -v grep | grep rabbitmq > /dev/null
        if [ $? -ne 0 ]; then
            echo "rabbitmq is not running, let's try to start it..."
            start_activemq_linux
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
	ps aux | grep -v grep | grep rabbitmq > /dev/null
        if [ $? -ne 0 ]; then
            echo "rabbitmq is not running, let's try to start it..."
            start_activemq_osx
        fi
    fi
}

function check_already_running {
        result=$(screen -ls | grep dummy-vnfm | wc -l);
        if [ "${result}" -ne "0" ]; then
                echo "dummy-vnfm is already running.."
		exit;
        fi
}

function start {

    if [ ! -d ${_dummy_vnfm_base}/build/  ]
        then
            compile
    fi

    check_rabbitmq
    check_already_running
    if [ 0 -eq $? ]
        then
	    #screen -X eval "chdir $PWD"
	    # TODO add check if the session openbaton already exists, else start the dummy-vnfm in a new screen session.
	    #
	    # At the moment the dummy-vnfm starts automatically in a second window in the openbaton session screen
	    pushd "${_openbaton_base}/nfvo"
	    screen -S openbaton -p 0 -X screen -t dummy-vnfm java -jar "../dummy-vnfm-amqp/build/libs/dummy-vnfm-amqp-${_version}.jar"
	    popd
	    #screen -c .screenrc -r -p 0
    fi
}

function stop {
    if screen -list | grep "openbaton"; then
	    screen -S openbaton -p 0 -X stuff "exit$(printf \\r)"
    fi
}

function restart {
    kill
    start
}


function kill {
    if screen -list | grep "openbaton"; then
	    screen -ls | grep openbaton | cut -d. -f1 | awk '{print $1}' | xargs kill
    fi
}


function compile {
    ./gradlew build -x test 
}

function tests {
    ./gradlew test
}

function clean {
    ./gradlew clean
}

function end {
    exit
}
function usage {
    echo -e "Open-Baton dummy-vnfm\n"
    echo -e "Usage:\n\t ./dummy-vnfm.sh [compile|start|stop|test|kill|clean]"
}

##
#   MAIN
##

if [ $# -eq 0 ]
   then
        usage
        exit 1
fi

declare -a cmds=($@)
for (( i = 0; i <  ${#cmds[*]}; ++ i ))
do
    case ${cmds[$i]} in
        "clean" )
            clean ;;
        "sc" )
            clean
            compile
            start ;;
        "start" )
            start ;;
        "stop" )
            stop ;;
        "restart" )
            restart ;;
        "compile" )
            compile ;;
        "kill" )
            kill ;;
        "test" )
            tests ;;
        * )
            usage
            end ;;
    esac
    if [[ $? -ne 0 ]]; 
    then
	    exit 1
    fi
done

