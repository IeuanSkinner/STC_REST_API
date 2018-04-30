How to deploy this Web App + REST API

Overview
I have used the Eclipse IDE along with Jersey to build this REST Web Serivce and an Apache Tomcat web server hosted @localhost to deploy. The reason for this is that it was the stack I used as un undergrad for a web application module I completed and so given the short notice to produce something decided to use this again, given the opportunity to develop a REST Web Service in the future I would undoubtedly use the Spring framework.

Pre-requisites:
	Database
	- I'm using MySQL to manage the database (USERS) which should contain the following (USER) table:

	CREATE TABLE IF NOT EXISTS USERS.USER (
		id INT(11) NOT NULL auto_increment,
		first varchar(255) NULL,
		last varchar(255) NULL,
		age varchar(255) NULL,
		location varchar(255) NULL,
		email varchar(255) NULL,
		PRIMARY KEY (id)
	)

	- Note: If you have setup user access controls for MySQL you will have to update the DB connection + 'username' and 'password' fields and pass these in. 

	Cryptography
	- I've implemented a public key cryptography method for encrypting and decryptying the user data. (Public key to encrypt, private to decrypt). In a real-world scenario I would obviously not share the private key but have done so for this example, you will have to update the path given to the keys to the absolute file path depending on your file-system. (Lines 145 & 168).

Once the aforementioned pre-reqs are taken care of you should be in a position to build the project and deploy it. I'm using Maven to manage this so you should simply be able to run:

mvn clean install

To build the project and run the tests that have been setup for the REST API which will walk through POST, GET and DELETE requests. 

To deploy the project as a web service I have used Apache Tomcat and have provided a bare bones webpage to handle creating users from a form (using AJAX from JQuery for submission). Setting this up should simply be a case of adding the built project as a resource to a server hosted at localhost.
