// =================================================
/**
 * MakeGoldAndCopperFromManualModel creates annotations for
 * the features for the xxxx_yes features that come from the manual (gold), and model (copper) so that
 * we can evaluate individually
 * 
 * We can only look at one gold/copper attribute at at time
 * 
 * "goldAnnotationSetName="
 * "copperAnnotationSetName="
 * "evaluateAnnotationName="
 * "evaluateAttributeName="
 * 
 * 
 * 
 * @author GD
 * @created 2023.05.17
 *
 * 
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.inFACT.x.Comcog_yes;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ComCogActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Section;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.Gold;

public class MakeGoldAndCopperFromManualModel extends JCasAnnotator_ImplBase {

  public static int _adaptation_ctr = 0;

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
       
       

      String goldAnnotationSetName = U.getOption(this.args, "--goldAnnotationSetName=", "");
      String copperAnnotationSetName = U.getOption(this.args, "--copperAnnotationSetName=", "");
      String copperEvaluateAnnotationName = U.getOption(this.args, "--copperEvaluateAnnotationName=", "gov.nih.cc.rmd.inFACT.x.Comcog_yes");
      String goldEvaluateAnnotationName = U.getOption(this.args, "--goldEvaluateAnnotationName=", "gov.nih.cc.rmd.inFACT.manual.Comcog_yes");
      String evaluateAttributeName = U.getOption(this.args, "--evaluateAttributeName=", "");
     
      
      // This code is there because there are differences between the annotation set names 
      // between ipir, comcog training, testing, and the deliverable a
      // Hopefully, by passing in the gold, copper set names, and attribute names, 
      // and handling that either at the pipeline or command line level, the following
      // code is now obsolete
      // 
      // obs_code(goldAnnotationSetName );
    
      List<Annotation> goldSetAnnotations = getAnnotationsFor(pJCas, goldAnnotationSetName, goldEvaluateAnnotationName);
      List<Annotation> copperSetAnnotations = getAnnotationsFor(pJCas, copperAnnotationSetName, copperEvaluateAnnotationName);
      
      List<Annotation> sectionsAnnotations = UIMAUtil.getAnnotations(pJCas, SectionName.typeIndexID, true );
   
      if ( goldSetAnnotations != null && !goldSetAnnotations.isEmpty())
      for (Annotation anAnnotation : goldSetAnnotations) {
   
          String featureValue = UIMAUtil.getFeatureValueByName(anAnnotation, evaluateAttributeName);
          if (featureValue != null && featureValue.toLowerCase().equals("true")) {
            createGold(pJCas, anAnnotation);
          } 
      } // loop through gold Annotations
      
      // ----------------------------------------
      // Create the copper annotations devoid of the gold's 
      if ( copperSetAnnotations != null && !copperSetAnnotations.isEmpty() ) {
        for ( Annotation aCopperAnnotation : copperSetAnnotations ) {
     
            // find the model comcog/ipir to create the copper
            String copperFeatureValue = UIMAUtil.getFeatureValueByName(aCopperAnnotation, evaluateAttributeName);

            if (copperFeatureValue != null && copperFeatureValue.toLowerCase().equals("true"))
              createCopper(pJCas, aCopperAnnotation);
            
          } // end if a copper Annotation was found

      } // loop through gold Annotations
      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(),"process","Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }

  } // end Method process() ----------------

  // =================================================
  /**
   * obs_code 
   * 
   * @param goldAnnotationSetName
  */
  // =================================================
  private final String obs_code(String goldAnnotationSetName) {
    int x = goldAnnotationSetName.indexOf("_");
    String bType = goldAnnotationSetName.substring(0, x);

    String cType = bType + "_yes";
    int aType = UIMAUtil.getLabelTypeId(cType);

    if (aType == -1) {
      bType = U.upCaseWord(bType);
      aType = UIMAUtil.getLabelTypeId(bType + "_yes");
      cType = bType + "_yes";
    }
    if (aType == -1) {
      bType = "IPIR";
      aType = UIMAUtil.getLabelTypeId("IPIRyes");
      cType = "IPIRyes";

    }
    return cType;
    
  } // end Method obs_code() -------------------

  // -----------------------------------------
  /**
   * wasSuggested returns true if this annotation has another annotation that
   * has the same offsets, that is from a model annotation set.
   * 
   * @param pJCas
   * @param pAnnotationType
   *          (comcog_yes|ipir_yes)
   * @param pGoldAnnotation
   * @return boolean
   * 
   */
  // -----------------------------------------
  private final Annotation findCopper(JCas pJCas, HashMap<String, Annotation> pHash, Annotation anAnnotation) {

    Annotation copperAnnotation = null;

    try {
      String key = makeOffsetKey(anAnnotation);
      copperAnnotation = pHash.get(key);

    } catch (Exception e) {

    }
    return copperAnnotation;

  } // end Method findCopper() -------------

  // -----------------------------------------
  /**
   * makeOffsetKey returns a String that is 0000000|00000000
   * 
   * @param pAnnotation
   * @return String
   * 
   */
  // -----------------------------------------
  private final String makeOffsetKey(Annotation anAnnotation) {
    String key = null;
    String beginOffset = U.zeroPad(anAnnotation.getBegin(), 7);
    String endOffset = U.zeroPad(anAnnotation.getEnd(), 7);
    key = beginOffset + "|" + endOffset;
    return key;
  } // end makeOffsetKey() -------------------

  // -----------------------------------------
  /**
   * wasSuggested returns true if this annotation has another annotation that
   * has the same offsets, that is from a model annotation set.
   * 
   * @param pJCas
   * @param pAnnotationType
   *          (comcog_yes|ipir_yes)
   * @param pGoldAnnotation
   * @return boolean
   * 
   */
  // -----------------------------------------
  private final boolean wasSuggested(JCas pJCas, int pAnnotationType, Annotation pGoldAnnotation) {
    boolean returnVal = false;

    List<Annotation> possibleCandidates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, pAnnotationType, pGoldAnnotation.getBegin(),
        pGoldAnnotation.getEnd());

    if (possibleCandidates != null && !possibleCandidates.isEmpty()) for (Annotation aCandidate : possibleCandidates) {
      String setName = null;
      try {
        setName = ((Concept) aCandidate).getAnnotationSetName();
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (setName != null && setName.toLowerCase().contains("model") || setName.toLowerCase().contains("x")) {
        returnVal = true;
        break;
      }

    }
    return returnVal;
  } // end Method wasSuggested() ----------------

  // -----------------------------------------
  /**
   * hashThis stuffs into a hash the set of copper annotations that hashed by offsets
   * 
   * @param pCopperSetAnnotation
   * @return HashMap<String, Annotation > beginOffset|endOffset -> annotation
   * 
   */
  // -----------------------------------------
  private final HashMap<String, Annotation> hashThis(List<Annotation> pCopperSetAnnotations) {

    HashMap<String, Annotation> returnHash = null;
    if (pCopperSetAnnotations != null) {
      returnHash = new HashMap<String, Annotation>(pCopperSetAnnotations.size());
      for (Annotation anAnnotation : pCopperSetAnnotations) {
        String key = makeOffsetKey(anAnnotation);
        returnHash.put(key, anAnnotation);
      }

    }

    return returnHash;
  } // end Method hashThis() -----------------
  
  // -----------------------------------------
  /**
   * getAnnotationsFor retrieves the annotations that come from
   * the pAnnotationSetName and are pAnnotationNames
   * 
   * @param pJcas
   * @param pAnnotatoinSetName
   * @param pAnnotationName
   * 
   */
  // -----------------------------------------
  final  List<Annotation> getAnnotationsFor(JCas pJCas, String pAnnotationSetName, String pAnnotationName) {
    List<Annotation> returnVal = null;
    
    // get all annotations for pAnnotationName  (because it's indexed this way) then cull by annotation set name
    // no, wait, we cannot, because I've got different name spaces for manual and model comcog yes and ipir yes
    // that get set at read time - how?  cued by what?  
    // No, wait, wait, the reader will choose the first of the annotation type from the mapping table devoid of
    // the annotation set name,  then assign an annotation set Name,  Sooo....
    // grap the annotationName (assuming I get the name space the same as the first that's in the mapping table
    // THEN  
    //  cull by annotation set name.
  
    try {
      String annotationName = pAnnotationName;
      int annotationTypeId = UIMAUtil.getLabelTypeId(annotationName);
      if (  annotationTypeId > -1 ) {
    	  returnVal = addMentionsAux( pJCas, annotationTypeId, pAnnotationSetName );
      } else if ( annotationName.contains("-") ) { // try again substituting dash with underbar
    		  annotationName = annotationName.replaceAll("-", "_");
    		  annotationTypeId = UIMAUtil.getLabelTypeId(pAnnotationName);
    	      if (  annotationTypeId > -1 ) {
    	    	  returnVal = addMentionsAux( pJCas, annotationTypeId, pAnnotationSetName );
    	      }
    	  }  if ( annotationName.contains("_") ) { // try again substituting underbar with dash
    		  annotationName = annotationName.replaceAll("_", "-");
    		  annotationTypeId = UIMAUtil.getLabelTypeId(pAnnotationName);
    	      if (  annotationTypeId > -1 ) {
    	    	  returnVal = addMentionsAux( pJCas, annotationTypeId, pAnnotationSetName );
    	      } else {
      
    	    	  String msg = "Not able to find the class for annotationName " + pAnnotationName;
    	    	  GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getAnnotationsFor", msg);
    	    	  // throw new RuntimeException( msg);
    	      } // end if I've got the annotation type right
    	  }
    } catch (Exception e) {
    	e.printStackTrace();
    	String msg = "Not able to find the class for annotationName " + pAnnotationName;
    	GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getAnnotationsFor", msg + ": " + e.toString());
    	// throw new RuntimeException( msg);
    } // end exception
    
    return returnVal;
  } // end Method getAnnotationsFor() -------
  
  //-----------------------------------------
  /**
  * addMentionsAux retrieves mentions for this mention type within the annotatoin set name
  * @param pJcas
  * @param pAnnotationTypeId
  * @param pAnnotatoinSetName
  * @return List<Annotation> 
  * 
  */
 // -----------------------------------------
  private final List<Annotation> addMentionsAux(JCas pJCas, int pAnnotationTypeId, String pAnnotationSetName) {
	  List<Annotation> returnVal = null;
	  List<Annotation> someMentions = UIMAUtil.getAnnotations(pJCas, pAnnotationTypeId);
      if ( someMentions != null && !someMentions.isEmpty()) {
        returnVal = new ArrayList<Annotation>();
        for ( Annotation aMention : someMentions) {
          String aSetName = ((Concept) aMention).getAnnotationSetName();
          if ( aSetName != null && isOneOf( aSetName, pAnnotationSetName)) {
            returnVal.add(aMention);
          } // end if the setName matches
        } // end loop through annotations
      } // end if there are any mentions
	return returnVal;
} // end Method addMentionsAux

  
//-----------------------------------------
 /**
  * isOneOf
  * 
  * @param pColonSeparatedList
  * @param pOnList
  * @return boolean
  * 
  */
 // -----------------------------------------
 final  boolean isOneOf( String pColonSeparatedList, String pOnList ) {
	 boolean returnVal = false;
	 
	 
	 if (pColonSeparatedList != null && !pColonSeparatedList.isEmpty()) {
		 String[] elements = U.split( pOnList, ":" );
		 if ( elements != null && elements.length > 0 )
			 for ( String element: elements ) {
				 if ( element.toLowerCase().equals(element.toLowerCase())) {
					 returnVal = true;
					 break;
				 }
			 }
	 }
	 
	 return returnVal;
	
 } // end Method isOneOf() -----------------

