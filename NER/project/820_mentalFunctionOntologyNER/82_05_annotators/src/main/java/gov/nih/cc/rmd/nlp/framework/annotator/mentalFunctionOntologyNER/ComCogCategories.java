// =================================================
/**
 * ComcogCategories creates ComCog categories overlaying the ComCog mentions
 * 
 * @author  GD
 * @created 2023.03.14
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.inFACT.Adaptation;
import gov.nih.cc.rmd.inFACT.AppliedMemory;
import gov.nih.cc.rmd.inFACT.ComcogEvidence;
import gov.nih.cc.rmd.inFACT.D110_D129_PurposefulSensoryExperiences;
import gov.nih.cc.rmd.inFACT.D130_D159_BasicLearning;
import gov.nih.cc.rmd.inFACT.D160_FocusingAttention;
import gov.nih.cc.rmd.inFACT.D163_Thinking;
import gov.nih.cc.rmd.inFACT.D166_Reading;
import gov.nih.cc.rmd.inFACT.D170_Writing;
import gov.nih.cc.rmd.inFACT.D172_Calculating;
import gov.nih.cc.rmd.inFACT.D175_SolvingProblems;
import gov.nih.cc.rmd.inFACT.D177_MakingDecisions;
import gov.nih.cc.rmd.inFACT.D179_ApplyingKnowledgeOther;
import gov.nih.cc.rmd.inFACT.D210_D220_UndertakingTasks;
import gov.nih.cc.rmd.inFACT.D230_CarryingOutDailyRoutine;
import gov.nih.cc.rmd.inFACT.D240_HandlingStress;
import gov.nih.cc.rmd.inFACT.D310_D329_ReceivingCommunication;
import gov.nih.cc.rmd.inFACT.D330_D349_ProducingCommunication;
import gov.nih.cc.rmd.inFACT.D350_D369_ConversationAndDiscussion;
import gov.nih.cc.rmd.inFACT.Manual_Adaptation;
import gov.nih.cc.rmd.inFACT.Manual_AppliedMemory;
import gov.nih.cc.rmd.inFACT.Manual_D110_D129_PurposefulSensoryExperiences;
import gov.nih.cc.rmd.inFACT.Manual_D130_D159_BasicLearning;
import gov.nih.cc.rmd.inFACT.Manual_D160_FocusingAttention;
import gov.nih.cc.rmd.inFACT.Manual_D163_Thinking;
import gov.nih.cc.rmd.inFACT.Manual_D166_Reading;
import gov.nih.cc.rmd.inFACT.Manual_D170_Writing;
import gov.nih.cc.rmd.inFACT.Manual_D172_Calculating;
import gov.nih.cc.rmd.inFACT.Manual_D175_SolvingProblems;
import gov.nih.cc.rmd.inFACT.Manual_D177_MakingDecisions;
import gov.nih.cc.rmd.inFACT.Manual_D179_ApplyingKnowledgeOther;
import gov.nih.cc.rmd.inFACT.Manual_D210_D220_UndertakingTasks;
import gov.nih.cc.rmd.inFACT.Manual_D230_CarryingOutDailyRoutine;
import gov.nih.cc.rmd.inFACT.Manual_D240_HandlingStress;
import gov.nih.cc.rmd.inFACT.Manual_D310_D329_ReceivingCommunication;
import gov.nih.cc.rmd.inFACT.Manual_D330_D349_ProducingCommunication;
import gov.nih.cc.rmd.inFACT.Manual_D350_D369_ConversationAndDiscussion;
import gov.nih.cc.rmd.inFACT.Manual_Pacing;
import gov.nih.cc.rmd.inFACT.Manual_Persistence;
import gov.nih.cc.rmd.inFACT.Pacing;
import gov.nih.cc.rmd.inFACT.Persistence;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ComCogActivities;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;
import gov.va.vinci.model.Concept;



public class ComCogCategories extends JCasAnnotator_ImplBase {
 
  
  
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
      List<Annotation> comcogMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID , false);
     
     if ( comcogMentions == null || comcogMentions.isEmpty()) {
       comcogMentions = getOntologyComcogMentions( pJCas );
     
       
     } // else {  // remove the ontology comcog sentences if there are manual mentions
       // List<Annotation> ontologyComcogMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID , false);
       // if (ontologyComcogMentions != null && !ontologyComcogMentions.isEmpty())
       //  for ( Annotation anOntologyMention : ontologyComcogMentions)
       //    anOntologyMention.removeFromIndexes();
     // }
     
      
      // ----------------------------------------------
      // If there are Comcog yes annotations, iterate through these sentences 
      if ( comcogMentions != null && !comcogMentions.isEmpty()) {
        for ( Annotation comcogYesSentence : comcogMentions ) 
        	processComcogYesSentence ( pJCas, comcogYesSentence);
        
        
      } else   if ( !INFACT_MODE &&  (comcogMentions == null || comcogMentions.isEmpty()) ) {
    	
    	  comcogMentions = UIMAUtil.getAnnotations(pJCas, ComCogActivities.typeIndexID, true );
      
    	  if ( comcogMentions != null && !comcogMentions.isEmpty())
    		  for ( Annotation mention : comcogMentions )
    			  processComcogMention ( pJCas, mention);
         
      }
      this.performanceMeter.stopCounter();
      
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with comcog Categories" + e.toString());
     //   throw new AnalysisEngineProcessException();
      }
  
  } // end Method process() ----------------
   


  // =================================================
  /**
   * getOntologyComcogMentions [TBD] summary
   * 
   * @param pJCas
   * @return
  */
  // =================================================
  private final List<Annotation> getOntologyComcogMentions(JCas pJCas) {
    
    List<Annotation>comcogMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID , false);
    
    
    // if we are running in inFACT mode, and ...
    //    we've picked up x.Comcog yes's that came from manual annotations
    //    These will have a annotationSetName=comcog_subcatory_model  and the ontology built ones
    //    will have a annotaitonSetname=framework
    // Then
    //   remove the framework based ones
    List<Annotation> newComcogMentions = null;
    if  (INFACT_MODE &&  (comcogMentions != null && !comcogMentions.isEmpty()) ) {
      newComcogMentions = new ArrayList<Annotation>(comcogMentions.size());
     
      for ( Annotation comcogYesSentence : comcogMentions ) {
        String annotationSetName = ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) comcogYesSentence).getAnnotationSetName();
        if ( !annotationSetName.equals("comcog_classification"))
         
          if ( annotationSetName == null ||  !this.payloadAnnotationSetName.contains( annotationSetName  )) {
            comcogYesSentence.removeFromIndexes();
           // comcogMentions.remove(comcogYesSentence);
          }
          else {
            newComcogMentions.add(comcogYesSentence);
          }
      }
    }
    if (newComcogMentions != null && !newComcogMentions.isEmpty())  {
      
      comcogMentions = newComcogMentions;
   }
    
    return comcogMentions;
  } // end Method getOntologyComcogMentions() --------



  // =================================================
  /**
   * processComcogMention finds the sentence with the mention in it, and processes the comcog sentence
   * 
   * @param pJCas
   * @param pMention
   * @throws Exception 
  */
  // =================================================
  private final void processComcogMention(JCas pJCas, Annotation pMention) throws Exception {
    
    try {
      
      // find the sentence this mention is within
      List<Annotation> comcogSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Utterance.typeIndexID, pMention.getBegin(), pMention.getEnd(), true );
      
      if ( comcogSentences != null && !comcogSentences.isEmpty()) {
        for ( Annotation sentence : comcogSentences )
          processComcogSentence( pJCas, sentence  );
      }
      
   
      
      
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRMention", "issue with processing the IPIR mention " + e.toString());
      throw e;
    }
  } // end Method processIPIRMention() ---------------


