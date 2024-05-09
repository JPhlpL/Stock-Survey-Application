package com.mycompany.excelcrudapp;

import com.toedter.calendar.JDateChooser;
import java.awt.Desktop;
import javax.swing.*;
import java.awt.Color;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.table.*;
import java.io.FileOutputStream;
import java.io.File;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.mindrot.jbcrypt.BCrypt;
import userinfo.userinfo;

public class App extends JFrame {
  
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton registerAccButton, editButton, updateButton, deleteButton, deleteAllButton, importButton, sendEmailButton, searchButton, logoutButton;
    private JTextField idField, transact_numField, part_nameField, part_numberField, supplier_nameField, onhand_stocksField, incoming_delField, shipment_schedField, qty_allot_yesField, qty_allot_noField, transact_dateField, searchField, usernameField, registerUsernameField, registerNameField;
    private JPasswordField registerPasswordField, registerConfirmPasswordField, passwordField;

    private BufferedImage myPicture;
    private final String DB_URL = "jdbc:sqlite:db/survey_response.db";
    
    //For Validation
    String regexForNumbers = "/^[A-Z@~`!@#$%^&*()_=+\\\\\\\\';:\\\"\\\\/?>.<,-]*$/i";
    private JDateChooser transactDatePicker; // Declare the JDateChooser component
    
