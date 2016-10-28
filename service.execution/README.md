# Execution Service

##Installation
This service is written in Java. Therefore you need an actuel version of the Java Virtual Machine (version 1.8 or higher). Please make sure that this requirement is fulfilled!

Checkout this project and go to the folder with the name [execution/target/](https://github.com/Ciro14/Elefant/tree/master/execution/target). This file bundles all things you need.

You can start this service from your command line. Just type the Following command.

    java -jar execution-0.0.1-SNAPSHOT.jar

This application uses the port 8080 by default. If already an other application listening on this port, you can specify the port using an additional parameter.

    java -jar execution-0.0.1-SNAPSHOT.jar --server.port=1234

##Example use

This example uses Postman to access the API of this service. You can install it from [here](https://www.getpostman.com/). Start Postman and click "Import". Then select the file [Elefant.postman_collection.json](https://github.com/Ciro14/Elefant/tree/master/execution/example) in the folder [execution/example](https://github.com/Ciro14/Elefant/tree/master/execution/example). After this, you should see a folder called "Elefant" in your collections at the left side, containing a sub folder "Execution" with two predefined requests:

######init

This requests sends the model of the whole system to this service. This must be done first, so that it is known how the system is structured and which features exists. The system in this example exists of an infrastructure with two virtual machines. You can find more detailed informations in the documentation of the [featuremodel](https://github.com/Ciro14/Elefant/tree/master/featuremodel).

######update

This request updates the model. The service try to figure out the steps needed for adapting the system and sends it back as response. Each step is a simple HTTP-Call. You have to provide the logic for adapting your system and take care that it can be called over this adresses. But for this example you don't need to do something, all things are pre configured.

Let's send something to the service. If it is not running, start the service by executing the steps from above. Open the "init" request in postman and click "Send". You should get a response with the status code 200. If not, make sure that the port on that the sevice is listening and the port to that you are sending the request are the same.
After this, open and execute the "update" Request. This call disables the second virtual machine. You should get a response containing on activity with the url "https://infrastructure.com/vm/cf091c5b-b529-43fc-b2d6-a3e70df7a258/deactivate". The UUID of the second machine is cf091c5b-b529-43fc-b2d6-a3e70df7a258.
