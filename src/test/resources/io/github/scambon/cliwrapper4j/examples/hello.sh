#!/bin/bash

echo Here we are!
sleep $1
echo 'What is your name?'
read name
sleep $1
echo Hello, $name!
echo This is the error stream 1>&2