    // Define a method to apply DocumentFilter and FocusListener to a JTextField
    private void setupNumericField(JTextField field) {
        if (field != null) {
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    StringBuilder filteredText = new StringBuilder();
                    for (char c : text.toCharArray()) {
                        if (Character.isDigit(c)) {
                            filteredText.append(c);
                        }
                    }
                    super.replace(fb, offset, length, filteredText.toString(), attrs);
                }
            });
            
            //does it compress and expose to the folder?
            //what if dont need to extract but it can go straight through the zip file without compressin

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String input = field.getText();
                    if (!input.matches("\\d*")) {
                        field.setText("");
                    }
                }
            });
        }
    }

    
    
    private void createTableComponents() {
        ImageIcon img = new ImageIcon("dist/img/survey.png");
        setIconImage(img.getImage());
        setTitle("Survey Application - Stocks Inventory by DNPH"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        
        getContentPane().setBackground(Color.BLACK); // Set rich black background color
       
        sendEmailButton = new JButton("Send Email");  // Initialize the new button
        // Create the search field
        searchField = new JTextField(20);
        searchButton = new JButton("Search All"); // Initialize the new search button\
        editButton = new JButton("Select");
        
         
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("DNPH Item Transact #");
        tableModel.addColumn("Part Name");
        tableModel.addColumn("Part Number");
        tableModel.addColumn("Supplier Name");
        tableModel.addColumn("Onhand Stocks");
        tableModel.addColumn("Incoming Delivery");
        tableModel.addColumn("Shipment Schedule");
        tableModel.addColumn("Qty Alloted? Yes");
        tableModel.addColumn("Qty Alloted? No");
        tableModel.addColumn("Transaction Date");
        //
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);

        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        deleteAllButton = new JButton("Delete All");
        importButton = new JButton("Import"); // Initialize the new button
        logoutButton = new JButton("Logout");
        registerAccButton = new JButton("Register Account");
        
        // Create the sidebar panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(Color.BLACK);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        
        idField = new JTextField(10);
        idField.setEditable(false);
        transact_numField = new JTextField(10);
        transact_numField.setEditable(false);
        part_nameField = new JTextField(10);
        part_nameField.setEditable(false);
        part_numberField = new JTextField(10);
        part_numberField.setEditable(false);
        supplier_nameField = new JTextField(10);
        supplier_nameField.setEditable(false);
        onhand_stocksField = new JTextField(10);
        incoming_delField = new JTextField(10);
        shipment_schedField = new JTextField(10);
        qty_allot_yesField = new JTextField(10);
        qty_allot_noField = new JTextField(10);
        transact_dateField = new JTextField(10);
        
        // Apply input validation and clearing behavior to fields
        setupNumericField(onhand_stocksField);
        setupNumericField(incoming_delField);
        setupNumericField(qty_allot_noField);
        
        
      // Create the transactDatePicker component
        transactDatePicker = new JDateChooser();
        transactDatePicker.setEnabled(false);

        // Set font colors to complement the design
        java.awt.Color textColor = java.awt.Color.WHITE;
        java.awt.Color buttonColor = java.awt.Color.DARK_GRAY;
        
        // Add sorting
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        // Apply color and font to buttons and text fields
        editButton.setForeground(textColor);
        updateButton.setForeground(textColor);
        deleteButton.setForeground(textColor);
        deleteAllButton.setForeground(textColor);
        importButton.setForeground(textColor);
        sendEmailButton.setForeground(textColor);
        searchButton.setForeground(textColor);
        logoutButton.setForeground(textColor);
        registerAccButton.setForeground(textColor);
        
        editButton.setBackground(buttonColor);
        updateButton.setBackground(buttonColor);
        deleteButton.setBackground(buttonColor);
        deleteAllButton.setBackground(buttonColor);
        importButton.setBackground(buttonColor);
        sendEmailButton.setBackground(buttonColor);
        searchButton.setBackground(buttonColor);
        logoutButton.setBackground(buttonColor);
        registerAccButton.setBackground(buttonColor);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 10); // Add margin
                
        // Create JPanel for logo 
        try {
            myPicture = ImageIO.read(new File("dist/img/denso_logo.png"));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(240, 130, Image.SCALE_SMOOTH)));
        
        // Set the gridwidth to 2 to make it span across two columns
        gbc.gridwidth = 2;

        // Reset the gridx and gridy to 0 to position the logo at the top
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add the logo to the panel
        inputPanel.add(picLabel, gbc);

        // Reset the gridwidth to 1 for the rest of the components
        gbc.gridwidth = 1;
        
        gbc.gridy = 1;
        inputPanel.add(new JLabel("ID:"), gbc);
        
        gbc.gridy = 2;
        inputPanel.add(new JLabel("DNPH Item Transact #"), gbc);
        
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Part Name"), gbc);
        
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Part Number"), gbc);
        
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Supplier Name"), gbc);
        
        gbc.gridy = 6;
        inputPanel.add(new JLabel("Onhand Stocks"), gbc);
        
        gbc.gridy = 7;
        inputPanel.add(new JLabel("Incoming Delivery"), gbc);
        
        gbc.gridy = 8;
        inputPanel.add(new JLabel("Shipment Schedule"), gbc);
        
        gbc.gridy = 9;
        inputPanel.add(new JLabel("Qty Alloted? Yes"), gbc);
        
        gbc.gridy = 10;
        inputPanel.add(new JLabel("Qty Alloted? No"), gbc);
        
        gbc.gridy = 11;
        inputPanel.add(new JLabel("Transaction Date"), gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridy = 1;
        inputPanel.add(idField, gbc);
        
        gbc.gridy = 2;
        inputPanel.add(transact_numField, gbc);

        gbc.gridy = 3;
        inputPanel.add(part_nameField, gbc);
        
        gbc.gridy = 4;
        inputPanel.add(part_numberField, gbc);
        
        gbc.gridy = 5;
        inputPanel.add(supplier_nameField, gbc);
        
        gbc.gridy = 6;
        inputPanel.add(onhand_stocksField, gbc);
        
        gbc.gridy = 7;
        inputPanel.add(incoming_delField, gbc);
        
        gbc.gridy = 8;
        inputPanel.add(shipment_schedField, gbc);
        
        gbc.gridy = 9;
        inputPanel.add(qty_allot_yesField, gbc);
        
        gbc.gridy = 10;
        inputPanel.add(qty_allot_noField, gbc);
        
        gbc.gridy = 11;
        inputPanel.add(transactDatePicker, gbc); // Add the JDatePickerImpl to the inputPanel
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 5, 5)); // Adjust as needed

        buttonPanel.add(updateButton);
        buttonPanel.add(importButton);
        buttonPanel.add(sendEmailButton);
        buttonPanel.add(logoutButton);
