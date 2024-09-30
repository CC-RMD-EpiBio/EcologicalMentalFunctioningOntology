// =================================================
/**
 * IPIRCategories creates IPIR categories overlaying the IPIR mentions
 * 
 * @author  GD
 * @created 2023.03.14
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.inFACT.BehaviorEvidence;
import gov.nih.cc.rmd.inFACT.D710_D720IPIRInteractions;
import gov.nih.cc.rmd.inFACT.D710_D720IPIRInteractionsEvidence;
import gov.nih.cc.rmd.inFACT.D730RelatingWithStrangers;
import gov.nih.cc.rmd.inFACT.D730RelatingWithStrangersEvidence;
import gov.nih.cc.rmd.inFACT.D730_D770RelationshipsIPIR;
import gov.nih.cc.rmd.inFACT.D7400AuthorityRelationships;
import gov.nih.cc.rmd.inFACT.D7400AuthorityRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D740FormalRelationships;
import gov.nih.cc.rmd.inFACT.D740FormalRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D750InformalRelationships;
import gov.nih.cc.rmd.inFACT.D750InformalRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D760FamilyRelationships;
import gov.nih.cc.rmd.inFACT.D760FamilyRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D770IntimateRelationships;
import gov.nih.cc.rmd.inFACT.D770IntimateRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D779OtherRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D779OtherRelationships;
import gov.nih.cc.rmd.inFACT.EmotionEvidence;

import gov.nih.cc.rmd.inFACT.IPIRParticipationEvidence;

import gov.nih.cc.rmd.inFACT.manual.IPIRyes;
import gov.nih.cc.rmd.inFACT.x.IPIR_yes;

import gov.nih.cc.rmd.inFACT.SupportEvidence;


import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.nih.cc.rmd.nlp.lexUtils.LRPRN;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Utterance;
import gov.va.vinci.model.Concept;



public class IPIRCategories extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * mentalFunctions from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started IPIRCategories");

   // Loop through utterances  (that's the span we won't cross) 
    
    
    List<Annotation> ipirMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID, false );
    
   
    // ----------------------------------------------
    // If there are IPIRyes annotations, iterate through these sentences 
    if ( ipirMentions != null && !ipirMentions.isEmpty()) {
      for ( Annotation ipirYesSentence : ipirMentions )
          processIPIRYesSentence ( pJCas, ipirYesSentence);
      
    } else   if ( !INFACT_MODE &&  (ipirMentions == null || ipirMentions.isEmpty()) ) {
    	ipirMentions = UIMAUtil.getAnnotations(pJCas, IPIRActivities.typeIndexID, true );
    
    	if ( ipirMentions != null && !ipirMentions.isEmpty())
    		for ( Annotation mention : ipirMentions )
    			processIPIRMention ( pJCas, mention);
    }
     
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End IPIRCategories");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with ipir Categories" + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


  // =================================================
  /**
   * processIPIRMention finds the sentence with the mention in it, and processes the ipir sentence
   * 
   * @param pJCas
   * @param pMention
  */
  // =================================================
  private final void processIPIRMention(JCas pJCas, Annotation pMention) {
    
    try {
      
      // find the sentence this mention is within
      List<Annotation> ipirSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Utterance.typeIndexID, pMention.getBegin(), pMention.getEnd(), true );
      
      if ( ipirSentences != null && !ipirSentences.isEmpty()) {
        for ( Annotation sentence : ipirSentences )
          processIPIRSentence( pJCas, sentence, pMention );
      }
      
   
      
      
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
      throw e;
    }
  } // end Method processIPIRMention() ---------------


  // =================================================
  /**
   * processIPIRSentence creates ipir sub category annotations an IPIR Sentence
   * 
   * @param pJCas
   * @param pIPIRYesSentence
  */
  // =================================================
   private final void processIPIRYesSentence(JCas pJCas, Annotation pIPIRYesSentence) {
    
     // find all the ipir Evidence, IPIRActivities within this IPIR Sentence
     String aSent = pIPIRYesSentence.getCoveredText();
     
     if ( !hasNonRelevantEvidence( pJCas, pIPIRYesSentence ) ) {
    	 
     
     List<Annotation> ipirMentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRActivities.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true );
     List<Annotation> ipirParticipationEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRParticipationEvidence.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true );
     List<Annotation> behaviorEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BehaviorEvidence.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd() , true);
     List<Annotation> supportEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SupportEvidence.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd() , true);
     List<Annotation> peopleFound = MentalFunctionOntologyNERAnnotator.findPeople( pJCas, pIPIRYesSentence );
     
     List<Annotation> someMentions = new ArrayList<Annotation>();
     
     if ( ipirMentions != null && !ipirMentions.isEmpty())  someMentions.addAll(ipirMentions); 
     if ( ipirParticipationEvidence != null && !ipirParticipationEvidence.isEmpty())  someMentions.addAll(ipirParticipationEvidence); 
     if ( behaviorEvidence != null && !behaviorEvidence.isEmpty())  someMentions.addAll(behaviorEvidence); 
     if ( supportEvidence != null && !supportEvidence.isEmpty())  someMentions.addAll(supportEvidence); 
     
     List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true );
     
     
     boolean someRelationshipFound = false;
     if ( !someMentions.isEmpty()) {
    	 
       for ( Annotation aMention : someMentions ) {
        if ( processIPIRSentence( pJCas,  pIPIRYesSentence,  aMention) )
        	someRelationshipFound = true;
       } // end loop through mentions
     } // end if there are mentions
     // -------------------------------------------------------
     // this is a catch all if we know we've got an IPIR statement, but couldn't find a subcategory for it
     //
     
     if ( !someRelationshipFound )
    	 if (  (peopleFound == null ||  peopleFound.isEmpty() || peopleFound.size() <  2) ) {
    	 
    	 // if there are any D7400 annotations already assigned for this sentence, then quit
    	    List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D7400AuthorityRelationships.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), false );
    	    if (!( existingIpirCategories != null && !existingIpirCategories.isEmpty() )) {
    	    	IPIRActivities statement = new D740FormalRelationships( pJCas);
    	    	createIPIRSubcategory( pJCas, pIPIRYesSentence, pIPIRYesSentence, statement, "default");
    	    } // end if there are not d7400 annotations already made
          
    	 }
     }
    
     setIPIRYesAttributes( pJCas, pIPIRYesSentence );
     
   } // end Method processIPIRSentence() ------------

   

