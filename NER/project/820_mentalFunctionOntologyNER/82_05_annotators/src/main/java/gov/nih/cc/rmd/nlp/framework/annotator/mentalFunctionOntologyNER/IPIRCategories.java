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
import org.junit.experimental.categories.Categories;

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
import gov.va.chir.model.DependentContent;
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
    
     
     // The ipirYesSentence may be a part of a slot value, or a question/answer so expand 
     // the ipirYesSentence to the larger utterance to process
   
     
     List<Annotation>     largerChunks = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), true );
     
     if ( largerChunks != null && !largerChunks.isEmpty()) {
       for (Annotation aChunk : largerChunks ) {
         processIPIRYesSentence( pJCas,  pIPIRYesSentence, aChunk);
       } // loop thru the slotValues around the ipIRYesSentence
     } else {
       processIPIRYesSentence( pJCas,  pIPIRYesSentence, pIPIRYesSentence );
     }
     
     
   } // end Method processIPIRYesSentenceObs() ----------

  
   // =================================================
   /**
    * processIPIRSentence creates ipir sub category annotations an IPIR Sentence if 
    * any subcategories were found
    * 
    * @param pJCas
    * @param pIPIRYesSentence
    * @returns boolean
   */
   // =================================================
    protected final boolean processIPIRYesSentence(JCas pJCas, Annotation pKnownIPIRYesSentence, Annotation pSpanningChunk) {
      
      // find all the ipir Evidence, IPIRActivities within this IPIR Sentence
      String aSent = pSpanningChunk.getCoveredText();
      boolean someRelationshipFound = false;
      
      if ( segmentRelevantFilter )
        if ( !hasNonRelevantEvidence( pJCas, pSpanningChunk ) ) 
          someRelationshipFound = processIPIRYesSentenceAux( pJCas,  pKnownIPIRYesSentence,  pSpanningChunk) ;
         else
           someRelationshipFound = processIPIRYesSentenceAux( pJCas,  pKnownIPIRYesSentence,  pSpanningChunk) ;
      
      return someRelationshipFound;
    } // end Method processIPIRYesSentence() ----------

   // =================================================
   /**
    * processIPIRSentence creates ipir sub category annotations an IPIR Sentence if 
    * any subcategories were found
    * 
    * @param pJCas
    * @param pIPIRYesSentence
    * @returns boolean
   */
   // =================================================
    protected final boolean processIPIRYesSentenceAux(JCas pJCas, Annotation pKnownIPIRYesSentence, Annotation pSpanningChunk) {
     
      boolean someRelationshipFound = false;
      
      List<Annotation> ipirMentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRActivities.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd(), true );
      List<Annotation> ipirParticipationEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRParticipationEvidence.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd(), true );
      List<Annotation> behaviorEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BehaviorEvidence.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd() , true);
      List<Annotation> supportEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SupportEvidence.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd() , true);
      List<Annotation> peopleFound = MentalFunctionOntologyNERAnnotator.findPeople( pJCas, pSpanningChunk );
      
      List<Annotation> someMentions = new ArrayList<Annotation>();
      
      if ( ipirMentions != null && !ipirMentions.isEmpty())  someMentions.addAll(ipirMentions); 
      if ( ipirParticipationEvidence != null && !ipirParticipationEvidence.isEmpty())  someMentions.addAll(ipirParticipationEvidence); 
      if ( behaviorEvidence != null && !behaviorEvidence.isEmpty())  someMentions.addAll(behaviorEvidence); 
      if ( supportEvidence != null && !supportEvidence.isEmpty())  someMentions.addAll(supportEvidence); 
      
      // List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd(), true );
      
      
    
      if ( !someMentions.isEmpty()) {
        
        for ( Annotation aMention : someMentions ) {
         if ( processIPIRSentence( pJCas,  pSpanningChunk,  aMention) )
           someRelationshipFound = true;
        } // end loop through mentions
      } // end if there are mentions
      // -------------------------------------------------------
      // this is a catch all if we know we've got an IPIR statement, but couldn't find a subcategory for it
      //
      
      if ( !someRelationshipFound )
        if (  (peopleFound == null ||  peopleFound.isEmpty() || peopleFound.size() <  2) ) {
        
        // if there are any D7400 annotations already assigned for this sentence, then quit
           List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D7400AuthorityRelationships.typeIndexID, pSpanningChunk.getBegin(), pSpanningChunk.getEnd(), false );
           if (!( existingIpirCategories != null && !existingIpirCategories.isEmpty() )) {
             IPIRActivities statement = new D740FormalRelationships( pJCas);
             createIPIRSubcategory( pJCas, pSpanningChunk, pSpanningChunk, statement, "default");
             someRelationshipFound = true;
           } // end if there are not d7400 annotations already made
           
        }
      
     
      setIPIRYesAttributes( pJCas, pKnownIPIRYesSentence );
      return someRelationshipFound;
      
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
	
   String buff2 = pSentence.getCoveredText();
  
   List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true );
	   
	if ( terms != null && !terms.isEmpty())
		for ( Annotation term: terms ) {
			String categories = ((LexicalElement) term).getSemanticTypes();
			if ( categories != null && !categories.isEmpty())
				if ( categories.contains("NotMFO") || categories.contains("NotIPIR")) {
					returnVal = true;
					 String buff = term.getCoveredText();
					 System.err.println("hasNonRelevantEvidenceIPIR|" + buff + "|");
					GLog.println(GLog.INFO_LEVEL, this.getClass(), ":hasNonRelevantEvidenceIPIR", "Ruling this out as non mental functioning |" + buff + "|");
					
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
 protected void setIPIRYesAttributes(JCas pJCas, Annotation pIPIRYesSentence) {
    
   
	  
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
	  
	  if ( // !pStatement.getInteraction() &&   // <----- the annotations have many interactions and d779's this hurt backing it off
			  !pStatement.getD730() &&
			  !pStatement.getD740() &&
			  !pStatement.getD7400() &&
			  !pStatement.getD750() &&
			  !pStatement.getD760() &&
			  !pStatement.getD770() &&
			  !pStatement.getD779() ) {
		  pStatement.setD779(true);   //  set to "other"  //  should be the most popular one
	  
		  //String buff = pStatement.getCoveredText();
		  
	  }
	  
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
   protected final boolean processIPIRSentence(JCas pJCas, Annotation pSentence, Annotation pMention) {
    
   
    
     boolean returnVal = false;
     boolean relationshipFound[] = new boolean[6];
     boolean someRelationshipFound = false;
     boolean interactionRelationshipFound = false;
     
    
     // if the mention is in a slot value, make sure the value has an asserted value 
     // otherwise, we cannot assert the slot
     
     boolean assertedValueOrSentence = isAssertedValueOrSentence(pJCas, pMention );
     
     if ( assertedValueOrSentence ) {
     
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
    
        interactionRelationshipFound = categorizeIPIRInteractions( pJCas, pSentence, pMention);
    
     } // end if this is not a slot value or it is and the value was asserted
    
    
     returnVal = someRelationshipFound || interactionRelationshipFound;
     return returnVal;
  } // end Method processIPIRSentence() --------------


// =================================================
  /**
   * isAssertedValueOrSentence returns true if this mention is within a slot value 
   *                           and the value is explicitly asserted or the mention
   *                           is not within a slotValue
   * @param pJCas
   * @param pMention
   * @return boolean
  */
  // =================================================
  private final boolean isAssertedValueOrSentence(JCas pJCas, Annotation pMention) {
    
    boolean returnVal = false;
    Annotation aSlotValue =  getSlotValue( pJCas, pMention);
    boolean assertedValue = false;
    if ( aSlotValue != null )
      assertedValue = isValueAsserted(pJCas, aSlotValue);
    
   if ( aSlotValue == null  || assertedValue ) 
     returnVal = true;
   
   return returnVal;
  } // end Method isAssertedValueOrSentence() -----------



// =================================================
  /**
   * getSlotValue returns the slotvalue if it overlaps with the mention 
   * 
   * @param pJCas
   * @param pSentence
   * @return  Annotation (null if there is none)
  */
  // =================================================
  private final Annotation getSlotValue(JCas pJCas, Annotation pMention) {
    Annotation returnVal = null;
    
    List<Annotation> someSlots = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pMention.getBegin(), pMention.getEnd(),true );
    if ( someSlots != null && !someSlots.isEmpty())
      returnVal = someSlots.get(0);
    
    return returnVal;
  } // end Method getSlotValue() --------------------



// =================================================
  /**
   * isValueAsserted returns true if the value has an explicit asserted
   * value like "yes"  or [X]
   * @param pJCas 
   * 
   * @param pSlotvalue
   * @return boolean
  */
  // =================================================
   private boolean isValueAsserted(JCas pJCas, Annotation pSlotValue) {
     boolean returnVal = false;
     
     DependentContent value = ((SlotValue) pSlotValue).getDependentContent();
     if ( value != null ) {
       String content = value.getCoveredText();
       if ( content != null && content.trim().length() > 0 ) {
         String lowercaseContent = content.trim().toLowerCase();
         /*
         switch ( lowercaseContent ) {
           case "[x]" : returnVal = true; break;
           case "yes" : returnVal = true; break;
           case "y"   : returnVal = true; break;
           case "good"   : returnVal = true; break;
           case "appropriate"   : returnVal = true; break;
           case "moderately"  : returnVal = true; break;
           case "limited"     : returnVal = true; break;
           case "improved"     : returnVal = true; break;
           case "well"     : returnVal = true; break;
          }
           */
         switch ( lowercaseContent ) {
           case "0" : returnVal = false; break;
           default : returnVal = true; break;
         }
         
       }
     }
       
     return returnVal;
     
   } // end Method isValueAsserted() -----------------



  // =================================================
  /**
   * isSentenceSlotValue [TBD] summary
   * 
   * @param pSentence
   * @return
  */
  // =================================================
  
  private boolean isSentenceSlotValue(Annotation pSentence) {
    // TODO Auto-generated method stub
    return false;
  }



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
                categories.contains("d710_basic_interpersonal_interactions") ||    
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
        
        List<Annotation> statementList = new ArrayList<Annotation>();
        terms = UIMAUtil.uniqueAnnotations(terms);
        for ( Annotation term : terms ) {
          String categories = ((LexicalElement) term).getSemanticTypes();
          
          // ------------------interacting with stranger relationships - like being a traveler on a bus -----------
          if      (  categories != null && ( categories.contains("StrangerRelationshipEvidence"))) {
            
            if ( !strangerFound ) {
              IPIRActivities statement = new   D730RelatingWithStrangers( pJCas);
              createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
              strangerFound = true;
              statementList.add( statement);
              
            }
              
            D730RelatingWithStrangersEvidence evidenceStatement = new   D730RelatingWithStrangersEvidence ( pJCas);
            createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
            statementList.add( evidenceStatement);
            
            if ( categories != null && !statementList.isEmpty() && ( 
                categories.contains("FamilyHistoryEvidence") ||
                categories.contains("InformalRelationshipEvidence") ||
                categories.contains("AuthorityPosition") ) ) {
              for ( Annotation statement : statementList )
                statement.removeFromIndexes();
              strangerFound = false;  
              break;
            } // end if we had to backtrack because there was also mention of a friend or family
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
    
	 String buff = pMention.getCoveredText();
	
	 
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
       boolean inferredAuthorityFound = false;
       boolean familySeen = false;
       boolean interactionsFound = false;
       List<Annotation> questionableMentions = new ArrayList<Annotation>();
       List<Annotation> questionableEvidence = new ArrayList<Annotation>();
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           String buf = term.getCoveredText();
           String categories = ((LexicalElement) term).getSemanticTypes();
           
           if ( categories != null && (
               categories.contains("IntimateRelationshipEvidence") ||
               categories.contains("InformalRelationshipEvidence") ||
               categories.contains("FamilyHistoryEvidence"))) {
             familySeen = true;
           }
             
               
           
           // ------------------Formal relationships - like dealing with your pumber -----------
           if      (  categories != null && ( categories.contains("NonAuthority")  || categories.contains("SometimesAuthority"))) {
            
             if ( !nonAuthorityFound ) {
               IPIRActivities statement = new D740FormalRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
               nonAuthorityFound = true;
               questionableMentions.add ( statement );
             
             }
             D740FormalRelationshipsEvidence evidenceStatement = new   D740FormalRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             questionableEvidence.add ( evidenceStatement );
            
             
             
           }
           if      (  categories != null && ( categories.contains("ICF_d710-d729") || 
                                              categories.contains("d710_basic_interpersonal_interactions") )) {
             interactionsFound = true;
           }
              
           
           if      (  categories != null && ( categories.contains("ICF_d740_formal_relationships") )) {
             IPIRActivities statement = new D740FormalRelationships( pJCas);
             questionableMentions.add ( statement );
             createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
             D740FormalRelationshipsEvidence evidenceStatement = new   D740FormalRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             inferredAuthorityFound = true;
             questionableEvidence.add ( evidenceStatement );
             
             
           }
           
           // ------------------Formal relationships - like dealing with your boss -------------
           //                   or inferred from snippets of forms given to claimants by an authority person
           if      ( categories != null && categories.contains("AuthorityPosition") || ( categories.contains("ICF_d7400_formal_relationships")) ) {
             if  (!authorityfound ) {
               
               IPIRActivities statement = new D7400AuthorityRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
               questionableMentions.add ( statement );
               
               IPIRActivities statement2 = new D740FormalRelationships( pJCas);
               questionableMentions.add ( statement2 );
               createIPIRSubcategory( pJCas, pSentence, pSentence, statement2, categories);
               authorityfound = true;
              
             }
             D7400AuthorityRelationshipsEvidence evidenceStatement = new   D7400AuthorityRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement, categories);
             questionableEvidence.add ( evidenceStatement );
             D7400AuthorityRelationshipsEvidence evidenceStatement2 = new   D7400AuthorityRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidenceStatement2, categories);
             questionableEvidence.add ( evidenceStatement2 );
           }
             
         }
           
       }
       
       if ( familySeen && !questionableMentions.isEmpty()) {
        for ( Annotation mention: questionableMentions)   mention.removeFromIndexes();
        for ( Annotation evidence: questionableEvidence ) evidence.removeFromIndexes();
       } else {
         if  (authorityfound || nonAuthorityFound ) 
           found = true;
         else if ( inferredAuthorityFound  && interactionsFound )
           found = true;
         else if ( inferredAuthorityFound  && !interactionsFound && !questionableMentions.isEmpty()) {
           for ( Annotation mention: questionableMentions)   mention.removeFromIndexes();
           for ( Annotation evidence: questionableEvidence ) evidence.removeFromIndexes();
         }
       }
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
       
      String buff = pSentence.getCoveredText().toLowerCase();
      
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
      boolean patientSeen = false;
      boolean sessionSeen = false;
      Annotation lastSessionTerm = null;
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
           if (termString.contains("patient") || 
                    termString.contains("veteran") || 
                    termString.contains("claimant") || 
                    termString.contains("resident") || 
                    termString.contains("participant") || 
                    termString.contains("client") ) {
             patientSeen = true;
           }
            if (termString.contains("discussion") || 
               termString.contains("group") || 
               termString.contains("session") || 
               termString.contains("prompt") || 
               termString.contains("feedback") ||
               termString.contains("share") || 
               termString.contains("engaged") ) {
              sessionSeen = true;
              lastSessionTerm = term;
            }
           
         } // end loop through terms
         
         if ( patientSeen && sessionSeen ) {
           if ( !informalRelationshipFound  ) {
             IPIRActivities statement = new   D750InformalRelationships( pJCas);
             createIPIRSubcategory( pJCas, pSentence, pSentence, statement, "InformalRelationshipEvidence");
             informalRelationshipFound = true;
           }
           
           D750InformalRelationshipsEvidence evidenceStatement = new   D750InformalRelationshipsEvidence ( pJCas);
           createIPIRSubcategory( pJCas, pSentence, lastSessionTerm, evidenceStatement, "InformalRelationshipEvidence");
           
         } // end if patientSeen and sessionSeen 
       } // end if there are terms
       
       // -----------------------
       // if no informalRelationshipFound found, BUT
       //             there are more than one pronouns in the pronounHash seen
       //         AND no other kind of relationship has been created around this sentence
       //          (this should be the last run )
       
       // make a new   IPIRActivities statement = new   D750InformalRelationships( pJCas);
       
       if ( this.usePronouns ) {
       if ( !informalRelationshipFound && pronounHash.size() > 1 && !otherRelationshipsFound( pJCas, pSentence) ) {
          buff = pSentence.getCoveredText();
        
    	   IPIRActivities statement = new   D750InformalRelationships( pJCas);
         String categories = "InformalRelationshipEvidence:Pronoun";
         createIPIRSubcategory( pJCas, pSentence, pSentence, statement, categories);
         informalRelationshipFound = true;
         D750InformalRelationshipsEvidence evidenceStatement = new   D750InformalRelationshipsEvidence ( pJCas);
         createIPIRSubcategory( pJCas, pSentence, lastPronounFound, evidenceStatement, categories);
       }
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
   * categorizeD770IntimateRelationships  find the d770 terms,  
   *                                         tempered by family history counter evidence  (mother, father, daughter, ...) 
   *                                         tempered by informal relationships (classmate, buddy ... )
   *                                         tempered by formal relationships (boss, supervisor ...)
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
       boolean familyFound = false;
       boolean partnerFound = false;
       List<Annotation> statementList = new ArrayList<Annotation>();
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           String categories = ((LexicalElement) term).getSemanticTypes();
                                                          
           if ( categories != null && categories.contains("IntimateRelationshipEvidence")) {
             
             if ( !intimateRelationshipFound) {
               IPIRActivities statement = new D770IntimateRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pMention, statement, categories);
               intimateRelationshipFound = true;
               statementList.add( statement);
             }
             D770IntimateRelationshipsEvidence evidence = new   D770IntimateRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidence, categories);
             statementList.add( evidence);
           }
           
           if ( categories != null && !statementList.isEmpty() && ( 
                               categories.contains("FamilyHistoryEvidence") ||
                               categories.contains("InformalRelationshipEvidence") ||
                               categories.contains("AuthorityPosition") ) ) 
             familyFound = true;
             
              if ( categories != null && !statementList.isEmpty() && 
                  categories.contains("IntimatePartner"))
                partnerFound = true;
         
                                 
         } // end loop thru terms
         
         // if family and friends are present, but not an intimate partner, roll this back.
         if ( statementList != null && !statementList.isEmpty() && !partnerFound && familyFound) 
           for ( Annotation statement : statementList ) {
             statement.removeFromIndexes();
             intimateRelationshipFound = false;
           }
       
       } // end if there are any terms
       
       
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
    	 String buff = pSentence.getCoveredText();
    	 if ( buff.contains("other"))
    	   System.err.println("other found");
      
       // if there are any D779IntimateRelationships already assigned for this sentence, then quit
       List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, D779OtherRelationships.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
         return true;
       
       // find the sentence this mention is within
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
       
       List<Annotation> statements = new ArrayList<Annotation>();
       boolean retractD779 = false;
       if ( terms != null && !terms.isEmpty()) {
         for ( Annotation term : terms ) {
           Annotation aStatement = null;
        	 String termName = term.getCoveredText();
        	 
           String categories = ((LexicalElement) term).getSemanticTypes();
           if ( categories != null && categories.contains("OtherRelationshipEvidence")) {
             
             if ( !otherRelationshipFound) {
               IPIRActivities statement = new D779OtherRelationships( pJCas);
               createIPIRSubcategory( pJCas, pSentence, pMention, statement, categories);
               statements.add( statement);
               otherRelationshipFound = true;
             }
             D779OtherRelationshipsEvidence evidence = new   D779OtherRelationshipsEvidence ( pJCas);
             createIPIRSubcategory( pJCas, pSentence, term, evidence, categories);
             statements.add( evidence);
           }
           
         //  if ( categories != null && categories.contains("NotIPIR") || categories.contains("NotMFO") )
         //  if ( categories != null && categories.contains("InformalRelationshipEvidence")  )  //   like friend
         //  
           if ( termName.toLowerCase().contains("friend")  || termName.toLowerCase().equals("cooperative") || categories.contains("notIPIR")) {
             retractD779 = true;
           }
           
         } // end loop thru terms of the mention
         
         
         if ( retractD779 ) {
           for ( Annotation badStatement : statements )
             badStatement.removeFromIndexes();
         }
       
        
       
       }
       
       
     } catch ( Exception e ) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
       throw e;
     }
     
   
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
  if ( this.performanceMeter != null )
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
    this.segmentRelevantFilter = Boolean.parseBoolean( U.getOption(pArgs, "--segmentRelevantFilter=", "true" ));
    
    this.usePronouns = Boolean.valueOf(U.getOption( pArgs, "--usePronounsInD750=", "true"));
      
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
   	boolean usePronouns = true;
   	boolean  segmentRelevantFilter = true;
   
  
} // end Class LineAnnotator() ---------------

