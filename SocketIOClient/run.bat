@ECHO OFF
SET /A first_id=%1
::ECHO %first_id%
SET /A clients_per_app=%2
::ECHO %clients_per_app%
SET /A apps=%3
::ECHO %apps%

setlocal enabledelayedexpansion

FOR /L %%X IN (1,1,%apps%) DO (
  SET cmd=java -jar socket-io-client.jar -n %clients_per_app% -i !first_id!
  ECHO !cmd!
  START !cmd!
  SET /A tmp=!first_id!
  SET /A first_id=!tmp! + %clients_per_app%
)