# Communication and Cognition (Comcog) Named Entity Recognition Methodology #

## Introduction ##

### Assumptions ###
The author assumes that the reader is familiar with statistical and rule based Natural Language Processing (NLP) in general, in particular, stand-off techniques and tools including those that underly the UIMA and GATE software platforms, where the original text is not altered, but annotations, or markups are created that refer back to positional or character-based offsets within the original document.  The author assumes the reader is familiar with NLP platforms that are built using a pipeline composed of annotators, where a representation of the input document and associated markups is passed from annotator to annotator.  Each annotator adds, edits or deletes the markups that refer back to the original text.  Each annotator handles a specific or atomic task that down-pipeline annotators rely upon. (This is the way of GATE and UIMA).  Describing algorithms that utilize this technology stack are best described by the sequence of annotators in general, only delving into specific annotator algorithm details where needed.
For these task, the author assumes the reader is familiar with clinical medical terminologies used for Natural Language Processing, and in particular the Unified Medical Language System (UMLS), and the International Classification of Functioning Disability and Health and (ICF).

## Prior Related Software ##

### Dictionary Lookup and Lexicon Sources ###
The dictionary lookup process involves loading all lexica into a hash table. Framework uses a lookup mechanism that starts with the span of a sentence, works from right to left (yes, right to left), that first uses all the tokens as a key to be looked up in the hash.  When failing, the left-most token is dropped and that key is looked up in the hash.  This continues until a key is found.  Once found, the tokens consumed change right side of the window to be searched.  This insures maximal span matches, favoring matches where the head of a term is found.  The English language is structured where the important parts of terms or the head a phrase or term is the last or right part. This right to left window technique favors capturing at least the important parts of terms when there is ambiguity. 

Framework comes with and runs with the following default lexica (unless otherwise tuned)
 
- 	UMLS SPECIALIST 2022 Lexicon
- 	Document Titles
- 	CCDA Section Names
- 	Page Header and Page Footer Evidence
- 	Common Clinical Slot Names
- 	Date and Time Terms
- 	Labs Terminology (from LOINC)   (not used for comcog)
- 	Blood Panel Terminology (from LOINC) (not used for comcog)
- 	Units of Measure Terminology
- 	Person Evidence Terms
- 	USPS Address Terms (not used for comcog)
- 	Clinical Demographics Terms  (not used for comcog)


|Number of Rows| File                                        | Description                                                                                                        |
| ------------ | ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
|              |                                             |               **General Ontology Dictionaries**                                                                      |
|         7439 | 001_TopExternalFactors.lragr                |  Top External Factors were terms extracted from 26 seed UMLS concepts that had external factors as a semantic type |
|        36168 | 002_TopPersonFactors.lragr                  |  Derived Person Factor inflected forms from 32 seed UMLS concepts                                                  |
|         9825 | 003_TopActivitiesAndParticipation.lragr     |  Derived Activities and Particiaption inflected forms from 17 seed UMLS concepts                                   |
|        19657 | 004_TopBehavior.lragr                       |  Dervied Behavior inflected forms from 31 seed UMLS concepts                                                       |
|        27176 | 005_TopActivitiesOfDailyLiving.lragr        |  Derived ADL inflected forms from 6 seed UMLS Concepts                                                             |
|           41 | 006_TopGeneralTasksAndDemands.lragr         |  Derived General tasks and demands from 1 UMLS Concept                                                             |
|         4494 | 007_TopLearningAndApplyingKnowledge.lragr   |  Derived Learning and Applying Knowledge from 2 UMLS Concepts                                                      |
|         5169 | 008_TopCommunicationActivities.lragr        |  Derived Communication Activities from 8 UMLS Concepts                                                             |
|        20567 | 009_TopCognitiveActivities.lragr            |  Derived Cognitive Activities from 5 UMLS Concepts                                                                 |
|        35869 | 010_TopVerbNetClasses.lragr                 |  IPIR, Comcog inflected forms from VerbNet Classes that were manually annotated with IPIR and/or ComCog subclasses |
|           36 | 011_TopIPIRNominalizations.lragr            |  Nominalizations of IPIR Classes from chosen VerbNet Classes                                                       |
|              |                                             |  Lexical Variants Generated from existing forms                                                                    | 
|        12630 | lvg_Verbs_inflected.lragr                   |  lvg generated inflections from the top seed files                                                                 |
|         7825 | lvg_verbs_DerivationsAndNomilizations.lragr |  lvg generated derived forms from the top seed files                                                               |
|         2947 | lvg_verbs_Synonyms.lragr                    |  lvg generated synonym forms from the top seed files                                                               |
|        12692 | lvg_verbs_spellingVariants_inflections_FACTS.lragr  |  lvg gnenerated spelling variants and inflections from the top seed files                                  |
|              |                                             |                        **Communication and Cognition related Dictionaries**                                          |
|          164 | ComCogUMLSTermsAndVariants.lragr            |  ComCog UMLS inflected forms with ComCog subclassifications                                                        |
|         6146 | ComCogVerbNetCategories.lragr               |  Verbnet ComCog inflected forms with curated ComCog sub-classifications                                            |
|         1755 | MoreComProducing.lragr                      |  Additional ComCog inflected forms from manual training annotations not found in other sources                     |
|          115 | missingTerms.lragr                          |  Variants manually created from existing forms that otherwise would have been missed                               |
|           33 | mentalFunctionTriggerTerms.lragr            |  Triggering function mentions like "able to"                                                                       |
|          349 | SSAListingTerms.lragr                       |  Comcog, IPIR terms from manual training annotations not in other sources                                          |
|              |                                             |             **Interpersonal Interactions and Relationship related Dictionaries**                                     |
|         1238 | IPIRInteractions.lragr                      |  IPIR (and not IPIR) forms from traversals through thesauri around seed IPIR interaction defintional terms         |
|          112 | informalRelationships.lragr                 |  Informal Relationships from traversals through thesauri around seed IPIR informal relationship definital terms    |
|          118 | intimateRelationships.lragr                 |  Intimate Relationships from traversals through thesauri around seed IPIR intimate relationship definital terms    |
|          119 | strangerRelationships.lragr                 |  Stranger Relationships from traversals through thesauri around seed IPIR stranger relationship definital terms    |
|          198 | OtherIPIRVerbs.lragr                        |  IPIR forms identified from VerbNet that got missed on the first pass thru VerbNet                                 |
|           14 | IPIRExceptions.lragr                        |  Confounding IPIR-like terms that are not IPIR terms                                                               |
|              |                                             |            **Dictionaries employed for Ontology related evidences**                                                 |
|          131 | denies.lragr                                |  A bunch of ways that negation evidence is seen                                                                    |
|          734 | emotion.lragr                               |  Emotions pulled from UMLS sources                                                                                 |
|         1075 | workRoles.lragr                             |  Work Roles, Occupations from BLS, SSA, ONGIG sources                                                              |


