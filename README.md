# BPM-RPA-System
This repository is part of my bachelorthesis. It contains a rudimentry prototype of a BPM-RPA system. 
It consists of the BPM process engine [Activiti](https://github.com/Activiti/Activiti) and the robotic process automation tool [Robot Framework](https://github.com/robotframework/robotframework).
This prototype was implemented in the [Visual Studio Code](https://code.visualstudio.com/) IDE

## Installation
1.  Make shure you have Java 21 and Python 2.11.0 installed and added to PATH when using Windows
2.  Clone this repository into your local repository
3.  Run followin commands to install Robot Framework:
     - run `pip install robotframework`
     - run `pip install --upgrade pip setuptools wheel`
     - run `pip install rpaframework`

## Run the Application
Go to the 'bpm_rpa' directory and run following commands: 
- `mvn clean install`
- `mvn spring-boot:run`

## Communicate with the REST API

> [!IMPORTANT]
> Basic authorization needed.
> 
> **Username**: admin
> 
> **Password**: admin

-  `GET http://localhost:8080/processes`

   Returns a list of all runnable processes

-  `GET http://localhost:8080/start-process/{key}`

   Starts a process instance of the process identified by the key

-  `GET http://localhost:8080/process-instances`

   Returns all runnung process instances

-  `GET http://localhost:8080/get-all-tasks`

   Returns all open user tasks

-  `GET http://localhost:8080/get-task/{process-instance-id}`

   Returns the open user task for this specific process instance

-  Completes this specific task. Variables are given in the body. This is an example to complete the user task for the process "Bewerbungsprozess".
```
   POST http://localhost:8080/complete-task/{tsak-id}
   Header: {Content-Type: application/json}
   Body:   {"accepted": "J"}
```

-  `GET http://localhost:8080/get-completed-processes`
   Returns all completed processes

## Run the Exaple Process 'Bewerbungsprozess'
1. Start the application
2. Go to the 'ressource/bewerbung/testBewerbung' where three examplatory processes are stored. One for each outcome of the process.
3. Edit one of the file and enter a valid e-mail adress
4. Copy the file into 'ressource/bewerbung'
5. Start process. Use the commands in the chapter above to communicate with the process engine and to controll the process flow.
6. Afte the process finished successfully check the mail account for the acception / rejection mail
