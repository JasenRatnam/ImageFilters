/*
 * Jasen Ratnam
 * Program for Computer Science with Maths.
 * In Vanier College.
 */

import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Image processing application to apply filters to an image.
 * Opens an image from the computers file.
 * Apply filters to the image to process it.
 * Save the newly updated image in the computer files.
 * Assignment 2
 * Date : 2017-09-28
 * @author Jasen Ratnam
 */
public class imageGUI extends javax.swing.JFrame {

    // Holds a buffered copy of the original image to apply filters on it.
    private BufferedImage img = null;
    // A boolean to know wether the apply button as been clicked or not.
    private boolean apllyButtonClicked = false;
    // Kernel matrix for box filter.
    private final float[][] Box_Filter =
    {
        {0.22222222f,0.22222222f,0.22222222f},
        {0.22222222f,0.22222222f,0.22222222f},
        {0.22222222f,0.22222222f,0.22222222f},
    };
    // Kernel matrix for sharpening
    final float[][] Sharpen = 
        {
            {0.0f,-1.0f,0.0f},
            {-1.0f,5.0f,-1.0f},
            {0.0f,-1.0f,0.0f},
            
        };
    // Kernel matrix for gaussian filter.
    private final float[][] Gaussian_Filter = 
    {
        {0.00000067f, 0.00002292f, 0.00019117f, 0.00038771f, 0.00019117f, 0.00002292f, 0.00000067f},
        {0.00002292f, 0.00078634f, 0.00655965f, 0.01330373f, 0.00655965f, 0.00078633f, 0.00002292f},
        {0.00019117f, 0.00655965f, 0.05472157f, 0.11098164f, 0.05472157f, 0.00655965f, 0.00019117f},
        {0.00038771f, 0.01330373f, 0.11098164f, 0.22508352f, 0.11098164f, 0.01330373f, 0.00038771f},
        {0.00019117f, 0.00655965f, 0.05472157f, 0.11098164f, 0.05472157f, 0.00655965f, 0.00019117f},
        {0.00002292f, 0.00078634f, 0.00655965f, 0.01330373f, 0.00655965f, 0.00078633f, 0.00002292f},
        {0.00000067f, 0.00002292f, 0.00019117f, 0.00038771f, 0.00019117f, 0.00002292f, 0.00000067f}

    };
    // Kernel matrix for edge detection filter.
    private final float[][] Edge_Detection_Filter = 
    {
        {-1.0f,-1.0f,-1.0f},
        {-1.0f,8.0f,-1.0f},
        {-1.0f,-1.0f,-1.0f},

    };
    
    
    
    /**
     * Creates new form imageGUI
     * Disable all other buttons than the open button.
     */
    public imageGUI()
    {
        initComponents();
        Box_Filter_Check_Box.setEnabled(false);
        Gamma_Correction_Slider.setEnabled(false);
        Edge_Detection_Filter_Check_Box.setEnabled(false);
        Apply_Button.setEnabled(false);
        Convert_To_Grey_Scales_Check_Box.setEnabled(false);
        Edge_Detection_Filter_Check_Box.setEnabled(false);
        Gaussian_Filter_Check_Box.setEnabled(false);
        saveButton.setEnabled(false);
        Sharpen_Check_Box.setEnabled(false);
        getContentPane().setBackground(black);
    }
    
    /**
     * Method to apply convolution filter to the image and update the image.
     * @param kernel  filter matrix to be applied on a image.
     */
    public void ApplyConvolutionFilter(float[][] kernel)
    {
        try
        {
            // Set the buffered image to the opened image.
            img = ImageIO.read(new File(posterImage_Updated.getImageFilename()));
        }
        catch(IOException e)
        {
            // If image cannot be buffered and exit the application.
            messageLabel.setText("Can't buffer image " + posterImage_Updated.getImageFilename());
            System.exit(-1);
        }
        
        // Create a copy of the image.
        BufferedImage outputImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        
        // Set output image from input image (img)
        for (int j=0; j<img.getHeight(); ++j)
        {
            for (int i=0; i<img.getWidth(); ++i)
            {
                // Calculate the filter and set it for the ouput image.
                Color pixelColor = ApplyKernel(kernel, i, j);
                outputImage.setRGB(i, j, pixelColor.getRGB());
                
            }
        }
        
        // Update the image shown on the application.
        posterImage_Updated.setImage(outputImage);
    }
    