For the ComCog task, additional lexica were curated, with two approaches used to gather the additional terminology.  
Terms thought to be the most general Comcog classes from the MF Ontology were gathered. Each of these were used as seed terms to traverse through the 2022 UMLS to gather all descendent terms to be incorporated into the terminology used for lookup.   Communication and Cognition activities is poorly covered in the UMLS, and this process was disappointing, only picking up ICF Activities and Participation subclasses, for the most part.  
It was recognized that communication and cognition activities evidence are likely to be verbs and adverbs.  An effort was made to classify the University of Colorado’s VerbNet classes into IPIR and Comcog categories manually.  That effort was expanded to have each of the Comcog categories (about 162 classes) to further be subclassified into the 20 subclasses manually.  This was done by 3 domain expert annotators and one software engineer (me).  This was a daunting task involving classifying over 5000 lemmas resulting from the 162 classes.  There was reason for classifying at the lemma level rather than class level.  The classes were too inclusive, with most classes involving lemmas (in VerbNet terminology: members) from the classes that didn’t really fit the semantic relatedness criteria we were classifying at.  We had time constraints so the effort was a quick and rough effort with the understanding that we’d go back and do better curation when time allowed.  For the most part, the VerbNet Comcog tagged lemmas is the terminology employed.  

# Pipelines, Algorithms, and Rules #

## Pre-processing Annotators ##
The IPIR NER built upon the Mental Functioning NER’s pipeline, which, in turn, is built upon a general NLP pipeline to segment text into constituent document-decomposition parts, which is built upon a UIMA infrastructure.  

## Document Decomposition Pipeline  ##
The Document Decomposition Pipeline identifies Sentences, Terms, Tokens, Section Names, Section Zones within the text.
  
 **Important: Term Lookup **

Of note here: the secret sauce if you will:  the terms identified are based first on dictionary/lexica lookup, where care has been taken to craft the content of the dictionaries, and where each of the dictionary entries contain term attributes including semantic categorization(s) for each term and the pedigree of where the term was sourced from. It is the semantic categorizations for each term identified within the text that is paramountly used down pipeline.  While there are many ways to identify terms within the text, how and what is contained within the dictionaries is the salient part to each task.

## Mental Functioning NER Pipeline and Annotator ##
The mental functioning NER pipeline is a series of annotators to identify Mental Functioning Ontology (MFO) concepts within text.  As such, it includes annotators that create Behavior Evidence, Emotion Evidence, and (financial, social, institutional) Support Evidence from the terms that have these semantic categories prior to an annotator that creates Mental Functioning Evidence and Mental Functioning Mention Markups.  The Mental Functioning Ontology Annotator identifies and creates Mental Functioning Evidence Mark ups from terms that have semantic categories that generalize up to Mental Functioning within the MFO Ontology. 
 
Specifically, a Mental Functioning Mention encapsulates the outer spans of any MFO evidence found within the scope of a sentence[1] or slot:value[2], or check-box[3].  Note that MFO mentions will not be made from within the bounds of a section name.  
This is over-generative. Filters are applied to weed out spurious mentions.  Underutilized but built in is a filter to filter out any mentions found within document types (identified from the document-decomposition pipeline) that indicate this document should not be processed.  Currently, the pipeline does not identify any forbidden document types.  In prior tasks, documents identified a-priori from their source HER were tagged as administrative or clinical, and those documents tagged as administrative were filtered out. The plan is when page and document classification become available, this feature will become important, particularly to ignore mentions that come from templated text coming from SSA forms found within the corpora we’ve been processing.

