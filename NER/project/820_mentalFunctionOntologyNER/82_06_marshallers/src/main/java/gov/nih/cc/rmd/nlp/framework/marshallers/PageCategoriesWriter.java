//=================================================
/**
 * PageCategoriesWriter creates a table of pageName|pageCatories
 * 
 * @author  GD
 * @created 2023.02.23
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers;


import java.io.PrintWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;



public class PageCategoriesWriter extends AbstractWriter {





  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public PageCategoriesWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  } // end Constructor() ----------------------



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

    String fileName = VUIMAUtil.getDocumentId(pJCas);
    fileName =  U.getFileNameOnly(fileName);
   
    String docTypes = VUIMAUtil.getDocumentType(pJCas);
    if ( docTypes != null && docTypes.trim().length() > 0 )
      docTypes = docTypes.replace('|', ':' );
    
    boolean processMe = VUIMAUtil.getDocumentProcessMe(pJCas);
    
    this.out.print(  fileName + COLUMN_DELIMITER + processMe + COLUMN_DELIMITER + docTypes + "\n");
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
  


//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  
  try {
    this.out.close();
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println( GLog.ERROR_LEVEL, this.getClass(), "destroy", "Issue closing the PageCategoriesWriter: " + e.toString() );
  }

  
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}






  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
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
      
   
    
      
    } // end Method initialize() --------------



    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
    
      String args[] = new String[1];
      args[0] = "--ouputDir=" + pOutputDir;
      
      initialize( args);
    
      
      
    } // end Method initialize() --------------


    


    
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
      this.outputDir = U.getOption(pArgs, "--outputDir=", "./output" ); 
      this.outputDir = this.outputDir + "/pageCategories";
      this.processingListings = Boolean.parseBoolean( U.getOption( pArgs,  "--processListings=", "false"));
      String columnDelimiter = U.getOption(pArgs,  "--columnDelimiter=", "PIPE");   //  COMMA|PIPE
      
      if ( columnDelimiter == null )
        COLUMN_DELIMITER = "|";
      else if ( columnDelimiter.equals("COMMA"))
        COLUMN_DELIMITER = ",";
        else if ( columnDelimiter.equals("PIPE"))
          COLUMN_DELIMITER = "|";
      else 
        COLUMN_DELIMITER = "|";
      
      
      try {
        
        U.mkDir(outputDir);
        
       initializeColumnNames();
       initializeInstanceFiles();
        
     
      } catch (Exception e) {
        throw new ResourceInitializationException( e);
      }
     
        
    } // end Method initialize() -------
   
    // =================================================
    /**
     * initialize_instanceFile opens up the file that
     * has the instance counts
     * @throws Exception 
     * 
    */
    // =================================================
    private final void initializeInstanceFiles() throws Exception {
      try {
        
        long threadId = Thread.currentThread().getId();
      
        
        String instanceFileName = this.outputDir + "/PageClassifier_" + threadId + ".csv" ; 
        this.out = new PrintWriter ( instanceFileName );
       
        this.out.print(  this.InstanceColumnNames + "\n");
       
       
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println (GLog.ERROR_LEVEL, this.getClass(), "initializeInstanceFile", "Something went wrong with opening the instance file " + e.toString());
        throw e;
      }
      
    } // end Method initialie_instanceFile() -----------


    // =================================================
    /**
     * initializeColumnNames 
     * 
    */
    // =================================================
    private void initializeColumnNames() {
      
      StringBuffer buff2 = new StringBuffer();
      buff2.append( U.spacePadRight(10, "RowNum"));         buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(30, "FileName"));       buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(50, "ProcessMe"));      buff2.append(COLUMN_DELIMITER );
      buff2.append( U.spacePadRight(30, "Categories"));       buff2.append(COLUMN_DELIMITER );
     
      
      // rowNum | fileName | processMe | category 
      this.InstanceColumnNames = buff2.toString();
      
    } // end Method initialize_column_names() ----------

    // ---------------------------------------
    // Global Variables
    // ---------------------------------------
    protected int annotationCtr = 0;
    ProfilePerformanceMeter performanceMeter = null;
    String Columns[] = null;
    String InstanceColumnNames = null;
    String ColumnNames = null;
    String outputDir = null;
    PrintWriter out = null;
    String COLUMN_DELIMITER = "\t,";
    int[] totalTable = null;
    int ctr = 0;
    int docCtr = 0;
    boolean processingListings = false;
    boolean highlightMentions = false;
    
    
    
     
  

} // end Class toCommonModel
