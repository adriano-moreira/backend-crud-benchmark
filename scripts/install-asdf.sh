#!/bin/bash
set -ex

#install asdf with git
if [ ! -d ~/.asdf ]; then
  git clone https://github.com/asdf-vm/asdf.git ~/.asdf --branch v0.14.1

  #configure .bashrc
  echo '. "$HOME/.asdf/asdf.sh"' >> ~/.bashrc 
  echo '. "$HOME/.asdf/completions/asdf.bash"' >> ~/.bashrc 
  . ~/.bashrc

  #config .zshrc if exists
  if [ -f ~/.zshrc ]; then
    echo '. "$HOME/.asdf/asdf.sh"' >> ~/.zshrc
    echo 'fpath=(${ASDF_DIR}/completions $fpath)' >> ~/.zshrc
    echo 'autoload -Uz compinit && compinit' >> ~/.zshrc
  fi

fi

