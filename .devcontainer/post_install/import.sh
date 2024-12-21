#!/bin/bash

if [ "$#" -ne 2 ]; then
  echo "Error: Two parameters are required."
  echo "Usage: $0 <user.name> <user.email>"
  exit 1
fi

git config --global user.name $1
git config --global user.email $2

gpg --quiet --delete-keys --yes 2> /dev/null
gpg --quiet --delete-secret-keys --yes 2> /dev/null

gpg --quiet --import public.key 2> /dev/null
if [ $? -ne 0 ]; then
  echo "Error: Failed to import public key."
  exit 1
fi

gpg --quiet --import private.key 2> /dev/null
if [ $? -ne 0 ]; then
  echo "Error: Failed to import private key."
  exit 1
fi

key=$(gpg --quiet --list-secret-keys --keyid-format=long | grep 'sec' | awk '{print $2}' | cut -d'/' -f2 | head -n 1)
if [ -n "$key" ]; then
  git config --global user.signingkey "$key"
  git config --global commit.gpgsign true
  git config --global tag.gpgSign true

  rm public.key private.key

  echo "Git configuration updated with key: $key"
else
  echo "No GPG secret key found."
  exit 1
fi