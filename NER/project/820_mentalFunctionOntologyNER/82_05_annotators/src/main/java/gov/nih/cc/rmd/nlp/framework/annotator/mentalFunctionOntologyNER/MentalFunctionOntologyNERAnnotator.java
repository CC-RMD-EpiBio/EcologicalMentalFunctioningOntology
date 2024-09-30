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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.inFACT.BehaviorEvidence;
import gov.nih.cc.rmd.inFACT.IPIRParticipationEvidence;
import gov.nih.cc.rmd.inFACT.SupportEvidence;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ActivitiesAndParticipation;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ActivitiesOfDailyLiving;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Activities_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.AdaptiveBehavior;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ApplyingKnowledge_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Behavior_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.BodyFunctions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.BodyStructures;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.CognitiveActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.CognitiveFunctions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ComCogActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.CommunicationActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.CommunicationFunctions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.CommunityActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Context_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ContextualFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.DomesticLifeActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Environment_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.EnvironmentalFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ExternalFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.FeedbackAppraisal;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Feedback_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.GeneralTasksAndDemands;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.GlobalMentalFunctions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.HealthConditionFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.HealthCondition_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.LearningAndApplyingKnowledge;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Learning_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MajorLifeAreasActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MaladaptiveBehavior;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioning;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioningMention;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctioningOntology;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MentalFunctions_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.MobilityActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.NeuroCognitiveProcessing;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.NonActions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.ObservableBehavior;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.Participation_;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.PersonFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.PersonalBackgroundFactors;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.SelfCareActivities;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.SensoryMotorFunctionFunctions;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.SensoryProcessing;
import gov.nih.cc.rmd.mentalFunctionOntologyNER.SpecificMentalFunctions;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.LexicalElement;

import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Phrase;
import gov.va.chir.model.Sentence;
import gov.va.vinci.model.FamilyHistoryEvidence;
import gov.va.vinci.model.SubjectIsOtherEvidence;
import gov.va.vinci.model.SubjectIsPatientEvidence;

// import gov.nih.cc.rmd.nlp.framework.annotator.pos.PosFixes01Annotator;



public class MentalFunctionOntologyNERAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started MentalFunctionOntologyNERAnnotator");
      
      String documentId = VUIMAUtil.getDocumentId(pJCas);
     
      if (VUIMAUtil.getDocumentProcessMe(pJCas)) {

        // Loop through utterances (that's the span we won't cross)

        List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID, true);
        List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID, true);

        if (sentences != null && !sentences.isEmpty())
          for (Annotation sentence : sentences)
            processSentence(pJCas, sentence);
        
        if (slotValues != null && !slotValues.isEmpty())
          for (Annotation slotValue : slotValues)
            processSentence(pJCas, slotValue);


      } // end if this is to be processed
      else {
        DocumentHeader aDoc = VUIMAUtil.getDocumentHeader(pJCas);
        String docType = aDoc.getDocumentType();
        GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", "Filtering out doc because it's classified as " + docType );
      }
   
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End MentalFunctionOntologyNERAnnotator");
	   this.performanceMeter.stopCounter();
     

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process","Issue with " + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

// =================================================
  /**
   * processSentence looks for mental functioning annotations
   * in this sentence (This might be too big a chunk, we'll see)
   * 
   * look for verb evidence 
   * 
   * @param pJCas
   * @param pSentence
  */
  // =================================================
  private final void processSentence(JCas pJCas, Annotation pSentence) {
   
    
	  
        
 List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true );
    
    List<Annotation> someMentions = new ArrayList<Annotation>();
    if ( terms != null && !terms.isEmpty()) {
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "processSentence"," Processing sentence with " + terms.size()  + " in it ");
    		 
      for ( Annotation term : terms ) {
        String categories = ((LexicalElement) term).getSemanticTypes();
        String sectionName = ((LexicalElement) term).getSectionName();
        String subjectIs =  ((LexicalElement) term).getSubject();
        List<MentalFunctioningOntology> someEvidences = null;
       
        String text = term.getCoveredText();
       
      
        if ( categories != null && categories.trim().length() > 0 )
             if (isValidSection( sectionName ) ) 
               if ( (someEvidences  = createMentalFunctioningEvidence( pJCas, categories, term, pSentence)) != null && !someEvidences.isEmpty() ) {
                 
                 // -----------------------------------------------------------
                 // eventually, we should be looking at kinds of evidence like mental function, cognition function
                 // along with 'ing' evidence before highlighting the sentence
                 //
                 //  that's going to require 
                 //    1. examples to work from
                 //    2. lexica of mental functions, body functions, contexts ....
                 // ------------------------------------------------------------
                 
                 MentalFunctioningMention aMention = createMentalFunctioningMention( pJCas,  someEvidences, pSentence);
                  if ( aMention != null ) someMentions.add( aMention );
               }
    
      } // end loop through terms
    } // end if there are terms
    
    // Weed out statements that are not about the subject (patient vs provider, family member, friend, other)
  
  
    // Unique and clean up the annotations
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "processSentence"," Uniqing the annotations" );
    
    uniqueAnnotations( pJCas, pSentence );
    
    // remove see Section xxxx mentions
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "processSentence"," Filtering out See Sections" );
    
    filterOutSeeSection( pJCas, pSentence, terms);
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "processSentence"," Filtering out SubjectIsOtherAnd We annotations" );
    
    filterOutSubjectIsOtherAndWeMentions( pJCas, pSentence, terms);
    
    // filter out IPIR mentions that don't have 2 people within the sentence
   // filterBadIPIRMentions( pJCas, pSentence, someMentions);
    
    make_yes_sentence( pJCas, pSentence);
    
   GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "processSentence"," finished" );
    
  } // end Method createMentalFunctioningMention() ----
  
  // =================================================
/**
 * filterOutSeeSection removes mentions that are of the form
 * see Section X.  In the listings these are 
 * see ss [\D]
 * see [\D]
 * 
 * These are not mental functioning mentions. 
 * 
 * @param pJCas
 * @param pSentence
 * @param pTerms 
*/
// =================================================
private void filterOutSeeSection(JCas pJCas, Annotation pSentence, List<Annotation> pTerms) {
  
  if (pTerms != null && !pTerms.isEmpty())
    for (int i = 0; i < pTerms.size() - 1; i++) {
      String termName = pTerms.get(i).getCoveredText();

      if (termName != null && !termName.isEmpty() && termName.trim().toLowerCase().equals("see")) {

        Annotation termAfter = pTerms.get(i + 1);

        if (termAfter != null) {
          String termAfterName = termAfter.getCoveredText();
          if (termAfterName != null && U.isNumber(termAfterName.trim()) || termAfterName.toLowerCase().startsWith("ss"))
            removeMentionAround(pJCas, pTerms.get(i));
          
          if (termAfterName != null && U.isNumber(termAfterName.trim()) || termAfterName.toLowerCase().startsWith("reserved"))
            removeMentionAround(pJCas, pTerms.get(i));
         
        }
      }
    }
} // end Method filterOutSeeSection() -------------