// -----------------------------------------
  /**
   * getAnnotationsFor retrieves the annotations that come from
   * the pAnnotationSetName and are pAnnotationNames
   * 
   * @param pJcas
   * @param pAnnotatoinSetName
   * @param pAnnotationName
   * 
   */
  // -----------------------------------------
  final  List<Annotation> getAnnotationsForObs(JCas pJCas, String pAnnotationSetName, String pAnnotationName) {

    List<Annotation> someAnnotations = UIMAUtil.getAnnotations(pJCas);
    List<Annotation> returnVal = new ArrayList<Annotation>();

    if (someAnnotations != null && !someAnnotations.isEmpty()) {
      for (Annotation anAnnotation : someAnnotations) {

        int beginOffset = -1;
        int endOffset = -1;
        try {
          beginOffset = ((Concept) anAnnotation).getBegin();
          endOffset = ((Concept) anAnnotation).getEnd();
        } catch (Exception e) {
          continue;
        }

  
        String annotationName = anAnnotation.getClass().getName();
        if (pAnnotationSetName.contains("_subcategory_model") && annotationName.contains("yes")
            && annotationName.toLowerCase().contains("ipir")) {
          
        }
        if (annotationName.toLowerCase().contains(pAnnotationName.toLowerCase())) {

          String annotationSetName = null;
          try {
            annotationSetName = ((Concept) anAnnotation).getAnnotationSetName();
            // annotationSetName = "model";
            if (anAnnotation.getClass().getName().contains("manual") && (!annotationSetName.contains("manual")
                && !annotationSetName.contains("model") && !annotationSetName.contains("_subcategory_model")))
              annotationSetName = "manual";
          } catch (Exception e) {
            e.printStackTrace();
            GLog.error_println(" I'm looking for type " + pAnnotationName);
            GLog.error_println(" just what kind of annotation are you? " + anAnnotation.getClass().getName());
            throw new RuntimeException();
          }
          
          if (pAnnotationSetName.contains(annotationSetName)) {
            
            returnVal.add(anAnnotation);
          }
        }
      }
    }
    return returnVal;

  } // end Method getAnnotationsFor() -------------------

  // =================================================
  /**
   * createGold
   * 
   * @param pJCas
   * @param pStatement
   * @param pMention
   */
  // =================================================
  private final Annotation createGold(JCas pJCas, Annotation pMention) {

    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "createGold", "looking for 22494 from offset " + pMention.getBegin());
   

    // get attributes from the closest overlapping term to this gold
    // However, this gold is/might be a sentence, in which case
    // it's a sentence that was imported without sentence attributes
    // so calculate the sentence attributes for this
    // assertion status == negated or asserted (if there is negative evidence, make the
    // sentence negated, otherwise asserted
    // historical status == historical if there is historical evidence
    // conditional status == conditional if the sentence has conditional evidence
    // subject status == other if there is evidence of other in the sentence (I, we, they, mother, husband ...)
    // attribution status = patient if there is evidence of patient speaker (patient reports ...)
    // section ==
    // evidence = all the gold comcog/ipir annotations that overlap this status

    Gold statement = new Gold(pJCas);
    statement.setBegin(pMention.getBegin());
    statement.setEnd(pMention.getEnd());
   
    try {
    statement.setId(((Concept) pMention).getId());
   // statement.setAnnotationSetName( ((Concept)pMention).getAnnotationSetName());
    } catch( Exception e) {
    	e.printStackTrace();
    	
    }
    ((Concept) statement).setAnnotationSetName(this.annotationSetName);

    setEvaluationAttributes(pJCas, statement);

    statement.addToIndexes();

    return statement;
  } // end Method createGold() ------------

  // =================================================
  /**
   * setEvaluationAttributes  
   *         get attributes from the closest overlapping term to this gold
   *         However, this gold is/might be a sentence, in which case
   *          it's a sentence that was imported without sentence attributes
   *          so calculate the sentence attributes for this
   *          assertion status == negated or asserted (if there is negative evidence, make the
   *          sentence negated, otherwise asserted
   *          historical status == historical if there is historical evidence
   *          conditional status == conditional if the sentence has conditional evidence
   *          subject status == other if there is evidence of other in the sentence (I, we, they, mother, husband ...)
   *          attribution status = patient if there is evidence of patient speaker (patient reports ...)
   *          section ==
   *          evidence = all the gold comcog/ipir annotations that overlap this status
   *          
   *          (Maybe this should migrate to VUIMAUtils?)
   * 
   * @param pJCas
   * @param statement
   */
  // =================================================
  public final void setEvaluationAttributes(JCas pJCas, Concept pStatement) {
    
    
    String sectionName = VUIMAUtil.getSectionName(pStatement);
    if ( sectionName == null ) sectionName = "UnknownSection";
    
    boolean              assertionStatus = VUIMAUtil.calculateAssertionStatus(pJCas,pStatement);
    boolean            conditionalStatus = VUIMAUtil.calculateConditionalStatus( pJCas, pStatement);
    boolean              subjectStatus = VUIMAUtil.calculateSubjectStatus(pJCas, pStatement);
    boolean   attributionIsPatientStatus = VUIMAUtil.calculateAttributionIsPatientStatus(pJCas, pStatement);
    boolean             historicalStatus = VUIMAUtil.calculateHistoricalStatus(pJCas, pStatement);
    String   someEvidence = getEvidence( pJCas, pStatement);
    
    pStatement.setSectionName( sectionName);
    pStatement.setAssertionStatus( VUIMAUtil.convertAssertionValue( assertionStatus));
    pStatement.setConditionalStatus( conditionalStatus);
    pStatement.setSubjectStatus( VUIMAUtil.convertSubjectValue(subjectStatus));
    pStatement.setAttributedToPatient( attributionIsPatientStatus);
    pStatement.setHistoricalStatus(historicalStatus);
    
    try {
    ((Gold) pStatement).setEvidence( someEvidence);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "MakeGoldAndCopper:setEvaluationAttributes: Issue adding evidence to gold/copper :" + e.toString();
      GLog.error_println(msg);
    }

  } // end Method setEvaluationAttributes() ----------

  // =================================================
  /**
   * createCopper
   * 
   * @param pJCas
   * @param pStatement
   * @param pMention
   */
  // =================================================
  private final Annotation createCopper(JCas pJCas, Annotation pMention) {

    Copper statement = new Copper(pJCas);
    statement.setBegin(pMention.getBegin());
    statement.setEnd(pMention.getEnd());
    ((Concept) statement).setAnnotationSetName(this.annotationSetName);

    // statement.setEvidence(evalGoldFocus);
    String evidence = getLexEvidence( pJCas, pMention);
    statement.setEvidence(evidence);
    statement.setAssertionStatus(((Concept) pMention).getAssertionStatus());
    statement.setSectionName(((Concept) pMention).getSectionName());
    statement.setConditionalStatus(((Concept) pMention).getConditionalStatus());
    statement.setSubjectStatus(((Concept) pMention).getSubjectStatus());
    statement.setAttributedToPatient(((Concept) pMention).getAttributedToPatient());
    statement.setHistoricalStatus(((Concept) pMention).getHistoricalStatus());
    statement.setId(((Concept) pMention).getId());
  
    statement.addToIndexes();

    return statement;
  } // end Method createCopper() ------------

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
    List<Annotation> comcogActivities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ComCogActivities.typeIndexID, pMention.getBegin(), pMention.getEnd(), true);
    
    List<Annotation> ipirActivities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRActivities.typeIndexID, pMention.getBegin(), pMention.getEnd(), true);


    if (comcogActivities != null && !comcogActivities.isEmpty()) 
      for (Annotation activity : comcogActivities) {
      String className = activity.getClass().getSimpleName();
      if (className != null && !className.contains("yes") && !className.contains("no") )
        activityHash.add(className);
    }
    
    if (ipirActivities != null && !ipirActivities.isEmpty()) 
      for (Annotation activity : ipirActivities) {
      String className = activity.getClass().getSimpleName();
      if (className != null && !className.contains("yes") && !className.contains("no") )
        activityHash.add(className);
    }
    
    if (!activityHash.isEmpty()) {
     String[] buff = new String[ activityHash.size()];
     activityHash.toArray( buff);
     Arrays.sort(buff);
     StringBuffer buff2 = new StringBuffer();
     for ( int i = 0; i < buff.length; i++) {
       buff2.append( buff[i]);
       if ( i < buff.length -1 )
         buff2.append(":");
       
     }
      returnVal = buff2.toString();
    }
      // GLog.println("grep Me: " + returnVal);
    

    return returnVal;

  } // end Method getEvidence() --------------
  
