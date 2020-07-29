#!/bin/bash
. ./.env
. checkenv.sh


function remove () {
  for SERVICE in "${SERVICES[@]}"
  do
    echo ----------[ removing $SERVICE ]----------
    cd $SERVICE
    serverless remove
    cd ..
  done
}


function domain () {
  cd resources
  serverless delete_domain
  cd ..
}


# remove frontend apps and data
aws s3 rm s3://${JOBS_LIST_APPS_BUCKET} --recursive
aws s3 rm s3://${JOBS_LIST_SOURCE_DATA_BUCKET} --recursive
aws s3 rm s3://${JOBS_LIST_LOG_DATA_BUCKET} --recursive

# remove resources and functions
SERVICES=(resources)
remove

# delete jobs-service API domain
domain

# delete jobs database table
aws dynamodb delete-table --table-name ${JOBS_LIST_JOBS_TABLE}-dev

# delete user pool domain
#. ./cognito.sh teardown