//      buttonPanel.add(registerAccButton);

        sidebarPanel.add(inputPanel, BorderLayout.NORTH);
        sidebarPanel.add(buttonPanel, BorderLayout.CENTER);

        // Create the main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Create the search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(editButton);
        searchPanel.add(deleteButton);
        searchPanel.add(deleteAllButton);

        // Create the outermost panel (holding sidebar, main content, and search)
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(sidebarPanel, BorderLayout.WEST);
        outerPanel.add(mainContentPanel, BorderLayout.CENTER);
        outerPanel.add(searchPanel, BorderLayout.NORTH);

        setLayout(new BorderLayout());
        add(outerPanel, BorderLayout.CENTER);

        // Load data from SQLite database
        loadData();

        
        editButton.addActionListener(e -> editRow());
        updateButton.addActionListener(e -> updateRow());
        deleteButton.addActionListener(e -> deleteRow());
        deleteAllButton.addActionListener(e -> deleteAllRow());
        sendEmailButton.addActionListener(e -> sendEmail());
        importButton.addActionListener(e -> importExcel());
        logoutButton.addActionListener(e -> logout());
        registerAccButton.addActionListener(e -> {
            getContentPane().removeAll(); // Clear the login panel
            createRegistrationPanel(); // Show the registration form
            revalidate();
            repaint();
        });
        
        searchButton.addActionListener(e -> searchAllFields(searchField.getText()));
        
        // Fix sort table by number
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                sorter.toggleSortOrder(columnIndex);
            }
        });
        // Set the UI to be consistent with the given design
       try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
    }
}

    private void loadData() {
        try {
            try (Connection conn = DriverManager.getConnection(DB_URL); 
                    Statement stmt = conn.createStatement(); 
                    ResultSet rs = stmt.executeQuery("SELECT * FROM tblsupplier_response")) {
                
                while (rs.next()) {
                    Object[] rowData = { rs.getInt("id"), rs.getString("transact_num"), rs.getString("part_name"),rs.getString("part_number"),rs.getString("supplier_name"),rs.getString("onhand_stocks"),rs.getString("incoming_del"),rs.getString("shipment_sched"),rs.getString("qty_allot_yes"),rs.getString("qty_allot_no"),rs.getString("date_transact")};
                    tableModel.addRow(rowData);
                }
                
            }
        } catch (SQLException e) {
        }
    }
     
     private void searchAllFields(String query) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) table.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }
    private void editRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(table.getValueAt(selectedRow, 0).toString());
            transact_numField.setText(table.getValueAt(selectedRow, 1).toString());
            part_nameField.setText(table.getValueAt(selectedRow, 2).toString());
            part_numberField.setText(table.getValueAt(selectedRow, 3).toString());
            supplier_nameField.setText(table.getValueAt(selectedRow, 4).toString());
            onhand_stocksField.setText(table.getValueAt(selectedRow, 5).toString());
            incoming_delField.setText(table.getValueAt(selectedRow, 6).toString());
            shipment_schedField.setText(table.getValueAt(selectedRow, 7).toString());
            qty_allot_yesField.setText(table.getValueAt(selectedRow, 8).toString());
            qty_allot_noField.setText(table.getValueAt(selectedRow, 9).toString());
              // Get the date string from the table and parse it to set in the transactDatePicker
            String dateString = table.getValueAt(selectedRow, 10).toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                java.util.Date parsedDate = dateFormat.parse(dateString);
                transactDatePicker.setDate(parsedDate);
            } catch (ParseException e) {
            }
        }
    }
    
    private static void openDirectory() throws IOException {
        File directory = new File("export/");
        Desktop.getDesktop().open(directory);
      }

    private void updateRow() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = Integer.parseInt(idField.getText());
                String transact_num = transact_numField.getText();
                String part_name = part_nameField.getText();
                String part_number = part_numberField.getText();
                String supplier_name = supplier_nameField.getText();
                String onhand_stocks = onhand_stocksField.getText();
                String incoming_del = incoming_delField.getText();
                String shipment_sched = shipment_schedField.getText();
                String qty_allot_yes = qty_allot_yesField.getText();
                String qty_allot_no = qty_allot_noField.getText();
                // Get the selected date from the transactDatePicker
                java.util.Date selectedDate = transactDatePicker.getDate();
                
                if( onhand_stocks.isBlank() || incoming_del.isBlank() || shipment_sched.isBlank() || qty_allot_yes.isBlank() || qty_allot_no.isBlank() || onhand_stocks == null || incoming_del == null || shipment_sched == null || qty_allot_yes == null || qty_allot_no == null)
                {
                     JOptionPane.showMessageDialog(null,
                    "Please check your inputs!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }

                else{
                    tableModel.setValueAt(id, selectedRow, 0);
                    tableModel.setValueAt(transact_num, selectedRow, 1);
                    tableModel.setValueAt(part_name, selectedRow, 2);
                    tableModel.setValueAt(part_number, selectedRow, 3);
                    tableModel.setValueAt(supplier_name, selectedRow, 4);
                    tableModel.setValueAt(onhand_stocks, selectedRow, 5);
                    tableModel.setValueAt(incoming_del, selectedRow, 6);
                    tableModel.setValueAt(shipment_sched, selectedRow, 7);
                    tableModel.setValueAt(qty_allot_yes, selectedRow, 8);
                    tableModel.setValueAt(qty_allot_no, selectedRow, 9);
                      // Format the selected date as "YYYY-MM-DD"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(selectedDate);
                    tableModel.setValueAt(formattedDate, selectedRow, 10);

                     // Update the row in the database
                    try {
                         JOptionPane.showMessageDialog(null,
                   "The existing record has been updated",
                   "Success",
                   JOptionPane.WARNING_MESSAGE);

                       try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {

                           stmt.executeUpdate("UPDATE tblsupplier_response SET transact_num = '" + transact_num + "', part_name = '" + part_name + "', part_number = '" + part_number + "', supplier_name = '" + supplier_name + "', onhand_stocks = '" + onhand_stocks + "', incoming_del = '" + incoming_del + "', shipment_sched = '" + shipment_sched + "', qty_allot_yes = '" + qty_allot_yes + "', qty_allot_no = '" + qty_allot_no + "', date_transact = '" + formattedDate + "' WHERE id = " + id);
                       }

                    } catch (HeadlessException | SQLException e) {
                        JOptionPane.showMessageDialog(null,
                   "Please check your inputs. Thank you!",
                   "Error!",
                   JOptionPane.ERROR_MESSAGE);
                    }

                   idField.setText("");
                   transact_numField.setText("");
                   part_nameField.setText("");
                   part_numberField.setText("");
                   supplier_nameField.setText("");
                   onhand_stocksField.setText("");
                   incoming_delField.setText("");
                   shipment_schedField.setText("");
                   qty_allot_yesField.setText("");
                   qty_allot_noField.setText("");
                   transactDatePicker.setDate(null);
                 
                }
            }
             else{
            }
        }
    }
    
    //Input validation if empty or null 
    //private static String validate(String s) {
    //    if (s == null || s.isBlank()) {
    //       JOptionPane.showMessageDialog(null,
    //            "Please check your inputs!",
    //            "Error",
    //            JOptionPane.ERROR_MESSAGE);
    //    } else {
    //        return s;
    //    }
    //    return null;
    //}
    
    private void deleteRow() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         int selectedRow = table.getSelectedRow();
         if (selectedRow >= 0) {
             // Get the ID of the row to be deleted
             int id = (int) table.getValueAt(selectedRow, 0);

             tableModel.removeRow(selectedRow);

             // Delete the row from the database
             try {
                 JOptionPane.showMessageDialog(null,
                "The record is now deleted",
                "Success",
                JOptionPane.WARNING_MESSAGE);
                           
                 try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate("DELETE FROM tblsupplier_response WHERE id = " + id);
                 }
             } catch (HeadlessException | SQLException e) {
                  JOptionPane.showMessageDialog(null,
                "The record is now deleted",
                "Success",
                JOptionPane.ERROR_MESSAGE);
             }

            idField.setText("");
            transact_numField.setText("");
            part_nameField.setText("");
            part_numberField.setText("");
            supplier_nameField.setText("");
            onhand_stocksField.setText("");
            incoming_delField.setText("");
            shipment_schedField.setText("");
            qty_allot_yesField.setText("");
            qty_allot_noField.setText("");
            transact_dateField.setText("");
             }
             else{
             }
         }
     }
    private void deleteAllRow() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all records?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
             try {   
                 try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate("DELETE FROM tblsupplier_response");
                 }
                   JOptionPane.showMessageDialog(null,
                "The record is now deleted",
                "Success",
                JOptionPane.WARNING_MESSAGE);
                 // Need change to refresh table
                 DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                 dtm.setRowCount(0);
             } catch (HeadlessException | SQLException e) {
                  JOptionPane.showMessageDialog(null,
                "The record is now deleted",
                "Success",
                JOptionPane.ERROR_MESSAGE);
             }
         }
     }
    
    private void deleteAllRowwithoutValidation() {
        try {   
            try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM tblsupplier_response");
            }
         
            DefaultTableModel dtm = (DefaultTableModel) table.getModel();
            dtm.setRowCount(0);
        } catch (HeadlessException | SQLException e) {
 
        }
     }
    
    private void sendEmail() {
    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        try {
            //Getting the username
            String username = usernameField.getText();
            userinfo user = new userinfo();
            user.setUsername(username);

            // Call the exportToExcel function
            exportToExcel();
            
            // Retrieve data from the SQLite database
            String monthNumeric = retrieveMonthFromDatabase();
            String monthWord = convertNumericToMonthWord(monthNumeric);

            // Launch default email application with attached Excel file
            Desktop desktop = Desktop.getDesktop();
            openDirectory();
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                String mailSubject = "Supplier Inventory and Stock Condition";
                String mailBody = "Dear Ma'am/Sir,\n\n" +
                        "Good day!\n\n" +
                        "Kindly see the attachment for the answered survey for the month of " + monthWord + ".\n\n" +
                        "Thank you!";
                
                // Encode the subject and body parameters
                String encodedSubject = URLEncoder.encode(mailSubject, "UTF-8").replaceAll("\\+", "%20");
                String encodedBody = URLEncoder.encode(mailBody, "UTF-8").replaceAll("\\+", "%20"); // Replace '+' with '%20'

                // Create a text file and write the email content
                String emailContent = "Mail to: jeanne.tianzon.a2c@ap.denso.com" + "\n\n" + "Subject: " + mailSubject + "\n\n" + mailBody;
                String fileName = "export/email_content.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(emailContent);
                }
                    
                URI mailtoURI = new URI("mailto:jeanne.tianzon.a2c@ap.denso.com?subject=" + encodedSubject + "&body=" + encodedBody);
                desktop.mail(mailtoURI);
                deleteAllRowwithoutValidation();
            } else {
                JOptionPane.showMessageDialog(this, "Default email application is not supported on this system.");
            }
        } catch (HeadlessException | IOException | URISyntaxException | SQLException e) {
        }
    } else {
        // Handle the case where the user selects "No" in the confirmation dialog
    }
}
    
    private String convertNumericToMonthWord(String monthNumeric) {
        // Assuming monthNumeric is in "MM" format
        int month = Integer.parseInt(monthNumeric);
        String[] monthNames = {
            "", // 0-indexed to make month numbers match array indices
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month];
    }
    
    private String retrieveSupplier() throws SQLException {
        String supplier_name = "";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();

            // Retrieve the most recent row's datetransact column from the responsetbl table
            String query = "SELECT supplier_name FROM tblsupplier_response ORDER BY id DESC LIMIT 1";
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                supplier_name = resultSet.getString("supplier_name");
              
            }
            else{
                  JOptionPane.showMessageDialog(null,
                "Error! No Data is inputted. ",
                "Error!",
                JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return supplier_name;
    }

    private String retrieveMonthFromDatabase() throws SQLException {
        String month = "";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();

            // Retrieve the most recent row's datetransact column from the responsetbl table
            String query = "SELECT strftime('%Y-%m-%d', date_transact) AS month FROM tblsupplier_response ORDER BY id DESC LIMIT 1";
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String dateTransact = resultSet.getString("month");
                month = dateTransact.substring(5, 7); // Extract month part (MM)
            }
            else{
                  JOptionPane.showMessageDialog(null,
                "Error! No Data is inputted. ",
                "Error!",
                JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return month;
    }
    
    private void exportToExcel() throws SQLException {
        try {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Data");
                // Create headers
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(table.getColumnName(i));
                }
                // Copy data from JTable to Excel
                for (int i = 0; i < table.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = table.getValueAt(i, j);
                        if (value != null) {
                            cell.setCellValue(value.toString());
                        }
                    }
                }

                // Protect the sheet and set cell protection
                sheet.protectSheet("passw0rd");
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        cell.setCellStyle(getLockedCellStyle(workbook));
                    }
                }

                // Get the current date and time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                LocalDateTime now = LocalDateTime.now();
                String formattedDateTime = formatter.format(now);

                String supplier = retrieveSupplier();
                
                String filename = "tblsupplier_response_" + supplier + "_" + formattedDateTime + ".xlsx";

                // Write Excel data to file
                try (FileOutputStream fileOut = new FileOutputStream("export/" + filename)) {
                    workbook.write(fileOut);
                }

                // Set the read-only attribute on the file (Windows only)
                File excelFile = new File("export/" + filename);
                excelFile.setReadOnly();
                
                JOptionPane.showMessageDialog(this, "Excel file exported successfully!\n\n"
                    + "Kindly attached the "+filename+" file into the email and send it to your purchasing contact person in Denso Philippines Corporation\n");
            }

            
        } catch (HeadlessException | IOException e) {
        }
    }

    
    private CellStyle getLockedCellStyle(Workbook workbook) {
        CellStyle lockedCellStyle = workbook.createCellStyle();
        lockedCellStyle.setLocked(true);
        return lockedCellStyle;
    }



    private void importExcel() {
    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        String desktopPath = System.getProperty("user.home") + File.separator +"Desktop";
        JFileChooser fileChooser = new JFileChooser(desktopPath);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fileInputStream = new FileInputStream(selectedFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                 Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {

                int onhand_stocks = 0;
                int incoming_del = 0;
                int qty_allot_no = 0;

                XSSFSheet sheet = workbook.getSheetAt(0);

                for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                    XSSFRow row = sheet.getRow(i);
                    String transact_num = row.getCell(0).getStringCellValue();
                    String part_name = row.getCell(1).getStringCellValue();
                    String part_number = row.getCell(2).getStringCellValue();
                    String supplier_name = row.getCell(3).getStringCellValue();

                    Cell onhand_stocksCell = row.getCell(4);
                    if (onhand_stocksCell.getCellType() == CellType.NUMERIC) {
                        onhand_stocks = (int) Math.round(onhand_stocksCell.getNumericCellValue());
                    }

                    Cell incoming_delCell = row.getCell(5);
                    if (incoming_delCell.getCellType() == CellType.NUMERIC) {
                        incoming_del = (int) Math.round(incoming_delCell.getNumericCellValue());
                    }

                    String shipment_sched = row.getCell(6).getStringCellValue();
                    String qty_allot_yes = row.getCell(7).getStringCellValue();

                    Cell qtyAllotNoCell = row.getCell(8);

                    if (qtyAllotNoCell.getCellType() == CellType.NUMERIC) {
                        qty_allot_no = (int) Math.round(qtyAllotNoCell.getNumericCellValue());
                    }

                    Cell dateTransactCell = row.getCell(9);
                    LocalDate dateTransact = null;

                    if (dateTransactCell.getCellType() == CellType.NUMERIC) {
                        dateTransact = dateTransactCell.getLocalDateTimeCellValue().toLocalDate();
                    } else if (dateTransactCell.getCellType() == CellType.STRING) {
                        dateTransact = LocalDate.parse(dateTransactCell.getStringCellValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }

                    PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO tblsupplier_response (transact_num, part_name, part_number, supplier_name, onhand_stocks, incoming_del, shipment_sched, qty_allot_yes, qty_allot_no, date_transact) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    preparedStatement.setString(1, transact_num);
                    preparedStatement.setString(2, part_name);
                    preparedStatement.setString(3, part_number);
                    preparedStatement.setString(4, supplier_name);
                    preparedStatement.setInt(5, onhand_stocks);
                    preparedStatement.setInt(6, incoming_del);
                    preparedStatement.setString(7, shipment_sched);
                    preparedStatement.setString(8, qty_allot_yes);
                    preparedStatement.setInt(9, qty_allot_no);
                    preparedStatement.setString(10, dateTransact.toString()); // Convert LocalDate to String
                    preparedStatement.executeUpdate();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {

                        int id = generatedKeys.getInt(1);
                        Object[] rowData = {id, transact_num, part_name, part_number, supplier_name, onhand_stocks, incoming_del, shipment_sched, qty_allot_yes, qty_allot_no, dateTransact};
                        tableModel.addRow(rowData);
                    }
                }
                JOptionPane.showMessageDialog(this, "Excel file imported and data saved successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error importing Excel file.");
            }
        }
    }
}


    private void createRegistrationPanel() {
        ImageIcon img = new ImageIcon("dist/img/survey.png");
        setIconImage(img.getImage());
        setTitle("Survey Application - Registration"); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the registration window
        setSize(800, 250);
//        setResizable(false);

        registerUsernameField = new JTextField(10);
        registerNameField = new JTextField(10);
        registerPasswordField = new JPasswordField(10);
        registerConfirmPasswordField = new JPasswordField(10);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 10); // Add margin

        registerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridy = 1;
        registerPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy = 2;
        registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 3;
        registerPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPanel.add(registerUsernameField, gbc);

        gbc.gridy = 1;
        registerPanel.add(registerNameField, gbc);

        gbc.gridy = 2;
        registerPanel.add(registerPasswordField, gbc);

        gbc.gridy = 3;
        registerPanel.add(registerConfirmPasswordField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(registerButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        registerPanel.add(backButton, gbc);

        registerButton.addActionListener((var e) -> {
            String username = registerUsernameField.getText();
            String name = registerNameField.getText();
            char[] password = registerPasswordField.getPassword();
            char[] confirmPassword = registerConfirmPasswordField.getPassword();

            if (username.isEmpty() || name.isEmpty() || password.length == 0 || confirmPassword.length == 0) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Arrays.equals(password, confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hashedPassword = BCrypt.hashpw(new String(password), BCrypt.gensalt());

            try {
                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement stmt = conn.prepareStatement("INSERT INTO tbluser (username, password, name) VALUES (?, ?, ?)")) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, name);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                getContentPane().removeAll(); // Clear the login panel
                createTableComponents(); // Show the login panel again
                revalidate();
                repaint();
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error registering user.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Clear the login panel
            createTableComponents();
            revalidate();
            repaint();
        });
        
        add(registerPanel, BorderLayout.CENTER);
     }
     private void createLoginPanel() {
        ImageIcon img = new ImageIcon("dist/img/survey.png");
        setIconImage(img.getImage());
        setTitle("Survey Application - Stocks Inventory by DNPH"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 230);
        //        setResizable(false);
              
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        JButton loginButton = new JButton("Login");

        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
             // Create JPanel for logo 
        try {
            myPicture = ImageIO.read(new File("dist/img/denso_logo.png"));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
        
        // Set the gridwidth to 2 to make it span across two columns
        gbc.gridwidth = 2;

        // Reset the gridx and gridy to 0 to position the logo at the top
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add the logo to the panel
        loginPanel.add(picLabel, gbc);

        // Reset the gridwidth to 1 for the rest of the components
        gbc.gridwidth = 1;
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 10); // Add margin
        
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        //gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            userinfo user = verifyUser(username, password);

            if (user != null) {
                // Show the main application window upon successful login
                JOptionPane.showMessageDialog(this, "Welcome to Stock Survey Report System!");
                showApp();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(loginPanel, BorderLayout.CENTER);
    }
     

    
    private userinfo verifyUser(String username, char[] password) {
    try {
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tbluser WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(new String(password), hashedPassword)) {
                        userinfo user = new userinfo();
                        user.setUsername(username);
                        // You can add more user-related information to the UserInfo object if needed
                        return user;
                    }
                }
            }
        }
        } catch (SQLException e) {
        }

        return null;
    }
    
    private void logout() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure?", "WARNING",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            getContentPane().removeAll(); // Clear the login panel
            createLoginPanel(); // Show the login panel again
            revalidate();
            repaint();
        }
        else{
        
        }
    }

    
    private void showApp() {
        getContentPane().removeAll();
        createTableComponents();
        // ... (add any other UI components you want to show)
        revalidate();
        repaint();
    }
    
   public static void main(String[] args) {

    // Continue with the Swing GUI setup
    SwingUtilities.invokeLater(() -> {
        App sqliteCRUDApp = new App();
        sqliteCRUDApp.createLoginPanel(); // Show the login panel initially
        sqliteCRUDApp.setVisible(true);
    });
}
}


