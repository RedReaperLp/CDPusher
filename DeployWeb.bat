@echo off

set "user=root"
set "server=45.81.234.99"
set "password=XJSNnAt8"

plink.exe -ssh %user%@%server% -pw %password% -batch -m "./deployweb.txt"