//=================================================
/**
* make_yes_sentence makes comcog_yes and ipir_yes sentences
* when there is evidence for each
* 
* @param pJCas
* @param pSentence
*/
// =================================================
private void make_yes_sentence(JCas pJCas, Annotation pSentence) {
	
	make_comcog_yes_sentence( pJCas, pSentence);
	make_ipir_yes_sentence( pJCas, pSentence);
} // end Method make_yes_sentence() ------------------

//=================================================
/**
* make_comcog_yes_sentence makes comcog_yes 
* when there is evidence for comcog 
* 
* @param pJCas
* @param pSentence
*/
//=================================================
private void make_comcog_yes_sentence(JCas pJCas, Annotation pSentence) {
	
	try {
		 List<Annotation> mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ComCogActivities.typeIndexID,pSentence.getBegin(), pSentence.getEnd(), true );
		 if ( mentions != null && !mentions.isEmpty()) {
			 
			 // filter out any manual activities
			 boolean found = false;
			 for ( Annotation mention : mentions ) {
				 String setName = null;
				 try {
			 	  setName = ((ComCogActivities) mention).getAnnotationSetName();
			 	  if ( setName == null )  {
			 		  found = true;
			 		  break;
			 	  }
			 	  if ( INFACT_MODE) continue;
			 	  if ( setName.toLowerCase().contains("manual")) continue;
			 	  if ( setName.toLowerCase().contains("model")) continue;
			 	  if ( setName.toLowerCase().contains("comcog_sentence")) continue;
			 	
				 } catch (Exception e) {}
			 
			 }
			 if ( found )
				 createComCogYes( pJCas, pSentence);
		 }
		 	 
	} catch (Exception e) {
		e.printStackTrace();
		String msg = "Issue making comcog yes sentence " + e.toString();
		GLog.println(GLog.ERROR_LEVEL, this.getClass(), "make_comcog_yes_sentence", msg);
	}
	
} // end Method make_yes_sentence() ------------------

//=================================================
/**
* make_IPIR_yes_sentence makes ipir_yes 
* when there is evidence for ipir 
* 
* @param pJCas
* @param pSentence
*/
//=================================================
private void make_ipir_yes_sentence(JCas pJCas, Annotation pSentence) {
	
	try {
		if ( ! this.INFACT_MODE ) {
		
		 List<Annotation> mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  IPIRActivities.typeIndexID,pSentence.getBegin(), pSentence.getEnd(), true );
		 if ( mentions != null && !mentions.isEmpty())
			 createIPIRYes( pJCas, pSentence);
		}
	  
	} catch (Exception e) {
		e.printStackTrace();
		String msg = "Issue making ipir yes sentence " + e.toString();
		GLog.println(GLog.ERROR_LEVEL, this.getClass(), "make_ipir_yes_sentence", msg);
	}
	
} // end Method make_yes_sentence() ------------------


// -----------------------------------------
/**
 * createComCogYes creates a new UIMA annotation that is the comCogYes 
 * 
 * @param pJcas
 * @param pManualComCog_yes
 * 
 */
// -----------------------------------------
 final  Annotation createComCogYes(JCas pJCas, Annotation pManual_ComCog_yes) {
      	
	  gov.nih.cc.rmd.inFACT.x.Comcog_yes statement = new gov.nih.cc.rmd.inFACT.x.Comcog_yes( pJCas);
	  statement.setBegin( pManual_ComCog_yes.getBegin());
	  statement.setEnd( pManual_ComCog_yes.getEnd());
	  statement.setId( "createComCogYes_" + this.annotationCtr++);
	  statement.setAnnotationSetName(this.annotationSetName);
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
	  statement.setAnnotationSetName(this.annotationSetName);
	  
	  // statement.setTimeBucket( "false" );
	  statement.setDifficult_to_determine( false);
	  statement.addToIndexes();
    
    
	  return statement;
	  
	  
} // end Method createComCogYes() -------------------
 
//-----------------------------------------
/**
 * createIPIRYes creates a new UIMA annotation that is the IPIRYes
 * 
 * @param pJcas
 * @param pManualIPIR_yes
 * 
 */
// -----------------------------------------
final  Annotation createIPIRYes(JCas pJCas, Annotation pManual_IPIR_yes) {

  gov.nih.cc.rmd.inFACT.x.IPIR_yes statement = new gov.nih.cc.rmd.inFACT.x.IPIR_yes(pJCas);
  statement.setBegin(pManual_IPIR_yes.getBegin());
  statement.setEnd(pManual_IPIR_yes.getEnd());
  statement.setId("createIPIRYes_" + this.annotationCtr++);
  statement.setD730(false);
  statement.setD740(false);
  statement.setD7400(false);
  statement.setD750(false);
  statement.setD760(false);
  statement.setD770(false);
  statement.setD779(false);
  statement.setInteraction(false);

  statement.setTimeBucket("false");
  statement.setDifficult_to_determine(false);
  statement.setAnnotationSetName(this.annotationSetName);
  statement.addToIndexes();

  return statement;

} // end Method createIPIRYes() -------------------


  // =================================================
  /**
   * removeMentionAround removes any mfo mention around the annotation passed in
   * 
   * @param pJCas
   * @param annotation
  */
  // =================================================
  private void removeMentionAround(JCas pJCas, Annotation pTerm) {
    
    List<Annotation> mentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  MentalFunctioningOntology.typeIndexID,pTerm.getBegin(), pTerm.getEnd(), true );
    
    if ( mentions != null && !mentions.isEmpty())
      for ( Annotation mention : mentions )
        mention.removeFromIndexes();
    
  } // end removeMentionAround() --------------------


  // =================================================