// =================================================
  /**
   * hasNonRelevantEvidence returns true if any terms that indicate the wrong subject
   * or exceptions turns up
   * 
   * @param pJCas
   * @param pSentence
   * @return boolean
  */
  // =================================================
  private final boolean hasNonRelevantEvidence(JCas pJCas, Annotation pSentence) {
	boolean returnVal = false;
	
   List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true );
	   
	if ( terms != null && !terms.isEmpty())
		for ( Annotation term: terms ) {
			String categories = ((LexicalElement) term).getSemanticTypes();
			if ( categories != null && !categories.isEmpty())
				if ( categories.contains("NotMFO") || categories.contains("NotIPIR")) {
					returnVal = true;
					 String buff = term.getCoveredText();
					GLog.println(GLog.INFO_LEVEL, this.getClass(), "hasNonRelevantEvidence:IPIR", "Ruling this out as non mental functioning |" + buff + "|");
					
				}
		}
   
	return returnVal;
} // end Method hasNonRelevantEvidence() -------------



// =================================================
  /**
   * setIPIRYesAttributes sets the (Interlingua) IPIRyes features 
   *             d730                true  if there exists D730RelatingWithStrangers          
   *             d740                true  if there exists D740FormalRelationships
   *             d7400               true  if there exists D7400AuthorityRelationships
   *             d750                true  if there exists D750InformalRelationship
   *             d760                true  if there exists D760FamilyRelationships
   *             d770                true  if there exists D770IntimateRelationships
   *             d779                true  if there exists D779OtherRelationships
   
   * @param pJCas
   * @param pIPIRYesSentence
  */
  // =================================================
 private void setIPIRYesAttributes(JCas pJCas, Annotation pIPIRYesSentence) {
    
   
	  
	 // create a new annotation from the manual or original one
	 // make sure it goes into the right annotation set
	 gov.nih.cc.rmd.inFACT.x.IPIR_yes statement =  (gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence;
	 //IPIR_yes statement = new IPIRYes( pJCas);
     //statement.setBegin( pIPIRYesSentence.getBegin());
     //statement.setEnd( pIPIRYesSentence.getEnd());
     //statement.setId( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence).getId() );
     //statement.setAnnotationSetName( this.annotationSetName);
     //statement.setDifficult_to_determine( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence).getDifficult_to_determine() );
     //statement.setTimeBucket( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence).getTimeBucket() );
     
     
   
    
     ((IPIR_yes)statement).setD730( UIMAUtil.mentionsExist(  pJCas, D730RelatingWithStrangers.typeIndexID,   pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
     ((IPIR_yes)statement).setD740(  UIMAUtil.mentionsExist( pJCas, D740FormalRelationships.typeIndexID,     pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
     ((IPIR_yes)statement).setD7400(UIMAUtil.mentionsExist(  pJCas, D7400AuthorityRelationships.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
     ((IPIR_yes)statement).setD750( UIMAUtil.mentionsExist(  pJCas, D750InformalRelationships.typeIndexID,   pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
     ((IPIR_yes)statement).setD760(  UIMAUtil.mentionsExist( pJCas, D760FamilyRelationships.typeIndexID,     pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
     ((IPIR_yes)statement).setD770( UIMAUtil.mentionsExist(  pJCas, D770IntimateRelationships.typeIndexID,   pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ) );
     ((IPIR_yes)statement).setD779( UIMAUtil.mentionsExist(  pJCas, D779OtherRelationships.typeIndexID,      pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
	   
	 ((IPIR_yes)statement).setInteraction(  UIMAUtil.mentionsExist( pJCas, D710_D720IPIRInteractions.typeIndexID,      pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true ));
	 
	 // statement.addToIndexes();
	// pIPIRYesSentence.removeFromIndexes();
	 // check to see that at least one attribute is set, and if not, set one.
	 setAtLeastOneIPIRAttribute( statement);
   
	 
	  
	 	 
  } // end Method setIPIRYesAttributes() -------------

 // =================================================
 /**
  * setAtLeastOneIPIRAttribute makes sure at least one
  * attribute has been set, if none are set, set a default one
  * 
  * @param pStatement 
  * 
 */
 // =================================================
  private final void setAtLeastOneIPIRAttribute ( IPIR_yes pStatement ){
	  
	  if (    !pStatement.getInteraction() &&
			  !pStatement.getD730() &&
			  !pStatement.getD740() &&
			  !pStatement.getD7400() &&
			  !pStatement.getD750() &&
			  !pStatement.getD760() &&
			  !pStatement.getD770() &&
			  !pStatement.getD779() )
		  pStatement.setD779(true);   //  set to "other"  //  should be the most popular one
	  
  } //end Method setAtLeastOneIPIRAttribute() --------

  // =================================================
  /**
   * processIPIRSentence creates ipir sub category annotations for this mention
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean   someRelationship found
  */
  // =================================================
   private final boolean processIPIRSentence(JCas pJCas, Annotation pSentence, Annotation pMention) {
    
   
    
     
     boolean relationshipFound[] = new boolean[6];
     boolean someRelationshipFound = false;
     
    
     relationshipFound[0] =categorizeD730RelatingWithStrangers ( pJCas, pSentence, pMention);
     relationshipFound[1] =categorizeD740FormalRelationships( pJCas, pSentence, pMention);
     relationshipFound[2] =categorizeD760FamilyRelationships( pJCas, pSentence, pMention);
     relationshipFound[3] =categorizeD770IntimateRelationships( pJCas, pSentence, pMention);
     relationshipFound[4] =categorizeD779OtherRelationships( pJCas, pSentence, pMention);
     
     
     // ------------- informal relationships is a default, and has to be computed last!!! ----
     relationshipFound[5] =categorizeD750InformalRelationships( pJCas, pSentence, pMention);
     
     for ( int i = 0; i < relationshipFound.length; i++)
       if ( relationshipFound[i]) {
         someRelationshipFound = true;
         break;
       }
    
     boolean interactionRelationshipFound = categorizeIPIRInteractions( pJCas, pSentence, pMention);
    
    
    
     return someRelationshipFound;
  } // end Method processIPIRSentence() --------------


// =================================================
  /**
   * catorizeIPIRInteractions 
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
  */
  // =================================================
   private boolean categorizeIPIRInteractions(JCas pJCas, Annotation pSentence, Annotation pMention) {
   
   
     boolean found = false;
     try {
       
       
       found = categorizeIPIRInteractionsPart1( pJCas,  pSentence,  pMention);
       
       if ( !found )
         found = categorizeIPIRBehaviorInteractions( pJCas, pSentence, pMention );
       
       
     } catch (Exception e) {
     
     }
     
     return found;
   } // end Method categorizeIPIRInteractions() ------
 
  // =================================================
  /**
   * catorizeIPIRInteractions 
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
  */
  // =================================================
 private boolean categorizeIPIRInteractionsPart1(JCas pJCas, Annotation pSentence, Annotation pMention) {
   
   
   boolean found = false;
   try {
     
     
     // if there are any D710_D720IPIRInteractions already assigned for this sentence, then quit
     List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D710_D720IPIRInteractions.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
     if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
       return true;
     
     
     // find the sentence this mention is within
     List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
     
   
    if ( terms != null && !terms.isEmpty()) {
       
       terms = UIMAUtil.uniqueAnnotations(terms);
       for ( Annotation term : terms ) {
    	   String buff = term.getCoveredText();
         String categories = ((LexicalElement) term).getSemanticTypes();
         
         // -----------------------------
         if      (  categories != null ) {
           
           if ( categories.contains("NotIPIR") || categories.contains("NotMFO")) continue;

         
           if ( categories.contains("ICF_d710-d729") ||
                categories.contains("IPIRAbility") ||                        //    these will already have IPIRParticipationEvidence created    
                categories.contains("IPIRParticipation") ) {                 //    these will already have IPIRParticipationEvidence created 
               
        	   if ( !found ) {
        		   IPIRActivities statement = new   D710_D720IPIRInteractions( pJCas);
        		   createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
        		   found = true;
        	   }
        	   
             D710_D720IPIRInteractionsEvidence evidenceStatement = new   D710_D720IPIRInteractionsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
           
           }
             
          
           
         } // end if there are categories
       } // end loop through terms
     } // end if there are terms
     
     // remove duplicates 
    // removeDuplicates( utx );
    
    
   } catch ( Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
     throw e;
   }
   
   return found;
  } // end Method categorizeIPIRInteractionsPart1() -------
 
//=================================================
/**
 * catorizeIPIRInteractions 
 * 
 * @param pJCas
 * @param pSentence
 * @param pMention
*/
// =================================================
private boolean categorizeIPIRBehaviorInteractions(JCas pJCas, Annotation pSentence, Annotation pMention) {
 
 
 boolean found = false;
 try {
   
  
   // if there are any D710_D720IPIRInteractions already assigned for this sentence, then quit
   List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D710_D720IPIRInteractions.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
   if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
     return true;
   
   
   // find the sentence this mention is within
   List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
   
   List<Annotation> person = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Person.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
   
   List<Annotation>   behaviorEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BehaviorEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
   List<Annotation>    emotionEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, EmotionEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
  
   
   List<Annotation>  allEvidences = new ArrayList<Annotation>();
//   if ( behaviorEvidences != null && !behaviorEvidences.isEmpty() )  allEvidences.addAll( behaviorEvidences);
   if ( emotionEvidences  != null && !emotionEvidences.isEmpty()  )  allEvidences.addAll( emotionEvidences);
 
 
   if ( allEvidences.size() > 0) {
     String categories = "BehaviorEvidence:EmotionEvidence";
     
     IPIRActivities statement = new   D710_D720IPIRInteractions( pJCas);
     createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
     found = true;
           
     for ( Annotation evidence : allEvidences) {
    	                           
    	 D710_D720IPIRInteractionsEvidence evidenceStatement = new   D710_D720IPIRInteractionsEvidence ( pJCas);
       createIPIRSubcategory( pJCas, pSentence, evidence, evidenceStatement, categories);
         
     }
           
        
       
   } // end if there are terms
   
   // remove duplicates 
  // removeDuplicates( utx );
  
  
 } catch ( Exception e ) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
   throw e;
 }
 
 return found;
} // end Method categorizeIPIRBehaviorInteractions() -------

  // =================================================
  /**
   * categorizeD730RelatingWithStrangers 
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean (true if this category was found
  */
  // =================================================
  private boolean categorizeD730RelatingWithStrangers(JCas pJCas, Annotation pSentence, Annotation pMention) {
   
    boolean strangerFound = false;
    try {
      
      
      // if there are any D730RelatingWithStrangers already assigned for this sentence, then quit
      List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D730RelatingWithStrangers.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
        return true;
      
      
      // find the sentence this mention is within
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      
      if ( terms != null && !terms.isEmpty()) {
        
        terms = UIMAUtil.uniqueAnnotations(terms);
        for ( Annotation term : terms ) {
          String categories = ((LexicalElement) term).getSemanticTypes();
          
          // ------------------interacting with stranger relationships - like being a traveler on a bus -----------
          if      (  categories != null && ( categories.contains("StrangerRelationshipEvidence"))) {
            
            if ( !strangerFound ) {
              IPIRActivities statement = new   D730RelatingWithStrangers( pJCas);
              createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
              strangerFound = true;
            }
              
            D730RelatingWithStrangersEvidence evidenceStatement = new   D730RelatingWithStrangersEvidence ( pJCas);
            createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
            
          } // end if !stranger found
        } // end loop through terms
      } // end if there are terms
      
      // remove duplicates 
     // removeDuplicates( utx );
     
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
      throw e;
    }
    
    return strangerFound;
    
  } // end Method categorizeD730RelatingWithStrangers() ----------



  // =================================================
  /**
   * removeDuplicates removes from the index duplicates from this list
   * 
   * @param pListOfAnnotations
  */
  // =================================================
  public static final void removeDuplicates2(List<Annotation> pListOfAnnotations) {
    HashSet<String> signitures = new HashSet<String>();
    if ( pListOfAnnotations !=null && !pListOfAnnotations.isEmpty())
      for (Annotation ut : pListOfAnnotations ) {
        String signiture = ut.getClass().getSimpleName() + "|" + ut.getBegin() + "|" + ut.getEnd();
        if (signitures.contains(signiture))
          ut.removeFromIndexes();
        else
          signitures.add(signiture);
      }
  } // end Method removeDuplicates() -------------------



  // =================================================
  /**
   * categorizeD740FormalRelationships looks for people with roles in the mention
   * and creates evidence and a D740FormalRelationship annotation across the sentence
   * with the mention.
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean
  */
  // =================================================
   private boolean categorizeD740FormalRelationships(JCas pJCas, Annotation pSentence, Annotation pMention) {
    
     boolean found = false;
     
  try {
    
    // if there are any D740FormalRelationships already assigned for this sentence, then quit
    List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D740FormalRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
    if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
      return true;
    
    
    // if there are any D740FormalRelationships already assigned for this sentence, then quit
    existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D7400AuthorityRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
    if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
      return true;
    
    
    
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
       boolean authorityfound = false;
       boolean nonAuthorityFound = false;
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           String buf = term.getCoveredText();
           String categories = ((LexicalElement) term).getSemanticTypes();
           
           // ------------------Formal relationships - like dealing with your pumber -----------
           if      (  categories != null && ( categories.contains("NonAuthority")  || categories.contains("SometimesAuthority"))) {
            
             if ( !nonAuthorityFound ) {
               IPIRActivities statement = new D740FormalRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
               nonAuthorityFound = true;
             
             }
             D740FormalRelationshipsEvidence evidenceStatement = new   D740FormalRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             
           }
             
           // ------------------Formal relationships - like dealing with your boss -------------
           if      ( categories != null && categories.contains("AuthorityPosition")) {
             if  (!authorityfound ) {
            	 GLog.println(" d7400 trigger-->" + term.getCoveredText() + "|" + categories );
               IPIRActivities statement = new D7400AuthorityRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
               IPIRActivities statement2 = new D740FormalRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement2, categories);
               authorityfound = true;
             }
             D7400AuthorityRelationships evidenceStatement = new   D7400AuthorityRelationships ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             D7400AuthorityRelationshipsEvidence evidenceStatement2 = new   D7400AuthorityRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement2, categories);
           }
             
         }
           
       }
       
       if  (authorityfound || nonAuthorityFound )
         found = true;
   
    } catch ( Exception e ) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
       throw e;
     }
  
     return found;
    
  } // end Method categorizeD740FormalRelationships() ---------


  // =================================================
  /**
   * categorizeD750InformalRelationships 
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return 
  */
  // =================================================
   private boolean categorizeD750InformalRelationships(JCas pJCas, Annotation pSentence, Annotation pMention) {
    
     boolean informalRelationshipFound = false;
     HashSet<String> pronounHash = new HashSet<String>(3);
     Annotation lastPronounFound = null;
     try {
       
       // if there are any D750InformalRelationships already assigned for this sentence, then quit
       List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D750InformalRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
         return true;
       
       
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
     
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           String termString = term.getCoveredText();
           String categories = ((LexicalElement) term).getSemanticTypes();
           
           // ------------------interacting with informal social relationships ------------
           if      (  categories != null && ( categories.contains("InformalRelationshipEvidence"))) {
             
             if ( !informalRelationshipFound  ) {
               IPIRActivities statement = new   D750InformalRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
               informalRelationshipFound = true;
             }
             
             D750InformalRelationshipsEvidence evidenceStatement = new   D750InformalRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             
           
           } // end if !informal found
           else if ( LRPRN.isPronoun(termString ) ) {
             if ( !pronounHash.contains( termString.toLowerCase() )) {
            	 if ( termString.toLowerCase().equals("his"))  termString = "he";
            	 if ( termString.toLowerCase().equals("her")) termString = "she";
            
               pronounHash.add(termString.toLowerCase());
               lastPronounFound = term;
             }
           }
         } // end loop through terms
       } // end if there are terms
       
       // -----------------------
       // if no informalRelationshipFound found, BUT
       //             there are more than one pronouns in the pronounHash seen
       //         AND no other kind of relationship has been created around this sentence
       //          (this should be the last run )
       
       // make a new   IPIRActivities statement = new   D750InformalRelationships( pJCas);
       
       if ( !informalRelationshipFound && pronounHash.size() > 1 && !otherRelationshipsFound( pJCas, pSentence) ) {
         IPIRActivities statement = new   D750InformalRelationships( pJCas);
         String categories = "InformalRelationshipEvidence:Pronoun";
         createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
         informalRelationshipFound = true;
         D750InformalRelationshipsEvidence evidenceStatement = new   D750InformalRelationshipsEvidence ( pJCas);
         createIPIRSubcategory( pJCas, pSentence, lastPronounFound, evidenceStatement, categories);
       }
        
      
     } catch ( Exception e ) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
       throw e;
     }
     return   informalRelationshipFound;
     
  } // end Method categorizeD750InformalRelationships() -----------

  // =================================================
  /**
   * otherRelationshipsFound returns true if there are any relationship annotations found
   * for this sentence
   * 
   * @param pJCas
   * @param pSentence
   * @return boolean
  */
  // =================================================
   private final boolean otherRelationshipsFound(JCas pJCas, Annotation pSentence) {
     boolean returnVal = false;
     
     List<Annotation> relationships = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D730_D770RelationshipsIPIR.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
     
     if ( relationships != null && !relationships.isEmpty() )
       returnVal = true;
     
     return returnVal;
  } // end Method otherRelationshipsFound() --------



  // =================================================
  /**
   * categorizeD760FamilyRelationships 
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean
  */
  // =================================================
  private boolean categorizeD760FamilyRelationships(JCas pJCas, Annotation pSentence, Annotation pMention) {
   

    boolean familyHistoryFound = false;
   
    
    try {
     
      // if there are any D760FamilyRelationships already assigned for this sentence, then quit
      List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D760FamilyRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
        return true;
      
      
      // find the sentence this mention is within
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      
     
      if ( terms != null && !terms.isEmpty()) {
        for ( Annotation term : terms ) {
          String categories = ((LexicalElement) term).getSemanticTypes();
          
          // ------------------interacting with informal social relationships ------------
          if      (  categories != null && ( categories.contains("FamilyHistoryEvidence"))) {
            
            if ( !familyHistoryFound  ) {
              IPIRActivities statement = new   D760FamilyRelationships( pJCas);
              createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
              familyHistoryFound = true;
            }
            
            D760FamilyRelationshipsEvidence evidenceStatement = new  D760FamilyRelationshipsEvidence ( pJCas);
            createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
           
          } // end if !stranger found
        } // end loop through terms
      } // end if there are terms
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
      throw e;
    }
    return   familyHistoryFound ;
    
  } // end Method categorizeD760FamilyRelationships() ------



  // =================================================
  /**
   * categorizeD770IntimateRelationships
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean
  */
  // =================================================
   private boolean categorizeD770IntimateRelationships(JCas pJCas, Annotation pSentence, Annotation pMention) {
   
     boolean intimateRelationshipFound = false;
     try {
       
       // if there are any D770IntimateRelationships already assigned for this sentence, then quit
       List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D770IntimateRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
         return true;
       
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
    
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           String categories = ((LexicalElement) term).getSemanticTypes();
           if ( categories != null && categories.contains("IntimateRelationshipEvidence")) {
             
             if ( !intimateRelationshipFound) {
               IPIRActivities statement = new D770IntimateRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pMention, statement, categories);
               intimateRelationshipFound = true;
             }
             D770IntimateRelationshipsEvidence evidence = new   D770IntimateRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidence, categories);
           }
         }
       }
       
       
     } catch ( Exception e ) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
       throw e;
     }
     
     return intimateRelationshipFound;
    
  } // end Method categorizeD770IntimateRelationships()--------
   
   
