// Sage.java

/****
* This program implements a "virtual concierge." The user will type a question
* in the graphical user interface and the program will answer as best it can.
* The program uses two CSV-files, "rome.csv" and "pointOfView.csv", to answer
* the questions. 
*
* When user types "bye" the conversation between user and the program is saved
* in conversationLog.txt.
*
* The user can print the conversation if the computer is connected to a printer.
*
* @author	Anna Ntenta
* @version	1.0 Last Modified 5/9/2014
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



public class Sage extends JFrame implements ActionListener, Printable
{
    private static final String GREETING = "\n               Welcome to Rome!  I'm your concierge, Signore Mario. (◕‿◕)ﾉ\n\n               How can I help you?  I'm an expert on food, sights and love!\n\n                                                  (Say \"bye\" to quit)\n              __________________________________________________";

    private static final String [] GENERAL_ANSWERS = {"Pazzo! Why you say that?", 
                                            "Sono confuso... Can you elaborate?", 
                                            "Cosa! How do you mean?", 
                                            "Non capisco. Can you explain?", 
                                            "Caro mio, please rephrase that.",
                                            "Nobody likes that. Qualcos'altro?",
                                            "Dio mio! Not in Rome! What else?",
                                            "Offeso! Go to Paris!\nCan I help you with anything else?"};

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

    // stores user's latest question 
    private String question;

    // stores some of user's questions to use again later
    private String storedQuestion;

    // a string representing a group of keywords used in pattern matching, built from a CSV file
    private String csvString;

    // a pattern used by the methods generalResponse and specificResponse
    private Pattern pattern;


/** 
 * constructor sets up the application's interface and 
 * builds a string of keywords from a CSV file.
 * The words in the string are used in pattern matching in 
 * the functions generalResponse() and specificResponse()
 */
    public Sage() throws FileNotFoundException, IOException
    {        
    	super ("Sage ver. 1.0");  
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

        // this string is the default stored question until the program has saved something
        // that the user has typed
        storedQuestion = "you might need help";

        // openCSV object, creates a reader out of a CSV file
        CSVReader reader = new CSVReader(new FileReader("rome.csv"));

        // a temporary object used to build a string of keywords from CSV file
        StringBuilder temp = new StringBuilder();
        
        // array that holds a pair of strings [keyword, response]
        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            // append every keyword from the CSV file to a "word boundary" regex
            temp.append(nextLine[0]).append("\\b|\\b");  
        }
        
        // removes the last "or"
        temp.deleteCharAt(temp.lastIndexOf("|"));

        // add parentheses to form a regular expression group 
        csvString = "(\\b" + temp.toString() + ")";

        // create a pattern from the regular expression
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
 * saveLogConversation() saves the conversation to a log (conversationLog.txt)
 * when the user ends the conversation 
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void saveLogConversation (JTextArea conversation) 
    {
        try { 
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            // add the date and time to the log
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
 * resetConversation() resets the JTextArea after user has typed "bye".
 * Resets and returns focus to the question field. 
 * Resets the stored question to its initial state
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */

    private void resetConversation (JTextArea conversation) 
    {
        conversation.setText(GREETING);
        questionField.setText(null);
        questionField.requestFocusInWindow();
        storedQuestion = "you had a question for me.";
    }



/**
   * print(Graphics g, PageFormat format, int pagenum) is called by
   * printConversation().
   * It is defined by the Printable interface.
   */
public int print(Graphics g, PageFormat format, int pageIndex) 
{
    // We want to print all pages. If pageIndex specifies a non-existent page return NO_SUCH_PAGE
    if (pageIndex > 0) 
      return Printable.NO_SUCH_PAGE;
    else
    // PAGE_EXISTS signifies that the page was rendered
    return Printable.PAGE_EXISTS;
}

/** 
 * printConversation() prints the conversation to a printer of user's choice.
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 * @throws PrinterException
 */
    private void printConversation (JTextArea conversation) throws PrinterException
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        // printDialog() returns true if the user does not cancel the dialog; false otherwise
        boolean printing = job.printDialog();
        if( printing ) 
        {
            try {
                    conversation.print();
                    questionField.requestFocusInWindow(); 
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
 *  transformQuestion() builds a HasMap of word pairs from a CSV-file with "point of view"-words.
 *  It looks for the first matching key word in user's question ("I", "you" etc.)
 *  
 *  It switches the matching words to the computer's point of view ("you", "I".)
 *
 *  It also randomly stores the question for later use as a generic answer ( see generalResponse() )
 *
 * @param question -- string representing user's last question
 * @return transformed question -- the question from the computer's "point of view"
 * @throws IOException - when CSVreader cannot be read from
 * @throws FileNotFoundException - when "pointOfView.csv" cannot be found
 */

    private String transformQuestion (String question) throws IOException, FileNotFoundException 
    {
        // new hash map of key words
        Map<String,String> powWords = new HashMap<String,String>();

        // openCSV object, creates a reader out of a CSV file containing "point of view" substitutes
        CSVReader reader = new CSVReader(new FileReader("pointOfView.csv"));

        // array of key word pairs [keyword, response]
        String [] nextLine; 

        // populate the hash map with all the pairs from the CSV file
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
        Pattern powPattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

        // create a matcher engine from the pattern and the question
        Matcher matcher = powPattern.matcher(question);

        // build up the transformed question with a StringBuffer
        StringBuffer sb = new StringBuffer();

        // scan the question for key words, replace key word with corresponding
        // replacement word
        while(matcher.find()) {
            matcher.appendReplacement(sb, powWords.get(matcher.group(1)));
        }
        // append last match to StringBuffer and convert into a string
        String transformQuestion = matcher.appendTail(sb).toString();

        // generate a random number
        int rand = (int) (Math.random() * 4);
        
        // if the random number is "2" store the question for later use
        if (rand == 2) {
            storedQuestion =  transformQuestion;

            // remove all non-word characters except single quote
            storedQuestion = storedQuestion.replaceAll("[\\W&&[^']&&[^\\s]]", "");
        
            // remove "please" etc 
            storedQuestion = storedQuestion.replaceAll("\\bplease\\b|\\bwell\\b", "");
        }
        return transformQuestion;
    }

/** 
 * generalResponse() searches user's latest question for key words
 * from a CSV-file. If a match is found it displays the question (already
 * translated into the computer's point of view) and then calls specificResponse() 
 * to display the corresponsing answer.
 *
 * If a match is not found we display one of 8 generic replies, one
 * of which is a restatement of a previous question posed by user.
 * The generic reply is picked at random.
 *
 * @param question -- string representing user's last question
 * @throws IOException - when specificResponse cannot read from CSVreader
 */

    private void generalResponse (String question) throws IOException 
    {
        // remove all non-word characters except single quote
        question = question.replaceAll("[\\W&&[^']&&[^\\s]]", "");
        
        // remove words like "please" and "well"
        question = question.replaceAll("\\bplease\\b|\\bwell\\b", "");

        // the Pattern "pattern" is initiated in the constructor
        // here we build a Matcher engine with the pattern and the question
        Matcher generalMatcher = pattern.matcher(question);

        // if we've found a matching key word, rephrase the question from computer's point of view
        if (generalMatcher.find()) {

            displayAnswer(conversation, "  " + question + "..."); 
            // generate a specific response based on the found keyword
            specificResponse(question);
        }
        // if no match is found, display a general response
        else {
            
            int rand = (int) (Math.random() * 12);
            
            // this string contains a question previously posed by user
            String s = storedQuestion;
            // for a random number between 8 - 11 display a rephrasing of user's previous question
            // but only if that wasn't the question that was just asked
            if ((rand > 7) && (!s.equals(question))) {
                displayAnswer(conversation, "  Earlier you said " + s + ". Can you tell me more?");
            }
            // if the stored question is the question that was just asked OR the random number
            // is less than 8 display one of the 7 predefined answers
            else {
                if (rand > 7) rand = rand - 4;
                s = GENERAL_ANSWERS[rand];
                displayAnswer(conversation, "  " + question + "? " + s);
            } 
        }
    }

/** 
 *  specificResponse() builds a HasMap of key word pairs from a CSV-file
 *  and looks for the first matching key word in user's question. 
 *
 *  We know that there is a matching word because otherwise generalResponse() 
 *  would have displayed one of the standard replies and would not have called
 *  this function.
 *
 *  displayAnswer() is called to display the corresponding response.
 *
 * @param question -- user's last question
 * @throws IOException - when CSVreader cannot be read from
 * @throws FileNotFoundException - when "rome.csv" cannot be found
 */

    private void specificResponse (String question) throws IOException, FileNotFoundException 
    {
        // initiate new hash map of key words
        Map<String,String> keyWords = new HashMap<String,String>();
        
        // openCSV object, creates a reader out of a CSV file
        CSVReader reader = new CSVReader(new FileReader("rome.csv"));

        // array of key word pairs [keyword, response]
        String [] nextLine; 

        // populate the hash map with all the pairs from the CSV file
        while ((nextLine = reader.readNext()) != null) 
        {
            keyWords.put(nextLine[0], (String)nextLine[1]);  
        }
        
        // the Pattern "pattern" is initiated in the constructor
        // here we build a Matcher engine with the pattern and the question
        Matcher matcher = pattern.matcher(question);

        // find the first match, display the corresponding answer
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
        }

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
	    }
    }

/**
 *  main() creates an instance of this class.
 */
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        Sage sage = new Sage ();
      
        sage.setSize (600, 700);
        sage.setVisible (true);
        sage.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE) ; 
    }    
}