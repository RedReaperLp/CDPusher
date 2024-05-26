@echo off

set "user=root"
set "server=45.81.235.52"
set "password=pw.txt"

"C:\Program Files\Putty\plink.exe" -ssh %user%@%server% -pwfile %password% -batch -m "./deployweb.txt"