// =================================================
  /**
   * processComcogYesSentence Given: this is a sentence with comCog evidence in it,
   * classify all the kinds of com cog - i.e., set attributes for all the comcog
   * evidence found within this mention
   * 
   * @param pJCas
   * @param pComcogYesSentence
   * @throws Exception 
  */
  // =================================================
  public final boolean processComcogYesSentence(JCas pJCas, Annotation pComcogYesSentence) throws Exception {
    
    boolean someComCogFound = false;
    // find all the comcog Evidence, ComcogActivities within this Comcog yes Sentence
    
    String buff = pComcogYesSentence.getCoveredText();
   
                                   
    if ( segmentRelevantFilter  ) {
      if (  !hasNonRelevantEvidence( pJCas, pComcogYesSentence )  )
        if ( processComcogSentence( pJCas,  pComcogYesSentence ) )
          someComCogFound = true;
    } else {
      if ( processComcogSentence( pJCas,  pComcogYesSentence ) )
        someComCogFound = true;
    }
   
    setComcogYesAttributes( pJCas, pComcogYesSentence );
    
    return someComCogFound;
    
  } // end Method processComcogYesSentence() ----------





// =================================================
  /**
   * hasNonRelevantEvidence cries foul if there is a contra evidence present
   * 
   * @param pJCas
   * @param pComcogYesSentence
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
         if ( categories.contains("NotMFO") || categories.contains("NotComCog")) {
        	 String buff = term.getCoveredText();
        	 // GLog.println(GLog.INFO_LEVEL, this.getClass(), "hasNonRelevantEvidence:ComCog", "Ruling this out as non mental functioning |" + buff + "|");
           returnVal = true;
         
         }
     }
    
   return returnVal;
  } // end Method hasNonRelevantEvidence() -----------





  // =================================================
  /**
   * processComcogSentence 
   * 
   * @param pJCas
   * @param pComcogYesSentence
   * @return boolean
   * @throws Exception 
  */
  // =================================================
   protected final boolean processComcogSentence(JCas pJCas, Annotation pComcogYesSentence ) throws Exception {
    
    
    boolean comcogFound[] = new boolean[20];
    for (int i = 0; i < comcogFound.length; i++ ) comcogFound[i] = false;
    boolean someComcogFound = false;
   
    String buff2 = pComcogYesSentence.getCoveredText();
    
   
    try {
    comcogFound[ D110_CAT_SENSORY] =categorize ( pJCas, pComcogYesSentence,  D110_D129_PurposefulSensoryExperiences.class, D110_D129_PurposefulSensoryExperiences.typeIndexID,      ComcogEvidence.class );
    comcogFound[ D130_CAT_LEARNING] =categorize ( pJCas, pComcogYesSentence,  D130_D159_BasicLearning.class,                D130_D159_BasicLearning.typeIndexID,                     ComcogEvidence.class );
    comcogFound[ D160_CAT_FOCUSING_ATTENTION] =categorize ( pJCas, pComcogYesSentence,  D160_FocusingAttention.class,       D160_FocusingAttention.typeIndexID,                      ComcogEvidence.class );
    comcogFound[ D163_CAT_THINKING] =categorize ( pJCas, pComcogYesSentence,  D163_Thinking.class,                          D163_Thinking.typeIndexID,                               ComcogEvidence.class );
    comcogFound[ D166_CAT_READING]  =categorize ( pJCas, pComcogYesSentence,  D166_Reading.class,                           D166_Reading.typeIndexID,                                ComcogEvidence.class );
    comcogFound[ D170_CAT_WRITING]  =categorize ( pJCas, pComcogYesSentence,  D170_Writing.class,                           D170_Writing.typeIndexID,                                ComcogEvidence.class );
    comcogFound[ D172_CAT_CAL]      =categorize ( pJCas, pComcogYesSentence,  D172_Calculating.class ,                      D172_Calculating.typeIndexID,                            ComcogEvidence.class ); 
    comcogFound[ D175_CAT_SOLVING]  =categorize ( pJCas, pComcogYesSentence,  D175_SolvingProblems.class,                   D175_SolvingProblems.typeIndexID,                        ComcogEvidence.class );    
    comcogFound[ D177_CAT_DECISIONS]=categorize ( pJCas, pComcogYesSentence,  D177_MakingDecisions.class,                   D177_MakingDecisions.typeIndexID,                        ComcogEvidence.class );    
    comcogFound[ D179_CAT_OTHER]    =categorize ( pJCas, pComcogYesSentence,  D179_ApplyingKnowledgeOther.class,            D179_ApplyingKnowledgeOther.typeIndexID,                 ComcogEvidence.class );    
   
    comcogFound[D210_CAT_TASKS]     =categorize ( pJCas, pComcogYesSentence,  D210_D220_UndertakingTasks.class ,            D210_D220_UndertakingTasks.typeIndexID,                  ComcogEvidence.class );
    comcogFound[D230_CAT_DAILY_ROUTINES] =categorize ( pJCas, pComcogYesSentence,  D230_CarryingOutDailyRoutine.class,           D230_CarryingOutDailyRoutine.typeIndexID,                ComcogEvidence.class );
    comcogFound[D240_CAT_STRESS]    =categorize ( pJCas, pComcogYesSentence,  D240_HandlingStress.class,                    D240_HandlingStress.typeIndexID,                         ComcogEvidence.class );
    comcogFound[D310_CAT_COM_RECEIVING]=categorize ( pJCas, pComcogYesSentence,  D310_D329_ReceivingCommunication.class,       D310_D329_ReceivingCommunication.typeIndexID ,           ComcogEvidence.class );
  
    comcogFound[D330_CAT_COM_PRODUCTING] =categorize ( pJCas, pComcogYesSentence,  D330_D349_ProducingCommunication.class,       D330_D349_ProducingCommunication.typeIndexID,            ComcogEvidence.class ); 
    comcogFound[D350_CAT_COM_DISCUSSION] =categorize ( pJCas, pComcogYesSentence,  D350_D369_ConversationAndDiscussion.class,    D350_D369_ConversationAndDiscussion.typeIndexID,         ComcogEvidence.class );
    comcogFound[CAT_ADAPTATION_] =categorize ( pJCas, pComcogYesSentence,  Adaptation.class,                             Adaptation.typeIndexID,                                  ComcogEvidence.class );
   
    if ( appliedMemoryComCogFound (comcogFound )) {
    
    	comcogFound[CAT_APPLIED_MEMORY] =categorize ( pJCas, pComcogYesSentence,  AppliedMemory.class,   AppliedMemory.typeIndexID,                      ComcogEvidence.class );
    //	createAppliedMemorySubcategory( pJCas,pComcogYesSentence );
    
    }
    if ( someComCogFound ( comcogFound )) {
     	comcogFound[CAT_PACING] =categorize ( pJCas, pComcogYesSentence,                 Pacing.class,   Pacing.typeIndexID ,                            ComcogEvidence.class );            
    	comcogFound[CAT_PERSISTENCE] =categorize ( pJCas, pComcogYesSentence,       Persistence.class,   Persistence.typeIndexID,                        ComcogEvidence.class );
    }
   
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to categorize " + ": " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processComcogSentence", msg );
      throw new Exception (e);
    }
  
  
   
    
    return someComcogFound;
   
  } // end Method processComcogSentence() ------------


   // =================================================
   /**
    * someComCogFound
    * 
    * @param pCategoriesFound
    * @param pCategoryTypeIndexId
    * @param pLexicalSemanticType
    * @return boolean (true if this category was found
    * @throws Exception 
   */
   // =================================================
   private boolean someComCogFound( boolean[] pCategoriesFound ) {
                              
     boolean someComcogFound = false;
     
    
     
     boolean returnVal = false;
     
     //if      ( pCategoriesFound[  D110_CAT_SENSORY         ]) returnVal = true;
     if       ( pCategoriesFound[  D130_CAT_LEARNING   ]) returnVal = true;
     else if  ( pCategoriesFound[  D160_CAT_FOCUSING_ATTENTION ]) returnVal = true;
    // else if  ( pCategoriesFound[  D163_CAT_THINKING  ]) returnVal = true;
     else if  ( pCategoriesFound[  D166_CAT_READING   ]) returnVal = true;
     else if  ( pCategoriesFound[  D170_CAT_WRITING   ]) returnVal = true;
     else if  ( pCategoriesFound[  D172_CAT_CAL       ]) returnVal = true;
     else if  ( pCategoriesFound[  D175_CAT_SOLVING   ]) returnVal = true;
   //  else if  ( pCategoriesFound[  D177_CAT_DECISIONS ]) returnVal = true;
     else if  ( pCategoriesFound[  D179_CAT_OTHER     ]) returnVal = true;
     else if  ( pCategoriesFound[  D210_CAT_TASKS          ]) returnVal = true;
     else if  ( pCategoriesFound[  D230_CAT_DAILY_ROUTINES ]) returnVal = true;
     else if  ( pCategoriesFound[  D240_CAT_STRESS         ]) returnVal = true;
 //  else if  ( pCategoriesFound[  D310_CAT_COM_RECEIVING  ]) returnVal = true;
 //    else if  ( pCategoriesFound[  D330_CAT_COM_PRODUCTING ]) returnVal = true;
  //   else if  ( pCategoriesFound[  D350_CAT_COM_DISCUSSION ]) returnVal = true;
  //   else if  ( pCategoriesFound[  CAT_ADAPTATION          ]) returnVal = true;
  //   else if  ( pCategoriesFound[  CAT_APPLIED_MEMORY      ]) returnVal = true;
  //   else if  ( pCategoriesFound[  CAT_PACING              ]) returnVal = true;
  //   else if  ( pCategoriesFound[  CAT_PERSISTENCE         ]) returnVal = true;
   
     return returnVal;
     
   } // end Method someFound() -----------------------
   
   // =================================================
   /**
    * someComCogFound
    * 
    * @param pCategoriesFound
    * @param pCategoryTypeIndexId
    * @param pLexicalSemanticType
    * @return boolean (true if this category was found
    * @throws Exception 
   */
   // =================================================
   private boolean appliedMemoryComCogFound( boolean[] pCategoriesFound ) {
                              
     boolean someComcogFound = false;
     
    
     
     boolean returnVal = false;
     
    // if      ( pCategoriesFound[  D110_CAT_SENSORY         ]) returnVal = true;
    // if       ( pCategoriesFound[  D130_CAT_LEARNING   ]) returnVal = true;
    // if  ( pCategoriesFound[  D160_CAT_FOCUSING_ATTENTION ]) returnVal = true;
     if  ( pCategoriesFound[  D163_CAT_THINKING  ]) returnVal = true;
     // else if  ( pCategoriesFound[  D166_CAT_READING   ]) returnVal = true;
     // else if  ( pCategoriesFound[  D170_CAT_WRITING   ]) returnVal = true;
     else if  ( pCategoriesFound[  D172_CAT_CAL       ]) returnVal = true;
     // else if  ( pCategoriesFound[  D175_CAT_SOLVING   ]) returnVal = true;
     else if  ( pCategoriesFound[  D177_CAT_DECISIONS ]) returnVal = true;
     // else if  ( pCategoriesFound[  D179_CAT_OTHER     ]) returnVal = true;
     else if  ( pCategoriesFound[  D210_CAT_TASKS          ]) returnVal = true;
     else if  ( pCategoriesFound[  D230_CAT_DAILY_ROUTINES ]) returnVal = true;
     // else if  ( pCategoriesFound[  D240_CAT_STRESS         ]) returnVal = true;
     else if  ( pCategoriesFound[  D310_CAT_COM_RECEIVING  ]) returnVal = true;
     else if  ( pCategoriesFound[  D330_CAT_COM_PRODUCTING ]) returnVal = true;
     else if  ( pCategoriesFound[  D350_CAT_COM_DISCUSSION ]) returnVal = true;
    
   
     return returnVal;
     
   } // end Method someFound() -----------------------

  // =================================================
  /**
   * categorize
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @param pCategoryTypeIndexId
   * @param pLexicalSemanticType
   * @return boolean (true if this category was found
   * @throws Exception 
  */
  // =================================================
  private boolean categorize(JCas pJCas, 
                             Annotation pSentence,  
                             Class<?> pComcogClass,
                             int pCategoryTypeIndexID, 
                             Class<?> pComcogEvidenceClass ) throws Exception {
   
    boolean categoryFound = false;
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    
    try {
       String buff2 = pSentence.getCoveredText();
       
      //   If you are looking to trace specific things - uncomment out this 
    	// if ( buff2.toLowerCase().contains("demonstrated understanding") &&  
      //  (
      //     pComcogClass.equals( D310_D329_ReceivingCommunication.class)
      //      
      //    ))
    	  
    		
    	String lexicalSemanticType = translateComCogClassToLRAGRSemanticType( pComcogClass.getSimpleName());
   
    	// if there are any comcog categories already assigned for this sentence, then quit
      List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, pCategoryTypeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
        return true;
      
      
      // find the sentence this mention is within
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
    
      if ( terms != null && !terms.isEmpty()) {
        
        terms = UIMAUtil.uniqueAnnotations(terms);
        List<Annotation> mentions = new ArrayList<Annotation>();
        boolean counterEvidence = false;
        boolean d230Seen = false;
        boolean adlsSeen = false;
        
        // Tough one - this spans across slot: value, can't make it a context excpetion
        // 
        if ( buff2.equals("memory: recalls")) return false ;
        int ctr = 0;
        for ( Annotation term : terms ) {
        	String buff = term.getCoveredText();
        	ctr++;
        	
        	
          String categories = ((LexicalElement) term).getSemanticTypes();
           
          // ------------------interacting with stranger relationships - like being a traveler on a bus -----------
          if      (  categories != null && lexicalSemanticType != null && aCategoryMatches(  lexicalSemanticType, categories)) {
            
            if ( !categoryFound ) {
           
              Constructor<?> constructor = pComcogClass.getConstructor(JCas.class);
               Object statement = constructor.newInstance(pJCas );
           
               mentions.add( (Annotation) statement);
              createComcogSubcategory( pJCas, pSentence, pSentence,  (Concept) statement, categories);
              categoryFound = true;
            }
         
            // check to see if the term is not a slot, like "signed by:" 
            if ( isPartOfSlotOrSectionName( pJCas, term ) || 
                (
                ((LexicalElement)term).getConditional() 
                && !pComcogClass.equals( D163_Thinking.class) 
                && !pComcogClass.equals(D230_CarryingOutDailyRoutine.class)
                && !pComcogClass.equals(Pacing.class))  ) {
              continue;
            }
          
            Constructor<?> constructor = pComcogEvidenceClass.getConstructor(JCas.class);
            Object evidenceStatement   = constructor.newInstance ( pJCas);
            mentions.add((Annotation) evidenceStatement);
            createComcogSubcategory( pJCas, pSentence, term, (Concept) evidenceStatement, categories);
            
            
            if (lexicalSemanticType.contains("d230_dailyRoutine")) {
              if ( categories.contains("d230_dailyRoutine")) 
                d230Seen = true;
              if ( categories.contains("ActivitiesOfDailyLiving")) 
                adlsSeen = true;
              
            }
            // --------------------------------------
            // coping stratigies needs to be specific for d240 - 
            if ( categories.contains("d240") && (buff.contains("coping strategies") || buff.contains("coping strategy"))) {
              int numberOfWordsToRightOfCopingStrategies = terms.size() - ctr;
              if (numberOfWordsToRightOfCopingStrategies < 4 ) {
                ((Annotation)evidenceStatement).removeFromIndexes();
              }
            }
            
              
            
          } // end if !aCategory was found
          
          if ( categories.contains( "NotComcog") || categories.contains("NotMFO")) {
            counterEvidence = true;
            break;
          }
        } // end loop through terms
        
        if ( lexicalSemanticType.contains("d163_thinking") && 
            ( buff2.contains(" denies ") ||  buff2.contains(" denied ") )) {
          counterEvidence = true; 
        }
        
        // ----- if looking at d230, need to see both d230 terms and adl terms, otherwise, retract this 
        if (lexicalSemanticType.contains("d230_dailyRoutine")) 
          if (!( d230Seen && adlsSeen ))
            counterEvidence = true;
      
        // retract any mentions if there is counter evidence
        if ( counterEvidence ) {
          if ( mentions != null && !mentions.isEmpty())
            for ( Annotation mention: mentions ) 
              mention.removeFromIndexes();
        }
     
      } // end if there are terms
      
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "categorize", "issue categorizing a comcog mention " + e.toString());
      throw e;
    }
    
    return categoryFound;
    
  } // end Method categorize() ----------
  