// =================================================
  /**
   * categorizeD770OtherRelationships
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @return boolean
  */
  // =================================================
   private boolean categorizeD779OtherRelationships(JCas pJCas, Annotation pSentence, Annotation pMention) {
   
     boolean otherRelationshipFound = false;
     try {
       
       // if there are any D779IntimateRelationships already assigned for this sentence, then quit
       List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D779OtherRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
         return true;
       
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
    
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
        	 String termName = term.getCoveredText();
        	 
           String categories = ((LexicalElement) term).getSemanticTypes();
           if ( categories != null && categories.contains("OtherRelationshipEvidence")) {
             
             if ( !otherRelationshipFound) {
               IPIRActivities statement = new D779OtherRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pMention, statement, categories);
               otherRelationshipFound = true;
             }
             D779OtherRelationshipsEvidence evidence = new   D779OtherRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidence, categories);
           }
         }
       }
       
       
     } catch ( Exception e ) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
       throw e;
     }
     
   //  STopped here
     return otherRelationshipFound;
    
  } // end Method categorizeD770IntimateRelationships()--------


   // =================================================
  /**
   * createIPIRSubcategory creates a specific category ipir category annotation
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @param pStatement
   * @param categoryEvidence
  */
  // =================================================
  private final void createIPIRSubcategory(JCas pJCas, Annotation pSentence, Annotation pMention, Concept pStatement, String pCategoryEvidence) {
   
    pStatement.setBegin(pMention.getBegin());
    pStatement.setEnd( pMention.getEnd());
    pStatement.setId("createIPIRSubCategory_" + this.categoryCtr++);
    pStatement.setCategories( pCategoryEvidence);
    pStatement.setAnnotationSetName(this.annotationSetName);
    
    String assertionStatus =  String.valueOf(VUIMAUtil.getAssertion_Status( pMention));
    
    boolean attribution =    VUIMAUtil.getAttributedToPatient( pMention );
    boolean conditionalStatus = VUIMAUtil.getConditional_Status ( pMention );
    boolean historical = VUIMAUtil.getHistorical_Status((pMention));	
    String subject = VUIMAUtil.getSubject(pMention);
    pStatement.setAssertionStatus( assertionStatus);
    pStatement.setAttributedToPatient( attribution );
    pStatement.setHistoricalStatus(historical);
    pStatement.setConditionalStatus(conditionalStatus);
    pStatement.setSubjectStatus(subject);
    pStatement.addToIndexes();
    
  } // end Method createIPIRSubcategory() ------------



