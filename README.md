# tpgen
Generates Code based input database

Overview
	-This project is used to generate presentation, bussiness logic, struts support class using the db
		metadata. Endless possibilities as long as its alligned with the db metadata
	-currently supports oracle 9i/ 10g, mysql 4,5, sql server anything else add the jar to lib

config.properties
Change build/config.properties to reflect
	-database info
	-table=<database table>
	-template=<name of the template to be used located inside the template dir>
	-output=<name of the output in in the gen folder>

	-className=<name of the class to generate>
	-module=<module name / package name>

build/ run
	-Ant should build it (JDK 1.8 recommended with ant 1.6.5+)
	-run bat will run and generate the output in the 'gen' dir, uses your properties from the 'build/config.properties'

Velocity
	-Templates should be written using velocity macros


Sample templates
	config.properties
	teamples/ui7


ENV Variables

TP_PROJECT_PATH - the environment variable where the project output exists
TP_MODULE - the module generated