// =================================================
  /**
   * aCategoryMatches returns true if any of the categories in a match any of the categories in b
   * Each of the inputs will be a colon delimited set
   * @param pSetA
   * @param pSetB
   * @return boolean
  */
  // =================================================
  private boolean aCategoryMatches(String pSetA, String pSetB) {
    boolean returnVal = false;
    
    if ( pSetA != null && pSetB != null ) {
      HashSet<String> hashA = new HashSet<String>();
    
      String[] setA = U.split(pSetA, ":");
      if ( setA != null && setA.length > 0 )
        for ( String aMember : setA )  hashA.add(aMember.trim());
    
      String[] setB = U.split(pSetB, ":");
      if ( setB != null && setB.length > 0 )
        for ( String bMember : setB) {
          if (hashA.contains(bMember)) {
            returnVal = true;
            break;
          }
        }
      }    
    return returnVal;
  } // end Method aCategoryMatches() ---------------



// =================================================
  /**
   * isPartOfSlotOrSectionName returns true if this term is part of a slot:value slot location
   * 
   * @param pJCas
   * @param term
   * @return boolean
  */
  // =================================================
   private final boolean isPartOfSlotOrSectionName(JCas pJCas, Annotation pTerm) {
    boolean returnVal = false;
    
    List<Annotation> slots = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pTerm.getBegin(), pTerm.getEnd(), false);
    
    if ( slots != null && !slots.isEmpty()) 
       returnVal = true;
    
    
    
    return returnVal;
  } // end Method isPartOfSlot() ------------------



