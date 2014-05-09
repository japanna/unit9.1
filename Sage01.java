// Sage01.java

/****
* 
*
* @author	Anna Ntenta
* @version	1.0 Last Modified 
*/

import javax.swing.*;                // Swing components
import java.awt.*;                   // Colors, Fonts, etc.
import java.io.*;                    // File I/O
import java.awt.event.*;             // ActionListener, etc.
import java.util.*;                  // Scanner class
import java.text.*;					// date
import java.awt.print.*;			// Print functions 
import java.lang.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import au.com.bytecode.opencsv.CSVReader;



public class Sage01 extends JFrame implements ActionListener, Printable
{
    private static final String GREETING = "\n               Welcome to Rome!  I'm your concierge, Signore Mario. (◕‿◕)ﾉ\n\n               How can I help you?  I'm an expert on food, sights and love!\n\n                                                  (Say \"bye\" to quit)\n              __________________________________________________";

	// question area
	private JPanel questionArea = new JPanel(); 
	private JLabel prompt =  new JLabel ("Please type your question here:", JLabel.RIGHT);
    private JTextField questionField = new JTextField (30); 

    // conversation area
	private JTextArea conversation = new JTextArea(GREETING);
    
    // print area
    private JPanel printArea = new JPanel();
    private JButton  printButton = 
    new JButton ("Print conversation");  

    // graphics component used to print the conversation
    private Component c;

    // stores user's latest question 
    private String question;

    // stores some of user's questions to use again later
    private String storedQuestion;

    // a string representing a group of keywords used in pattern matching, built from a CSV file
    private String csvString;

    private Pattern pattern;


/** 
 * constructor sets up the application's interface and 
 * builds a string of keywords from a CSV file.
 * The keywords in the string are used in pattern matching in 
 * the functions generalResponse() and specificResponse()
 */
    public Sage01() throws FileNotFoundException, IOException
    {        
    	super ("Sage ver. 01");  
    	setLayout (new BorderLayout ());      
        Font f = new Font ("Arial", Font.PLAIN, 14);

    	// question area        
    	add (questionArea, BorderLayout.NORTH);
    	questionArea.add (prompt);
    	questionArea.add (questionField);
        questionField.addActionListener(this);

    	// conversation area
        add (new JScrollPane (conversation), BorderLayout.CENTER);
        conversation.setFont(f);
        conversation.setLineWrap (true); 
        conversation.setEditable(false);
        

        // print area
        add (printArea, BorderLayout.SOUTH);

        printArea.add(printButton);
        printButton.addActionListener (this);
        printButton.setForeground (Color.BLUE.darker());

        storedQuestion = "you might need help";

        // openCSV object, creates a reader out of a CSV file
        CSVReader reader = new CSVReader(new FileReader("rome.csv"));

        // a temporary object used to build a string of keywords from CSV file
        StringBuilder temp = new StringBuilder();
        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            // append every keyword from the CSV file to a "word boundary" regex
            temp.append(nextLine[0]).append("\\b|\\b");  
        }
        
        // remove the last "or"
        temp.deleteCharAt(temp.lastIndexOf("|"));
        // add parentheses to form a group regex (see )
        csvString = "(\\b" + temp.toString() + ")";

        pattern = Pattern.compile(csvString);
        
    } 


/** 
 * displayAnswer() displays the answer in the conversation field.
 *
 * @param answer -- a String representing user's question
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void displayAnswer (JTextArea conversation, String answer) 
    {
        conversation.append("\n\n" + answer);
        questionField.setText(null);
        questionField.requestFocusInWindow(); 
    }

/** 
 * printConversation() prints the conversation to a printer of user's choice.
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void printConversation (JTextArea conversation) throws PrinterException
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        //job.setPrintable(this);
        boolean printing = job.printDialog();
        if( printing ) 
        {
            try {
                    conversation.print();
                    questionField.requestFocusInWindow(); 
                    //conversation.append("\n\nPrinting this conversation...");  
            }  
            catch( PrinterException e ) {
                conversation.append("\n\nSomething went wrong. Please check the printer.");  
            }
        }  
        else {
            conversation.append("\n\nYou cancelled the print job.");  
            questionField.requestFocusInWindow(); 
        } 
    }

/** 
 * saveLogConversation() saves the conversation to a log (conversationLog.txt)
 * when the user ends the conversation (or prints?)
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void saveLogConversation (JTextArea conversation) 
    {
        try { 
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            conversation.append("\n\nDate_Time: " + timeStamp + "\n--------------------------");
        
            //save conversation to a log
            File fileName = new File("conversationLog.txt");
            FileWriter outStream =  new FileWriter (fileName, true);
        
            outStream.append ("\n" + conversation.getText() + "\n");
            outStream.close ();
        } 
        catch (IOException e) 
        {
            conversation.append("\n\nIOERROR: " + e.getMessage() + "\n");
            e.printStackTrace();
        }  
    }

/** 
 * resetConversation() resets the JTextArea after user has typed "bye"
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */

    private void resetConversation (JTextArea conversation) 
    {
        conversation.setText(GREETING);
        questionField.setText(null);
        questionField.requestFocusInWindow();
    }

