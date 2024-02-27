@echo off

set "user=root"
set "server=45.81.235.52"
set "password=enF3XDmU"

plink.exe -ssh %user%@%server% -pw %password% -batch -m "./deployweb.txt"