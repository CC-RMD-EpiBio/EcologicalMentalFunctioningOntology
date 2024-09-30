echo " The Number of Rows in LRAGR files "> numberOfRowsInLRAGRFiles.txt
echo `date` >> numberOfRowsInLRAGRFiles.txt
echo "\n\n" >> numberOfRowsInLRAGRFiles.txt

cat 001_TopExternalFactors.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 002_TopPersonFactors.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 003_TopActivitiesAndParticipation.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 004_TopBehavior.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 005_TopActivitiesOfDailyLiving.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 006_TopGeneralTasksAndDemands.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 007_TopLearningAndApplyingKnowledge.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 008_TopCommunicationActivities.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 009_TopCognitiveActivities.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 010_TopVerbNetClasses.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat 011_TopIPIRNominalizations.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat ComCogUMLSTermsAndVariants.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat ComCogVerbNetCategories.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat IPIRExceptions.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat IPIRInteractions.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat MoreComProducing.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat OtherIPIRVerbs.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat SSAListingTerms.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat denies.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat emotion.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat informalRelationships.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat intimateRelationships.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat lvg_Verbs_inflected.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat lvg_verbs_DerivationsAndNomilizations.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat lvg_verbs_Synonyms.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat lvg_verbs_spellingVariants_inflections_FACTS.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat mentalFunctionTriggerTerms.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat missingTerms.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat pp14.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat strangerRelationships.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt

cat workRoles.lragr |grep -v "^#" |wc -l >> numberOfRowsInLRAGRFiles.txt