//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    String mentalFunctioningOntologyCategories = U.getOption(pArgs,"--mfoCategories=", "all" );  // or individual ones or major categories of
   
    this.annotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_categories_rulebased_model");
  
    this.INFACT_MODE = Boolean.valueOf( U.getOption(pArgs, "--INFACT_MODE=", "false"));
      
  } // end Method initialize() -------
 
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
  // private HashSet<String> keptCategories = new HashSet<String>();
   private int categoryCtr = 0;
   private String annotationSetName = "ipir_categories_rulebased_model";
   
   public static final String InFACTIPIRCategoriesOutputTypes = 
       MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyOutputTypes + ":" +
       "D710_D720IPIRInteractions" + ":" +
       "D730RelatingWithStrangers" + ":" +
       "D740FormalRelationships" + ":" +
       "D7400AuthorityRelationships" + ":" +
       "D750InformalRelationships" + ":" +
       "D760FamilyRelationships" + ":" +
       "D770IntimateRelationships" + ":"  +
       
       "StrangerRelationshipEvidence" + ":" +
       "InformalSocialRelationshipEvidence" + ":" +
       "FormalRelationshipEvidence" + ":" +
       "FamilyRelationshipEvidence" + ":" +
       "IntimateRelationshipEvidence" + ":" +
       "IPIRInteractionRelationshipEvidence" + ":" +
       "BehaviorEvidence" + ":" + 
       "EmotionEvidence" + ":" + 
       "SupportEvidence" 
       
       
       
       ;
  
   	boolean INFACT_MODE = false; 
   
  
} // end Class LineAnnotator() ---------------