/**
   * This is the "callback" method that the PrinterJob will invoke.
   * This method is defined by the Printable interface.
   */
public int print(Graphics g, PageFormat format, int pagenum) {return Printable.PAGE_EXISTS;}
 /* public int print(Graphics g, PageFormat format, int pagenum) {
    // The PrinterJob will keep trying to print pages until we return
    // this value to tell it that it has reached the end
    if (pagenum > 0) 
      return Printable.NO_SUCH_PAGE;

    // We're passed a Graphics object, but it can always be cast to Graphics2D
    Graphics2D g2 = (Graphics2D) g;

    // Use the top and left margins specified in the PageFormat Note
    // that the PageFormat methods are poorly named.  They specify
    // margins, not the actual imageable area of the printer.
    g2.translate(format.getImageableX(), format.getImageableY());

    // Tell the Component to draw itself to the printer by passing in 
    // the Graphics2D object.  This will not work well if the Component
    // has double-buffering enabled.
    c.paint(g2);

    // Return this constant to tell the PrinterJob that we printed the page
    return Printable.PAGE_EXISTS;
  }*/

/** 
 * transformQuestion() takes a sentence and looks for certain key words ("I", "you".)
 * It switches the point of view from the user's to the computer's.
 *
 * @param question -- string representing user's last question
 * @return transformed question -- the question from the computer's "point of view"
 */

    private String transformQuestion (String question) throws IOException, FileNotFoundException 
    {
        // new hash map of key words
        Map<String,String> powWords = new HashMap<String,String>();

        // openCSV object, creates a reader out of a CSV file containing "point of view" substitutes
        CSVReader reader = new CSVReader(new FileReader("pointOfView.csv"));

        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            powWords.put(nextLine[0], (String)nextLine[1]);  
        }

        // Build a string of the above key words
        StringBuilder builder = new StringBuilder();
        for(String s : powWords.keySet()){
            builder.append(s).append("\\b|\\b");   
        }
        // remove the last "or"
        builder.deleteCharAt(builder.lastIndexOf("|"));

        // convert into regex group string (\\b is "word boundary")
        String patternString = "(\\b" + builder.toString() + ")";

        // compile the regular expression into a pattern
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

        // create a matcher from the pattern and the question
        Matcher matcher = pattern.matcher(question);

        StringBuffer sb = new StringBuffer();
        // scan the question for key words, replace key word with corresponding
        // replacement word
        while(matcher.find()) {
            matcher.appendReplacement(sb, powWords.get(matcher.group(1)));
        }
        String transformQuestion = matcher.appendTail(sb).toString();
        int rand = (int) (Math.random() * 4);
        System.out.println("Stored question rand: " + rand);
        if (rand == 2) {
            storedQuestion =  transformQuestion;
            // remove all non-word characters except single quote
            storedQuestion = storedQuestion.replaceAll("[\\W&&[^']&&[^\\s]]", "");
        
            // remove "please"
            storedQuestion = storedQuestion.replaceAll("please|\\byes\\b|\\bwell\\b", "");
        }
        return transformQuestion;
    }

/** 
 * generalResponse() takes a question sentence and looks for key words.
 * It generates a response based on whether the sentence matches a 
 * predefined pattern.
 *
 * @param question -- string representing user's last question
 * @return response -- string representing a response to question 
 */

    private void generalResponse (String question) throws IOException 
    {
        // remove all non-word characters except single quote
        question = question.replaceAll("[\\W&&[^']&&[^\\s]]", "");
        
        // remove "please"
        question = question.replaceAll("please|\\byes\\b|\\bwell\\b", "");

        // Pattern generalPattern = Pattern.compile(csvString);
        Matcher generalMatcher = pattern.matcher(question);

        // if we've found some matching key words, display a response (a rephrasing of the question)
        if (generalMatcher.find()) {
            displayAnswer(conversation, "  " + question + "..."); 

            specificResponse(question);
        }
        // if not, ask for more information by displaying a general response
        else {
            final String[] generalAnswers1 = {"Pazzo! Why you say that?", 
                                            "Sono confuso... Can you elaborate?", 
                                            "Cosa! How do you mean?", 
                                            "Non capisco. Can you explain?", 
                                            "Caro mio, please rephrase that.",
                                            "Nobody likes that. Qualcos'altro?",
                                            "Dio mio! Not in Rome! What else?",
                                            "Offeso! Go to Paris for that!\nCan I help you with anything else?"};
            int rand = (int) (Math.random() * 12);
            System.out.println("general response rand and question: " + rand + "  " + storedQuestion );
            String s = storedQuestion;
            if ((rand > 7) && (!s.equals(question))) {
                displayAnswer(conversation, "  Earlier you indicated " + s + ". Can you tell me why?");
            }
            else {
                if (rand > 7) rand = rand - 3;
                s = generalAnswers1[rand];
                displayAnswer(conversation, "  " + question + "? " + s);
            }
            
        }
    }