/**
 * uniqueAnnotations 
 * 
 * @param pJCas
 * @param pSentence
*/
// =================================================
private void uniqueAnnotations(JCas pJCas, Annotation pSentence) {
  
  try {
    
    HashMap<String, Annotation> uniquedHash = new HashMap<String, Annotation>();
    List<Annotation> evidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  MentalFunctioningOntology.typeIndexID,  pSentence.getBegin(), pSentence.getEnd(), true);
    
    if ( evidences != null ) 
      for ( Annotation anEvidence : evidences ) {
        
       String key = getAnnotationSigniture( anEvidence);
       Annotation firstInstance = uniquedHash.get( key);
       
       if ( firstInstance == null ) 
         uniquedHash.put( key, anEvidence );  
       else
         // ------------------------------
         // this is not the first instance of this annotation: remove it
         anEvidence.removeFromIndexes(pJCas );
      }
    
    
    
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println( GLog.ERROR_LEVEL, this.getClass(), "uniqueAnnotations", "Issue uniquing the mental functioning evidence " + e.toString() );
  }
  
  
} // end Method uniqueAnnotations() ------------------


  // =================================================
  /**
   * getAnnotationSigniture 
   * 
   * @param anEvidence
   * @return String   className|begin|end
  */
  // =================================================
   private final String getAnnotationSigniture(Annotation anEvidence) {
  
     String key = null;
     String t1 = anEvidence.getClass().getName();  
     String className = U.getClassNameFull(t1);
     String offset_begin = U.zeroPad( anEvidence.getBegin(), 7 );
     String offset_end = U.zeroPad( anEvidence.getEnd(), 7 );
    
     key = t1 + "|" + offset_begin + "|" + offset_end ;
     return key ;
    
  } // end Method getAnnotationSigniture() -----------


  // =================================================
  /**
   * filterOutSubjectIsOtherMentions will remove mental function mentions
   * that are within phrases that are not about the patient as the subject.
   * 
   * @param pJCas
   * @param pSentence  
   * @param pTerms 
   */
  // =================================================
  private final void filterOutSubjectIsOtherAndWeMentions(JCas pJCas, Annotation pSentence, List<Annotation> pTerms) {

    boolean weHaveBeenSeen = false;
    for ( Annotation term: pTerms) {  

      String subjectIs =  ((LexicalElement) term).getSubject();
      String coveredText = term.getCoveredText();
      List<Annotation> mentalFunctioningEvidences = null;
      
      
      if ( coveredText.toLowerCase().equals("we") || coveredText.toLowerCase().equals("our") // || 
        //  coveredText.equals("I")
          ) {

  //     find I, we, and then find the next mental function verb, and mark it as subject is other
      //     unless your is found
        weHaveBeenSeen = true;
        ((LexicalElement) term).setSubject("Provider");
      
      }
        
      if ( coveredText.toLowerCase().equals("you") || 
          coveredText.toLowerCase().equals("your") ||  
          coveredText.toLowerCase().equals("his")  ||
          coveredText.toLowerCase().equals("her")  ||
          coveredText.toLowerCase().equals("she")  ||
          coveredText.toLowerCase().equals("him") )
        weHaveBeenSeen = false;
     
      mentalFunctioningEvidences = findMentalFunctioningEvidence(pJCas, term);
       
      if ( mentalFunctioningEvidences != null && weHaveBeenSeen )
        for ( Annotation evidence: mentalFunctioningEvidences ) {
          ((MentalFunctioningOntology)evidence).setSubjectStatus("Provider");
          // per Julia, we want to filter out mentions that are not about the claimant
            evidence.removeFromIndexes(pJCas );
        }
      
    } // loop through terms of a sentence

    } // end Method filterOutSubjectIsOtherMentions() ----

   // =================================================
  /**
   * findMentalFunctioningEvidence returns 
   * 
   * @param pJCas
   * @param pTerm
   * @return List<MentalFunctioningEvendence>
  */
  // =================================================
  private List<Annotation> findMentalFunctioningEvidence(JCas pJCas, Annotation pTerm) {
   
    List<Annotation> returnVal = null;
    List<Annotation> evidences = UIMAUtil.getAnnotationsBySpan(pJCas, MentalFunctioningOntology.typeIndexID, pTerm.getBegin(), pTerm.getEnd(), true );
    
    if ( evidences != null && !evidences.isEmpty()) {
      returnVal = new ArrayList<Annotation>(evidences.size());
      for ( Annotation evidence : evidences ) {
        String category = evidence.getClass().getName();
        if ( !category.contains("Mention"))
          returnVal.add(evidence);
      }
    }
    
    return returnVal;
  } // end Method findMentalFunctioningEvidence() ----





  // =================================================
  /**
   * createMentalFunctioningEvidence  -- secret sauce here
   * 
   * @param pJCas
   * @param pCategories
   * @param pTerm
   * @param pSentence
   * @return List<MentalFunctioningOntology>
  */
  // =================================================
  private List<MentalFunctioningOntology> createMentalFunctioningEvidence(JCas pJCas, String pCategories, Annotation pTerm, Annotation pSentence) {
    
    
  
   List<MentalFunctioningOntology> statements = new ArrayList<MentalFunctioningOntology>();
   try {
    if ( pCategories!= null && pCategories.trim().length() > 0 )
      
      // Input
       if ( pCategories.contains( "ExternalFactors" ) )                 statements.add ((MentalFunctioningOntology) new ExternalFactors( pJCas));
       if ( pCategories.contains( "ContextualFactors" ) )               statements.add ((MentalFunctioningOntology) new ContextualFactors( pJCas));
       if ( pCategories.contains( "EnvironmentalFactors" ) )            statements.add ((MentalFunctioningOntology) new EnvironmentalFactors(pJCas));
      
       if ( pCategories.contains( "Context_" ) )                        statements.add ((MentalFunctioningOntology) new Context_(pJCas));
       if ( pCategories.contains( "Environment_" ) )                    statements.add ((MentalFunctioningOntology) new Environment_(pJCas));
       
      // Throughput 
    
       if ( pCategories.contains( "PersonFactors" ) )                   statements.add ((MentalFunctioningOntology) new PersonFactors( pJCas));    
       if ( pCategories.contains( "PersonalBackgroundFactors" ) )       statements.add ((MentalFunctioningOntology) new PersonalBackgroundFactors( pJCas));
       if ( pCategories.contains( "HealthCondition_" ) )                statements.add ((MentalFunctioningOntology) new HealthCondition_( pJCas));
       
       if ( pCategories.contains( "HealthConditionFactors" ) )          statements.add ((MentalFunctioningOntology) new HealthConditionFactors( pJCas));
       
       
       if ( pCategories.contains( "BodyStructures" ) )                  statements.add ((MentalFunctioningOntology) new BodyStructures( pJCas));
       if ( pCategories.contains( "BodyFunctions" ) )                   statements.add ((MentalFunctioningOntology) new BodyFunctions( pJCas));
   
       if ( pCategories.contains( "SensoryProcessing" ) )               statements.add ((MentalFunctioningOntology) new SensoryProcessing( pJCas));
       if ( pCategories.contains( "SensoryMotorFunctionFunctions" ) )   statements.add ((MentalFunctioningOntology) new SensoryMotorFunctionFunctions( pJCas));
       if ( pCategories.contains( "NeuroCognitiveProcessing" ) )        statements.add ((MentalFunctioningOntology) new NeuroCognitiveProcessing( pJCas));
       
       
       if ( pCategories.contains( "MentalFunctions_" ) )                statements.add ((MentalFunctioningOntology) new MentalFunctions_( pJCas));
       if ( pCategories.contains( "GlobalMentalFunctions" ) )           statements.add ((MentalFunctioningOntology) new GlobalMentalFunctions( pJCas));
       if ( pCategories.contains( "SpecificMentalFunctions" ) )         statements.add ((MentalFunctioningOntology) new SpecificMentalFunctions( pJCas));
       
       
       
       
    //   if ( pCategories.contains( "CognitiveFacilities" ) )             statements.add ((MentalFunctioningOntology) new CognitiveFacilities( pJCas));
    //  if ( pCategories.contains( "CognitionFunctions" ) )              statements.add ((MentalFunctioningOntology) new CognitiveFacilities( pJCas));
       if ( pCategories.contains( "CognitiveFunctions" ) )               statements.add ((MentalFunctioningOntology) new CognitiveFunctions( pJCas));
    
       if ( pCategories.contains( "CommunicationFunctions" ) )          statements.add ((MentalFunctioningOntology) new CommunicationFunctions( pJCas));
       if ( pCategories.contains( "FeedbackAppraisal" ) )               statements.add ((MentalFunctioningOntology) new FeedbackAppraisal( pJCas));
       
       
      // Output
       if ( pCategories.contains( "MentalFunctioning" ) )              statements.add ((MentalFunctioningOntology) new MentalFunctioning( pJCas));
       if ( pCategories.contains( "ObservableBehavior" ) )             statements.add ((MentalFunctioningOntology) new ObservableBehavior( pJCas));
       if ( pCategories.contains( "ActivitiesAndParticipation" ) )     statements.add ((MentalFunctioningOntology) new ActivitiesAndParticipation( pJCas));
      
       if ( pCategories.contains( "Activities_" ) )                    statements.add ((MentalFunctioningOntology) new Activities_( pJCas));
       if ( pCategories.contains( "Participation_" ) )                 statements.add ((MentalFunctioningOntology) new Participation_( pJCas));
       
    
       if ( pCategories.contains( "Behavior_" ) )                     { statements.add ((MentalFunctioningOntology) new Behavior_( pJCas));
                                                                        statements.add ((MentalFunctioningOntology) new IPIRActivities( pJCas));
       }
       if ( pCategories.contains( "ActivitiesOfDailyLiving" ) )        statements.add ((MentalFunctioningOntology) new ActivitiesOfDailyLiving( pJCas));
       if ( pCategories.contains( "CommunicationActivities" ) )        statements.add ((MentalFunctioningOntology) new CommunicationActivities( pJCas));
       
       if ( pCategories.contains( "CognitiveActivities" ) )            statements.add ((MentalFunctioningOntology) new CognitiveActivities( pJCas));
       if ( pCategories.contains( "CognitionActivities" ) )            statements.add ((MentalFunctioningOntology) new CognitiveActivities( pJCas));
     
       if ( pCategories.contains( "LearningAndApplyingKnowledge" ) )   statements.add ((MentalFunctioningOntology) new LearningAndApplyingKnowledge( pJCas));
       if ( pCategories.contains( "Learning_" ) )                      statements.add ((MentalFunctioningOntology) new Learning_( pJCas));
       if ( pCategories.contains( "ApplyingKnowledge_" ) )             statements.add ((MentalFunctioningOntology) new ApplyingKnowledge_( pJCas));
       if ( pCategories.contains( "GeneralTasksAndDemands" ) )         statements.add ((MentalFunctioningOntology) new GeneralTasksAndDemands( pJCas));
         
       
       if ( pCategories.contains( "MobilityActivities" ) )             statements.add ((MentalFunctioningOntology) new MobilityActivities( pJCas));
       if ( pCategories.contains( "SelfCareActivities" ) )             statements.add ((MentalFunctioningOntology) new SelfCareActivities( pJCas));
       if ( pCategories.contains( "DomesticLifeActivities" ) )         statements.add ((MentalFunctioningOntology) new DomesticLifeActivities( pJCas));
       if ( pCategories.contains( "IPIRActivities" ) )                 statements.add ((MentalFunctioningOntology) new IPIRActivities( pJCas));
       if ( pCategories.contains( "MajorLifeAreasActivities"))         statements.add ((MentalFunctioningOntology) new MajorLifeAreasActivities( pJCas));
       if ( pCategories.contains( "CommunityActivities" ) )            statements.add ((MentalFunctioningOntology) new CommunityActivities( pJCas));
 
       if ( pCategories.contains( "NonActions" ) )                     statements.add ((MentalFunctioningOntology) new NonActions( pJCas));
       if ( pCategories.contains( "AdaptiveBehavior" ) )               statements.add ((MentalFunctioningOntology) new AdaptiveBehavior( pJCas));
       if ( pCategories.contains( "MaladaptiveBehavior" ) )            statements.add ((MentalFunctioningOntology) new MaladaptiveBehavior( pJCas));
       
      
      // Feedback
       if ( pCategories.contains( "Feedback_" ) )                        statements.add ((MentalFunctioningOntology) new Feedback_( pJCas));
  
      if ( statements != null && !statements.isEmpty() ) 
        for ( MentalFunctioningOntology statement : statements ) {
  
          statement.setBegin( pTerm.getBegin());
          statement.setEnd( pTerm.getEnd());
          statement.setId("CreateMentalFunctioningEvidence_" + this.annotationCtr++);
          
          // ----------------------
          // bread crumb attributes I'd like to see
          // the umls concept id           <----
          // the seed concept it came from <----
          // the umls source vocabulary it came from  
          // the the lragr it came from 
         
          // ---------------------
          StringArray otherInfoz = ((LexicalElement) pTerm).getOtherFeatures();
          String conceptName = ((LexicalElement) pTerm).getCitationForm();
          String cui = ((LexicalElement) pTerm).getEuis(0);
         
          if ( otherInfoz != null ) {
           String otherInfos = UIMAUtil.stringArrayToString(otherInfoz);
           if ( otherInfos != null ) {
             String catCols[] = U.split( otherInfos);
             
            
             
           String ontologyCategory =   ((LexicalElement) pTerm).getSemanticTypes();
           String ontologyId = catCols[0];
           String sab = catCols[1];
           String seedCUI = null;
           String seedConceptName = null;
           
           if ( catCols.length > 4 ) {
              seedCUI = catCols[3];
              try {
                seedConceptName = catCols[4];
              } catch (Exception e) {
                e.printStackTrace();
              }
           } else {
             
             /*  In the lvg lragr files, which stuff only 2 columns in the other info, 
              * - the category is actually in the second catColumn,  the seed term (the verbnet category), in the uninflect column
              */
            // seedCUI = ((LexicalElement) pTerm).
             
           }
          
           statement.setCategories( ontologyCategory);
           statement.setCuis( cui );
           statement.setConceptNames( conceptName );
           statement.setOntologyId ( ontologyId);
           statement.setSab(  sab);
           statement.setSeedConceptName( seedConceptName);
           statement.setSeedCUI( seedCUI);
           statement.setSubjectStatus("patient");
           String status = ((LexicalElement) pTerm).getSubject();
           if ( status != null && status.toLowerCase().equals("other"))
             statement.setSubjectStatus("other");
           }
          }
           statement.setAssertionStatus( ((LexicalElement) pTerm).getNegation_Status());
           statement.setConditionalStatus(((LexicalElement) pTerm).getConditional());
           statement.setHistoricalStatus(((LexicalElement) pTerm).getHistorical() );
           statement.setAttributedToPatient(  ((LexicalElement) pTerm).getAttributedToPatient() );
           
           statement.addToIndexes();
          
        
        }
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processSentence", "Something wrong creating a statement " + e.toString());
     throw e;
   }
      
    return statements;
    
  } // end Method processSentence() ------------------


  // =================================================
  /**
   * createMentalFunctioningMention
   * 
   * @param pJCas
   * @param pSomeEvidences
   * @param pSentence
   * @return MentalFunctioningMention
  */
  // =================================================
  private MentalFunctioningMention createMentalFunctioningMention(JCas pJCas, List<MentalFunctioningOntology> pSomeEvidences, Annotation pSentence) {
   
    // check to see that another mentalFunctionMention is not already in this area
    MentalFunctioningMention statement = null;
    if ( pSomeEvidences != null && !pSomeEvidences.isEmpty() )
      statement = getMentalFunctionMention(pJCas, pSomeEvidences.get(0));
    
    if ( statement == null ) {
      Phrase mentionScope = getMentionScope( pJCas, pSentence, pSomeEvidences );
     statement = new MentalFunctioningMention( pJCas );
     statement.setBegin( mentionScope.getBegin());
     statement.setEnd( mentionScope.getEnd());
     statement.setId( "createMentalFunctioningMention_" + this.annotationCtr++);
    
     FSArray fsEvidences = UIMAUtil.list2FsArray(pJCas,  pSomeEvidences);
     statement.setMentalFunctionEvidences( fsEvidences);
     statement.addToIndexes();
     
    } else {
      // update the fs array with the new evidences
      FSArray priorEvidencez = statement.getMentalFunctionEvidences();
      @SuppressWarnings("unchecked")
      List<MentalFunctioningOntology> priorEvidences = UIMAUtil.fSArray2List(pJCas,  priorEvidencez);
      priorEvidences.addAll(pSomeEvidences);
      FSArray moreEvidences = UIMAUtil.list2FsArray(pJCas, priorEvidences);
      statement.setMentalFunctionEvidences(moreEvidences);
      
    }
    
    return statement;
     
  } // end Method createMentalFunctioningMention() ---


  // =================================================
  /**
   * getMentionScope is where the magic happens - scoping what's a mention
   * 
   *   is it the phrase that covers where the evidence is?
   *   is it the phrase + the phrase(s) that also have evidence in them within the scope of the sentence?
   *   is it the list element that has the evidence in it?
   *   is it the list that has the list element with the evidence in it?
   *   
   * 
   * @param pJCas
   * @param pSentence
   * @param pSomeEvidences
   * @return Phrase
  */
  // =================================================
  private final Phrase getMentionScope(JCas pJCas, Annotation pSentence, List<MentalFunctioningOntology> pSomeEvidences) {
    
    Phrase returnVal = new Phrase(pJCas);
    returnVal.setBegin( pSentence.getBegin());
    returnVal.setEnd( pSentence.getEnd());
    
    // returnVal.addToIndexes();  <---- don't want this to stick around  - this could make it a memory leak though if I don't
    
    
    return returnVal;
  } // end Method getMentionScope() ------------------


  // =================================================
  /**
   * getMentalFunctionMention returns with a MentalFunctionMention for
   * the evidence passed in.
   * 
   * @param pJCas
   * @param pSomeEvidence
   * @return MentalFunctioningMention
  */
  // =================================================
  private final MentalFunctioningMention getMentalFunctionMention(JCas pJCas,  MentalFunctioningOntology pSomeEvidence) {
   
    MentalFunctioningMention returnVal = null;
    List<Annotation> someMentions = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  MentalFunctioningMention.typeIndexID, pSomeEvidence.getBegin(), pSomeEvidence.getEnd(), true );
    
    if ( someMentions != null && !someMentions.isEmpty())
      returnVal = (MentalFunctioningMention)someMentions.get(0);
    
    return returnVal;
  } // end Method getMentalFunctionMention() ---------


  // =================================================
  /**
   * isValidSection 
   *   there should be sections that we do not want to process
   *   Headers, footers, family history, medications, labs,
   *   
   *   medical history?
   * 
   * @param sectionName
   * @return boolean
  */
  // =================================================
  private boolean isValidSection(String sectionName) {
    // TODO Auto-generated method stub
    return true;
  } // end Method isValidSection() ------------------


