// =================================================
/**
 * FromMultiPageText reads in text, and creates text files and jcas's for each page
 * in the input file.
 * 
 * @author GD
 * @created 2023.02.15
 * 
 *          This might need a writer to put the output files back together at the end of the pipeline?
 * 
 */
// =================================================

package gov.nih.cc.rmd.framework.marshallers.pages;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.text.FromText;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineWriters;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

public class FromMultiPageText extends FromText {

  // =======================================================
  /**
   * Constructor FromMultiPageText is called from SuperReader
   * assumes that the initialize() method will be called later on.
   *
   */
  // =======================================================
  public FromMultiPageText() {

  } // end Constructor() ---------------------

  // =======================================================
  /**
   * Constructor FromMultiPageText
   *
   * @param pInputDir
   * @throws ResourceInitializationException
   */
  // =======================================================
  public FromMultiPageText(String pInputDir) throws ResourceInitializationException {

    initialize(pInputDir, true);

  } // end Constructor() ---------------------

  // =======================================================
  /**
   * Constructor FromMultiPageText
   *
   * @param pInputDir
   * @param pRecurseIntoSubDirs
   * 
   * @throws ResourceInitializationException
   */
  // =======================================================
  public FromMultiPageText(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {

    initialize(pInputDir, pRecurseIntoSubDirs);

  } // end Constructor() ---------------------

  // =================================================
  /**
   * Constructor
   *
   * @param args
   * @throws ResourceInitializationException
   * 
   **/
  // =================================================
  public FromMultiPageText(String[] args) throws ResourceInitializationException {

    initialize(args);
  }

  // =======================================================
  /**
   * getFiles retrieves the list of files from the directory
   * that match the filtering criteria
   * 
   * @param pListOfFiles
   * @param pFilesAndSubDirs
   * @throws ResourceInitializationException 
   * 
   */
  // =======================================================
  @Override
  public void getFiles(List<File> pListOfFiles, File[] pFilesAndSubDirs) throws ResourceInitializationException {

    for (File file : pFilesAndSubDirs)
      if (file.isDirectory()) {
        getFiles(pListOfFiles, file.listFiles());
      } else if (filterInTextFiles(file)) {

        try {
          getFilesAux(file, pListOfFiles);
        } catch (Exception e) {
          e.printStackTrace();
          String msg = "Issue breaking files into page files " + e.toString() + "for file " + file.getAbsolutePath() ;
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getFiles", msg);
          throw new ResourceInitializationException( );
        }
      }

  } // End Method getFiles() ======================

  // =================================================
  /**
   * getFilesAux gets the file, breaks it into page files
   * if there are no page breaks, don't make a separate
   * directory for it
   * 
   * @param file
   * @param pListOfFiles
   */
  // =================================================
  private void getFilesAux(File file, List<File> pListOfFiles) throws Exception {
   // System.err.println("adding file " + file.getAbsolutePath());
    // break each file into a subdirectory with a file for each page of the file
    // hmmm - can't write the files in the input dir, do it in the text/outputDir/

    // create outputDir/txt/fileName_ directory
    String[] pages = getTextPages(file);
    
    if ( pages != null && pages.length > 0 ) {
      
      if ( pages.length == 1 ) {
        pListOfFiles.add(file);
      } else {
        
        String newDirName = U.getFileNameOnly(file.getAbsolutePath());
        String justFileName = U.getFileNameOnly(file.getAbsolutePath());
        this.tempDir = this.outputDir + "/" + "txt";
        U.mkDir(this.tempDir);
        String pagesDir = this.outputDir + "/" + "txt" + "/" + newDirName + "_pages";
        U.mkDir(pagesDir);

        for (int i = 0; i < pages.length; i++) {
          
          // -------------------------------------------
          //  Question - should this file be removed if text writer is not 
          //             specified, in the destroy ?   yes?
          String pageName = pagesDir + "/" + justFileName +  "_" + U.zeroPad(i, 4) + ".txt";
          PrintWriter out = new PrintWriter(pageName);
          out.print(pages[i]);
          out.close();
          File aFile = new File(pageName);
          pListOfFiles.add(aFile);
        } // end loop thru pages
        
      } // end if there is more than one page
      
    } // end if there are pages
    
  } // end Method getFilesAux() -----------------------

  // =================================================
  /**
   * getTextPages opens the file up, reads it, returns
   * an array of String, one element per page
   * 
   * @param pFile
   * @return List<String>
   * @throws Exception 
   */
  // =================================================
 public final String[] getTextPages(File pFile) throws Exception {

   String[]  returnVals = null;
    
    try {
     String contents = U.readFile( pFile);
     returnVals = getTextPages( contents );
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue breaking file " + pFile.getAbsolutePath() + " into pages " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getTextPages", msg );
      throw new Exception (msg);
    }
    
    return returnVals;
  } // end Method getTextPages() -----------

  // =================================================
  /**
   * getTextPages decomposes text into a list of text
   * where each element is a page (demarked by a page break)
   * 
   * @param pContents
   * @return String[] 
  */
  // =================================================
  public String[]  getTextPages(String pContents) {
    
    String pages[] = U.split( pContents, U.PAGE_BREAK_STRING );
    
     return pages;
  } // end Method getTextPages() ------------
  
  

//=================================================
 /**
  * destroy  cleans up after processing
 */
 // =================================================
  @Override
  public void destroy() {
    
    boolean writeTextFilesOut = false;
    String outputFormatTypes = U.getOption(this.args,  "--outputFormat=" , "none" );
    
    if (outputFormatTypes.contains( FrameworkBaselineWriters.TEXT_WRITER_) )
      writeTextFilesOut = true;
    
    try {
    if ( this.tempDir != null )
      if ( !writeTextFilesOut ) {
        File aDir = new File( this.tempDir);
        U.deleteDirectory(aDir);
      }
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroy", "Issue with removing temp dir " + e.toString());
    }
    
  } // end Method destroy() ----------------

  // ----------------------------------------
  // Class Variables
  // ----------------------------------------
  private String tempDir = null;

} // end Class FromMultiPageText() -------------
