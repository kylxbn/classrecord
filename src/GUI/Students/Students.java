/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Students;

import GUI.Students.EditStudent;
import GUI.Students.AddStudent;
import DB.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author orthocube
 */
public class Students extends javax.swing.JFrame {
    int id, caps;
    
    /**
     * Creates new form Students
     */
    public Students(int i, int c) {
        initComponents();
        id = i;
        caps = c;
        
        refresh();
        
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

        txtFN = new javax.swing.JTextField();
        txtLN = new javax.swing.JTextField();
        txtSID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmdAimai = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        cmdAdd = new javax.swing.JButton();
        cmdEdit = new javax.swing.JButton();
        cmdDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Students");

        txtFN.setToolTipText("");
        txtFN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFNActionPerformed(evt);
            }
        });
        txtFN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFNKeyTyped(evt);
            }
        });

        txtLN.setToolTipText("");
        txtLN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLNActionPerformed(evt);
            }
        });
        txtLN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLNKeyTyped(evt);
            }
        });

        txtSID.setToolTipText("");
        txtSID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSIDActionPerformed(evt);
            }
        });
        txtSID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSIDKeyTyped(evt);
            }
        });

        jLabel1.setText("ID:");

        jLabel2.setText("Last Name:");

        jLabel3.setText("First Name:");

        cmdAimai.setText("[...]");
        cmdAimai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAimaiActionPerformed(evt);
            }
        });

        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblStudents.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblStudentsMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblStudents);

        cmdAdd.setText("Add");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdEdit.setText("View / Edit");
        cmdEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditActionPerformed(evt);
            }
        });

        cmdDelete.setText("Delete");
        cmdDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFN, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLN, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSID, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmdAimai))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cmdDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAdd)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(cmdAimai))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdAdd)
                    .addComponent(cmdEdit)
                    .addComponent(cmdDelete))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFNKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFNKeyTyped
//        try {
//            tblStudents.setModel(DB.getStudentsTable(txtFN.getText(), txtLN.getText(), txtSID.getText()));
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
//            Logger.getLogger(Students.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtFNKeyTyped

    private void txtLNKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLNKeyTyped
//        try {
//            tblStudents.setModel(DB.getStudentsTable(txtFN.getText(), txtLN.getText(), txtSID.getText()));
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);            
//            Logger.getLogger(Students.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtLNKeyTyped

    private void txtSIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSIDKeyTyped
//        try {
//            tblStudents.setModel(DB.getStudentsTable(txtFN.getText(), txtLN.getText(), txtSID.getText()));
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
//            Logger.getLogger(Students.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtSIDKeyTyped

    private void tblStudentsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStudentsMouseReleased

    }//GEN-LAST:event_tblStudentsMouseReleased

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        new AddStudent(id, caps, this).setVisible(true);
    }//GEN-LAST:event_cmdAddActionPerformed

    private void cmdEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditActionPerformed
        int sid;
        sid = tblStudents.getSelectedRow();
        if (sid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a student to edit first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        new EditStudent(id, caps, (int)tblStudents.getValueAt(sid, 0), this).setVisible(true);
    }//GEN-LAST:event_cmdEditActionPerformed

    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        int sid;
        sid = tblStudents.getSelectedRow();
        if (sid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a student to delete first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        try {
            DB.deleteStudent((int)tblStudents.getValueAt(sid, 0));
            refresh();
            } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Students.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdDeleteActionPerformed

    private void txtFNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFNActionPerformed
        refresh();
    }//GEN-LAST:event_txtFNActionPerformed

    private void cmdAimaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAimaiActionPerformed
        refresh();
    }//GEN-LAST:event_cmdAimaiActionPerformed

    private void txtLNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLNActionPerformed
        refresh();
    }//GEN-LAST:event_txtLNActionPerformed

    private void txtSIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSIDActionPerformed
        refresh();
    }//GEN-LAST:event_txtSIDActionPerformed

    public void refresh()
    {
        try {
            tblStudents.setModel(DB.getStudentsTable(txtFN.getText(), txtLN.getText(), txtSID.getText()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Students.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdAimai;
    private javax.swing.JButton cmdDelete;
    private javax.swing.JButton cmdEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStudents;
    private javax.swing.JTextField txtFN;
    private javax.swing.JTextField txtLN;
    private javax.swing.JTextField txtSID;
    // End of variables declaration//GEN-END:variables
}