//=================================================
 /**
  * filterBadIPIRMentions removes IPIR mentions that do not have
  * 2 people within the scope 
  * 
  * look for verb evidence
  * 
  * @param pJCas
  * @param pSentence
  * @param pSomeMentions
  * 
  */
 // =================================================
 private final void filterBadIPIRMentions(JCas pJCas, Annotation pSentence, List<Annotation> pSomeMentions) {

   List<Annotation> ipirActivities = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, IPIRActivities.typeIndexID, pSentence.getBegin(),  pSentence.getEnd(), true);
    boolean weSeen = lookForWe(pJCas, pSentence);
   
    List<Annotation> otherPersonsEvidence = findPeople( pJCas, pSentence );
   
   
   if (ipirActivities != null && !ipirActivities.isEmpty())
     for (Annotation activity : ipirActivities) {
       
       String buff = activity.getCoveredText();
      
      if (otherPersonsEvidence != null && otherPersonsEvidence.size() >= 1) {
        if ( 1 == 0 ) { 
          
          // this is taken out because it's of limited use, and calling the pos
          // tagger makes the exe too big. 
          
        // if ( PosFixes01Annotator.isVerb(pJCas, activity) 
        //	  && !isOther( pJCas,pSomeMentions ) 
        //     && !weSeen ) {
          //    && !isConditional( pJCas, activity)) {
           ;;
         } else {
           if (  !moreCalvinRules( pJCas, pSentence, activity, otherPersonsEvidence ) ) 
             activity.removeFromIndexes();      
         }
       } else {
         
         // delete the ipir
         if ( !moreCalvinRules( pJCas,pSentence, activity, otherPersonsEvidence ))
           activity.removeFromIndexes();
        // System.err.println("deleting because it's not refers to 2 or more people ipir " + npu + " for " + bbb);

       } // end else delete ipir fp
       
     } // end loop through activities
 } // end Method filterBadIPIRMentions() -----------------

 

