REM Copyright 2018-2019 Sylvain Cambon
REM 
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM 
REM     http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

@echo off
echo Here we are!
ping 127.0.0.1 -n %1 > nul
set /p name="What is your name? "
ping 127.0.0.1 -n %1 > nul
echo Hello, %name%!
echo This is the error stream 1>&2