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
	// question area
	private JPanel questionArea = new JPanel(); // commands
	private JLabel prompt = // prompt
         new JLabel ("Please type your question here:", JLabel.RIGHT);
    private JTextField questionField = new JTextField (30); // nameField

    // conversation area
	private JTextArea conversation = //display
         new JTextArea("Hello. I'm your concierge, Mario Nintendino!\n\nPlease ask me anything about Rome!");
    
    // print area
    private JPanel printArea = new JPanel();
    private JButton  printButton = 
         new JButton ("Print conversation");  

    private Component c;

    // stores user's question (a sentence)
    private String question;


/** 
 * constructor sets up the application's interface.
 */
    public Sage01() 
    {        
    	super ("Sage ver. 01");  
    	setLayout (new BorderLayout ());      

    	// question area        
    	add (questionArea, BorderLayout.NORTH);
    	questionArea.add (prompt);
    	questionArea.add (questionField);
        questionField.addActionListener(this);

    	// conversation area
    	//instead of ...  add (display, BorderLayout.CENTER);
        add (new JScrollPane (conversation), BorderLayout.CENTER);
        conversation.setLineWrap (true); 

        // save/print area
        add (printArea, BorderLayout.SOUTH);

        printArea.add(printButton);
        printButton.addActionListener (this);
        printButton.setForeground (Color.BLUE.darker());
    } 

/** 
 * displayQuestion() displays the question in the conversation field.
 *
 * @param question -- a String representing user's question
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void displayQuestion (JTextArea conversation, String question) 
    {
    	conversation.append("\n\n  " + question);
    	questionField.setText(null);
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
        conversation.setText("Hello. I'm the concierge.");
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

    private String transformQuestion (String question) 
    {
        // new hash map of key words
        Map<String,String> keyWords = new HashMap<String,String>();
        keyWords.put("me", "you");
        keyWords.put("i", "you");
        keyWords.put("i'm", "you're");
        keyWords.put("i am", "you're");
        keyWords.put("im", "you're");
        keyWords.put("i'd", "you'd");
        keyWords.put("my", "your");
        keyWords.put("you", "I");
        keyWords.put("your", "my");
        keyWords.put("are you", "am I");
        //keyWords.put("a", "the");


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
        // Read 
        CSVReader reader = new CSVReader(new FileReader("keyWords.csv"));
        String [] nextLine;
        StringBuilder builder = new StringBuilder();
        while ((nextLine = reader.readNext()) != null) {
            builder.append(nextLine[0]).append("\\b|\\b");  
            
        }


        // remove all non-word characters except single quote
        question = question.replaceAll("[\\W&&[^']&&[^\\s]]", "");
        
        // remove "please"
        question = question.replaceAll("please|sorry|no|yes", "");

        String [] test = {"hello", "horse", "house"};
        // Build a string of the above key words
       /* StringBuilder builder = new StringBuilder();
        for(String s : test){
            builder.append(s).append("\\b|\\b");   
        }*/
        // remove the last "or"
        builder.deleteCharAt(builder.lastIndexOf("|"));
        // convert into regex group string (\\b is "word boundary")
        String needString = "(\\b" + builder.toString() + ")";
        System.out.println(needString);

        // create regex group string for all questions of "I need" or "i want"-type
        //String needString = "(.*\\s*)(\\bneed\\b|\\bwant\\b|\\bfind\\b|\\blook\\b|\\blike\\b|\\byou're\\b|\\bbuy\\b|\\bwhere\\b) (a\\s.+|.+)";
        Pattern needPattern = Pattern.compile(needString);
        Matcher needMatcher = needPattern.matcher(question);

        //String[] deconstructedAnswer = new String[3];
        // if the question matechs any pattern, store the different parts in an array
        /*while(needMatcher.find()) {
            deconstructedAnswer[0] = needMatcher.group(1);
            deconstructedAnswer[1] = needMatcher.group(2);
            deconstructedAnswer[2] = needMatcher.group(3);
        }

        // create regex group string for all questions of "where is"-type
        String whereString = "(\\bwhere\\b) (is\\s.+|.+) (.+)";
        Pattern wherePattern = Pattern.compile(whereString);
        Matcher whereMatcher = wherePattern.matcher(question);

        // if the question matechs any pattern, store the different parts in an array
        while(whereMatcher.find()) {
            deconstructedAnswer[0] = whereMatcher.group(1);
            deconstructedAnswer[1] = whereMatcher.group(2);
            deconstructedAnswer[2] = whereMatcher.group(3);
        }


        // make string for display of rephrased question
        StringBuilder builder = new StringBuilder();
        for (String s : deconstructedAnswer) {
                if (s != null) builder.append(" " + s);
        }

        System.out.println(Arrays.toString(deconstructedAnswer));
        // if we've found some matching key words, display a response (a rephrasing of the question)
        if(builder.length() > 0) {*/
        if (needMatcher.find()) {
            displayAnswer(conversation, question + ". Let me see..."); 
            specificResponse(question);
        }
        // if not, ask for more information by displaying a general response
        else {
            final String[] generalAnswers1 = {"Can you tell me more?", "Can you elaborate on that?", 
                                            "I'm afraid I'm at a loss. How do you mean?", 
                                            "I'm interested, can you explain in more detail?", 
                                            "I'd love to help, can you phrase your question differently?"};
            int rand = (int) (Math.random() * 5);
            System.out.println(rand);
            String s = generalAnswers1[rand];
            displayAnswer(conversation, question + "? " + s);
        }
        // return array containing the question in a broken down state
        
    }

/** 
 * specificResponse() takes a string and looks for key words.
 * It generates a response based on whether the sentence matches a 
 * predefined pattern.
 *
 * @param question -- string representing the relevant part of user's last question
 * @return response -- string representing a response to question 
 */

    private void specificResponse (String noun) 
    {
        // new hash map of key words
        Map<String,String> keyWords = new HashMap<String,String>();
        keyWords.put("restaurant", "You may want to eat at \"Tutti di Mare\" on Via Veneto 14, 11432 Rome.\nStay away from the squid though. It's disgustoso!");
        keyWords.put("museum", "The Sistine Chapel is marvellous. It's on Viale Vaticano, 2, Vatican City.\nBe sure to see the bathrooms! They're divine!");
        keyWords.put("taxi", "For a cab, please call +39 444 3232. Ask for Luigi, he has the license!");
        keyWords.put("cab", "For a cab, please call +39 444 3232.\nDon't ride with Luigi! He just got out of jail.");
        keyWords.put("food", "\"Gelato Maximus\" is deliziozo! It's on Via Spiga 22, 11232 Rome.\nMake sure you get the clean spoon!");
        keyWords.put("hotel", "\"The Shangri La\" is bellissimo! It's on Via Trevi 8, 11232 Rome.\nJust don't flirt with the bartender - pazzesco!");

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
        if (matcher.find()) {
            displayAnswer(conversation, keyWords.get(matcher.group(1))); 
        }
        else {
            displayAnswer(conversation, "Scusa, my English is piccolo. Can you say it di nuovo?"); 
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
                // display the question in the JTextArea
                //displayQuestion (conversation, question); 
                // transform the question to conciegge's "point of view"
                String newQuestion = transformQuestion(question.toLowerCase());
                // generate an answer
                try
            {
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
    public static void main(String args[])
    {
        Sage01 sage = new Sage01 ();
      
        sage.setSize (600, 700);
        sage.setVisible (true);
        sage.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE) ; 
    }    
}