//=================================================
/**
 * findPeople finds evidence of person entities within this span.
 *     This includes subjectIsPatient|subjectIsOther|FamilyHistory|personEvidences|
 *     pronouns|
 *     
 * @param pJCas
 * @param pSentence
 * @return List<annotaton>
 * 
 */
// =================================================
public static final List<Annotation> findPeople(JCas pJCas, Annotation pSentence ) {

  List<Annotation> personEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Person.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
  List<Annotation> familyHistoryEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, FamilyHistoryEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
  List<Annotation> subjectIsOtherEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SubjectIsOtherEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
  List<Annotation> subjectIsPatientEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SubjectIsPatientEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
 
  List<Annotation> otherPersonsEvidence = new ArrayList<Annotation>();
  if ( personEvidences != null && !personEvidences.isEmpty()) otherPersonsEvidence.addAll(personEvidences);
  if ( familyHistoryEvidence != null && !familyHistoryEvidence.isEmpty() )  otherPersonsEvidence.addAll(familyHistoryEvidence);
  if ( subjectIsOtherEvidence != null && !subjectIsOtherEvidence.isEmpty() )  otherPersonsEvidence.addAll(subjectIsOtherEvidence);
  if ( subjectIsPatientEvidence != null && !subjectIsPatientEvidence.isEmpty() )  otherPersonsEvidence.addAll(subjectIsPatientEvidence);

  otherPersonsEvidence = filterToOneInstanceOfHe( pJCas, otherPersonsEvidence );
 
  return otherPersonsEvidence;
   
} // end Method findPeople() ------------------------


 // =================================================
