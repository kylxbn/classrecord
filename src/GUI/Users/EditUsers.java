/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Users;

import Data.*;
import DB.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author orthocube
 */
public class EditUsers extends javax.swing.JFrame {    
    int id, caps;
    
    /**
     * Creates new form EditUsers
     * @param i User ID
     * @param c User Permissions
     */
    public EditUsers(int i, int c) {
        initComponents();
        id = i;
        caps = c;
        
        ArrayList<User> users = null;
        try {
            users = DB.getUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DefaultListModel l = new DefaultListModel();
        for (User u : users)
        {
            l.addElement(u.getUsername());
        }
        lstUsers.setModel(l);
        
        if ((c&4)==0)
        {
            cmdAdd.setEnabled(false);
            cmdRemove.setEnabled(false);
            jPanel1.setEnabled(false);
            chkUser.setEnabled(false);
            chkEdit.setEnabled(false);
        }
        
        User u = null;
        try {
            u = DB.getUser(i);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtID.setText(Integer.toString(u.getID()));
        txtUsername.setText(u.getUsername());
        txtPassword.setText(u.getPassword());
        if ((caps&4)==4)
            chkUser.setSelected(true);
        else
            chkUser.setSelected(false);
        
        if ((caps&2)==2)
            chkEdit.setSelected(true);
        else
            chkEdit.setSelected(false);
        
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstUsers = new javax.swing.JList<>();
        txtUsername = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkUser = new javax.swing.JCheckBox();
        chkEdit = new javax.swing.JCheckBox();
        cmdSave = new javax.swing.JButton();
        chkVisible = new javax.swing.JCheckBox();
        txtPassword = new javax.swing.JPasswordField();
        txtPassword2 = new javax.swing.JPasswordField();
        txtID = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmdAdd = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Users");

        lstUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstUsersValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstUsers);

        jLabel1.setText("Username:");

        jLabel2.setText("Password:");

        jLabel3.setText("(Repeat):");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Permissions:"));

        chkUser.setText("User editing");

        chkEdit.setText("Data editing");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUser)
                    .addComponent(chkEdit))
                .addContainerGap(163, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEdit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cmdSave.setText("Update");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        chkVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVisibleActionPerformed(evt);
            }
        });

        txtID.setEditable(false);

        jLabel4.setText("ID:");

        cmdAdd.setText("Add");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdRemove.setText("Remove");
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtID)
                                    .addComponent(txtUsername)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtPassword)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(chkVisible))
                                    .addComponent(txtPassword2)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmdRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(chkVisible))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdSave)
                    .addComponent(cmdAdd)
                    .addComponent(cmdRemove))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVisibleActionPerformed
        if (chkVisible.isSelected())
        {
            txtPassword.setEchoChar('\u0000');
            txtPassword2.setVisible(false);
            jLabel3.setVisible(false);
        }        
        else
        {
            txtPassword.setEchoChar('\u2022');
            txtPassword2.setVisible(true);
            jLabel3.setVisible(true);
        }
    }//GEN-LAST:event_chkVisibleActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        try {
            if (DB.userExists(Integer.parseInt(txtID.getText()), txtUsername.getText()) == 0)
            { 
                JOptionPane.showMessageDialog(this, "User does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if (Integer.parseInt(txtID.getText()) == id)
            {
                JOptionPane.showMessageDialog(this, "You cannot delete yourself.\nLog in with another account first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                if (DB.removeUser(Integer.parseInt(txtID.getText())))
                {
                    JOptionPane.showMessageDialog(this, "User deleted", "OK", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Deleting this would remove the only administrator account", "Error", JOptionPane.ERROR_MESSAGE);               
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdRemoveActionPerformed

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        if (!String.valueOf(txtPassword.getPassword()).equals(String.valueOf(txtPassword2.getPassword())))
        {
            if (!chkVisible.isSelected())
            {
                JOptionPane.showMessageDialog(this, "Password confirmation does not match", "Error", JOptionPane.ERROR_MESSAGE); 
                return;
            }
        }
        if (txtUsername.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Username cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);         
            return;
        }
        if (String.valueOf(txtPassword.getPassword()).trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Password cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);         
            return;
        }
        try {
            if (DB.addUser(txtUsername.getText(), String.valueOf(txtPassword.getPassword()), chkUser.isSelected(), chkEdit.isSelected()))
                JOptionPane.showMessageDialog(this, "User already exists", "Error", JOptionPane.ERROR_MESSAGE); 
            else
            {
                JOptionPane.showMessageDialog(this, "New user added", "OK", JOptionPane.INFORMATION_MESSAGE); 
                this.dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdAddActionPerformed

    private void lstUsersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstUsersValueChanged
        if ((caps&4)==0)
        {
            JOptionPane.showMessageDialog(this, "You can only view your own account.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        
        String sel = lstUsers.getSelectedValue();
        User u = null;
        try {
            u = DB.getUser(sel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtID.setText(Integer.toString(u.getID()));
        txtUsername.setText(u.getUsername());
        txtPassword.setText(u.getPassword());
        int c = u.getCaps();
        if ((c&4)==4)
            chkUser.setSelected(true);
        else
            chkUser.setSelected(false);
        
        if ((c&2)==2)
            chkEdit.setSelected(true);
        else
            chkEdit.setSelected(false);
        
        
    }//GEN-LAST:event_lstUsersValueChanged

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        try {
            if (DB.userExists(Integer.parseInt(txtID.getText())) > 0)
            {
                if ((caps&4)== 0)
                    if (Integer.parseInt(txtID.getText())!=id)
                    {
                        JOptionPane.showMessageDialog(this, "You can't update an account that isn't yours.", "Error", JOptionPane.ERROR_MESSAGE); 
                        return;
                    }
                
                if (!String.valueOf(txtPassword.getPassword()).equals(String.valueOf(txtPassword2.getPassword())))
                {
                    if (!chkVisible.isSelected())
                    {
                        JOptionPane.showMessageDialog(this, "Password confirmation does not match", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (txtUsername.getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(this, "Username cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (String.valueOf(txtPassword.getPassword()).trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(this, "Password cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (DB.updateUser(Integer.parseInt(txtID.getText()), txtUsername.getText(), String.valueOf(txtPassword.getPassword()), chkUser.isSelected(), chkEdit.isSelected()))
                    JOptionPane.showMessageDialog(this, "That ID does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                else
                {
                    JOptionPane.showMessageDialog(this, "User updated", "OK", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "You can't update a non-existent user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(EditUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkEdit;
    private javax.swing.JCheckBox chkUser;
    private javax.swing.JCheckBox chkVisible;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JButton cmdSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> lstUsers;
    private javax.swing.JTextField txtID;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPassword2;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
