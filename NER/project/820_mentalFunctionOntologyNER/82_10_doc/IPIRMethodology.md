# Inter-Personal Interaction and Relationship (IPIR)  Named Entity Recognition Methodology #

## Introduction ##

### Assumptions ###

The author assumes that the reader is familiar with statistical and rule based Natural Language Processing (NLP) in general, in particular, stand-off techniques and tools including those that underly the UIMA and GATE software platforms, where the original text is not altered, but annotations, or markups are created that refer back to positional or character-based offsets within the original document.  The author assumes the reader is familiar with NLP platforms that are built using a pipeline composed of annotators, where a representation of the input document and associated markups is passed from annotator to annotator.  Each annotator adds, edits or deletes the markups that refer back to the original text.  Each annotator handles a specific or atomic task that down-pipeline annotators rely upon. (This is the way of GATE and UIMA).  Describing algorithms that utilize this technology stack are best described by the sequence of annotators in general, only delving into specific annotator algorithm details where needed.

For these task, the author assumes the reader is familiar with clinical medical terminologies used for Natural Language Processing, and in particular the Unified Medical Language System (UMLS), and the International Classification of Functioning Disability and Health and (ICF).

### Dictionary Lookup and Lexicon Sources ###

The dictionary lookup process involves loading all lexica into a hash table. Framework uses a lookup mechanism that starts with the span of a sentence, works from right to left (yes, right to left), that first uses all the tokens as a key to be looked up in the hash.  When failing, the left-most token is dropped and that key is looked up in the hash.  This continues until a key is found.  Once found, the tokens consumed change right side of the window to be searched.  This insures maximal span matches, favoring matches where the head of a term is found.  The English language is structured where the important parts of terms or the head a phrase or term is the last or right part. This right to left window technique favors capturing at least the important parts of terms when there is ambiguity. 

Framework comes with and runs with the following default lexica (unless otherwise tuned)
 
- 	UMLS SPECIALIST 2022 Lexicon
- 	Document Titles
- 	CCDA Section Names
- 	Page Header and Page Footer Evidence
- 	Common Clinical Slot Names
- 	Date and Time Terms
- 	Labs Terminology (from LOINC)   (not used for ipir)
- 	Blood Panel Terminology (from LOINC) (not used for ipir)
- 	Units of Measure Terminology
- 	Person Evidence Terms
- 	USPS Address Terms (not used for ipir)
- 	Clinical Demographics Terms  (not used for ipir)


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


# Pipelines, Algorithms, and Rules #

## Pre-processing Annotators ##

The IPIR NER built upon the Mental Functioning NER’s pipeline, which, in turn, is built upon a general NLP pipeline to segment text into constituent document-decomposition parts, which is built upon a UIMA infrastructure.  

## Document Decomposition Pipeline  ##

The Document Decomposition Pipeline identifies Sentences, Terms, Tokens, Section Names, Section Zones within the text.  

### Important: Term Lookup  ###

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

## The IPIR Sub-categorization Annotator ##

The IPIR Sub-Categorization annotator further sets 8 attributes to each IPIR_yes.
For each IPIR_yes sentence span, all the MFO generated evidence that generalizes to IPIR Activities from the Ontology, along with IPIR Participation evidence, behavior and Support evidence that cover that span are gathered and if there is any of these found, further processing is done.
There are 6 attributes set: 

- ICF d710-d729 General Interpersonal Interactions
- ICF d730 Relating with Strangers
- ICF d740 Formal Relationships
- ICF d7400 Relating with Persons in Authority
- ICF d750 Informal Social Relationships
- ICF d760 Family Relationships
- ICF d770 Intimate Relationships
- ICF d779 Particular interpersonal relationships, other specified and unspecified

There had been a rule that if there was no evidence for an IPIR_yes mention, AND there was one or less evidence of a person (pronouns, roles, person names, etc.) it was assumed that a provider was the author of the mention, and that the provider had the role of one in authority, so thus marked.  However, it was observed that these mentions were contained to only some observation around dress and hygiene, so this rule was backed off because it produced too many false positives in our training and test sets.

The annotation guidelines specify that at least one of these attributes get set for an IPIR_yes mention.  If no evidence of any of these above attributes were found within an IPIR_yes, span, the d779 other attribute gets set to satisfy that criteria.

