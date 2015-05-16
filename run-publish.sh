#!/bin/bash
set -ev
if [ "${TRAVIS_BRANCH}" = "master" ]; then
  echo "${TRAVIS_BUILD_NUMBER}"
else
  echo "else: ${TRAVIS_BRANCH}"
fi

