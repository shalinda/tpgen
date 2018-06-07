1. Using the generator

To create a new module make a copy of the 'test' module in 'meta/test' and rename it to the module name you want. 
For example say 'simplereport'.

Change the settings in the 'module.properties' and add the sql in the 'module.sql'

Run 'ant gen' and input the module folder name you created.

The generator will create files under gen/simpleproject.

2. Modifying templates.

The Velocity templates for generated files are in the 'templates' folder. You can alter them to change the output.

To add new templates. Create a new template and add the corresponding item under the 'gen' task in the 'build.xml' file.

3. On version control

- Do add meta information for reuse. 
- Don't add generated files.