/**
 * moreCalvinRules returns true if this is an IPIR statement
 * 
 * @param pJCas
 * @param pSentence
 * @param activity
 * @param otherPersonsEvidence 
 * @return boolean
*/
// =================================================
 private final boolean moreCalvinRules(JCas pJCas, Annotation pSentence, Annotation activity, List<Annotation> pOtherPersonsEvidence) {
  
   boolean returnVal = false;
  
   returnVal = participateIPIRMentions( pJCas, activity );
   
   // look for signs of behavior and support 
   
   if ( !returnVal ) {
     List<Annotation> behaviorEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, BehaviorEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd() , true);
     List<Annotation> supportEvidence = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SupportEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd() , true);
     
     
     if ( behaviorEvidence != null && !behaviorEvidence.isEmpty())
       returnVal = true;
     
     else if ( supportEvidence != null && !supportEvidence.isEmpty() )
       returnVal = true;
     
     else if (  pSentence.getClass().getName().contains("SlotValue")) 
       returnVal = true;
     
    // else if (  pOtherPersonsEvidence == null || pOtherPersonsEvidence.isEmpty() ) 
    //     returnVal = true;
       
       
   } // end if this hasn't already been assigned by participation
   
   return returnVal;
  } // end Method moreCalvinRules() ---------


  // end Method moreCalvinRules() ----------------


// =================================================
/**
 * participateIPIRMentions returns true if this mention has to do with
 * the "participation" or "activities" part of the IPIR, where
 * another party need be mentioned, only the fact that the
 * patient/claimant participated in something - like group or OT
 * 
 * @param pMention
 * @return boolean
*/
// =================================================
private boolean participateIPIRMentions(JCas pJCas, Annotation pMention) {
  boolean returnVal = false;
  
  String categories = ((MentalFunctioningOntology)pMention).getCategories();
  
  String buff = pMention.getCoveredText();
  
  if ( categories != null && categories.trim().length() > 0 )
    if ( categories.contains("IPIRAbility") ||
        categories.contains("IPIRParticipation") ) {
      returnVal = true;
   
      // create IPIRParticipation Evidence here
      createIPIRParticipationEvidence( pJCas, pMention);
    
    }
  
  
   
  
  return returnVal;
}

 
  // =================================================
  /**
   * createIPIRParticipationEvidence 
   * 
   * @param pJCas
   * @param pMention
  */
  // =================================================
  private final void createIPIRParticipationEvidence(JCas pJCas, Annotation pMention) {
    Annotation statement = new IPIRParticipationEvidence(pJCas);
    statement.setBegin( pMention.getBegin());
    statement.setEnd( pMention.getEnd());
    statement.addToIndexes();
  
  }


// end Method participateIPIRMentions()-----------


// =================================================
		 /**
		  * isConditional  returns true if the mention has been marked as conditional 
		  * 
		  * 
		  * @param pActivity
		  * @return boolean
		 */
		 // =================================================
		  private boolean isConditional(JCas pJCas, Annotation pMention) {
		    boolean returnVal = false;
		    
		    List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pMention.getBegin(), pMention.getEnd(), true);
		    if ( terms != null && !terms.isEmpty()) 
		      for ( Annotation term : terms ) {
		        returnVal= ((LexicalElement)term).getConditional();
		        break;
		      }
		        
		   return returnVal;
		 } // end Method lookForWe() ------------------------


// =================================================
/**
 * filterToOneInstanceOfHe returns only one instance of he, or his, 
 * - insight - his brother - 
 *   - her/his/their  should be filtered out - 
 * 
 * @param pJCas
 * @param personEvidences
 * @return
*/
// =================================================
private static List<Annotation> filterToOneInstanceOfHe(JCas pJCas, List<Annotation> personEvidences) {
  
  String lastKey = "";
  List<Annotation> returnVal = new ArrayList<Annotation>( personEvidences.size() );
  if ( personEvidences != null && !personEvidences.isEmpty()) {
    for ( Annotation  personEvidence : personEvidences ) {
      String key = personEvidence.getCoveredText().toLowerCase().trim();
      if ( returnVal.isEmpty() || !( isPosessive ( key) && samePerson(key, lastKey) ) )
        returnVal.add( personEvidence);
      lastKey = key;
    }
    
    // uniq the keys
    returnVal = uniqThePronouns( returnVal);
    
  }
    return returnVal;
} // end Method filterToOneInstanceOfHe() ----------


// =================================================
/**
 * samePerson returns true if he|his  she|her 
 * 
 * @param person1
 * @param person2
 * @return boolean
*/
// =================================================
private static boolean samePerson(String person1, String person2) {
  
  boolean returnVal = false;
  
  if  ( _samePerson == null ) {

    _samePerson = new HashSet<String>();
    _samePerson.add( "he|his");
    _samePerson.add( "she|her");
    _samePerson.add( "his|he");
    _samePerson.add( "her|she");
    _samePerson.add( "they|their");
    _samePerson.add( "their|they");
    _samePerson.add( "you|your");
    _samePerson.add( "your|you");
    _samePerson.add( "our|we");
    _samePerson.add( "we|our");
    
  }
  String buff = person1.trim().toLowerCase() + "|" + person2.trim().toLowerCase() ;
  if ( _samePerson.contains(buff))
    returnVal = true;
  
  return returnVal;
  
} // end Method samePerson() ------------------------


// =================================================
/**
 * isPosessive returns true if the string is possessive
 *   Definition was taken from the SPECIALIST Lexicon's lrprn
 *    - I've hard coded it because it's 15 lines
 * 
 * @param key
 * @return boolean
*/
// =================================================
  public static final boolean isPosessive(String key) {
    boolean returnVal = false;
    
   
    if (staticPosessiveHash == null ) {
      staticPosessiveHash = new HashSet<String>();
      staticPosessiveHash.add( "her");
      staticPosessiveHash.add( "hers");
      staticPosessiveHash.add( "his");
      staticPosessiveHash.add( "mine");
      staticPosessiveHash.add( "my");
      staticPosessiveHash.add( "one's");
      staticPosessiveHash.add( "our");
      staticPosessiveHash.add( "ours");
      staticPosessiveHash.add( "their");
      staticPosessiveHash.add( "theirs");
      staticPosessiveHash.add( "whose");
      staticPosessiveHash.add( "your");
      staticPosessiveHash.add( "yours");
    }
    if ( staticPosessiveHash.contains(key))
        returnVal = true;
    
  return returnVal;
  } // end Method  isPossessive() -----------------
  
//=================================================
/**
* isPosessive returns true if the string is possessive
*   Definition was taken from the SPECIALIST Lexicon's lrprn
*    - I've hard coded it because it's 15 lines
* 
* @param key
* @return boolean
*/
//=================================================
 public static final boolean isPronoun(String key) {
   boolean returnVal = false;
   
  
   if (staticPosessiveHash == null ) {
     staticPosessiveHash = new HashSet<String>();
     staticPosessiveHash.add( "her");
     staticPosessiveHash.add( "hers");
     staticPosessiveHash.add( "his");
     staticPosessiveHash.add( "mine");
     staticPosessiveHash.add( "my");
     staticPosessiveHash.add( "one's");
     staticPosessiveHash.add( "our");
     staticPosessiveHash.add( "ours");
     staticPosessiveHash.add( "their");
     staticPosessiveHash.add( "theirs");
     staticPosessiveHash.add( "whose");
     staticPosessiveHash.add( "your");
     staticPosessiveHash.add( "yours");
   }
   if ( staticPosessiveHash.contains(key))
       returnVal = true;
   
 return returnVal;
 } // end Method  isPossessive() -----------------