//=================================================
 /**
  * categorizeAux
  * 
  * @param pJCas
  * @param pSentence
  * @param pCategoryTypeIndexId
  * @param pLexicalSemanticType  (More than one category, the first is the icf code, the rest are evidences
  * @return boolean (true if this category was found
  * @throws Exception 
 */
 // =================================================
 private boolean categorizeAux( JCas pJCas,
		 	                    Annotation pSentence,  
		                        Class<?> pComcogClass,
                            int pCategoryTypeIndexID, 
                            Class<?> pComcogEvidenceClass ) throws Exception {
  
   boolean categoryFound = false;
   try {
    String buff2 = pSentence.getCoveredText();
  	String lexicalSemanticTypes = translateComCogClassToLRAGRSemanticType( pComcogClass.getSimpleName());
   
     // if there are any comcog categories already assigned for this sentence, then quit
     List<Annotation> existingIpirCategories = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, pCategoryTypeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
     if ( existingIpirCategories != null && !existingIpirCategories.isEmpty() )
       return true;
     
     String[] evidences = U.split( lexicalSemanticTypes, ":") ;
     
     // find the sentence this mention is within
     List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
     int evidenceCounter = 0;
     List<Annotation> newAnnotations = new ArrayList<Annotation>();
     if ( terms != null && !terms.isEmpty()) {
       
       terms = UIMAUtil.uniqueAnnotations(terms);
       for ( Annotation term : terms ) {
       	String buff = term.getCoveredText();
       	
         String categories = ((LexicalElement) term).getSemanticTypes();
         
         // ---------------- -----------
         if      (  categories != null )   {
        	 for ( int i = 0; i < evidences.length; i++ ) {
        		 if ( categories.contains( evidences[i])) {
        			 
        			 
        			 if ( !categoryFound ) {
        				 @SuppressWarnings("unchecked")
        				 Constructor<?> constructor = pComcogClass.getConstructor(JCas.class);
        				 Object statement = constructor.newInstance(pJCas );
          
        				 createComcogSubcategory( pJCas, pSentence, pSentence,  (Concept) statement, categories);
        				 categoryFound = true;
        				 newAnnotations.add( (Annotation) statement);
        			 }
           
        			 @SuppressWarnings("unchecked")
        			 Constructor<?> constructor = pComcogEvidenceClass.getConstructor(JCas.class);
        			 Object evidenceStatement   = constructor.newInstance ( pJCas);
        			 createComcogSubcategory( pJCas, pSentence, term, (Concept) evidenceStatement, categories);
        			 newAnnotations.add( (Annotation) evidenceStatement);
        			 
        			 // The sentence is not going to have assertion, conditional, attribution , so copy the terms's status
        			 // to the sentences features.  The last term seen's features will be the sentences feature.
        			 
        			 ((Concept) pSentence).setConditionalStatus( ((LexicalElement)term).getConditional() );
        			 ((Concept) pSentence).setAssertionStatus(((LexicalElement)term).getNegation_Status() );
        			 ((Concept) pSentence).setSubjectStatus(((LexicalElement)term).getSubject() );
        			 ((Concept) pSentence).setAttributedToPatient( ((LexicalElement)term).getAttributedToPatient());
        			 ((Concept) pSentence).setHistoricalStatus( ((LexicalElement)term).getHistorical());
        			 
        			
        			
        			
        			 evidenceCounter++;
        		 } // end an evidence found
        	 } // end loop through evidence labels
         } // end if !aCategory was found
       } // end loop through terms
     } // end if there are terms
     
     // --------------------------
     // There should be at least 2 evidences for this to be lajit
     // -------------------------
    if ( evidenceCounter < 2)
    	for ( Annotation badAnnotation : newAnnotations)
    		badAnnotation.removeFromIndexes();
     
   } catch ( Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "categorize", "issue categorizing a comcog mention " + e.toString());
     throw e;
   }
   
   return categoryFound;
   
 } // end Method categorize() ----------
 
  
