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
 * It also saves the conversation to a log (conversationLog.txt)
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void printConversation (JTextArea conversation) throws PrinterException
    {
    	try {
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    conversation.append("\n\nDate_Time: " + timeStamp + "\n--------------------------");
	    conversation.print();
	    //save conversation to a log
		File fileName = new File("conversationLog.txt");
	    FileWriter outStream =  new FileWriter (fileName, true);
	    
		outStream.append ("\n" + conversation.getText() + "\n");
		outStream.close ();
		// reset all fields
		conversation.setText("Hello. I'm the concierge.");
		questionField.setText(null);
		questionField.requestFocusInWindow();   
		}
		catch (IOException e) 
         {
            conversation.append("\n\nIOERROR: " + e.getMessage() + "\n");
            e.printStackTrace();
         }
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
 *  The method actionPerformed() handles input from the question field
 *	and the button clicks.
 */
    public void actionPerformed (ActionEvent evt) 
    {
    	String question = questionField.getText();
  
        if (evt.getSource() == questionField) // and (or) a questionButton
        {
            displayQuestion (conversation, question); 
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