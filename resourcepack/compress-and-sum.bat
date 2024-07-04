@echo off

set /p DUMMY=Continuer ce script implique lancer le script compress-and-sum.ps1 et executer ses commandes. Veuillez vous assurer que c'est bien ce que vous voulez faire et que le script n'a pas ete modifie en quelque maniere.

powershell -NoProfile -ExecutionPolicy Bypass -File "compress-and-sum.ps1"

pause