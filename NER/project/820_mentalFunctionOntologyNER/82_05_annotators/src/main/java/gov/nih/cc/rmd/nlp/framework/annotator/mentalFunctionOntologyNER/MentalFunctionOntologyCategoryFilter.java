// =================================================
/**
 * MentalFunctionOntologyNERAnnotator creates MentalFunction ontology derived annotatons in text
 * 
 * @author  G.o.d.
 * @created 2022.03.28
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

import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioningOntology;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class MentalFunctionOntologyCategoryFilter extends JCasAnnotator_ImplBase {
 
  
  
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
    
    
    
    List<Annotation> mfoMentions = UIMAUtil.getAnnotations(pJCas, MentalFunctioningOntology.typeIndexID, true );
    
    if ( mfoMentions != null && !mfoMentions.isEmpty())
      for ( Annotation mention : mfoMentions ) 
        if ( !keepMention( mention ))
          mention.removeFromIndexes();
      
     
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "process", "Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


  // =================================================
  /**
   * keepCategories
   * 
   * @param pMention
   *
   * @return boolean
  */
  // =================================================
  private boolean keepMention ( Annotation pMention) {
    
    boolean returnVal = false;
    String conceptName = ((MentalFunctioningOntology)pMention).getClass().getName();
    if ( conceptName != null )
      conceptName = U.getClassName(conceptName);
    
    
    if ( this.keptCategories.contains("all"))
      returnVal= true;
    else 
      returnVal = this.keptCategories.contains( conceptName);
    
    if ( !returnVal ) {
      String kind = getKind ( conceptName );
      if ( keptCategories.contains( kind))
        returnVal = true;
    }
      
    return returnVal;
   } // end Method keepMention() ------------------


  // =================================================
  /**
   * getKind
   * 
   * @param pConceptName
   * 
  */
  // =================================================
  private String getKind( String pConceptName ) {
   
    String returnVal = "none";
    
    switch (  pConceptName ) {
    
     //  *  ------   Context&Environment
     case "ExternalFactors"              :
     case "ContextualFactors"            :
     case "EnvironmentalFactors"         :
     case "Context_"                     :
     case "Environment_"                 : returnVal = "Context&Environment" ; break;
     // * ------ Throughput 
     case "PersonFactors"                :  
     case "PersonalBackgroundFactors"    :
     case "HealthCondition_"             :
     case "HealthConditionFactors"       :
     case "BodyStructures"               :
     case "BodyFunctions"                :
     case "SensoryProcessing"            :
     case "SensoryMotorFunctionFunctions":
     case "NeuroCognitiveProcessing"     :
     case "MentalFunctions_"             :
     case "GlobalMentalFunctions"        :
     case "SpecificMentalFunctions"      :
     case "CognitiveFunctions"           :
     case "CommunicationFunctions"       :
     case "FeedbackAppraisal"            :    returnVal = "Throughput"; break;
     // * ------- Output
     case "MentalFunctioning"            :
     case "ObservableBehavior"           :
     case "ActivitiesAndParticipation"   :
     case "Activities_"                  :
     case "Participation_"               :
     case "Behavior_"                    :
     case "ActivitiesOfDailyLiving"      :
     case "CommunicationActivities"      :
     case "CognitiveActivities"          :
     case "CognitionActivities"          :
     case "LearningAndApplyingKnowledge" :
     case "Learning_"                    :
     case "ApplyingKnowledge_"           :
     case "GeneralTasksAndDemands"       :
     case "MobilityActivities"           :
     case "SelfCareActivities"           :
     case "DomesticLifeActivities"       :
     case "IPIRActivities"               :
     case "MajorLifeAreasActivities"     :
     case "CommunityActivities"          :
     case "NonActions"                   :
     case "AdaptiveBehavior"             :
     case "MaladaptiveBehavior"          :  returnVal = "Output";   break;
      // * ---------  Feedback
     case "Feedback_"                    :  returnVal = "Feedback"; break;
     default                             :  returnVal = "none";
    }
    
    return returnVal;
  } // end Method getKind() -------------------------



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
   
    loadCategories( mentalFunctioningOntologyCategories );
   
      
  } // end Method initialize() -------
 
  // =================================================
  /**
   * createMentalFunctioningEvidence  -- secret sauce here
   * 
   * ------   Context&Environment
   * ------   Throughput 
   * ------   Output
   * ------   Feedback
   * 
   * 
   * @param pMentalFunctioningOntologyCategories  a colon separated list of
   *  ------   Context&Environment
   *    ExternalFactors
   *    ContextualFactors
   *    EnvironmentalFactors  
   *    Context_
   *    Environment_
   * ------ Throughput 
   *    PersonFactors    
   *    PersonalBackgroundFactors
   *    HealthCondition_
   *    HealthConditionFactors
   *    BodyStructures
   *    BodyFunctions
   *    SensoryProcessing
   *    SensoryMotorFunctionFunctions
   *    NeuroCognitiveProcessing
   *    MentalFunctions_
   *    GlobalMentalFunctions
   *    SpecificMentalFunctions
   *    CognitiveFunctions
   *    CommunicationFunctions
   *    FeedbackAppraisal
   * ------- Output
   *    MentalFunctioning
   *    ObservableBehavior
   *    ActivitiesAndParticipation
   *    Activities_
   *    Participation_
   *    Behavior_
   *    ActivitiesOfDailyLiving
   *    CommunicationActivities
   *    CognitiveActivities
   *    CognitionActivities
   *    LearningAndApplyingKnowledge
   *    Learning_
   *    ApplyingKnowledge_
   *    GeneralTasksAndDemands
   *    MobilityActivities
   *    SelfCareActivities
   *    DomesticLifeActivities
   *    IPIRActivities
   *    MajorLifeAreasActivities
   *    CommunityActivities
   *    NonActions
   *    AdaptiveBehavior
   *    MaladaptiveBehavior
   * ---------  Feedback
   *    Feedback_  
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
  
   
  
} // end Class LineAnnotator() ---------------

