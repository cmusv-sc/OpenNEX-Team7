# OpenNEX-T7
OpenNEX Team 7 Penalty - Share software/workflow

## Prerequisites

* Sign up and sign in.
* Dummy services running together with application.

## Create Services

* Click "Services" on the navigation bar.
* Click "Add New" button.
* Input the information of the Tokenize Service as follows: Name - Tokenize, URL - http://localhost:9000/api/v1/tokenize.
* Add other related information if needed.
* Click "Submit" button.
* See the Tokenize Service in the service list.
* Add Map Service(Name - Map, URL - http://localhost:9000/api/v1/map), Sort Service(Name - Sort, URL - http://localhost:9000/api/v1/sort), Reduce Service(Name - Reduce, URL - http://localhost:9000/api/v1/reduce).

## Create Workflow

* Click "Workflows" on the navigation bar.
* Click "Add New" button.
* Input the information of the Word Count Workflow as follows: Name - Word Count.
* Drag Tokenize Service, Map Service, Sort Service, and Reduce Service into the "Selected services" in the right order.
* Add other related information if needed.
* Click "Submit" button.
* See the Word Count Workflow in the workflow list.

## Execute Workflow

* Click "Workflows" on the navigation bar.
* Click "Execute" button in the Word Count Workflow.
* Input "apple orange apple banana banana orange apple" as the Input content.
* Click "Execute" button.
* See the real-time execution console below together with the output "Output: apple 3 banana 2 orange 2 ".
* Click "Workflows" on the navigation bar.
* Click Word Count Workflow.
* See the detail and previous results.

## Change Service

* Click "Services" on the navigation bar.
* Click Tokenize Service in the service list.
* Click "Edit" button in the Tokenize Service.
* Change "Version" to "2.0".
* Click "Submit" button.
* Click "Home" on the navigation bar.
* See the notifcation of updated service which is used by the current user's workflow.