    /*
    * Method to be used by the ApplyConvolutionFilter method to do the kernel calculation.
    */
    private Color ApplyKernel(float[][] kernel, int row, int column)
    {
        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;

        int minIndexH = -(kernel.length / 2);
        int maxIndexH = minIndexH + kernel.length;
        int minIndexV = -(kernel[0].length / 2);
        int maxIndexV = minIndexV + kernel[0].length;
        
        for (int i = minIndexH; i < maxIndexH; ++i)
        {
            for (int j=minIndexV; j<maxIndexV; ++j)
            {
                int indexH = WrapHorizontalIndex(row + i);
                int indexV = WrapVerticalIndex(column + j);
                Color col = new Color(img.getRGB(indexH, indexV));

                red += col.getRed() * kernel[i-minIndexH][j-minIndexV];
                green += col.getGreen() * kernel[i-minIndexH][j-minIndexV];
                blue += col.getBlue() * kernel[i-minIndexH][j-minIndexV];
            }
        }

        red = Math.min(Math.max(red, 0.0f), 255.0f);
        green = Math.min(Math.max(green, 0.0f), 255.0f);
        blue = Math.min(Math.max(blue, 0.0f), 255.0f);
        
        return new Color((int) red, (int) green, (int) blue);
    }
    
    /*
    * Method to be used by the Applykernel method to do the kernel calculation.
    */
    private int WrapHorizontalIndex(int i)
    {
        // This takes care of negative and positive indices beyond the image resolution
        return (i + img.getWidth()) % img.getWidth();
    }