/** 
 * specificResponse() builds a HasMap of key words and looks for these words
 * in user's question.
 * It generates a response based on whether the sentence matches a 
 * predefined pattern.
 *
 * @param question -- string, user's last question
 * @return response -- string representing a response to question 
 */

    private void specificResponse (String question) throws IOException, FileNotFoundException 
    {
        // initiate new hash map of key words
        Map<String,String> keyWords = new HashMap<String,String>();
        // openCSV object, creates a reader out of a CSV file
        CSVReader reader = new CSVReader(new FileReader("rome.csv"));

        
        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            keyWords.put(nextLine[0], (String)nextLine[1]);  
        }
         /*  
        // Build a string of the above key words
        StringBuilder builder = new StringBuilder();
        for(String s : keyWords.keySet()){
            builder.append(s).append("\\b|\\b");   
        }
        // remove the last "or"
        builder.deleteCharAt(builder.lastIndexOf("|"));
        // convert into regex group string (\\b is "word boundary")
        String patternString = "(\\b" + builder.toString() + ")"; */

        // compile the regular expression into a pattern
        //Pattern pattern = Pattern.compile(csvString, Pattern.CASE_INSENSITIVE);
        // create a matcher from the pattern and the question
        Matcher matcher = pattern.matcher(question);

        // scan the question for key words, display correspomding answer
        if(matcher.find()) {
            displayAnswer(conversation, keyWords.get(matcher.group(1))); 
        }
    }

/**
 *  The method actionPerformed() handles input from the question field
 *	and the "print" button.
 *
 *  If user types "bye" in the question field a JOptionPane pops up, asking
 *  if user really wants to quit. If user quits, the conversation is written
 *  into a log file together with YYYYMMDD_HHMMSS.
 *
 *  If user clicks the "print" button the conversation prints
 */
    public void actionPerformed (ActionEvent evt) 
    {
        // user clicked "enter" (return) in the question field
        if ( evt.getSource() == questionField )
        {
            question = questionField.getText();

            // if user chooses to quit
            if (question.equals("bye")) 
            {
                // display a farewell answer in the JTextArea
                displayAnswer(conversation, "Farewell. Enjoy Roma!");
                // make sure user really means to quit
                int reply = JOptionPane.showConfirmDialog (null, "Click \"OK\" if you want to quit and erase this conversation.", "Farewell", JOptionPane.OK_CANCEL_OPTION);
                // if user chooses to quit
                if (reply == JOptionPane.OK_OPTION) {
                    // save the conversation to the log
                    saveLogConversation(conversation);
                    // reset all fields
                    resetConversation(conversation);
                }
                // if user chooses not to quit, continue with the conversation
                else {
                    questionField.setText(null);
                    questionField.requestFocusInWindow();
                }
            }
            // if user types anything in the question field
            else if (!question.equals(""))
            {
                try {
                    // transform the question to "concierge's point of view"
                    String newQuestion = transformQuestion(question.toLowerCase());

                    // scroll the JTextArea to the end so that we always see the last answer
                    conversation.setCaretPosition(conversation.getText().length());
                    
                    // process the question (from the program's point of view) to generate an answer
                    generalResponse (newQuestion);
                }
                catch (IOException e) {}
            }
        } // question field entry

        // if user clicks the "print" button
	    if (evt.getSource() == printButton)
	    {
	    	try
	    	{
	    		printConversation(conversation);
	    	}
	    	catch (PrinterException e) {
                displayAnswer(conversation, "Printer error. Please try again.");
            }
	    } // print button
    }

/**
 *  main() creates an instance of this class.
 */
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        Sage01 sage = new Sage01 ();
      
        sage.setSize (600, 700);
        sage.setVisible (true);
        sage.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE) ; 
    }    
}