// =================================================
/**
 * lookForWe returns true if the sentence has the word "we" in it.  Such sentences
 * do have IPIR Activities, but likely from the perspective of a clinician or SSA
 * 
 * 
 * @param pSentence
 * @return boolean
*/
// =================================================
 private boolean lookForWe(JCas pJCas, Annotation pSentence) {
   boolean returnVal = false;
   
   List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true);
   if ( terms != null && !terms.isEmpty()) 
     for ( Annotation term : terms ) {
       String aTerm = term.getCoveredText();
       if ( aTerm != null && aTerm.toLowerCase().equals("we")) {
           returnVal = true;
           break;
       }
     }
       
  return returnVal;
} // end Method lookForWe() ------------------------
 
//=================================================
/**
* uniqThePronouns culls multiple references to the same
* pronoun to prevent "he state that he went to the store"
* 
* 
* @param key
* @return boolean
*/
//=================================================
public static final List<Annotation> uniqThePronouns( List<Annotation> pPronouns) {
  List<Annotation> returnVal = null;
  if ( pPronouns != null && !pPronouns.isEmpty() ) {
	  HashSet<String> keyHash = new HashSet<String>( pPronouns.size());
	  returnVal = new ArrayList<Annotation>( pPronouns.size());
	 for ( Annotation aPronoun : pPronouns ) {
		 String key = aPronoun.getCoveredText().trim().toLowerCase();
		 if ( !keyHash.contains( key) ) {
			 keyHash.add(key);
			 returnVal.add( aPronoun);
		 }
	 }
 }
 return returnVal;
} // end Method uniqThePronouns() -----------------

//----------------------------------
/**
* isOther returns true if one of these mentions is not about the patient
* 
* @param pJCas
* @param someMentions
* @return boolean
* 
**/
//----------------------------------
public boolean isOther( JCas pJCas, List<Annotation> pSomeMentions ) {
  
  boolean returnVal = false;
  
  if ( pSomeMentions != null && !pSomeMentions.isEmpty())
    for ( Annotation aMention : pSomeMentions) {
      List<Annotation> someTerms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID,  aMention.getBegin(), aMention.getEnd(), true );
      if ( someTerms != null && !someTerms.isEmpty())
        for ( Annotation aTerm : someTerms ) {
          String subject = ((LexicalElement)aTerm).getSubject();
          if ( subject != null && subject.toLowerCase().contains("Other")) {
            returnVal = true;
            break;
          }
        }
    
    }
    
  return returnVal;
  
  
} // end Method isOther() ----------

//=================================================
/**
* isComCogActivities returns true if the categories 
* are one of the following from the categories in the lragr files
* 
* N.B.  there is a better way to do this via finding the category
* and generalizing from the uima types.  
* 
* @param pLRAGRCategories
* @return boolean
*/
// =================================================
public static final boolean isComCogActivities(String pLRAGRCategories ) {
	  boolean returnVal = false;
	  
	  if ( pLRAGRCategories != null && !pLRAGRCategories.isEmpty()) {
		
		  String[] cats =  U.split(pLRAGRCategories, ":");
		  
		  if ( cats != null && cats.length > 0 )
			  for ( String cat: cats ) {
				  cat = cat.trim().toLowerCase();
				  if ( isComCogActivity( cat)) {
					  returnVal = true;
					  break;
				  }
			  }
		  
	  }
	  
	  
	  return returnVal;
	  
} // end Method isComCogActivities() -------------

//=================================================
/**
* isComCogActivity returns true if the category
* are one of the following from the categories in the lragr files
* 
* N.B.  there is a better way to do this via finding the category
* and generalizing from the uima types.  
* 
* @param pLRAGRCategory
* @return boolean
*/
// =================================================
public static final boolean isComCogActivity(String pLRAGRCategory ) {
	  boolean returnVal = false;
	  
	  if ( _comCogCategories_hash == null ) {
		  loadComCogCategories_hash( );
	  }
	  returnVal = _comCogCategories_hash.contains(pLRAGRCategory);
	  
	  return returnVal;
	  
} // end Method isComCogActivities() -------------

