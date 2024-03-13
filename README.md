# Ecological Mental Functioning Ontology
**An Ecological Mental Functioning Ontology** 


# Repository Contents #
| FileName     | Description  |
| ------------ |------------- |
|emfo.owl      | The official RDF/XML OWL file           |
|emfo.owl.json | The OWL file rendered in JSON for WebVOWL visualization |
|EMFOConceptsAndRelationships.xlsx     | The spreadsheet with the ontology in it |
|EMFOCON.CSV   | The Classes sheet in CSV format         |
|EMFOREL.CSV   | The Relationships sheet in CSV format   |
|EMFODEFC.CSV  | The Classes Definitions in CSV format   |
|EMFODEFR.CSV  | The Relations Definitions in CSV format |
|LICENSE.MD    | The License agreement for the EMFO's use|
|EMFO_Picture.pptx| A visual Representation of the top Levels of the Ontology |


## Summary
The Ecological Mental Functioning Ontology (EMFO) is a representation of mental functioning based on the Ecological Model of Mental Functioning (EMMF) (reference) that integrates concepts taken from the International Classification of Functioning, Disability and Health (ICF) (World Health Organization, 2001); person-environment-activities transactive models (Dean et al., 2019; Dunn et al., 1994; Law et al., 1996), social-ecological theories (Stineman & Streim, 2010; Van Assche et al., 2019); and Open System Theory (von Bertalanffy, 1968).   
<p> 
What is Mental functioning? Is a question that has been difficult to garner a consensus to answer.
Human mental functioning, represented as observable behavior, is understood as a transaction between the person and their intrinsic attributes, the context and environment where functioning takes place, and the nature and type of activities the person engages in.  We have defined mental functioning as the “mind-directed observable external behaviors that represent what people actually do (or do not do) in response to decisions occurring during real-time transactions as they unfold moment by moment in daily life and over time” (Sacco et al., 2023).  
<p>
The EMFO differentiates the concept of mental functioning from mental functions. The ICF has conceptual coverage of mental functioning but there are gaps. The understanding of what is involved with mental functioning has evolved in the last 23 years since its release. The language of mental functions is included within SNOMED, MeSH, and MedRE but little coverage of mental functioning. 
The EMFO can be used conceptually to understand and explain behaviors indicative of mental functioning from either the individual’s perspective or the perspective of others, such as a clinical observer.  For the purpose of our use case (described below), we adopt the perspectives of a clinician observer, such as a behavioral health specialist or rehabilitation professional who document functioning in patients’ clinical records. 
<p>
The EMFO perspective represents observations that are a point-in-time, and written in the language of functioning.  Impairments that may or may not affect functioning, are classified as attributes of the person, rather than diagnoses or pathologies.  
The EMFO is positioned as a domain ontology which includes classes and mostly hierarchical relationships between the classes, with some part-of and non-hierarchical relationships.
<p>
The EMFO adheres to most of the prescribed ontology and terminology desiderata including no implied relationship cycles and having unique class names. The EMFO includes class and relationship definitions. The EMFO semantics is noted to include upper levels of the ontology providing the role of mental functioning categories to subsequent descendant classes. The EMFO semantics are similar to that adopted by the UMLS.
<p>
The initial use case was to support the United States Social Security Administration (SSA) disability determination process by leveraging clinical natural language processing methods to identify mental functioning mentions within clinical documents to provide evidence for or against disability benefits adjudication. Domain experts created guidelines, based on the EMFO perspectives, then manually annotated a large, diverse national clinical record corpus. This was an iterative process providing feedback that further refined the guidelines and ontology as the domain experts came to consensus of what should and should not be included as a mention. The subsequent annotations have also provided training and validation for rule-based and BERT based BioLSTM NERs extracting functioning. The EMFO includes a derived terminology, initially built from identifying relevant top level UMLS concepts and all their descendants. This has been augmented by adding a curated set of classes from VerbNet, where our domain experts classified VerbNet classes with EMFO categories for communication and cognition (ComCog) and interpersonal interactions and relationships (IPIR). The terminology has been further tuned by including terms from the manual annotation effort which had no representation in our existing sets and the removal of terms from our terminology where the NERs identified too many false positives mentions.
<p>
We are using the EMFO as a rhetorical device to foster a dialog to garner consensus.
<p>
The Ontology has been packaged into a set of csv and excel files, an RDF/XML Owl file, and a webVowl json file.


![image](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/EMFO_Picture.png)
Figure 1: Visualization of the Upper Levels of the Ecological Mental Functioning Ontology
<p>

# About Us #
This work stems from the Epidemiology and Biostatistics section of the Rehabilitation Medicine Department, National Institutes of Health (NIH) Clinical Center that uses the ICF as a framework to develop analytic tools for clinical natural language processing and the development of functional assessment measures.  The interdisciplinary team is intentionally composed of a diverse set of scientists, including those in health informatics, mathematics, statistics, computer science, computational linguistics, public health, and healthcare.
  
The ontology team, in particular, is made up of individuals representing computer science, occupational and physical therapy, medicine, and psychiatry, with consults from speech and language pathology. 

# Publications #
Sacco, M. J., Divita, G., & Rasch, E. (2023). Development of an ontology to characterize mental functioning. Disabil Rehabil, 1-10. https://doi.org/10.1080/09638288.2023.2252337

# License #
See the [***License file***](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/LICENSE.txt)


# Contact Us #
[TBD]


# References #
- Dean…
- Dunn W, Brown C, McGuigan A. The ecology of human Performance A framework for considering the effect of context [article. Am J Occup Ther. 1994; Jul48(7):595–607. doi: 10.5014/ajot.48.7.595.
- Law…
- Sacco, M. J., Divita, G., & Rasch, E. (2023). Development of an ontology to characterize mental functioning. Disabil Rehabil, 1-10. https://doi.org/10.1080/09638288.2023.2252337
- Van Assche, K., Verschraegen, G., Valentinov, V., & Gruezmacher, M. (2019). The social, the ecological, and the adaptive. Von Bertalanffy's general systems theory and the adaptive governance of social-ecological systems [Article]. Systems Research and Behavioral Science, 36(3), 308-321. https://doi.org/10.1002/sres.2587
- von Bertalanffy, L. (1968). General System Theory: Foundations, Development, Applications. Aldine Publishing Co.
- World Health Organization. International classification of functioning, disability and health: ICF. Geneva: world Health Organization; 2001.

