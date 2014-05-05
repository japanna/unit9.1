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
        
        //readButton.setIcon ( new ImageIcon ("happyFace.gif"));
        //Font f = new Font ("Helvetica", Font.BOLD, 30);
        //saveButton.setFont (f);
        //askButton.setFont (f);
        //prompt.setFont (f);
       	//questionField.setToolTipText ("Type the name of a file that you want to READ or Write!");
        //questionField.setFont (f);
        //conversation.setFont (f);
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
 */

    private void transformQuestion (String question) 
    {
        // new hash map of key words
        Map<String,String> tokens = new HashMap<String,String>();
        tokens.put("me", "you");
        tokens.put("i", "you");
        tokens.put("my", "your");
        tokens.put("you", "I");
        tokens.put("your", "my");

        // Build a string of the above key words
        StringBuilder builder = new StringBuilder();
        for(String s : tokens.keySet()){
            builder.append(s).append("\\b|\\b");   
        }
        // remove the last "or"
        builder.deleteCharAt(builder.lastIndexOf("|"));
        // convert into regex String
        String patternString = "(\\b" + builder.toString() + ")";
        System.out.println(patternString);

        // compile the regular expression into a pattern
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        // create a matcher from the pattern and the question
        Matcher matcher = pattern.matcher(question);

        // assemble the transformed question
        StringBuffer sb = new StringBuffer();
        // scan the question, look for key words, replace key word with corresponding
        // replacement word
        while(matcher.find()) {
            matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
        }
        matcher.appendTail(sb);

System.out.println(sb.toString());
      /*  String[] words = question.split("\\s+");
        System.out.println(Arrays.toString(words));
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[\\W]", "");
            words[i] = words[i].replaceAll("I", "you");
            words[i] = words[i].replaceAll("me", "you");

        }
        StringBuilder builder = new StringBuilder();
        for(String s : words) {
            if (!s.equals(""))
        builder.append(" " + s);
}
        String s = builder.toString();
        System.out.println(s); */
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
                transformQuestion(question.toLowerCase());
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