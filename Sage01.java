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

public class Sage01 extends JFrame implements ActionListener
{
	// question area
	private JPanel questionArea = new JPanel(); // commands
	private JLabel prompt = // prompt
         new JLabel ("Please type your question here:", JLabel.RIGHT);
    private JTextField questionField = new JTextField (30); // nameField

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
    	questionArea.add (prompt);
    	questionArea.add (questionField);
        questionField.addActionListener(this);

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
    	questionField.setText(null);
    }

/** 
 * saveConversation() saves the conversation to a file of user's choice.
 *
 * @param conversation -- a JTextArea where questions and answers are displayed
 */
    private void saveConversation (JTextArea conversation) 
    {
    	try {
    	JFileChooser chooser = new JFileChooser();
    	File currentDirectory = new File (".");
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    	chooser.setSelectedFile(new File(currentDirectory.getAbsolutePath() + "/" + timeStamp + ".txt"));
    	chooser.showSaveDialog(this);
    	String name = chooser.getSelectedFile().toString();

    	File fileName = new File(name);
	    // by setting the file writer boolean to "true" append is turned on
		FileWriter outStream =  new FileWriter (fileName, true);
		outStream.append ("\n\n" + conversation.getText());
		outStream.close ();
		JOptionPane.showMessageDialog (null, "Your conversation has been saved.");
		conversation.setText("Hello. I'm the concierge.");
		questionField.setText(null);
	    questionField.requestFocusInWindow();
		}
		catch (IOException e) 
          {
               conversation.setText("IOERROR: " + e.getMessage() + "\n");
               e.printStackTrace();
          }
    }

/**
     *  The method actionPerformed() handles input from the question field
     */
    public void actionPerformed (ActionEvent evt) 
    {
    	String question = questionField.getText();
  
        if (evt.getSource() == questionField) // and (or) a questionButton
        {
            displayQuestion (conversation, question); 
        }
	    if (evt.getSource() == saveButton)
	    {
	    	saveConversation(conversation);
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