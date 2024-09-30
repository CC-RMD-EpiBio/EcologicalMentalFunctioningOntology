// =================================================
/**
 * MakeComcogCopperAnnotator creates Copper annotations for x.comcog_yes
 * sentences
 *   
 * 
 * @author  GD
 * @created 2023.10.05
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.mentalFunctionOntologyNER.ComCogActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Copper;




public class MakeComcogCopperAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
   
    List<Annotation> focusAnnotations = UIMAUtil.getAnnotations(pJCas,gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID , false);
    
    if (focusAnnotations != null && !focusAnnotations.isEmpty() )
      for ( Annotation focusAnnotation : focusAnnotations ) {
    
    	  String aCategoryName = focusAnnotation.getClass().getSimpleName();
    	 String evidences = getEvidences( pJCas, focusAnnotation, "ComCogActivities");
    	 
    	 if ( evidences == null )
    		 evidences = getEvidencesAll( pJCas, focusAnnotation);
    	 createCopperAnnotation( pJCas, aCategoryName, focusAnnotation, evidences);
      }
        
  
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   
//=================================================
  /**
   * getEvidences accumulates the surface forms that are the evidence for this annotation 
   * 
   * @param pJCas
   * @param pStatement 
   * @param pEvalFocus
   * 
  */
  // =================================================
  private String getEvidences(JCas pJCas, Annotation pStatement, String pEvalFocus ) {
	  String returnVal = null;
	  StringBuffer buff = new StringBuffer();
	  
	  String aFocus = "ComcogActivities";
	 
	  try {
		  List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pStatement.getBegin(), pStatement.getEnd(), true );
		  if ( terms != null && !terms.isEmpty() )
			  for ( Annotation term : terms ) {
				  String categories = ((LexicalElement)term).getSemanticTypes();
				  if ( categories != null && !categories.isBlank()) {
					    if ( MentalFunctionOntologyNERAnnotator.isComCogActivities(categories) ) {
							  buff.append(term.getCoveredText().trim().toLowerCase() + ":" );
							  continue;
					    }
				  }
			  }
		  
		  if ( buff != null && buff.toString().length() > 0 )
			  returnVal = buff.toString();
	  } catch (Exception e) {
		  e.printStackTrace();
	      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getEvidences", "Issue with getting evidence " + e.toString());
	   //   throw new AnalysisEngineProcessException();
	  }
	  
	  return returnVal;
} // end METHOD getEvidences() ---------------------
  
  
//=================================================
  /**
   * getEvidences accumulates the surface forms that are the evidence for this annotation 
   * 
   * @param pJCas
   * @param pStatement 
   * @param pEvalFocus
   * 
  */
  // =================================================
  private String getEvidencesAll(JCas pJCas, Annotation pStatement  ) {
	  String returnVal = null;
	  HashSet<String> buff = new HashSet<String>();
	  
	
	  try {
		  List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pStatement.getBegin(), pStatement.getEnd(), true );
		  if ( terms != null && !terms.isEmpty() )
			  for ( Annotation term : terms ) {
				  String categories = ((LexicalElement)term).getSemanticTypes();
				  if ( categories != null && !categories.isBlank()) {
					  String evalFocuses[] = U.split( categories, ":");
					  if ( evalFocuses != null && evalFocuses.length > 0 )
						  for (String aFocus: evalFocuses )
							  if ( aFocus != null )
								  buff.add(term.getCoveredText().trim().toLowerCase()  );
				  }
			  }
		  
		  if ( buff != null && buff.toString().length() > 0 ) {
			  Iterator<String> keys = buff.iterator();
			  StringBuffer buff2 = new StringBuffer();
			  while (keys.hasNext())
				 buff2.append( keys.next() + ":" );
		  
			  returnVal = buff2.toString();
		  }
		  
		  // get the d codes this mention has
		  HashSet<String> classNames = new HashSet<String>();
		  List<Annotation> activities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ComCogActivities.typeIndexID, pStatement.getBegin(), pStatement.getEnd(), true );
		  if ( terms != null && !terms.isEmpty() )
			  for ( Annotation activity : activities ) {
				  String aName = activity.getClass().getSimpleName();
				  if ( aName != null && !aName.contains("Evidence"))
					  classNames.add(aName);
			  }
		  returnVal = returnVal + "~";
		  if ( classNames != null )
			  returnVal = returnVal + classNames.toString();
		  GLog.println("grep Me: " + returnVal);
		  
	  } catch (Exception e) {
		  e.printStackTrace();
	      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getEvidences", "Issue with getting evidence " + e.toString());
	   //   throw new AnalysisEngineProcessException();
	  }
	  
	  return returnVal;
} // end METHOD getEvidences() ---------------------
 
  // =================================================
  /**
   * createCopperAnnotation 
   * 
   * @param pJCas
   * @param pStatement 
   * @param pMention
   * @param pEvidences;
   */
  // =================================================
  private final Annotation createCopperAnnotation(JCas pJCas, String pName,  Annotation pMention, String pEvidences) {
          
         
        	  Copper statement = new Copper ( pJCas);
        	  statement.setBegin( pMention.getBegin() );
        	  statement.setEnd(  pMention.getEnd() );
        	  statement.setOtherInfo(pName);
        	  statement.setEvidence( pEvidences);
        	  String assertionstatus =  ((Concept)pMention).getAssertionStatus();
        	  boolean attribution =  ((Concept)pMention).getAttributedToPatient();
        	  boolean conditionalStatus = ((Concept)pMention).getConditionalStatus();
        	  boolean historical = ((Concept)pMention).getHistoricalStatus();
        	  String subjectStatus = ((Concept)pMention).getSubjectStatus();
        	  statement.setAssertionStatus( assertionstatus);
        	  statement.setAttributedToPatient( attribution );
        	  statement.setHistoricalStatus(historical);
        	  statement.setSubjectStatus(subjectStatus);
        	 
        	  statement.setConditionalStatus(conditionalStatus);
        		
        			
        	  ((Concept)statement).setAnnotationSetName  ( this.annotationSetName );
        	  
        	  statement.addToIndexes();
		
        	  return statement;
        }  // end Method createCopperAnnotation() ------------


           
           
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
    this.evalCopperFocus  =  U.getOption(pArgs, "--evalCopperFocus=" ,"ComCogActivities" );
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
   String evalCopperFocus = null;
   String annotationSetName = "efficacy";
  
   
  
} // end Class LineAnnotator() ---------------

