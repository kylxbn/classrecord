/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.StudentsInClass;

import DB.DB;
import GUI.Classes.Classes;
import GUI.Students.EditStudent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author orthocube
 */
public class ViewEnrollees extends javax.swing.JFrame {
    int id, caps, classid;
    Classes caller;
    
    /**
     * Creates new form ViewStudentsInClass
     */
    public ViewEnrollees(int i, int c, int ci, Classes ca) {
        initComponents();
        
        id = i;
        caps = c;
        classid = ci;
        caller = ca;
        
        try {
            Data.Class cl = DB.getClass(classid);
            lblName.setText(cl.getName());
            lblYear.setText(Integer.toString(cl.getYear()));
            lblSY.setText(Integer.toString(cl.getSchoolYear()));
            lblSem.setText(Integer.toString(cl.getSem()));
            
            tblStudents.setModel(DB.getEnrollees(classid));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(ViewEnrollees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblYear = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblSY = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblSem = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        cmdAdd = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();
        cmdEdit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enrollees");

        jLabel1.setText("Class name:");

        lblName.setText("ERROR");

        jLabel3.setText("Year:");

        lblYear.setText("ERROR");

        jLabel2.setText("School Year:");

        lblSY.setText("ERROR");

        jLabel5.setText("Semester:");

        lblSem.setText("ERROR");

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
        jScrollPane1.setViewportView(tblStudents);

        cmdAdd.setText("Add");

        cmdRemove.setText("Remove");
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });

        cmdEdit.setText("View / Edit");
        cmdEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cmdRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAdd))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblYear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblYear)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblSY))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblSem))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdAdd)
                    .addComponent(cmdRemove)
                    .addComponent(cmdEdit))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditActionPerformed
        int sid;
        sid = tblStudents.getSelectedRow();
        if (sid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select an enrollee to edit first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        sid = (int)tblStudents.getValueAt(sid, 0);
        try {
            if (DB.isCollege(classid))
            {
                new EditEnrollee(id, caps, sid, this).setVisible(true);
            }
            else
            {
                new EditSHSEnrollee(id, caps, sid, this).setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(ViewEnrollees.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_cmdEditActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        int sid;
        sid = tblStudents.getSelectedRow();
        if (sid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select an enrollee to remove first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        sid = (int)tblStudents.getValueAt(sid, 0);
        try {
            if (DB.isCollege(classid))
            {
                DB.deleteEnrollee(sid);
            }
            else
            {
                DB.deleteSHSEnrollee(sid);
            }
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(ViewEnrollees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdRemoveActionPerformed

    public void refresh()
    {
        try {
            Data.Class cl = DB.getClass(classid);
            lblName.setText(cl.getName());
            lblYear.setText(Integer.toString(cl.getYear()));
            lblSY.setText(Integer.toString(cl.getSchoolYear()));
            lblSem.setText(Integer.toString(cl.getSem()));
            
            tblStudents.setModel(DB.getEnrollees(classid));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(ViewEnrollees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdEdit;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSY;
    private javax.swing.JLabel lblSem;
    private javax.swing.JLabel lblYear;
    private javax.swing.JTable tblStudents;
    // End of variables declaration//GEN-END:variables
}