//=================================================
 /**
  * translateComCogClassToLRAGRSemanticType 
  * 
  * @param pClassName
  * @return String 
 */
 // =================================================
  public static final String translateComCogClassToLRAGRSemanticType(String pClassName ) {
	 
	  String returnVal = null;
	  
	if      ( pClassName.equals("D110_D129_PurposefulSensoryExperiences"))returnVal = "d110_d129_purposefulSensoryExperiences";
  else if ( pClassName.equals("D130_D159_BasicLearning"))               returnVal =                "d130_d159_basicLearning";
  else if ( pClassName.equals("D160_FocusingAttention"))              returnVal =                            "d160_focusing";
	else if ( pClassName.equals("D163_Thinking"))                         returnVal =                          "d163_thinking";
	else if ( pClassName.equals("D166_Reading"))                          returnVal =                        "d166_cogReading";
	else if ( pClassName.equals("D170_Writing"))                          returnVal =                        "d170_cogWriting";
	else if ( pClassName.equals("D172_Calculating"))                      returnVal =                    "d172_cogCalculating"; 
	else if ( pClassName.equals("D175_SolvingProblems"))                  returnVal =                    "d175_ProblemSolving";
	else if ( pClassName.equals("D177_MakingDecisions"))                  returnVal =                   "d177_MakingDecisions";
	else if ( pClassName.equals("D179_ApplyingKnowledgeOther"))           returnVal =            "d179_ApplyingKnowledgeOther";
	else if ( pClassName.equals("D210_D220_UndertakingTasks"))            returnVal = "d210_d220_tasks";
	else if ( pClassName.equals("D230_CarryingOutDailyRoutine"))          returnVal = "d230_dailyRoutine:ActivitiesOfDailyLiving";                    //  "d230_dailyRoutine:ActivitiesOfDailyLiving:SelfCareActivities:DomesticLifeActivities";
	else if ( pClassName.equals("D240_HandlingStress"))                   returnVal =                    "d240_handlingStress";
	else if ( pClassName.equals("D310_D329_ReceivingCommunication"))      returnVal =                 "d310_d329_comReceiving";
	else if ( pClassName.equals("D330_D349_ProducingCommunication"))      returnVal =                 "d330_d349_comProducing";
	else if ( pClassName.equals("D350_D369_ConversationAndDiscussion"))   returnVal =                "d350_d369_comDiscussion";
	else if ( pClassName.equals("AppliedMemory"))                         returnVal =                          "appliedMemory";
	else if ( pClassName.equals("Adaptation"))                            returnVal =                             "adaptation";
	else if ( pClassName.equals("Pacing"))                                returnVal =                                 "pacing";
	else if ( pClassName.equals("Persistence"))                           returnVal =                            "persistence";         

	
	
	return returnVal;
} // end Method translateComCogClassToLRAGRSemanticType() --------------------

 
	 

