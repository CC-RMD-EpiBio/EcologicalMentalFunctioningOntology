// =================================================
/**
 * MakeComCogGoldFromManualAnnotations creates gold annotations 
 * from comcog_sentence_manual:ComCog_yes with
 * 
 * comcog_sentence_rulebased_model:ComCog_yes
 * 
 * and 
 * 
 * comcog_setnence_model:ComCog_yes
 *  
 *   
 * 
 * @author  GD
 * @created 2023.10_05
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




public class MakeComCogGoldFromManualAnnotations extends JCasAnnotator_ImplBase {
 
  
  
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
    	  
    	  // Make sure other annotations that are coming in from the gate file that are not manual
    	  // get filtered out.  
    	  // I was picking up the sentence_model versions of comcog_yes by mistake.
    	 String setName = ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes).getAnnotationSetName();
    	 if ( setName.toLowerCase().contains("manual")  || setName.toLowerCase().equals("comcog_sentence")){
    	 
    	 	// Create a uima comCog yes to work from.
    	 	createGold( pJCas, ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes));
    	 }
      }
        
   
    // ------------------------
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
   * createGold creates Gold annotaton
   * 
   * @param pJcas
   * @param pManualComCog_yes
   * 
   */
  // -----------------------------------------
   final static Annotation createGold(JCas pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes pManual_ComCog_yes) {
        	
	  Gold statement = new Gold( pJCas);
	  statement.setBegin( pManual_ComCog_yes.getBegin());
	  statement.setEnd( pManual_ComCog_yes.getEnd());
	  statement.setId( pManual_ComCog_yes.getId());
	 // statement.setEvidence(null);
	  statement.addToIndexes();
      
      
	  return statement;
	  
	  
} // end Method createGold() -------------------

	

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
    this.evalGoldFocus  =  U.getOption(pArgs, "--evalGoldFocus=" ,"ComCog_yes" );
    this.annotationSetName      = U.getOption(pArgs, "--evaluationAnnotationSetName=" ,"evaluation") ;
    
    
  
    
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