//----------------------------------
 /**
  * getLexEvidence returns the semantic types for the terms participating in this copper statement
  * 
  * 
  * @return String
  * 
  **/
 // ----------------------------------
 private final String getLexEvidence(JCas pJCas, Annotation pMention) {
   HashSet<String> activityHash = new HashSet<String>();
   String returnVal = "";

   // get the d codes this mention has
   List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pMention.getBegin(), pMention.getEnd(), true);
   HashSet<String> someTypesHash = new HashSet<String>(5);
  
   if (terms != null && !terms.isEmpty()) {
     for (Annotation term : terms) {
     String semanticTypes = ((LexicalElement) term).getSemanticTypes();
     if ( semanticTypes != null && !semanticTypes.isEmpty()) {
       String[] someTypes = U.split ( semanticTypes, ":");
       if ( someTypes != null && someTypes.length > 0  ) 
         for ( String aType: someTypes )
           someTypesHash.add( aType );
       }
     }
     if ( !someTypesHash.isEmpty()) {
       StringBuffer buff2 = new StringBuffer();
       String[] buff = new String[ someTypesHash.size() ];
       buff = someTypesHash.toArray( buff );
         
       Arrays.sort( buff );
       for ( int i = 0; i < buff.length; i++) {
          buff2.append(buff[i]);
          if ( i < buff.length -1 )
            buff2.append(":");
        } // end loop thru sorted, uniq'd buffer 
       returnVal = buff2.toString();
     } // end if there are some entries in the hash
   } // if there are terms in this mention
  
   return returnVal;

 } // end Method getLexEvidence() --------------

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  }

  // ----------------------------------
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
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize(args);

    } catch (Exception e) {
      String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg); // <------ use your own logging here
      throw new ResourceInitializationException();
    }

  } // end Method initialize() -------

  // ----------------------------------
  /**
   * initialize initializes the class. Parameters are passed in via a String
   * array with each row containing a --key=value format.
   * 
   * It is important to adhere to the posix style "--" prefix and
   * include a "=someValue" to fill in the value to the key.
   * 
   * This has an optional parameter, a resource directory with pattern files in it
   * --pagecategoryPatternsDir= ./resources/vinciNLPFramework/pageFiltering
   *
   * @param pArgs
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
    this.evalGoldFocus = U.getOption(pArgs, "--evalGoldFocus=", "D730_Manual");
    this.annotationSetName = U.getOption(pArgs, "--evaluationAnnotationSetName=", "evaluation");
    this.args = pArgs;

    try {

    } catch (Exception e) {
      throw new ResourceInitializationException();
    }

  } // end Method initialize() -------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private PerformanceMeter performanceMeter  = null;
  String                   evalGoldFocus     = null;
  String                   annotationSetName = "efficacy";
  String[]                 args              = null;

} // end Class LineAnnotator() ---------------