// =================================================
  /**
   * createComcogSubcategory creates a specific category comcog category annotation
   * 
   * @param pJCas
   * @param pSentence
   * @param pMention
   * @param pStatement
   * @param categoryEvidence
  */
  // =================================================
  private final void createComcogSubcategory(JCas pJCas, Annotation pSentence, Annotation pMention, Concept pStatement, String pCategoryEvidence) {
   
    pStatement.setBegin(pMention.getBegin());
    pStatement.setEnd( pMention.getEnd());
    pStatement.setId("createComcogSubCategory_" + this.categoryCtr++);
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
    
  } // end Method createComcogSubcategory() ------------
  

//=================================================
 /**
  * createAppliedMemorySubcategory creates an applied Memory category comcog category annotation
  * 
  * @param pJCas
  * @param pSentence
  * @param categoryEvidence
 */
 // =================================================
 private final void createAppliedMemorySubcategory(JCas pJCas, Annotation pMention ) {
  
     catalog_other_mentions(pJCas, pMention );
     
     AppliedMemory statement = new AppliedMemory(pJCas);
    statement.setBegin(pMention.getBegin());
    statement.setEnd( pMention.getEnd());
    statement.setId("createComcogSubCategory_" + this.categoryCtr++);
    statement.setCategories( "AppliedMemory");
    statement.setAnnotationSetName(this.annotationSetName);
    statement.addToIndexes();
    
 
   
 } // end Method createIPIRSubcategory() ------------



