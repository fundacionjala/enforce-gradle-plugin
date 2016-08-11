#!/bin/bash
set -ev
if [ "${TRAVIS_BRANCH}" = "master" ]; then
  ./gradlew bintrayUpload
fi
