###QAM2 — QAM2 Task 1: Java Application Development

###Purpose of application
The purpose of this application is to provide a GUI based scheduling desktop application.

###Author Information
author: luke williams
contact: lwi1731@wgu.edu
application version: 1.0
date: 08/21/2023

###IDE and java module Information
IntelliJ IDEA 2022.3.2 (Community Edition)
Build #IC-223.8617.56, built on January 25, 2023
Runtime version: 17.0.5+1-b653.25 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
java version "17.0.6" 2023-01-17 LTS
Java(TM) SE Runtime Environment (build 17.0.6+9-LTS-190)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.6+9-LTS-190, mixed mode, sharing)
javafx: openjfx-17.0.2
mysql connector: mysql-connector-java-8.0.33

###Additional report
For the custom report, I chose to display customers and their respective division. I chose to have SQL do the work for this. This SQL query retrieves data from two tables, customers and first_level_divisions, by performing an inner join based on the relationship between their Division_ID columns. The query returns the Customer_Name from the customers table and the Division from the first_level_divisions table for each record that matches the join condition.

###How to run the program
As the program starts, a login screen is presented. The user will be required to have a valid username and password that matches information in a mysql database. This program requires java 11 and has not been tested with any other jvm.