//=================================================
/**
 * catalog_other_mentions - creates a table of other manual annotations
 * over this area so that I can find out what types are not being made
 * into applied memory
 * 
 * remove this after we get the answer
 * 
 * @param pJCas
 * @param pComcogYesSentence
*/
// =================================================
  private void catalog_other_mentions(JCas pJCas, Annotation pMention)  {
	
	  try {
	  
	  
	  List<Annotation> d110 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D110_D129_PurposefulSensoryExperiences.typeIndexID,  pMention.getBegin(), pMention.getEnd(), false );  
	
	  List<Annotation> d130 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D130_D159_BasicLearning.typeIndexID,                 pMention.getBegin(), pMention.getEnd(), false );                   
      List<Annotation> d160 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D160_FocusingAttention.typeIndexID,                  pMention.getBegin(), pMention.getEnd(), false );                  
      List<Annotation> d163 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D163_Thinking.typeIndexID,                           pMention.getBegin(), pMention.getEnd(), false );                          
      List<Annotation> d166 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D166_Reading.typeIndexID,                            pMention.getBegin(), pMention.getEnd(), false );                            
      List<Annotation> d170 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D170_Writing.typeIndexID,                            pMention.getBegin(), pMention.getEnd(), false );                            
  
      List<Annotation> d172 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D172_Calculating.typeIndexID,                        pMention.getBegin(), pMention.getEnd(), false );                        
      List<Annotation> d175 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D175_SolvingProblems.typeIndexID,                    pMention.getBegin(), pMention.getEnd(), false );                    
      List<Annotation> d177 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D177_MakingDecisions.typeIndexID,                    pMention.getBegin(), pMention.getEnd(), false );                    
      List<Annotation> d179 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D179_ApplyingKnowledgeOther.typeIndexID,             pMention.getBegin(), pMention.getEnd(), false );             
      List<Annotation> d210 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D210_D220_UndertakingTasks.typeIndexID,              pMention.getBegin(), pMention.getEnd(), false );              
  
      List<Annotation> d230 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D230_CarryingOutDailyRoutine.typeIndexID,            pMention.getBegin(), pMention.getEnd(), false );            
      List<Annotation> d240 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D240_HandlingStress.typeIndexID,                     pMention.getBegin(), pMention.getEnd(), false );                     
      List<Annotation> d310 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D310_D329_ReceivingCommunication.typeIndexID,        pMention.getBegin(), pMention.getEnd(), false );        
  
      List<Annotation> d330 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D330_D349_ProducingCommunication.typeIndexID,        pMention.getBegin(), pMention.getEnd(), false );        
      List<Annotation> d350 = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_D350_D369_ConversationAndDiscussion.typeIndexID,     pMention.getBegin(), pMention.getEnd(), false );     
      List<Annotation> adap = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_Adaptation.typeIndexID,                              pMention.getBegin(), pMention.getEnd(), false );                              
   
      List<Annotation> pacing = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_Pacing.typeIndexID,                                  pMention.getBegin(), pMention.getEnd(), false );                                  
    
      List<Annotation>  pers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_Persistence.typeIndexID,                             pMention.getBegin(), pMention.getEnd(), false ); 
      List<Annotation>  aplm = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Manual_AppliedMemory.typeIndexID,                           pMention.getBegin(), pMention.getEnd(), false ); 
      
	 if ( d110 != null && !d110.isEmpty()) this.d110_count++;
	 if ( d130 != null && !d130.isEmpty()) this.d130_count++;
	 if ( d160 != null && !d160.isEmpty()) this.d160_count++;
	 if ( d163 != null && !d163.isEmpty()) this.d163_count++;
	 if ( d166 != null && !d166.isEmpty()) this.d166_count++;
	 if ( d170 != null && !d170.isEmpty()) this.d170_count++;
	 if ( d172 != null && !d172.isEmpty()) this.d172_count++;
	 if ( d175 != null && !d175.isEmpty()) this.d175_count++;
	 if ( d177 != null && !d177.isEmpty()) this.d177_count++;
	 if ( d179 != null && !d179.isEmpty()) this.d179_count++;
	 if ( d210 != null && !d210.isEmpty()) this.d210_count++;
	 if ( d230 != null && !d230.isEmpty()) this.d230_count++;
	 if ( d240 != null && !d240.isEmpty()) this.d240_count++;
	 if ( d310 != null && !d310.isEmpty()) this.d310_count++;
	 if ( d330 != null && !d330.isEmpty()) this.d330_count++;
	 if ( d350 != null && !d350.isEmpty()) this.d350_count++;
	 if ( adap != null && !adap.isEmpty()) this.adap_count++;
	 if ( pacing != null && !pacing.isEmpty()) this.pacing_count++;
	 if ( pers != null && !pers.isEmpty()) this.pers_count++;
	 if ( aplm != null && !pers.isEmpty()) this.aplm_count++;
	
  
	 
  } catch ( Exception e) {}

			  
 } // end method catalog_other_mentions() ------------