    /*
    * Method to be used by the ApplyKernel method to do the kernel calculation.
    */
    private int WrapVerticalIndex(int i)
    {
        // This takes care of negative and positive indices beyond the image resolution
        return (i + img.getHeight()) % img.getHeight();
    }
    
    
    

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        messageLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        posterImage_Updated = new ImagePanel();
        posterImage = new ImagePanel();
        openButton = new javax.swing.JButton();
        Box_Filter_Label = new javax.swing.JLabel();
        Gaussian_Filter_Label = new javax.swing.JLabel();
        Edge_Detection_Filter_Label = new javax.swing.JLabel();
        Gamma_Correction_Label = new javax.swing.JLabel();
        Convert_To_Grey_Scales_Label = new javax.swing.JLabel();
        Apply_Button = new javax.swing.JButton();
        Box_Filter_Check_Box = new javax.swing.JCheckBox();
        Gaussian_Filter_Check_Box = new javax.swing.JCheckBox();
        Edge_Detection_Filter_Check_Box = new javax.swing.JCheckBox();
        Convert_To_Grey_Scales_Check_Box = new javax.swing.JCheckBox();
        Gamma_Correction_Slider = new javax.swing.JSlider();
        Sharpen_Label = new javax.swing.JLabel();
        Sharpen_Check_Box = new javax.swing.JCheckBox();
        Menu_Bar = new javax.swing.JMenuBar();
        File_Menu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setForeground(java.awt.Color.white);
        setResizable(false);

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        saveButton.setBackground(white);
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout posterImage_UpdatedLayout = new javax.swing.GroupLayout(posterImage_Updated);
        posterImage_Updated.setLayout(posterImage_UpdatedLayout);
        posterImage_UpdatedLayout.setHorizontalGroup(
            posterImage_UpdatedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        posterImage_UpdatedLayout.setVerticalGroup(
            posterImage_UpdatedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout posterImageLayout = new javax.swing.GroupLayout(posterImage);
        posterImage.setLayout(posterImageLayout);
        posterImageLayout.setHorizontalGroup(
            posterImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        posterImageLayout.setVerticalGroup(
            posterImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        openButton.setBackground(white);
        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        Box_Filter_Label.setText("Box Filter:");

        Gaussian_Filter_Label.setText("Gaussian Filter:");

        Edge_Detection_Filter_Label.setText("Edge Detection Filter:");

        Gamma_Correction_Label.setText("Gamma Correction:");

        Convert_To_Grey_Scales_Label.setText("Convert to Grey Scales:");

        Apply_Button.setBackground(white);
        Apply_Button.setText("Apply");
        Apply_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Apply_ButtonActionPerformed(evt);
            }
        });

        Box_Filter_Check_Box.setBackground(white);

        Gaussian_Filter_Check_Box.setBackground(white);

        Edge_Detection_Filter_Check_Box.setBackground(white);

        Convert_To_Grey_Scales_Check_Box.setBackground(white);

        Gamma_Correction_Slider.setBackground(white);
        Gamma_Correction_Slider.setMaximum(200);
        Gamma_Correction_Slider.setPaintLabels(true);
        Gamma_Correction_Slider.setPaintTicks(true);
        Gamma_Correction_Slider.setSnapToTicks(true);
        Gamma_Correction_Slider.setToolTipText("");
        Gamma_Correction_Slider.setValue(100);
        Gamma_Correction_Slider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Gamma_Correction_Slider.setRequestFocusEnabled(false);
        Gamma_Correction_Slider.setValueIsAdjusting(true);
        Gamma_Correction_Slider.setVerifyInputWhenFocusTarget(false);

        Sharpen_Label.setText("Sharpen:");

        Sharpen_Check_Box.setBackground(white);

        Menu_Bar.setBackground(cyan);

        File_Menu.setText("File");
        Menu_Bar.add(File_Menu);
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {

                dispatchEvent(new WindowEvent(imageGUI.this, WindowEvent.WINDOW_CLOSING));

                try
                {
                    File file = new File("sounds/Click.wav");
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();

                    clip.open(ais);
                    clip.start();
                }
                catch(Exception e)
                {
                    messageLabel.setText("Error loading sound.");
                }
            }
        });
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        File_Menu.add(quitItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {

                saveButtonActionPerformed(ae);

                try
                {
                    File file = new File("sounds/Click.wav");
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();

                    clip.open(ais);
                    clip.start();
                }
                catch(Exception e)
                {
                    messageLabel.setText("Error loading sound.");
                }
            }
        });
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        File_Menu.add(saveItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {

                openButtonActionPerformed(ae);

                try
                {
                    File file = new File("sounds/Click.wav");
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();

                    clip.open(ais);
                    clip.start();
                }
                catch(Exception e)
                {
                    messageLabel.setText("Error loading sound.");
                }
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        File_Menu.add(openItem);

        JMenuItem resetItem = new JMenuItem("Reset Image");
        resetItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {

                posterImage_Updated.setImage(posterImage.getBufferedImage());

                try
                {
                    File file = new File("sounds/Click.wav");
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();

                    clip.open(ais);
                    clip.start();
                }
                catch(Exception e)
                {
                    messageLabel.setText("Error loading sound.");
                }
            }
        });
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        File_Menu.add(resetItem);

        setJMenuBar(Menu_Bar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Apply_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Gaussian_Filter_Label)
                            .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(posterImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(posterImage_Updated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Convert_To_Grey_Scales_Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Convert_To_Grey_Scales_Check_Box))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(Box_Filter_Label)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Box_Filter_Check_Box))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(Gaussian_Filter_Check_Box)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(Edge_Detection_Filter_Label)
                                            .addGap(18, 18, 18)
                                            .addComponent(Edge_Detection_Filter_Check_Box))))
                                .addGap(36, 36, 36)
                                .addComponent(Sharpen_Label)
                                .addGap(29, 29, 29)
                                .addComponent(Sharpen_Check_Box)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 59, Short.MAX_VALUE)
                        .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 60, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Gamma_Correction_Label)
                        .addGap(30, 30, 30)
                        .addComponent(Gamma_Correction_Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(posterImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(posterImage_Updated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(openButton))
                .addGap(18, 18, 18)
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Box_Filter_Check_Box)
                    .addComponent(Box_Filter_Label)
                    .addComponent(Sharpen_Label)
                    .addComponent(Sharpen_Check_Box))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Gaussian_Filter_Label)
                    .addComponent(Gaussian_Filter_Check_Box))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Edge_Detection_Filter_Label)
                    .addComponent(Edge_Detection_Filter_Check_Box))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(Gamma_Correction_Label))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Gamma_Correction_Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Convert_To_Grey_Scales_Label)
                        .addGap(26, 26, 26)
                        .addComponent(Apply_Button))
                    .addComponent(Convert_To_Grey_Scales_Check_Box))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /*
    * Method to tell what happens whe the save button is clicked.
    * The save menu opens and asks the user where do you want to save the file and in what name. 
    */
    @SuppressWarnings("empty-statement")
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        
        if(apllyButtonClicked == false)
        {
            messageLabel.setText("Cannot save: No image processing as been done.");
        }
        else
        {
            try
            {
                File file = new File("sounds/Click.wav");
                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();

                messageLabel.setText("File is being saved.....");

                clip.open(ais);
                clip.start();
                JFileChooser saveFile = new JFileChooser();
                saveFile.setCurrentDirectory(new File("./images"));
                String[] imageFormats = new String[] {"jpg","jpeg", "bmp","png"};
                FileFilter filter = new FileNameExtensionFilter("Image file", imageFormats);

                saveFile.setFileFilter(filter);
                saveFile.addChoosableFileFilter(filter);

                if(saveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    File outputfile = saveFile.getSelectedFile();

                    BufferedImage img = posterImage_Updated.getBufferedImage();
                    ImageIO.write(img, "png", outputfile);

                    messageLabel.setText("File has been saved");
                }
                else
                {
                    messageLabel.setText("Save command cancelled by user.");
                }
            
            }
            catch(Exception e)
            {
                messageLabel.setText("Error saving file...");
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    /*
    * Method that allows it users to opena file from the computer process.
    * Filters file type.
    */
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        
        try
        {
            File file = new File("sounds/Click.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            
            messageLabel.setText("File is being opened.....");
            
            clip.open(ais);
            clip.start();
            
            JFileChooser openFile = new JFileChooser();
            openFile.setCurrentDirectory(new File("./images"));
            String[] imageFormats = new String[] {"jpg","jpeg", "bmp","png"};
            FileFilter filter = new FileNameExtensionFilter("Image file", imageFormats);
            
            openFile.setFileFilter(filter);
            openFile.addChoosableFileFilter(filter);
            
            if(openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                String filename = openFile.getSelectedFile().getPath();
                
                posterImage.setImage(filename);
                posterImage_Updated.setImage(filename);
                posterImage_Updated.setVisible(false);
                
                messageLabel.setText("File has been opened");
                
                Box_Filter_Check_Box.setEnabled(true);
                Gamma_Correction_Slider.setEnabled(true);
                Edge_Detection_Filter_Check_Box.setEnabled(true);
                Apply_Button.setEnabled(true);
                Convert_To_Grey_Scales_Check_Box.setEnabled(true);
                Edge_Detection_Filter_Check_Box.setEnabled(true);
                Gaussian_Filter_Check_Box.setEnabled(true);
                saveButton.setEnabled(true);
                Sharpen_Check_Box.setEnabled(true);
                
            }
            else
            {
                messageLabel.setText("Open command cancelled by user.");
            }

            
        }
        catch(Exception e)
        {
            messageLabel.setText("Error opening file...");
        }
    }//GEN-LAST:event_openButtonActionPerformed

    /*
    * Method that tells waht happens when the apply button is clicked by the user,
    * Applys all selected filters to the opened image.
    * Makes apllyButtonClicked true to know that a filters was applied before saving,
    */
    private void Apply_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Apply_ButtonActionPerformed
        
        apllyButtonClicked = true;
        messageLabel.setText("Image Successfully Processed!");
        posterImage_Updated.setVisible(true);
        // Makes a magic sound when the button is clicked.
        try
        {
            File file = new File("sounds/Magic.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();

            clip.open(ais);
            clip.start();
        }
        catch(Exception e)
        {
            messageLabel.setText("Error loading sound.");
        }

        if(Box_Filter_Check_Box.isSelected())
        {
            ApplyConvolutionFilter(Box_Filter);
        }

        if(Gaussian_Filter_Check_Box.isSelected())
        {
            ApplyConvolutionFilter(Gaussian_Filter);
        }   

        if(Edge_Detection_Filter_Check_Box.isSelected())
        {
            ApplyConvolutionFilter(Edge_Detection_Filter);
        }
        
        if(Sharpen_Check_Box.isSelected())
        {
            ApplyConvolutionFilter(Sharpen);
        }
        
        // Set the gamma value to the value from the slider.
        double gamma = Gamma_Correction_Slider.getValue() / 100.0f;
        
        if(gamma != 1.0)
        {
            try
            {
                BufferedImage img = posterImage_Updated.getBufferedImage();

                for (int j=0; j<img.getHeight(); ++j)
                {
                    for (int i=0; i<img.getWidth(); ++i)
                    {
                        Color rgb = new Color(img.getRGB(i, j));
                        float[] hsb = Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), null);
                        double brightness = Math.pow(hsb[2], gamma);
                        hsb[2] = (float) brightness;
                        int corrected = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

                        img.setRGB(i, j, corrected);
                    }
                }
                posterImage_Updated.repaint();
            }
            catch(Exception e)
            {
                messageLabel.setText("Error doing gamma correction");
            }
        }

        if(Convert_To_Grey_Scales_Check_Box.isSelected())
        {
            try
            {
                // Convert image to black and white
                BufferedImage img = posterImage_Updated.getBufferedImage();

                for (int j=0; j<img.getHeight(); ++j)
                {
                    for (int i=0; i<img.getWidth(); ++i)
                    {
                        int argb = img.getRGB(i, j);
                        int a = 255; // alpha 255 = fully opaque
                        int r = (argb & 0xFF0000) >> 16;
                        int g = (argb & 0x00FF00) >> 8;
                        int b = (argb & 0x0000FF) >> 0;
                        int avg = (r + g + b) / 3;

                        argb = a << 24 | avg << 16 | avg << 8 | avg << 0;
                        img.setRGB(i, j, argb);
                    }
                }
                posterImage_Updated.repaint();
            }
            catch(Exception e)
            {
                messageLabel.setText("Error converting to grey scales!");
            }
        }
        
        else if(Convert_To_Grey_Scales_Check_Box.isSelected() == false && gamma == 1.0 && Sharpen_Check_Box.isSelected() == false && Edge_Detection_Filter_Check_Box.isSelected() == false && Gaussian_Filter_Check_Box.isSelected() == false && Box_Filter_Check_Box.isSelected() == false)
        {
            messageLabel.setText("No image processing applied!!");
            posterImage_Updated.setImage(posterImage.getBufferedImage());
        }
    }//GEN-LAST:event_Apply_ButtonActionPerformed

    /**
     * @param args runs the application.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(imageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(imageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(imageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(imageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new imageGUI().setVisible(true);
            }
        });
       
    // Change the look of the buttons.
    try 
    {
       UIManager.setLookAndFeel(
       UIManager.getCrossPlatformLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) 
    {
       
    }
}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Apply_Button;
    private javax.swing.JCheckBox Box_Filter_Check_Box;
    private javax.swing.JLabel Box_Filter_Label;
    private javax.swing.JCheckBox Convert_To_Grey_Scales_Check_Box;
    private javax.swing.JLabel Convert_To_Grey_Scales_Label;
    private javax.swing.JCheckBox Edge_Detection_Filter_Check_Box;
    private javax.swing.JLabel Edge_Detection_Filter_Label;
    private javax.swing.JMenu File_Menu;
    private javax.swing.JLabel Gamma_Correction_Label;
    private javax.swing.JSlider Gamma_Correction_Slider;
    private javax.swing.JCheckBox Gaussian_Filter_Check_Box;
    private javax.swing.JLabel Gaussian_Filter_Label;
    private javax.swing.JMenuBar Menu_Bar;
    private javax.swing.JCheckBox Sharpen_Check_Box;
    private javax.swing.JLabel Sharpen_Label;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton openButton;
    private ImagePanel posterImage;
    private ImagePanel posterImage_Updated;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
