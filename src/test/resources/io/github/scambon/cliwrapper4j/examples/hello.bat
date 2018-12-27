@echo off
echo Here we are!
ping 127.0.0.1 -n %1 > nul
set /p name="What's your name? "
ping 127.0.0.1 -n %1 > nul
echo Hello, %name%!
echo This is the error stream 1>&2