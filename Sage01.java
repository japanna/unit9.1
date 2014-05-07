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
    private static final String GREETING = "\n                   Welcome to Rome!  I'm your concierge, Signore Mario. (◕‿◕)ﾉ\n\n                   How can I help you?  I'm an expert on food, sights and love!\n                  ___________________________________________________________";

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

    // a string representing a group of keywords used in pattern matching, built from a CSV file
    private String csvString;

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

    	// question area        
    	add (questionArea, BorderLayout.NORTH);
    	questionArea.add (prompt);
    	questionArea.add (questionField);
        questionField.addActionListener(this);

    	// conversation area
        add (new JScrollPane (conversation), BorderLayout.CENTER);
        conversation.setLineWrap (true); 
        

        // print area
        add (printArea, BorderLayout.SOUTH);

        printArea.add(printButton);
        printButton.addActionListener (this);
        printButton.setForeground (Color.BLUE.darker());

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
    	conversation.print();
        conversation.append("\n\nThis conversation is printing..."); // add check for printing error
        questionField.requestFocusInWindow(); 
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
  public int print(Graphics g, PageFormat format, int pagenum) {
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
  }

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
        Map<String,String> keyWords = new HashMap<String,String>();

        // openCSV object, creates a reader out of a CSV file containing "point of view" substitutes
        CSVReader reader = new CSVReader(new FileReader("pointOfView.csv"));

        
        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            keyWords.put(nextLine[0], (String)nextLine[1]);  
        }

        // Build a string of the above key words
        StringBuilder builder = new StringBuilder();
        for(String s : keyWords.keySet()){
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
            matcher.appendReplacement(sb, keyWords.get(matcher.group(1)));
        }
        return matcher.appendTail(sb).toString();
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
        question = question.replaceAll("please|sorry", "");

        Pattern generalPattern = Pattern.compile(csvString);
        Matcher generalMatcher = generalPattern.matcher(question);

        // if we've found some matching key words, display a response (a rephrasing of the question)
        if (generalMatcher.find()) {
            displayAnswer(conversation, "  " + question + "? Let me see..."); 
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
                                            "Offeso! Go to Paris for that!"};
            int rand = (int) (Math.random() * 7);
            System.out.println(rand);
            String s = generalAnswers1[rand];
            displayAnswer(conversation, "  " + question + "? " + s);
        }
    }

/** 
 * specificResponse() takes a string and looks for key words.
 * It generates a response based on whether the sentence matches a 
 * predefined pattern.
 *
 * @param question -- string representing the relevant part of user's last question
 * @return response -- string representing a response to question 
 */

    private void specificResponse (String noun) throws IOException, FileNotFoundException 
    {
        // new hash map of key words
        Map<String,String> keyWords = new HashMap<String,String>();
        // openCSV object, creates a reader out of a CSV file
        CSVReader reader = new CSVReader(new FileReader("rome.csv"));

        
        String [] nextLine; 

        // create a string of keywords from CSV file to be put in a regex group 
        while ((nextLine = reader.readNext()) != null) 
        {
            System.out.println(Arrays.toString(nextLine));
            
            keyWords.put(nextLine[0], (String)nextLine[1]);  
        }
        
/* 
       
        keyWords.put("restaurant", "You may want to eat at \"Tutti di Mare\" on Via Veneto 14, 11432 Rome.\nStay away from the squid! It's disgustoso!");
        keyWords.put("museum", "The Sistine Chapel is marvellous. It's on Viale Vaticano, 2, Vatican City.\nBe sure to see the bathrooms! They're divine!");
        keyWords.put("taxi", "For a cab, please call +39 444 3232. Ask for Luigi, he has the license!");
        keyWords.put("cab", "For a cab, please call +39 444 3232.\nDon't ride with Luigi! He just got out of jail.");
        keyWords.put("food", "\"Gelato Maximus\" is deliziozo! It's on Via Spiga 22, 11232 Rome.\nMake sure you get the clean spoon!");
        keyWords.put("hotel", "\"The Shangri La\" is bellissimo! It's on Via Trevi 8, 11232 Rome.\nJust don't flirt with the bartender - pazzesco!");
   */ System.out.println(keyWords.toString());
        // Build a string of the above key words
        StringBuilder builder = new StringBuilder();
        for(String s : keyWords.keySet()){
            builder.append(s).append("\\b|\\b");   
        }
        // remove the last "or"
        builder.deleteCharAt(builder.lastIndexOf("|"));
        // convert into regex group string (\\b is "word boundary")
        String patternString = "(\\b" + builder.toString() + ")";

        // compile the regular expression into a pattern
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        // create a matcher from the pattern and the question
        Matcher matcher = pattern.matcher(noun);

        // scan the question for key words, replace key word with corresponding
        // replacement word
        while(matcher.find()) {
            displayAnswer(conversation, keyWords.get(matcher.group(1))); 
        }
    }

/**
 *  The method actionPerformed() handles input from the question field
 *	and the "print" button.
 */
    public void actionPerformed (ActionEvent evt) 
    {
        if (evt.getSource() == questionField) // and (or) a questionButton
        {
            // stores the question string
            question = questionField.getText();
            // if user chooses to quit
            if (question.equals("bye")) 
            {
                // save the conversation to the log
                saveLogConversation(conversation);
                // reset all fields
                resetConversation(conversation);
            }
            else 
            {
                 try {
                // transform the question to "concierge's point of view"
                String newQuestion = transformQuestion(question.toLowerCase());

                // scroll the JTextArea to the end in order to always see the last answer
                conversation.setCaretPosition(conversation.getText().length());


                // generate an answer
                
                generalResponse (newQuestion);
                }
                catch (IOException e) {}
                //catch (ArrayStoreException e) {}
                //displayAnswer(conversation, Arrays.toString(deconstructedAnswer));
                //
            }
        }
	    if (evt.getSource() == printButton)
	    {
	    	try
	    	{
	    		printConversation(conversation);
	    	}
	    	catch (PrinterException e) {}
	    }
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