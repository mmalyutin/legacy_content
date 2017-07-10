#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ] ; do SOURCE="$(readlink "$SOURCE")"; done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

# Define
#SANDBOX_URL="site-jdf.rhcloud.com"
#SANDBOX_SSH_USERNAME="f35451447e0d4bfbaf37c8a039bb5e6a"
#SANDBOX_REPO="ssh://${SANDBOX_SSH_USERNAME}@${SANDBOX_URL}/~/git/site.git/"
#SANDBOX_CHECKOUT_DIR=$DIR/_tmp/sandbox

JBORG_USER="jdf"
JBORG_DIR="${JBORG_USER}/presentations"
JBORG_REPO="filemgmt.jboss.org:www_htdocs"

STAGING_URL="jboss.org/${JBORG_DIR}/stage"
STAGING_DIR="${JBORG_DIR}/stage"

PRODUCTION_DIR="${JBORG_DIR}"
PRODUCTION_URL="jboss.org/${PRODUCTION_DIR}"

shallow_clean() {
  echo "**** Cleaning site  ****"
  rm -rf $DIR/_site
  echo "**** Cleaning asciidoc cache  ****"
  rm -rf $DIR/_tmp/asciidoc
}

deep_clean() {
  echo "**** Cleaning site  ****"
  rm -rf $DIR/_site
  echo "**** Cleaning caches  ****"
  rm -rf $DIR/_tmp/lanyrd
  rm -rf $DIR/_tmp/remote_partial
  rm -rf $DIR/_tmp/datacache
  rm -rf $DIR/_tmp/restcache
  rm -rf $DIR/_tmp/asciidoc
}

sandbox() {
  deep_clean
  echo "**** Generating site ****"
  awestruct -Psandbox

  if [ ! -d "$SANDBOX_CHECKOUT_DIR/.git" ]; then
    echo "**** Cloning OpenShift repo ****"
    mkdir -p $SANDBOX_CHECKOUT_DIR
    git clone $SANDBOX_REPO $SANDBOX_CHECKOUT_DIR
  fi

  cp -rf $DIR/_site/* $SANDBOX_CHECKOUT_DIR/php


  echo "**** Publishing site to http://${SANDBOX_URL} ****"
  cd $SANDBOX_CHECKOUT_DIR
  git add *
  git commit -a -m"deploy"
  git push -f
  shallow_clean
}

production() {
  deep_clean
  echo "**** Generating site ****"
  awestruct -Pproduction

  echo "**** Publishing site to http://${PRODUCTION_URL} ****"
  rsync -Pqr --protocol=28 $DIR/_site/* ${JBORG_USER}@${JBORG_REPO}/${PRODUCTION_DIR}

  shallow_clean
}

staging() {
  deep_clean
  echo "**** Generating site ****"
  awestruct -Pstaging

  echo "**** Publishing site to http://${STAGING_URL} ****"
  rsync -Pqr --protocol=28 $DIR/_site/* ${JBORG_USER}@${JBORG_REPO}/${STAGING_DIR}

  shallow_clean
}


usage() {
  cat << EOF
usage: $0 options

This script publishes the JDF presentations, either to staging or to production

OPTIONS:
   -s      Publish staging version of the site to http://${STAGING_URL}
   -p      Publish production version of the site to http://${PRODUCTION_URL}
   -c      Clear out all caches
EOF
}

while getopts "spch" OPTION

do
     case $OPTION in
         s)
             staging
             exit
             ;;
         p)
             production
             exit
             ;;
         c)
             deep_clean
             exit
             ;;
         h)
             usage
             exit
             ;;
         [?])
             usage
             exit
             ;;
     esac
done

usage
