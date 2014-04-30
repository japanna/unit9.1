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

public class Sage01 extends JFrame implements ActionListener
{
	// question area
	private JPanel questionArea = new JPanel(); // commands
	private JLabel prompt = // prompt
         new JLabel ("Please type your question here:", JLabel.RIGHT);
    private JTextField questionField = new JTextField (30); // nameField
    private JButton askButton = // writeButton
         new JButton ("Ask question");

    // conversation area
	private JTextArea conversation = //display
         new JTextArea("Hello. I'm the concierge.");
    
    // save area
    private JPanel saveArea = new JPanel();
    private JLabel saveLabel = // prompt
         new JLabel ("Click button to save conversation:", JLabel.RIGHT);
    private JButton  saveButton = // readButton
         new JButton ("Save");  

/** 
 * constructor sets up the application's interface.
 */
    public Sage01() 
    {        
    	super ("Sage ver. 01");  
    	setLayout (new BorderLayout ());      

    	// question area        
    	add (questionArea, BorderLayout.NORTH);
    	//questionArea.setLayout (new GridLayout (1, 3, 1, 1));   
    	questionArea.add (prompt);
    	questionArea.add (questionField);
        //questionArea.add (askButton);
        questionField.addActionListener(this);
        //askButton.addActionListener (this);
        askButton.setForeground (Color.BLUE);

    	// conversation area
    	//instead of ...  add (display, BorderLayout.CENTER);
        add (new JScrollPane (conversation), BorderLayout.CENTER);
        conversation.setLineWrap (true); 

        // save area
        add (saveArea, BorderLayout.SOUTH);
        saveArea.add(saveLabel);
        saveArea.add (saveButton);
    	saveButton.addActionListener (this);
        saveButton.setForeground (Color.GREEN.darker().darker());
        
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
    }

/**
     *  The method actionPerformed() handles clicks on the read or write buttons
     */
    public void actionPerformed (ActionEvent evt) 
    {
    	String question = questionField.getText();
  
        if (evt.getSource() == questionField) 
        {
            displayQuestion (conversation, question); 
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