## Non Relevant Evidence ##
There is a check to throw out mentions that are not IPIR, ComCog, or Mental Functioning related due to some known context.  For example, if the sentence has the word invoice, or billing in it, this is not an MFO mention.  Within the IPIR terminologies, there are 162 terms marked as not IPIR related. More examples include balance support, math support, and with respect to.  There are an additional 1389 terms marked as NotMFO which includes case number, department of social services, social security act along with terms like swelling and please.

These non-relevant evidence terms were garnered and added from frequency distributions of terms appearing in false positive mentions and never appearing in true positive or false negative mentions. 
The IPIR annotator creates evidence markups for each term it deems IPIR evidence, and the sentence span with the evidence in it (that has not been filtered out by non-relevant counter evidence) is used to create one or more of the IPIR sub category mark up.  This is in addition to having the IPIR_yes attributes set.  There are two reasons for the creation of these additional markups:   

- Visualization:  in GATE and UIMA, you cannot see attributes.  Making them markups with spans allow one to turn that layer on or off to see each of the attributes.
- Evaluation:  The existing evaluation functionality counts at the mark-up level, not at the attribute level.  It was easier to create markups than to alter the complicated evaluation code.

ICF d710-d729 General Interpersonal Interaction markups are created when there is Emotion Evidence present.  I had also tried creating interaction markups when the sentence span included person evidence and behavior evidence.  While the resulting mentions looked appropriate, this caused too many false positives and was backed off. 

ICF d730 Relating with Stranger markups are created when there is evidence of Stranger Relationship Evidence.  These kinds of terms come from a lexicon of 100 terms like shopper, public places, and visitor.   The bulk of these terms were garnered from examples seen in the data.  

ICF d740 and ICF d7400 Formal Relationship markups are created when there are terms categorized as Non-Authority or Sometimes-Authority. These kinds of terms come from a person-role and title lexicon that was augmented with non-Authority, Sometimes-Authority and Authority-Position semantic categories, garnered from the Bureau of Labor Statistics public sources.  Those terms marked with Authority-Position also had d7400 evidence and mentions made.

 
Figure x: IPIR d7400 & d740 Subclassification

ICF d750 Informal Social Relationship markups are created when there is Informal Relationship Evidence found in the span of the IPIR yes sentence.  If there is no such evidence, but there is the presence of pronouns, an informal social relationship markup is made.  This works only because we already know this is an IPIR_yes sentence, so cases where the pronouns relate to possessions rather than to other people would have already been filtered out.  The UMLS terms categorized with family history and person roles were the impetus for the informal relationships lexicon employed here.  There are currently 125 terms in this resource. 

ICF d760 Family Relationship markups are created when there is Family History Evidence found in the span of the IPIR_yes sentence.  The underlying terminology used here are the terms in the UMLS categorized with their Family-History semantic type.  

ICF d770 Intimate Relationship markups are created when there is Intimate Relationship Evidence found in the span of the IPIR_yes sentence.  A small lexicon of 130 terms were created mostly from the guideline examples, observations, introspection and use of thesauri to garner adjectives and nouns that indicate an intimacy.  Such terms include significant other, old lady, and inseparable.

ICF d779 Particular interpersonal relationships, other specified and unspecified markups are also created when there is evidence of Other-Relationship Evidence found in the span of IPIR_yes sentence.  There were only 30 or so terms identified with this tag, which included others, nobody, and withdrawn.  The guidelines and training data were source for this terminology. 

## Annotation Guidelines, Selected Corpus for Training and Testing, Annotation Efforts ##

The domain expert annotators created a list containing likely IPIR words within pages that have IPIR mentions in them.  From a set of 1.5 million SSA claimant pages from 2019[x],  400 pages were selected based on a uniform frequency distribution among the presence of n number of words for annotation purposes. That is, some records were selected with many instances of IPIR words, and some were selected with only a few instances of IPIR words.  The set was randomly segmented into a small set for inter-rater-reliability (IRR) (20 pages), development set (80 pages), training set(160 pages), and a validation set (80 pages).

Two domain expert annotators meticulously developed annotation guidelines for IPIR (see x), honed the definitions and assessed their internalization of the guidelines into annotation efforts resulting with IRR= 0.70.  Once the IIR task was complete and the IIR acceptable, the rest of the samples annotated by one or the other annotator (not duplicative annotation) in an effort favoring getting annotations expediently to the modelers to model. 
  







