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


public class Sage01 extends JFrame implements ActionListener, Printable
{
	// question area
	private JPanel questionArea = new JPanel(); // commands
	private JLabel prompt = // prompt
         new JLabel ("Please type your question here:", JLabel.RIGHT);
    private JTextField questionField = new JTextField (30); // nameField

    // conversation area
	private JTextArea conversation = //display
         new JTextArea("Hello. I'm the concierge.");
    
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
 * generateResponse() takes a question sentence and looks for key words.
 * It generates a response based on whether the sentence matches a 
 * predefined pattern.
 *
 * @param question -- string representing user's last question
 * @return response -- string representing a response to question 
 */

    private void generateResponse (String question) 
    {
        // remove all non-word characters except single quote
        question = question.replaceAll("[\\W&&[^']]", " ");
        // regex group string
        String needString = "(.*\\s*)(\\bneed\\b|\\bwant\\b|\\bfind\\b|\\blook\\b|\\blike\\b|\\byou're\\b) (a\\s.+|.+)";
        Pattern needPattern = Pattern.compile(needString);
        Matcher needMatcher = needPattern.matcher(question);

        while(needMatcher.find()) {
        System.out.println("found1: " + needMatcher.group(1) + "\n" +
                        "found2: "       + needMatcher.group(2) + "\n" +
                        "found3: "       + needMatcher.group(3));
        }

        //regex group string
        String whereString = "(\\bwhere\\b) (is\\s.+|.+) (.+)";
        Pattern wherePattern = Pattern.compile(whereString);
        Matcher whereMatcher = wherePattern.matcher(question);

        while(whereMatcher.find()) {
        System.out.println("found1: " + whereMatcher.group(1) + "\n" +
                        "found2: "       + whereMatcher.group(2) + "\n" +
                        "found3: "       + whereMatcher.group(3));
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
                displayQuestion (conversation, question); 
                // transform the question to conciegge's "point of view"
                String newQuestion = transformQuestion(question.toLowerCase());
                // generate an answer
                generateResponse (newQuestion);
                //displayAnswer(conversation, newQuestion);
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