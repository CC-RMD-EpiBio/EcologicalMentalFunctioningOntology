# Project Directory Description

This project directory includes all the modules which make up the EMFO NER Project specifically.

You may notice that there is an ordering to the directories.  The numbers that preface each directory name enforces that logical ordering. 

Those directories that start with 00 are direct copies (a white lie) of modules from the framework-legacy code.  They are included here because we currently have no nexus hosting mechanism to stuff these modules into to reference as a dependency to be pulled by maven.  

(the white lie - I renamed the directory name of these modules to enforce the ordering for this project.  The directory names are slightly differently prefaced in framework-legacy, but the namespaces are not changed.)

# Directory Contents:

 | Directory Name     | Description |
 | ------------------ | ----------- |
 |00_0_parent         | This is framework-legacy's parent pom                                                    |
 |00_1_ThirdParty-jars| These are framework legacy dependencies that were not published in a public nexus server |
 |00_2_framework-jars | These are the framework legacy jars, that would have been built if it were cloned out of the framework-legacy repo.  These are the guts of the underlying framework that the projects are built upon. |
 |                    |             |
 | 82_01_data         | A directory filled with example data |
 | 82_01_libs         | This is where the executable jars are placed when made by maven (this will get made when building) |
 | 82_01_parent       | This is this project's parent pom.  It does also reference the framework-legacy's parent pom |
 | 82_01_resources    | This is where the lexica (dictionary) resources are for this project |
 | 82_03_type0descriptors | This is where the definitions for the UIMA and GATE based labels reside |
 | 82_05_annotators   | This is where the source code for the UIMA annotators for this project reside |
 | 82_08_pipelines    | This is where the source code for the UIMA pipeline definitions reside |
 | 82_09_applications | This is where the source code for the applications/mains for the general EMFO NER project reside |
 | 82_09_03_inFACT_IPIR_And_ComCog_Categories_Applications | This is where the source code for the applications/mains for the ComCog and IPIR subcategories project reside |
 | 82_10_doc          | Some useful documentation |

# General Comments About This Project

This project is built from java-nlp-framework, a suite of functionality, that, itself is built upon [UIMA](https://uima.apache.org/)  2.9.  Framework-legacy is an evolution of [V3-NLP Framework](https://pubmed.ncbi.nlm.nih.gov/27683667/); which was built specifically for use within the Veteran Administration's [VINCI environment](https://www.hsrd.research.va.gov/for_researchers/vinci/workspace.cfm) to process, at scale, VA clinical documents.  

Note:  java-framework relies upon [uimaFIT](https://uima.apache.org/uimafit.html), to define the pipelines in code rather than through the myriad of configuration files.  You will not find the UIMA configuration files in the above projects, except to retrofit and work with other UIMA based tools. 

This project is built using Maven and Java.  Maven reactors should kick off the cascade of sub-projects to be made.

Note: See [Java-NLP-Framework/readme.md](https://github.com/CC-RMD-EpiBio/java-nlp-framework#readme) for details.


## The Jars Compiled from Java-NLP-Frameowrk distributed within this Repo
The following jars are distributed here (we do not have a nexus service to host these directly).  As part of the build, these jars get locally installed into your *.m2* directory as part of the 00_2_framework-jars project called from the reactor pom. 

- 01-nlp-resources-2021.02.0.jar
- 03-nlp-type-descriptors-2021.02.0.jar
- 04.0-nlp-util-2021.02.0.jar
- 04.1-nlp-vUtil-2021.02.0.jar
- 05-nlp-annotators-2021.02.0.jar
- 06-nlp-marshallers-2021.02.0.jar
- 07.0-nlp-pUtils-2021.02.0.jar
- 08-nlp-pipelines-2021.02.0.jar 
- 82_07-mentalFunctionOntologyNER-nlp-pUtils-2022.03.0.jar

## Third Party Software and Jars Also Not Distributed via a Nexus Service
This project, when built from this repo, relies on a few jars that cannot be found via a nexus service.  They are found within the 00_1_ThirdParty-Jars project and are built from the reactor pom. 

# Building
For reference sake, $EMFO_NER_HOME equates to where this repo got cloned out then changed to the ./project directory.
  
Install the parent,parent pom first.  This is the pom in $EMFO_NER_HOME/00_0_parent.  This parent pom is a copy of the Java-NLP-Framework's parent pom. All is built upon the Java-NLP-Framework, so referencing that project's parent pom is a must.
(Skip this if you've prior installed the Java-NLP-Framework project).
<pre> 
cd $EMFO_NER_HOME
cd 00_0_parent
mvn install
cd ..
</pre> 
 
Once Java-NLP-Framework's parent pom has been installed, there is a EMFO NER parent pom to install.
<pre>
cd $EMFO_NER_HOME
cd 820_mentalFunctionOntologyNER/82_01_parent
mvn install
cd ..
</pre> 

Once the  EMFO NER parent pom has been installed, go back and build the set of projects.  There is a reactor pom in the $EMFO_NER_HOME directory that refers to each project to be built.

 
### Example Build from the command line:

> cd $EMFO_NER_HOME
> mvn install

</pre>
When the process is complete, the output from the process should look something like this:
<pre>

[INFO] 
[INFO] --- install:3.1.2:install (default-install) @ EMFO_NER-Top ---
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] unmavenedJars 2022.09.0 ............................ SUCCESS [  5.792 s]
[INFO] 02-thirdParty 2022.09.0 ............................ SUCCESS [  0.104 s]
[INFO] 00_framework-jars 2022.09.0 ........................ SUCCESS [  1.018 s]
[INFO] gov.nih.cc.rmd.framework: 82_01-mentalFunctionOntologyNER-parent 2022.03.0 SUCCESS [  0.012 s]
[INFO] 82_01-mentalFunctionOntologyNER-resources 2022.03.0  SUCCESS [  1.126 s]
[INFO] 82_03-mentalFunctionOntologyNER-type-descriptors 2022.03.0 SUCCESS [ 20.611 s]
[INFO] 82_05-mentalFunctionOntologyNER-annotators 2022.03.0 SUCCESS [  6.409 s]
[INFO] 82_06-mentalFunctionOntologyNER-nlp-marshallers 2022.03.0 SUCCESS [  1.550 s]
[INFO] 82_08-mentalFunctionOntologyNER-pipelines 2022.03.0  SUCCESS [  1.353 s]
[INFO] 82_09-mentalFunctionOntologyNER-Application 2022.03.0 SUCCESS [ 38.164 s]
[INFO] 82_09_03_inFACT_IPIR_And_ComCog_Categories_Application 2022.03.0 SUCCESS [ 29.516 s]
[INFO] 82_00-mentalFunctionOntologyNER-Top 2022.03.0 ...... SUCCESS [  0.008 s]
[INFO] EMFO_NER-Top 2022.03.0 ............................. SUCCESS [  0.010 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:45 min
[INFO] Finished at: 2024-09-29T11:25:11-04:00
[INFO] ------------------------------------------------------------------------

</pre>