// =================================================
  /**
   * setComcogYesAttributes [TBD] summary
   * 
   * @param pJCas
   * @param pComcogYesSentence
  */
  // =================================================
  protected final void setComcogYesAttributes(JCas pJCas, Annotation pComcogYesSentence) {
    
     gov.nih.cc.rmd.inFACT.x.Comcog_yes statement = (gov.nih.cc.rmd.inFACT.x.Comcog_yes)pComcogYesSentence;
     
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD110_d129   ( UIMAUtil.mentionsExist(  pJCas, D110_D129_PurposefulSensoryExperiences.typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.bstract.Comcog_yes)statement).setD130_d159   ( UIMAUtil.mentionsExist(  pJCas, D130_D159_BasicLearning               .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD160        ( UIMAUtil.mentionsExist(  pJCas, D160_FocusingAttention                .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD163        ( UIMAUtil.mentionsExist(  pJCas, D163_Thinking                         .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD166        ( UIMAUtil.mentionsExist(  pJCas, D166_Reading                          .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD170        ( UIMAUtil.mentionsExist(  pJCas, D170_Writing                          .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD172        ( UIMAUtil.mentionsExist(  pJCas, D172_Calculating                      .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD175        ( UIMAUtil.mentionsExist(  pJCas, D175_SolvingProblems                  .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD177        ( UIMAUtil.mentionsExist(  pJCas, D177_MakingDecisions                  .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD179        ( UIMAUtil.mentionsExist(  pJCas, D179_ApplyingKnowledgeOther            .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
     
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD210_d220    ( UIMAUtil.mentionsExist(  pJCas, D210_D220_UndertakingTasks            .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD230         ( 
    		  UIMAUtil.mentionsExist(  pJCas, D230_CarryingOutDailyRoutine          .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD240         ( UIMAUtil.mentionsExist(  pJCas, D240_HandlingStress                   .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD310_d329    ( UIMAUtil.mentionsExist(  pJCas, D310_D329_ReceivingCommunication      .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      
      boolean v = UIMAUtil.mentionsExist(  pJCas, D330_D349_ProducingCommunication     .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true );
      
    
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD330_d349    ( v);
          
      
    		
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setD350_d369    ( UIMAUtil.mentionsExist(  pJCas, D350_D369_ConversationAndDiscussion   .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setApplied_memory( UIMAUtil.mentionsExist(  pJCas, AppliedMemory                         .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setAdaptation     ( UIMAUtil.mentionsExist(  pJCas, Adaptation                              .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setPacing       ( UIMAUtil.mentionsExist(  pJCas, Pacing                                .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
      ((gov.nih.cc.rmd.inFACT.x.Comcog_yes)statement).setPersistence  ( UIMAUtil.mentionsExist(  pJCas, Persistence                           .typeIndexID,   pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), true ));
     
      setAtLeastOneComcogAttribute( statement);
    
   
  } // end Method setComcogYesAttributes() ------------


//=================================================
/**
 * setAtLeastOneComcogAttribute makes sure at least one
 * attribute has been set, if none are set, set a default one
 * 
 * @param pStatement 
 * 
*/
// =================================================
 private final void setAtLeastOneComcogAttribute ( gov.nih.cc.rmd.inFACT.x.Comcog_yes pStatement ){
	  
	  if (    !pStatement.getAdaptation() &&
			  !pStatement.getApplied_memory() &&
			  !pStatement.getPacing() &&
			  !pStatement.getPersistence() &&
			  !pStatement.getD110_d129() &&
			  !pStatement.getD130_d159() &&
			  !pStatement.getD160() &&
			  !pStatement.getD163() &&
			  !pStatement.getD166() &&
			  !pStatement.getD170() &&
			  !pStatement.getD172() &&
			  !pStatement.getD175() &&
			  !pStatement.getD177() &&
			  !pStatement.getD179() &&
			  !pStatement.getD210_d220() &&
			  !pStatement.getD230() &&
			  !pStatement.getD240() &&
			  !pStatement.getD310_d329() &&
			  !pStatement.getD330_d349() &&
			  !pStatement.getD350_d369() )
			  
			 
		  pStatement.setD330_d349(true);   // should be the most popular one  com producting (notes, reports, states ...)
			  
	  
 } //end Method setAtLeastOneIPIRAttribute() --------


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
    
    this.annotationSetName = U.getOption(pArgs, "--comcogAnnotationSetName=", "comcog_categories_rulebased_model");
    
    this.payloadAnnotationSetName = U.getOption(pArgs, "--payloadAnnotationSetName=", "ipir_sentence_model:comcog_sentence_manual:comcog_sentence_model:ipir:comcog:comcog_sentence:ipir_sentence");
    
    this.segmentRelevantFilter = Boolean.parseBoolean( U.getOption(pArgs, "--segmentRelevantFilter=", "true" ));
    
    this.INFACT_MODE = Boolean.valueOf( U.getOption(pArgs, "--INFACT_MODE=", "false"));
  
    loadCategories( mentalFunctioningOntologyCategories );
   
      
  } // end Method initialize() -------
 
  // =================================================
  /**
   * createMentalFunctioningEvidence  -- secret sauce here
   * 
   
   *
   *
   * @return boolean
  */
  // =================================================
  private void loadCategories (String pMentalFunctioningOntologyCategories) {
    
  String cols[] = U.split( pMentalFunctioningOntologyCategories, ":" );
  
  if ( cols != null && cols.length > 0 ) 
    for ( String kind : cols )
      if ( kind != null && kind.trim().length() > 0 )
        this.keptCategories.add ( kind.trim() );
    
    
  } // end Method loadCategories() ------------------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   private HashSet<String> keptCategories = new HashSet<String>();
   private int categoryCtr = 0;
   private String annotationSetName = "comcog_categories_rulebased_model";
   private String payloadAnnotationSetName = "comcog_sentence";
   private boolean INFACT_MODE = false;
   private boolean segmentRelevantFilter = true;
   
   int D110_CAT_SENSORY = 0;
   int D130_CAT_LEARNING = 1;
   int D160_CAT_FOCUSING_ATTENTION = 2;
   int D163_CAT_THINKING = 3;
   int D166_CAT_READING = 4;
   int D170_CAT_WRITING = 5;
   int D172_CAT_CAL     = 6;
   int D175_CAT_SOLVING  = 7;
   int D177_CAT_DECISIONS = 8;
   int D179_CAT_OTHER = 9;
   int D210_CAT_TASKS = 10;
   int D230_CAT_DAILY_ROUTINES = 11;
   int D240_CAT_STRESS = 12;
   int D310_CAT_COM_RECEIVING = 13;
   int D330_CAT_COM_PRODUCTING = 14;
   int D350_CAT_COM_DISCUSSION = 15;
   int CAT_ADAPTATION_= 16;
   int CAT_APPLIED_MEMORY = 17;
   int CAT_PACING = 18;
   int CAT_PERSISTENCE = 19;
 int d110_count = 0;
 int d130_count = 0;
 int d160_count = 0;
 int d163_count = 0;
 int d166_count = 0;
 int d170_count = 0;
 int d172_count = 0;
 int d175_count = 0;
 int d177_count = 0;
 int d179_count = 0;
 int d210_count = 0;
 int d230_count = 0;
 int d240_count = 0;
 int d310_count = 0;
 int d330_count = 0;
 int d350_count = 0;
 int adap_count = 0;
 int aplm_count = 0;
 int pacing_count = 0;
 int pers_count = 0;
   public static final String InFACTComcogCategoriesOutputTypes = 
       MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyOutputTypes + ":" +
           "ComCogCategories" + ":" +
           "D110_D129_PurposefulSensoryExperiences" + ":" + 
           "D130_D159_BasicLearning"                + ":" + 
           "D160_FocusingAttention"                 + ":" + 
           "D163_Thinking"                          + ":" +
           "D166_Reading"                           + ":" +
           "D170_Writing"                           + ":" +
           "D172_Calculating"                       + ":" +
           "D175_SolvingProblems"                   + ":" +
           "D210_D220_UnderstandingTasks"           + ":" +
           "D230_CarryingOutDailyRoutine"           + ":" +
           "D240_HandlingStress"                    + ":" +
           "D310_D329_ReceivingCommunication"       + ":" +
           "D330_D349_ProducingCommunication"       + ":" +
           "D350_D369_ConversationAndDiscussion"    + ":" +
           "AppliedMemory"                          + ":" +
           "Adaption"                               + ":" +
           "Pacing"                                 + ":" +
           "Persistence"                            + ":" +
           "ComcogEvidence"                         + ":" +
           "Comcog_no"                              + ":" +
           "Comcog_yes"                             + ":" +
           "ComCogActivities"                       + ":" +
           "manual.Comcog_no"                       + ":" +
           "manual.Comcog_yes"    ;                       
           
       
  
  
} // end Class LineAnnotator() ---------------

