// =================================================
/**
 * ManualIPIRFeataurestoAnnotations creates annotations for 
 * the features for the IPIR_yes features so that
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

import gov.nih.cc.rmd.inFACT.D730_Manual;
import gov.nih.cc.rmd.inFACT.D7400_Manual;
import gov.nih.cc.rmd.inFACT.D740_Manual;
import gov.nih.cc.rmd.inFACT.D750_Manual;
import gov.nih.cc.rmd.inFACT.D760_Manual;
import gov.nih.cc.rmd.inFACT.D770_Manual;
import gov.nih.cc.rmd.inFACT.D779_Manual;
import gov.nih.cc.rmd.inFACT.Interaction_Manual;
import gov.nih.cc.rmd.inFACT.manual.IPIRyes;
import gov.nih.cc.rmd.inFACT.x.IPIR_yes;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Gold;




public class MakeGoldFromIPIRFeatures extends JCasAnnotator_ImplBase {
 
  
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
    
    List<Annotation> IPIRYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.IPIRyes.typeIndexID , false);
    
    
    if (IPIRYess != null && !IPIRYess.isEmpty() )
      for ( Annotation ipirYes : IPIRYess ) {
    	  
    	  String buff = ipirYes.getCoveredText();
    	 
    	 if ( ((IPIRyes) ipirYes).getD730() )        { D730_Manual statement = new D730_Manual( pJCas);               createIPIRAnnotation(pJCas, "D730_Manual",        statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getD740() )        { D740_Manual statement = new D740_Manual( pJCas);               createIPIRAnnotation(pJCas, "D740_Manual",        statement, ipirYes ); } 	 
    	 if ( ((IPIRyes) ipirYes).getD7400())        { D7400_Manual statement= new D7400_Manual( pJCas);              createIPIRAnnotation(pJCas, "D7400_Manual",       statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getD750() )        { D750_Manual statement = new D750_Manual( pJCas);               createIPIRAnnotation(pJCas, "D750_Manual",        statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getD760() )        { D760_Manual statement = new D760_Manual( pJCas);               createIPIRAnnotation(pJCas, "D760_Manual",        statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getD770() )        { D770_Manual statement = new D770_Manual( pJCas);               createIPIRAnnotation(pJCas, "D770_Manual",        statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getD779() )        { D779_Manual statement = new D779_Manual( pJCas);               createIPIRAnnotation(pJCas, "D779_Manual",        statement, ipirYes ); }
    	 if ( ((IPIRyes) ipirYes).getInteraction() ) { Interaction_Manual statement = new Interaction_Manual( pJCas); createIPIRAnnotation(pJCas, "Interaction_Manual", statement, ipirYes ); }
   	   
        
    	 // Create a uima ipir yes to work from.
    	   createIPIRYes( pJCas, ((gov.nih.cc.rmd.inFACT.manual.IPIRyes) ipirYes), this.annotationSetName);
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
   
  // =================================================
  /**
   * createIPIRAnnotation
   * 
   * @param pJCas
   * @param pStatement
   * @param pIPIRYes
   */
  // =================================================
  private final Annotation createIPIRAnnotation(JCas pJCas, String pName, Annotation pStatement, Annotation pIPIRYes) {

    pStatement.setBegin(pIPIRYes.getBegin());
    pStatement.setEnd(pIPIRYes.getEnd());
    pStatement.addToIndexes();

    if (this.evalGoldFocus.contains(pName) || this.evalGoldFocus.replace("-", "_").contains(pName)) {
      Gold statement = new Gold(pJCas);
      statement.setBegin(pIPIRYes.getBegin());
      statement.setEnd(pIPIRYes.getEnd());
      ((Concept) statement).setAnnotationSetName(this.annotationSetName);
      String evidence = getEvidence(pJCas, pIPIRYes);
      statement.setEvidence(evidence);

      statement.addToIndexes();
    }

    return pStatement;
  } // end Method createBehaviorEvidence() ------------

  // -----------------------------------------
  /**
   * createIPIRYes creates a new UIMA annotation that is the IPIRYes
 * @param ipirAnnotationSetName 
   * 
   * @param pJcas
   * @param pManualIPIR_yes
   * @param pIpirAnnotationSetName
   * 
   */
  // -----------------------------------------
  final static Annotation createIPIRYes(JCas pJCas, 
      gov.nih.cc.rmd.inFACT.bstract.IPIR_yes pManual_IPIR_yes, 
		  String pIpirAnnotationSetName) {

    gov.nih.cc.rmd.inFACT.x.IPIR_yes statement = new gov.nih.cc.rmd.inFACT.x.IPIR_yes(pJCas);
    statement.setBegin(pManual_IPIR_yes.getBegin());
    statement.setEnd(pManual_IPIR_yes.getEnd());
    statement.setId(pManual_IPIR_yes.getId());
    statement.setD730(false);
    statement.setD740(false);
    statement.setD7400(false);
    statement.setD750(false);
    statement.setD760(false);
    statement.setD770(false);
    statement.setD779(false);
    statement.setInteraction(false);

    statement.setTimeBucket(pManual_IPIR_yes.getTimeBucket());
    statement.setDifficult_to_determine(pManual_IPIR_yes.getDifficult_to_determine());
    statement.setAnnotationSetName(pIpirAnnotationSetName);
    statement.addToIndexes();

    return statement;

  } // end Method createIPIRYes() -------------------
  
  
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

  // ----------------------------------
  /**
   * getEvidence returns the overlapping manual annotation kinds
   * 
   * @return String
   * 
   **/
  // ----------------------------------
  private final String getEvidence(JCas pJCas, Annotation pMention) {
    HashSet<String> activityHash = new HashSet<String>();
    String returnVal = null;

    // get the d codes this mention has
    List<Annotation> activities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRActivities.typeIndexID, pMention.getBegin(),
        pMention.getEnd(), true);

    if (activities != null && !activities.isEmpty())
      for (Annotation activity : activities) {
        String className = activity.getClass().getSimpleName();
        if (className != null && !className.contains("yes"))
          activityHash.add(className);
      }
    if (!activityHash.isEmpty())
      returnVal = "~" + activityHash.toString();
   
    return returnVal;

  } // end Method getEvidence() --------------

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
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
    this.annotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_subcategory_model");

    
    
  
    
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
   String annotationSetName = "ipir_subcategory_model";
  
   
  
} // end Class LineAnnotator() ---------------

