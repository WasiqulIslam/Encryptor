//7:13 PM 3/29/2004
//Programmed By Wasiqul Islam
//This program is used to encrypt or decrypt a file
//v1.4( advanced code entry bug solved and more ) 3:37 PM 11/16/2005
//u 3:46 PM 11/16/2005
//enhanced 4:43 PM 11/16/2005


import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;

public class Encryptor extends JFrame implements ActionListener
{
   private static Encryptor obj;
   private Container container;
   private JLabel passwordLabel, inputPathLabel, outputPathLabel, statusLabel;
   private JTextField inputPathField, outputPathField;
   private JPasswordField passwordField;
   private JButton openButton, saveButton, encryptButton, decryptButton, helpButton, resetButton;
   private GridBagLayout layout;
   private GridBagConstraints constraints;

   private boolean encrypt;
   private boolean busy;
   private int code[];
   private int codeLength;
   private int currentCodeIndex;
   private DecimalFormat decimalFormat;
   private byte byteData[];
   private long filePointer, lengthOfFile;
   private int dataSize;

   private RandomAccessFile inputFilePointer;
   private RandomAccessFile outputFilePointer;

   public Encryptor()
   {
      super("File Encryptor");
      obj = this;
      container = getContentPane();
      container.setBackground( Color.white );
      layout = new GridBagLayout();
      container.setLayout(  layout  );
      constraints = new GridBagConstraints();

      passwordLabel = new JLabel( "Code: " );
      inputPathLabel = new JLabel( "Read from: " );
      outputPathLabel = new JLabel( "Write to: " );
      statusLabel = new JLabel( "Status: stopped" );
      passwordField = new JPasswordField( 12 );
      passwordField.setEchoChar( '*' );
      inputPathField = new JTextField( 12 );
      outputPathField = new JTextField( 12 );
      openButton = new JButton("?");
      saveButton = new JButton("?");
      encryptButton = new JButton("Encrypt");
      decryptButton = new JButton("Decrypt");
      helpButton = new JButton("Help");
      resetButton = new JButton("Reset");
      decimalFormat = new DecimalFormat( "00" );

      passwordLabel.setHorizontalAlignment( SwingConstants.LEFT );
      inputPathLabel.setHorizontalAlignment( SwingConstants.LEFT );
      outputPathLabel.setHorizontalAlignment( SwingConstants.LEFT );
      statusLabel.setHorizontalAlignment( SwingConstants.LEFT );

      openButton.setBackground( Color.blue );
      saveButton.setBackground( Color.blue );
      resetButton.setBackground( Color.green );
      helpButton.setBackground( Color.green );
      encryptButton.setBackground( Color.red );
      decryptButton.setBackground( Color.red );
      passwordLabel.setBackground( Color.white );
      inputPathLabel.setBackground( Color.white );
      outputPathLabel.setBackground( Color.white );
      passwordLabel.setForeground( Color.red );
      inputPathLabel.setForeground( Color.blue );
      outputPathLabel.setForeground( Color.blue );
      statusLabel.setBackground( Color.white );
      statusLabel.setForeground( Color.red );

      openButton.setToolTipText( "Click here to open a file that you want to alter" );
      saveButton.setToolTipText( "Click here to pick a destination file" );
      resetButton.setToolTipText( "Click here to erase file paths" );
      helpButton.setToolTipText( "Click here to view help texts" );
      encryptButton.setToolTipText( "Click here to make a file ambiguous" );
      decryptButton.setToolTipText( "Click here to make a previously altered ambiguous file readable" );
      inputPathField.setToolTipText( "Type a file path here" );
      outputPathField.setToolTipText( "Type a destination file path here" );
      passwordField.setToolTipText( "<html>Type any text as a code or password for file altering.<p>Press ENTER to enter advanced integer ASCII code(for advanced users).<p>Be sure to remember your code.</html>" );

      addComponent( inputPathLabel, 1, 1, 3, 1 );
      addComponent( inputPathField, 1, 4, 4, 1 );
      addComponent( openButton , 1, 8, 1, 1);
      addComponent( outputPathLabel , 1, 10, 3, 1);
      addComponent( outputPathField, 1, 13, 4, 1 );
      addComponent( saveButton, 1, 17, 1, 1 );
      addComponent( passwordLabel , 2, 1, 3, 1);
      addComponent( passwordField, 2, 4, 4, 1 );
      addComponent( resetButton, 2, 13, 2, 1 );
      addComponent( helpButton, 2, 15, 2, 1 );
      addComponent( encryptButton, 3, 1, 3, 1 );
      addComponent( decryptButton, 3, 5, 3, 1 );
      addComponent( statusLabel, 3, 11, 7, 1 );

      openButton.addActionListener( this );
      saveButton.addActionListener( this );
      encryptButton.addActionListener( this );
      decryptButton.addActionListener( this );
      helpButton.addActionListener( this );
      resetButton.addActionListener( this );
      passwordField.addActionListener( this );

      setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
      this.addWindowListener( 
         new WindowAdapter()
         {
            public void windowClosing(WindowEvent e)
            {
               int result = JOptionPane.showConfirmDialog( Encryptor.this, "Do you really Want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION );
               if( result == JOptionPane.YES_OPTION )
               {
                  System.exit( 0 );
               }
            }
         }
      );
      pack();
      setSize( 600, 150 );
      setLocation( 20, 150 );
      setResizable( false );
      setVisible( true );
   }
   public void addComponent( Component component, int row, int column, int width, int height )
   {
      constraints.weightx = 0;
      constraints.weighty = 0;
      constraints.gridx = column;
      constraints.gridy = row;
      constraints.gridwidth = width;
      constraints.gridheight = height;
      layout.setConstraints( component, constraints );
      container.add( component );
   }
   public void actionPerformed( ActionEvent event )
   {
      if( event.getSource() == openButton )
      {
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
         int result = fileChooser.showOpenDialog( this );
         if( result != JFileChooser.APPROVE_OPTION )
            return;
         File fileName = fileChooser.getSelectedFile();
         if(  fileName == null || fileName.getName().equals("")  )
         {
               JOptionPane.showMessageDialog( this, "Invalid file name" , "Error", JOptionPane.ERROR_MESSAGE );
         }
         else
         {
            inputPathField.setText(  fileName.getPath()  );
            if( outputPathField.getText().equals( "" ) )
            {
               outputPathField.setText(  fileName.getPath()  );
            }
         }
      }
      else if( event.getSource() == saveButton )
      {
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
         int result = fileChooser.showSaveDialog( this );
         if( result != JFileChooser.APPROVE_OPTION )
            return;
         File fileName = fileChooser.getSelectedFile();
         if(  fileName == null || fileName.getName().equals("")  )
         {
               JOptionPane.showMessageDialog(this, "Invalid file name" , "Error", JOptionPane.ERROR_MESSAGE );
         }
         else
         {
            outputPathField.setText(  fileName.getPath()  );
         }
      }
      else if( event.getSource() == encryptButton  || event.getSource() == decryptButton )
      {
         Worker worker = new Worker( event, container );
         worker.start();
      }
      else if( event.getSource() == helpButton )
      {
            String tmp = "File Encryptor v1.4(WED 4:43 PM 16-NOV-2005)\nProgrammed by Wasiqul Islam\nE-mail: 'wasiqul_islam@yahoo.com'\n\n\nThis program is used to make a file(encrypt a file) such that \nNO ONE CAN READ IT.\nType file Paths and Type character codes\nand click ENCRYPT or DECRYPT Button to perform file operation.\nIf you encrypt a file with a code you can retrieve original file by\ndecrypting it with only the previously applied code used for encryption.\n\n\n" +
            "A simple example:\nClick RESET.\nClick the left blue '?' marked button.\nSelect any file\n(do not select important file yet, as you are practicing).\nThen type a password or code at the code text field.\nClick ENCRYPT button.\nAt this step your file is encrypted.\nJust click the DECRYPT button again to decrypt the file to\nretain to its original contents.\nThis is a simple example but\nyou can change the path of 'Write to' text field to another file\nfor keeping the source file unchanged(specially for safety, but requires much space).\n\n\nWarning:\nDo not encrypt a file more than once.\nAlways make sure that you DO NOT FORGET THE CODE." +
            "\n\n\nNote:\nEncrypt a file with your secret code to make it unreadable\nand when you want to read that file decrypt it with the same code to make it readable.\nYou can use this program to any kind of file such as text file, execution file etc.\nThis program processes only one file at a time so\nif you want to encrypt several files at a time convert them into a single file\n(for example making all the files as a zip file etc.)and encrypt that single file.\nThis program takes about 2 to 3 minutes to process a file of 500 MB length.\nNote that you cannot change a file which is set to 'Read-Only' mode by the operating system.\nUncheck the 'Read-only' attribute before proceeding." +
            "\n\n\nExtra Notes(for advanced users only):\nEncrypt operation with a code is total inverse of\nDecrypt operation with the same code,\nso if you find any problem such as 'you encrypted twice' etc.\ntry doing the inverse operation the same number of time in inverse sequence.\n\n\nAdvanced code:\nYou can also enter code as ASCII integer values by pressing ENTER\nwhen the focus is on the code text field.\nEnter as many integers as you want but\nmake sure to write it down sequentially or remember its sequence.";
            JTextArea ta = new JTextArea( 10, 40 );
            ta.setText( tmp );
            ta.setEditable( false );
            ta.select( 0, 0 );
            JOptionPane.showMessageDialog( this, new JScrollPane( ta ), "Help/About", JOptionPane.INFORMATION_MESSAGE );
      }
      else if( event.getSource() == resetButton )
      {
         inputPathField.setText( "" );
         outputPathField.setText( "" );
      }
      else if( event.getSource() == passwordField )
      {
         try
         {
            if( busy )
            return;
            busy = true;
            int rslt;
            int length=0;
            String inputString;
            int input;
            byte byteTmp;
            Vector byteCode = new Vector();
            int count = 0;
            while( true )
            {
               rslt = JOptionPane.showConfirmDialog( this, "Proceed with Integer number " + (count+1) + "?", "Proceed?", JOptionPane.YES_NO_OPTION);
               if( rslt != JOptionPane.YES_OPTION )
               {
                  break;
               }
               inputString = JOptionPane.showInputDialog( this, "Enter an Integer\n(Range: 0 - 255)" );
               input = Integer.parseInt( inputString );
               if( input < 0 || input > 255 )
               {
                  break;
               }
               byteTmp = ( byte )input;
               byteCode.addElement( new Byte( byteTmp ) );
               length++;
               count++;
            }
            if( length > 0 )
            {
               String output = "";
               for( count = 0; count < length; count++ )
               {
                  output += ( char )( ( ( Byte ) byteCode.elementAt( count ) ).byteValue() );
               }
               passwordField.setText( output );
            }
            busy = false;
         }
         catch( Exception exception )
         {
            JOptionPane.showMessageDialog(this, exception.toString(), "Exception", JOptionPane.ERROR_MESSAGE );
            busy = false;
         }
      }
   }
   public static void main( String args[] )
   {
      Encryptor application = new Encryptor();
   }
   public class UpdateThread extends Thread
   {
      JLabel label;
      int progress, total; 
      public UpdateThread( JLabel a, int b, int c )
      {
         label = a;
         progress = b;
         total = c;
      }
      public void run()
      {
         int a;
         a = ( progress * 100 ) / total;
         String b = "Progress: ";

         b += decimalFormat.format( a );

         b += " %";
         label.setText( b );
      }
   }
   public class Worker extends Thread
   {
      ActionEvent event;
      Container container;
      public Worker( ActionEvent a, Container b )
      {
         event = a;
         container = b;
      }
      public void run()
      {
         try
         {
            busy = true;

            container.setCursor( Cursor.getPredefinedCursor(  Cursor.WAIT_CURSOR  ) );

            openButton.setEnabled( false );
            saveButton.setEnabled( false );
            encryptButton.setEnabled( false );
            decryptButton.setEnabled( false );
            helpButton.setEnabled( false );
            resetButton.setEnabled( false );

            if( event.getSource() == encryptButton )
               encrypt = true;
            else
               encrypt = false;

            if( inputPathField.getText().trim().length() < 1 )
            {
               JOptionPane.showMessageDialog( Encryptor.this , "Enter an input file name" , "Error", JOptionPane.ERROR_MESSAGE );
               return;
            }

            if( outputPathField.getText().trim().length() < 1 )
            {
               JOptionPane.showMessageDialog( Encryptor.this , "Enter a output file name" , "Error", JOptionPane.ERROR_MESSAGE );
               return;
            }

            String tmpString = new String( passwordField.getPassword() );
   
            codeLength = tmpString.length();
            if( codeLength < 1 )
            {
               JOptionPane.showMessageDialog( Encryptor.this, "Please type a code.", "Error", JOptionPane.ERROR_MESSAGE );
               return;
            }
            code = new int[ codeLength ];
            for( int count = 0; count < codeLength; count++ )
            {
               code[ count ] =    (    (  int  )(  tmpString.charAt( count )  )   %   256    ) ;
            }

            if( !encrypt )
            {
               for(int count = 0; count < codeLength; count++ )
               {
                  code[ count ] =   (    256    -    (  ( int )code[ count ]  )   )  % 256;
               }
            }
            File file1 = new File( inputPathField.getText() );
            File file2 = new File( outputPathField.getText() );
            if(  !file1.exists() || !file1.isFile()  )
            {
               JOptionPane.showMessageDialog( Encryptor.this, "Invalid input file name" , "Error", JOptionPane.ERROR_MESSAGE );
               return;
            }
            else if( !file1.canRead())
            {
               System.err.println("Input File Not Readable");
               return;
            }

            if( file2.exists() && !file2.canWrite() )
            {
               JOptionPane.showMessageDialog( Encryptor.this, "The selected output file is read-only!\nPlease select another output file.", "Operation halted", JOptionPane.ERROR_MESSAGE );
               return;
            }

            String str1 = "File Size " + file1.length() + " bytes\nDo you want to proceed with this operation?";
            if( file1.getPath().equals(  file2.getPath() ) )
            {
               str1 += "\n\n( Warning : If a power failure occurs  during this process\nall file data to be corrapted and can't be retrieved anymore,\nbecause you are going to overwrite existing file rather \ncreating a new file.)";
            }
            else
            {
               if( file2.exists() )
               {
                  str1 += "\n\n( Warning : You are going to overwrite an existing file.)";
               }
            }
            int result = JOptionPane.showConfirmDialog( Encryptor.this, str1, "Confirmation", JOptionPane.YES_NO_OPTION );
            if( result != JOptionPane.YES_OPTION )
            {
               return;
            }

            if( file1.getPath().equals(  file2.getPath() )  )
            {
               inputFilePointer = new RandomAccessFile( file1 , "r" );
               outputFilePointer = new RandomAccessFile( file2 , "rw");
            }
            else
            {
               inputFilePointer = new RandomAccessFile( file1, "r" );
               FileOutputStream fos = new FileOutputStream(file2);
               fos.close();
               outputFilePointer = new RandomAccessFile( file2 , "rw");
            }

            currentCodeIndex = 0;

            inputFilePointer.seek( 0 );
            outputFilePointer.seek( 0 );
            lengthOfFile = file1.length();
            filePointer = 0;
            dataSize = 1000000;
            byteData = new byte[ dataSize ];
            int pro, tot;
            tot = ( int )( lengthOfFile / dataSize ) + 1;
            pro = 0;

            while( true )
            {
               if(  ( filePointer + dataSize ) < lengthOfFile  )
               {

                  inputFilePointer.read( byteData, 0, dataSize );
                  
                  for( int count = 0; count < dataSize; count++ )
                  {
                     byteData[count] = (      byte      )(     (     (    (   (int)byteData[count] + 128 ) + code[ currentCodeIndex++ ]    ) % 256     )     -     128     );
                     currentCodeIndex %= codeLength;
                  }

                  outputFilePointer.write( byteData, 0, dataSize );
                  filePointer+=dataSize;
                  pro++;
                  SwingUtilities.invokeLater( new UpdateThread( statusLabel, pro, tot ) );
               }
               else
               {
                  inputFilePointer.read( byteData, 0, (int)(lengthOfFile-filePointer)  );
                  
                  for( int count = 0; count < (lengthOfFile-filePointer) ; count++ )
                  {
                     byteData[count] = (      byte      )(     (     (    (   (int)byteData[count] + 128 ) + code[ currentCodeIndex++ ]    ) % 256     )     -     128     );
                     currentCodeIndex %= codeLength;
                  }

                  outputFilePointer.write( byteData, 0, (int)(lengthOfFile-filePointer)  );
                  filePointer = lengthOfFile;
                  pro++;
                  SwingUtilities.invokeLater( new UpdateThread( statusLabel, pro, tot ) );
                  break;
               }
            }
            JOptionPane.showMessageDialog( Encryptor.this, "File operation(" + ( encrypt?"Encryption":"Decryption" ) + ") complete.\nOutput file: '" + file2.getAbsolutePath() + "'." , "Job Done", JOptionPane.INFORMATION_MESSAGE );
         }
         catch( IOException exception )
         {
               JOptionPane.showMessageDialog( Encryptor.this, exception.toString(), "Exception", JOptionPane.ERROR_MESSAGE );
         }
         catch( Exception exception )
         {
               JOptionPane.showMessageDialog( Encryptor.this, exception.toString(), "Exception", JOptionPane.ERROR_MESSAGE );
        }
         finally
         {
            code = null;
            busy = false;
            openButton.setEnabled( true );
            saveButton.setEnabled( true );
            encryptButton.setEnabled( true );
            decryptButton.setEnabled( true );
            helpButton.setEnabled( true );
            resetButton.setEnabled( true );
            statusLabel.setText( "Status: stopped" );

            try
            {
               if( inputFilePointer != null )
               {
                  inputFilePointer.close();
                  inputFilePointer = null;
               }
               if( outputFilePointer != null )
               {
                  outputFilePointer.close();
                  outputFilePointer = null;
               }
               container.setCursor( Cursor.getPredefinedCursor(  Cursor.DEFAULT_CURSOR  ) );
            }
            catch( Exception exception )
            {
               JOptionPane.showMessageDialog( Encryptor.this, exception.toString(), "Exception", JOptionPane.ERROR_MESSAGE );
            }
         }
      }
   }
}