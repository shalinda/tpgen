#!/bin/bash
function perform() {
    mkdir -p ${TP_PROJECT_PATH}/front/src/app/views/${TP_MODULE}s
    cp gen1/model/${TP_MODULE}.ts ${TP_PROJECT_PATH}/front/src/app/model
    cp gen1/models/${TP_MODULE}.js ${TP_PROJECT_PATH}/backend/models
    cp gen1/controllers/${TP_MODULE}.controller.js ${TP_PROJECT_PATH}/backend/controllers
    cp gen1/views/${TP_MODULE}* ${TP_PROJECT_PATH}/front/src/app/views/${TP_MODULE}s/
    cp gen1/service/${TP_MODULE}.service.ts ${TP_PROJECT_PATH}/front/src/app/service/    
}

if [  -z "${TP_PROJECT_PATH}" ]; then
    echo 'TP_PROJECT_PATH not set'
elif [  -z "${TP_MODULE}" ]; then
    echo 'TP_MODULE not set'
elif [ -d "${TP_PROJECT_PATH}/front/src/app/views/${TP_MODULE}" ]; then
    if [ "$1" == "force" ]; then    
        echo "...overide merge"
        perform1\
    else
        echo [${TP_PROJECT_PATH}/front/src/app/views/${TP_MODULE}s] exists use force option
    fi
else
    echo "...initial merge"
    perform
fi
