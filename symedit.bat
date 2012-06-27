@echo off


setlocal
set jvmargs=

:setupJVMArgs
if ""%1""=="""" goto doneSetupJVMArgs
if not ""%1:~0,1""=="-" goto doneSetupJVMArgs
set jvmargs=%jvmargs% %1
shift
goto setupJVMArgs

:doneSetupJVMArgs

run-java %jvmargs% kanga.kcae.view.swing.SymbolEditorFrame %*