//=================================================
/**
*loadComCogCategories_hash
* are one of the following from the categories in the lragr files
* 
* N.B.  there is a better way to do this via finding the category
* and generalizing from the uima types.  
* 

*/
// =================================================
public static void loadComCogCategories_hash( ) {
	
	  String[] cats = U.split(lragrCategoriesForComCogActivities, ":");
	   _comCogCategories_hash = new HashSet<String>( cats.length + 1); 
	   for ( String cat: cats )
		   _comCogCategories_hash.add( cat.trim().toLowerCase());
	
	  
} // end Method isComCogActivities() -------------
	 

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
    this.annotationSetName = U.getOption(pArgs, "--AnnotationSetName=" ,"framework") ;
    this.INFACT_MODE = Boolean.valueOf( U.getOption(pArgs, "--INFACT_MODE=", "false"));
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private boolean INFACT_MODE =false;
  
  private static HashSet<String> staticPosessiveHash = null;
  private String annotationSetName = null;
  public static final String MentalFunctioningOntologyNERLexicaSMALL =  "resources/vinciNLPFramework/mentalFunctionOntologyNER/010_TopVerbNetClasses.lragr" ;
      
  public static final String MentalFunctioningOntologyNERLexica = 
    
       AssertionEvidenceAnnotator.EvidenceLRAGRFiles + ":" 
    + "resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr" + ":"
 
    + "resources/vinciNLPFramework/sections/documentTitles.lragr" + ":"
    + "resources/vinciNLPFramework/sections/pageHeaderPageFooterEvidence.lragr"  + ":"
    + "resources/vinciNLPFramework/dateAndTime/dateAndTime.lragr" + ":" 
    + "resources/vinciNLPFramework/sections/labsEvidence.lragr" + ":"
    + "resources/vinciNLPFramework/sections/knownSlots.lragr" + ":"
    + "resources/vinciNLPFramework/sections/slotValueEquations.lragr" + ":"
    + "resources/vinciNLPFramework/sections/panelNames.lragr" + ":"
   
    + "resources/vinciNLPFramework/ucum/ucum.lragr" + ":"
  //  +  AddressEvidenceAnnotator.Address_Lexica + ":" 
  //  +  DemographicEvidenceAnnotator.Demographic_Lexica + ":"
  //  +  PageHeaderFooterEvidenceAnnotator.PageHeaderFooter_Lexica + ":" 
    +  PersonAnnotator.persons_Lexica + ":" 
    
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/lvg_verbs_DerivationsAndNomilizations.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/lvg_verbs_spellingVariants_inflections_FACTS.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/lvg_Verbs_inflected.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/lvg_verbs_Synonyms.lragr"  + ":"
   //  "resources/vinciNLPFramework/mentalFunctionOntologyNER/filteredMFVerbNet.lragr" + ":"  <----- superceded by below
    
   
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/001_TopExternalFactors.lragr" + ":" 
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/002_TopPersonFactors.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/003_TopActivitiesAndParticipation.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/004_TopBehavior.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/005_TopActivitiesOfDailyLiving.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/006_TopGeneralTasksAndDemands.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/007_TopLearningAndApplyingKnowledge.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/008_TopCommunicationActivities.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/009_TopCognitiveActivities.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/missingTerms.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/SSAListingTerms.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/010_TopVerbNetClasses.lragr" + ":"
    +  "resources/vinciNLPFramework/mentalFunctionOntologyNER/011_TopIPIRNominalizations.lragr" + ":" 
    
    +  "resources/vinciNLPFramework/IPIRCategories/intimateRelationships.lragr" + ":"
    +  "resources/vinciNLPFramework/IPIRCategories/workRoles.lragr" + ":"
    +  "resources/vinciNLPFramework/IPIRCategories/emotion.lragr" + ":"
    +  "resources/vinciNLPFramework/IPIRCategories/strangerRelationships.lragr" + ":"
    +  "resources/vinciNLPFramework/IPIRCategories/informalRelationships.lragr" + ":"
    +  "resources/vinciNLPFramework/IPIRCategories/IPIRInteractions.lragr" + ":" 
    +  "resources/vinciNLPFramework/IPIRCategories/OtherIPIRVerbs.lragr" + ":"  
    +  "resources/vinciNLPFramework/IPIRCategories/IPIRExceptions.lragr" + ":"
    
    + "resources/vinciNLPFramework/ComCogCategories/ComCogVerbNetCategories.lragr" + ":"
    + "resources/vinciNLPFramework/ComCogCategories/ComCogUMLSTermsAndVariants.lragr" + ":"
    + "resources/vinciNLPFramework/ComCogCategories/MoreComProducing.lragr" + ":"
    
    
   

    
    +  SyntaticPipeline.SyntaticTerminologyFiles ;
  
   ;
   public static final String MentalFunctioningOntologyOutputTypes = 
    
   "ActivitiesAndParticipation:  ActivitiesOfDailyLiving :  Activities_ :  AdaptiveBehavior  :  ApplyingKnowledge_ :" +
   "Behavior_  :  BodyFunctions  :  BodyStructures :" +
   "CognitiveActivities :  CognitiveFunctions  :  CommunicationActivities  :  CommunicationFunctions  :  CommunityActivities :  Context_  :  ContextualFactors :" +
   "DomesticLifeActivities  :  Environment_  :  EnvironmentalFactors  :  ExternalFactors: " + 
   "FeedbackAppraisal  :  Feedback_  :  GeneralTasksAndDemands  :  GlobalMentalFunctions :  HealthConditionFactors :  HealthCondition_ :" +
   "IPIRActivities :  LearningAndApplyingKnowledge  :  Learning_ :" +
   "MajorLifeAreasActivities:  MaladaptiveBehavior :  MentalFunctioning  :  MentalFunctioningMention  :  MentalFunctioningOntology  :  MentalFunctions_ :  MobilityActivities : " +
       
   "NeuroCognitiveProcessing  :  NonActions    :  ObservableBehavior : BehaviorEvidence : " +
   
   "Person_  :  Participation_ :  PersonFactors :  PersonalBackgroundFactors : " +
   "SelfCareActivities    :  SensoryMotorFunctionFunctions    :  SensoryProcessing    :  SpecificMentalFunctions ";
   
   // These are a subset of the Ontology categories that are consistant with the RMD message
   // These include the activities and participation categories, ignores the body function
   // categories, and ignores the categories we've not scoped terminologies out yet
   // Activities_ :  AdaptiveBehavior  :  ApplyingKnowledge_ : Behavior_  :  BodyFunctions  : Context_, ContextualFactors,  Environment_, ExternalFactors, Feedback_, HealthCondition< HealthConditionFactors, 
   // BodyStructures ,  GeneralTasksAndDemands, GlobalMentalFunctions, LearningAndApplyingKnowledge  :  Learning: MajorLifeAreasActivities:  MaladaptiveBehavior :_ 
   // MentalFunctioning  :  MentalFunctioningMention  :  MentalFunctioningOntology MentalFunctions_ : 
   // NeuroCognitiveProcessing  :  NonActions    :  ObservableBehavior : 
   // Person_  :  Participation_ :  PersonFactors :  PersonalBackgroundFactors 
   // SensoryMotorFunctionFunctions    :  SensoryProcessing    :  SpecificMentalFunctions
   public static final String MentalFunctioningOntologyOutputTypes_JP = 
       
       "ActivitiesAndParticipation:  ActivitiesOfDailyLiving :  " +
       "CognitiveActivities :  CognitiveFunctions  :  CommunicationActivities  :  CommunicationFunctions  :  CommunityActivities:" +
       "DomesticLifeActivities  :   EnvironmentalFactors  :  ExternalFactors: " + 
       "FeedbackAppraisal  :  " +
       "IPIRActivities : " +
       "MobilityActivities : " +
       "SelfCareActivities ";
       
   public static final String ComCogOntologyOutputTypes = 
       
    //   "ActivitiesAndParticipation:  ActivitiesOfDailyLiving :  MobilityActivities : DomesticLifeActivities : SelfCareActivities :  Activities_ : " + 
       "ActivitiesAndParticipation : " + 
       "AdaptiveBehavior  : Behavior_ :   MaladaptiveBehavior : BehaviorEvidence : ObservableBehavior :  NonActions : ApplyingKnowledge_ : LearningAndApplyingKnowledge  :  Learning_ :"  +
       "CognitiveActivities :  CognitiveFunctions  :  CommunicationActivities  :  CommunicationFunctions  : " +
       "CommunityActivities : GeneralTasksAndDemands  : MajorLifeAreasActivities:  " +
       "Context_  :  ContextualFactors :" + 
       "Environment_  :  EnvironmentalFactors  :  ExternalFactors: PersonalBackgroundFactors : PersonFactors " ;
      
      
       
   
   private static HashSet<String> _samePerson = null;
   
   
 private static HashSet<String>_comCogCategories_hash = null;
   
   private static final String lragrCategoriesForComCogActivities = "CognitiveActivities: CommunicationActivities:" 
        + "Behavior_: ActivitiesAndParticipation:"
   		+ "GeneralTasksAndDemands: LearningAndApplyingKnowledge: Learning_: ApplyingKnowldege_:"
   		+ "AbnormalBehavior: MaladaptiveBehavior: InappropriateBehavior: NormalBehavior: AdaptiveBehavior_:"
   		+ "appliedMemory: CognitiveAbilities: adaptation: GeneralTasksAndDemands: d110_d129_purposefulSensoryExperiences:"
   		+ "d130_d159_basicLearning: d160_focusing: d163_thinking: d166_cogReading: d170_cogWriting: d172_cogCalculating:"
   		+ "d175_ProblemSolving: d177_MakingDecisions: d179_ApplyingKnowledgeOther: d179_ApplingKnowledgeOther:"
   		+ "d160_focusing: d210_d220_tasks: d230_dailyRoutine: d240_handlingStress: d310_d329_comReceiving:"
   		+ "d330_d349_comProducing: d350_d369_comDiscussion: Adaption: Participation: persistence: pacing"
   		;
      
   
  
} // end Class LineAnnotator() ---------------

