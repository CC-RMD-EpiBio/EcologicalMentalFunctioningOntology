// =================================================
/**
 * ManualComCogFeataurestoAnnotations creates annotations for 
 * the features for the ComCog_yes features so that
 * we can evaluate individually
 *   
 * 
 * @author  GD
 * @created 2023.05.17
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

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
import gov.nih.cc.rmd.inFACT.Pacing;
import gov.nih.cc.rmd.inFACT.Persistence;
import gov.nih.cc.rmd.inFACT.bstract.Comcog_yes;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ComCogActivities;
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


import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Gold;




public class MakeGoldFromComCogFeatures extends JCasAnnotator_ImplBase {
 
  
  
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
    
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes.typeIndexID , false);
                                                                 
    
    if (comCogYess != null && !comCogYess.isEmpty() )
      for ( Annotation comcogYes : comCogYess ) {
    	  
    	  // create a non manual comcog yes that will be picked up by the ComCogCategories.
    	  
    	  
    	  
    	  String buff = comcogYes.getCoveredText();
    	 
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD110_d129   () )  { D110_D129_PurposefulSensoryExperiences statement = new Manual_D110_D129_PurposefulSensoryExperiences( pJCas);  createComCogAnnotation(pJCas, "d110_d129", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD130_d159   () )  { D130_D159_BasicLearning                statement = new Manual_D130_D159_BasicLearning( pJCas);                 createComCogAnnotation(pJCas, "d130_d159", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD160        () )  { D160_FocusingAttention                 statement = new Manual_D160_FocusingAttention( pJCas);                  createComCogAnnotation(pJCas, "d160",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD163        () )  { D163_Thinking                          statement = new Manual_D163_Thinking( pJCas);                           createComCogAnnotation(pJCas, "d163",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD166        () )  { D166_Reading                           statement = new Manual_D166_Reading( pJCas);                            createComCogAnnotation(pJCas, "d166",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD170        () )  { D170_Writing                           statement = new Manual_D170_Writing( pJCas);                            createComCogAnnotation(pJCas, "d170",      statement, comcogYes );	}
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD172        () )  { D172_Calculating                       statement = new Manual_D172_Calculating( pJCas);                        createComCogAnnotation(pJCas, "d172",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD175        () )  { D175_SolvingProblems                   statement = new Manual_D175_SolvingProblems( pJCas);                    createComCogAnnotation(pJCas, "d175",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD177        () )  { D177_MakingDecisions                   statement = new Manual_D177_MakingDecisions( pJCas);                    createComCogAnnotation(pJCas, "d177",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD179        () )  { D179_ApplyingKnowledgeOther            statement = new Manual_D179_ApplyingKnowledgeOther( pJCas);             createComCogAnnotation(pJCas, "d179",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD210_d220   () )  { D210_D220_UndertakingTasks             statement = new Manual_D210_D220_UndertakingTasks( pJCas);              createComCogAnnotation(pJCas, "d210_d220", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD230        () )  { D230_CarryingOutDailyRoutine           statement = new Manual_D230_CarryingOutDailyRoutine( pJCas);            createComCogAnnotation(pJCas, "d230",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD240        () )  { D240_HandlingStress                    statement = new Manual_D240_HandlingStress( pJCas);                     createComCogAnnotation(pJCas, "d240",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD310_d329   () )  { D310_D329_ReceivingCommunication       statement = new Manual_D310_D329_ReceivingCommunication( pJCas);        createComCogAnnotation(pJCas, "d310_d329", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD330_d349   () )  { D330_D349_ProducingCommunication       statement = new Manual_D330_D349_ProducingCommunication( pJCas);        createComCogAnnotation(pJCas, "d330_d349", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getD350_d369   () )  { D350_D369_ConversationAndDiscussion    statement = new Manual_D350_D369_ConversationAndDiscussion( pJCas);     createComCogAnnotation(pJCas, "d350_d369", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getApplied_memory()) { AppliedMemory                          statement = new Manual_AppliedMemory( pJCas);                           createComCogAnnotation(pJCas, "AppliedMemory", statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getAdaptation   () ) { Adaptation                             statement = new Manual_Adaptation( pJCas);                              createComCogAnnotation(pJCas, "Adaption",      statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getPacing       () ) { Pacing                                 statement = new Manual_Pacing( pJCas);                                  createComCogAnnotation(pJCas, "Pacing",        statement, comcogYes ); }
        if ( ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getPersistence  () ) { Persistence                            statement = new Manual_Persistence( pJCas);                             createComCogAnnotation(pJCas, "Persistence",   statement, comcogYes ); }
    		
       // Create a uima comCog yes to work from.

        createComCogYes( pJCas, ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes), this.annotationSetName);

      }
        
   
    // -------------------------
    // now that the gold is made, add some evidence to help debugging
    addEvidenceToGold( pJCas);
    
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

  // -----------------------------------------
  /**
   * createComCogYes creates a new UIMA annotation that is the comCogYes 
   * 
   * @param pJcas
   * @param pManualComCog_yes
   * 
   */
  // -----------------------------------------
   final static Annotation createComCogYes(JCas pJCas, Comcog_yes comcogYes, String pAnnotationSetName) {
        	
	  gov.nih.cc.rmd.inFACT.x.Comcog_yes statement = new gov.nih.cc.rmd.inFACT.x.Comcog_yes( pJCas);
	  statement.setBegin( comcogYes.getBegin());
	  statement.setEnd( comcogYes.getEnd());
	  statement.setId( comcogYes.getId());
	  statement.setAnnotationSetName(pAnnotationSetName);
	  statement.setD110_d129  ( false );
	  statement.setD130_d159  ( false );      
	  statement.setD160  ( false );              
	  statement.setD163  ( false );                       
	  statement.setD166   ( false );          
	  statement.setD170   ( false );     
	  statement.setD172   ( false );   
	  statement.setD175    ( false );  
	  statement.setD177   ( false );   
	  statement.setD179   ( false );     
	  statement.setD210_d220 ( false );    
	  statement.setD230      ( false );     
	  statement.setD240      ( false );  
	  statement.setD310_d329 ( false );   
	  statement.setD330_d349  ( false );  
	  statement.setD350_d369   ( false ); 
	  statement.setApplied_memory  ( false );
	  statement.setAdaptation    ( false );   
	  statement.setPacing        ( false );
	  statement.setPersistence   ( false );
	  
	  statement.setTimeBucket( comcogYes.getTimeBucket());
	  statement.setDifficult_to_determine(comcogYes.getDifficult_to_determine());
	  statement.setAnnotationSetName(pAnnotationSetName);
	  statement.addToIndexes();
      
      
	  return statement;
	  
	  
} // end Method createComCogYes() -------------------

		// =================================================
        /**
         * createComCogAnnotation 
         * 
         * @param pJCas
         * @param pStatement 
         * @param pMention
        */
        // =================================================
           private final Annotation createComCogAnnotation(JCas pJCas, String pName, Annotation pStatement, Annotation pMention) {
          
           pStatement.setBegin( pMention.getBegin());
           pStatement.setEnd( pMention.getEnd());
           pStatement.addToIndexes();
          
          
          
		if ( this.evalGoldFocus.contains( pName )  || this.evalGoldFocus.replace("-","_").contains(pName)) {
        	  Gold statement = new Gold ( pJCas);
        	  statement.setBegin( pMention.getBegin() );
        	  statement.setEnd(  pMention.getEnd() );
        	  ((Concept)statement).setAnnotationSetName  ( this.annotationSetName );
        	  String evidence = getEvidence ( pJCas, pMention);
        	  statement.setEvidence( evidence );
        	  
        	  statement.addToIndexes();
		}

          return pStatement;
        }  // end Method createBehaviorEvidence() ------------


  //----------------------------------
  /**
   * addEvidenceToGold  
   * @throws Exception
   * 
   **/
  // ----------------------------------
  private final void addEvidenceToGold(JCas pJCas ) throws Exception {
      
	 
 List<Annotation> golds = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID );
	  
 if ( golds != null && !golds.isEmpty())
		  for (Annotation gold: golds ) {
			  String evidences = getEvidence( pJCas, gold);
			  if ( evidences != null) 
				  ((Gold)gold).setEvidence( "~" + evidences);
			  
		  }
	  
  } // end Method addEvidenceToGold() -------
  
  //----------------------------------
  /**
   * getEvidence returns the overlapping manual annotation kinds
   * @return String
   * 
   **/
 // ----------------------------------
 private final String getEvidence(JCas pJCas, Annotation pMention) {
	 HashSet<String> activityHash = new HashSet<String>();
	 String returnVal = null;
	 
	 // get the d codes this mention has
	  List<Annotation> activities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ComCogActivities.typeIndexID, pMention.getBegin(), pMention.getEnd(), true );
	  
	  if ( activities != null && !activities.isEmpty())
		  for (Annotation activity: activities ) {
			  String className = activity.getClass().getSimpleName();
			  if ( className != null  && !className.contains("yes"))
				  activityHash.add( className) ;
		  }
	  if ( !activityHash.isEmpty())
		  returnVal = "~" + activityHash.toString();
	  //GLog.println("grep Me: " + returnVal);
	  
	  return returnVal;
	  
	 
 } // end Method getEvidence() --------------


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
   *                
   *   This has an optional parameter, a resource directory with pattern files in it
   *   --pagecategoryPatternsDir= ./resources/vinciNLPFramework/pageFiltering
   *
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    this.evalGoldFocus  =  U.getOption(pArgs, "--evalGoldFocus=" ,"D730_Manual" );
    this.annotationSetName      = U.getOption(pArgs, "--goldAnnotationSetName=" ,"comcog_sentence_manual") ;

    
  
    
    try {
      
    
    } catch (Exception e) {
      throw new ResourceInitializationException();
    }
   
   
      
  } // end Method initialize() -------
 
  


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   String evalGoldFocus = null;
   String annotationSetName = "efficacy";
  
   
  
} // end Class LineAnnotator() ---------------