## Ad-hoc Filters ##
There are additional ad-hoc filters applied as well. 
 
Documents that include SSA forms, where the templated text contains what look like mental functioning mentions, but are not, because the mentions are within templated text that are most likely to be instructions on how to fill out the form.  Such mentions that included see [x] within the scope of the sentence are filtered out.

Similarly, mentions that can be attributed to the author of the document, not the client or patient are filtered out. These mentions have evidence that includes the use of pronouns like we and our along with use of pronouns like you, your, his, her, she, and him.  Such mentions are also tagged with Provider attribution as a feature at the term and sentence level.

## Customization for inFACT Processing ##
The Mental Functioning Ontology pipeline is being embedded within the inFACT workflow, where it is important that the same segmentation is used across all the inFACT processing.  As such, the functionality being described here assumes the input is GATE/Interlingua formatted files that include sentence mark-ups. An additional preprocessing annotator was added to the MFO pipeline that swaps in the inFACT sentences for the framework sentences.  Also, upstream inFACT processing will have created IPIR_yes, IPIR_no, Comcog_yes, and Comcog_no annotations within the bounds of each sentence via trained LLM models.  

For the purposes of inFACT, it is these inFACT labeled _yes mark-up is then further processed down pipeline.  The MFO evidence and mentions that overlays these markups are available for the downstream processing.  Outside of inFACT, equivalent MFO markups that generalize up to IPIR and ComCog mentions have been generated and can be used independent of inFACT processing. 

## The Comcog Sub-categorization Annotator ##
The Comcog Sub-Categorization annotator further sets 20 attributes to each Comcog_yes.

For each Comcog_yes sentence span, all the MFO generated evidence that generalizes to Comcog Activities from the Ontology, along with behavior and Support evidence that cover that span are gathered and if there is any of these found, further processing is done.

There are 20 attributes set: 

- 	ICF d110_d129_Purposeful Sensory Experiences
- 	ICF d130_d159_Basic Learning
- 	ICF d160_Focusing Attention
- 	ICF d163_Thinking
- 	ICF d166_Reading
- 	ICF d170_Writing
- 	ICF d172_Calculating
- 	ICF d175_Solving Problems
- 	ICF d177_Making Decisions
- 	ICF d179_Applying Knowledge Other
- 	ICF d210_d220_UndertakingTasks
- 	ICF d230_Carrying Out Daily Routine
- 	ICF d240_Handling Stress
- 	ICF d310_d329_Receiving Communication
- 	ICF d330_d349_Producing Communication
- 	ICF d350_d369_Conversation and Discussion
- 	Adaptation
- 	Applied Memory 
- 	Pacing
- 	Persistence

There is a rule that if there was no evidence for an Comcog_yes mention found, an ICF d179_Applying Knowledge Other is created to satisfy that criteria.

## Non Relevant Evidence ##

There is a check to throw out mentions that are not IPIR, ComCog, or Mental Functioning related due to some known context.  For example, if the sentence has 
the word *invoice*, or *billing* in it, this is not an *MFO mention*.  There are 1389 terms marked as *NotMFO* which includes *case number*, *department of social services*, *social security act* along with terms like *swelling* and *please*.

These non-relevant evidence terms were garnered and added from frequency distributions of terms appearing in false positive mentions and never appearing in true positive or false negative mentions. 

The Comcog annotator creates evidence markups for each term it deems Comcog evidence, and the sentence span with the evidence in it (that has not been filtered out by non-relevant counter evidence) is used to create one or more of the Comcog sub category mark up.  This is in addition to having the Comcog_yes attributes set.  There are two reasons for the creation of these additional markups:   

- Visualization:  in GATE and UIMA, you cannot see attributes.  Making them markups with spans allow one to turn that layer on or off to see each of the attributes.
- Evaluation:  The existing evaluation functionality counts at the mark-up level, not at the attribute level.  It was easier to create markups than to alter the complicated evaluation code.

Each of the 17 of the 20 Comcog annotators work the same way.  They look for terms that the lexicon has categorized with the Comcog subcategory within the span of the Comcog_yes sentence.  When found, a subcategory evidence markup is created and a subcategory markup is created for the span of the Comcog_yes.  Only one evidence or mention can be created for each possible subcategory.  

Three of the four SSA mapping subcategories are treated differently.  The methods that work on these subcategories are run after the other 17 have been run. 
A prerequisite for the applied memory category being applied is that the Comcog_yes span would also have to already been marked with a d163, d172, d177, d210, d230, d310, d330, or d350 mention before looking for any terms that would have an applied memory category from the lexicons.

Pacing and Persistence categories have the prerequisites that d130, d160, d160, d166, d170, d172, d175, d179, or d240 mentions within the span of the Comcog_yes span would have to be present before looking for any terms that have to do with pacing or persistence categories from the lexicons.

