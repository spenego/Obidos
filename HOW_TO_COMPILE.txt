How to Compile Obidos
=====================
The sources can be compiled with jdk >= 17

Requirements
============
    - Chrome
    - Make sure jDK is >= 17. (Code was tested using JDK17.) 
    - JAVA_HOME must be set properly
    - pom file is pom.xml
    - GWT 2.12.2 (latest) - maven will pull this as it is configured in pom.xml.

Compiling
==========
There are 3 modes for compiling: production, qa and development

Example:
$ ./compile.sh
ERROR: Missing argument
Usage: ./compile.sh [dev|qa|prod]

Production mode
===============
./compile.sh prod

This will produce target/Obidos-1.0.1.war file.
This war file has to be deplyed to jetty webapps directory as ROOT.war.

Development & QA modes
=====================
Note that dev & qa modes differ only in that qa mode updates the database schema.
( 'qa' mode is required only if there has been any updates to the schema.
 The current schema version is 72.)

GTW has 2 dev mode, classic and 'so called' modern. The classic mode uses 
a built in jetty process. It only works with jdk 1.8. This mode is 
deprecated and does not work with modern browsers. The 'modern' dev mode 
is also called super dev mode. In this mode, jetty and codeserver run as 
completely separate processes. GWT's instructions on how to use this mode 
do not work at all. We did a lot of experiments, pain and sufferings to 
make it work. Calude, Gemini AI do not know anything beyond what the GWT's
doc says. None works of course. Now we've got a soulution which works.

- Use jdk17 

- If you use Eclipse, must turn off auto compilation from
Project -> Build Automatically

- Compile first
 ./compile.sh dev

- Start dev mode. It runs a script with tmux. This script is written by
Gemini AI Pro from my instructions. It saves lot of pain to start, stop
scripts manually in 2 terminals.
  ./dev_tmux.sh
  Usage: ./dev_tmux.sh {start|stop|status|attach}

 ./dev_tmux.sh start

This script starts tmux:
 - At top pane, it starts jetty using the script ./dev_run_jetty.sh
 - At bottom pane, it starts codeserver using the script ./dev_run_codeserver.sh

The following steps must be done the very first time:
 - Look at codeserver URL
 - Point chrome to the URL
 - Drag the 'Dev Mode on' and 'Dev Mode off' to bookmark bar. It must be done.
 GWT doc says in newer version of GWT, it's not necessary. It's a false
 statement.

Follow these steps from now on:
 - point chrome  to jetty URL, which is http://127.0.0.1:8888
 - Click on 'Dev Mode off', Then click on 'Dev Mode on'
 - Click on 'Compile'. It will compile the code. If certain files
 are changed, repeat the process, it will only compile the changed
 code.
 - To close jetty and codeserver, use the command 'CTRL+B x' for each
 pane
--
Updated Mar-08-2026 
