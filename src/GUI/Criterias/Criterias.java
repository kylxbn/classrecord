/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Criterias;

import GUI.Classes.Classes;
import DB.DB;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author orthocube
 */
public class Criterias extends javax.swing.JFrame {
    int id, caps, classid;
    
    /**
     * Creates new form EditCriterias
     */
    public Criterias(int i, int c, int cid) {
        initComponents();
        
        id = i;
        caps = c;
        classid = cid;
        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCriterias = new javax.swing.JTable();
        cmdAdd = new javax.swing.JButton();
        cmdEdit = new javax.swing.JButton();
        cmdDelete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblPercent = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblRemaining = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Criterias");

        tblCriterias.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCriterias);

        cmdAdd.setText("Add");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdEdit.setText("Edit");
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

        jLabel1.setText("Class Name:");

        lblName.setText("ERROR");

        jLabel2.setText("Total:");

        lblPercent.setText("jLabel3");

        jLabel3.setText("%");

        jLabel4.setText("Remaining:");

        lblRemaining.setText("jLabel5");

        jLabel5.setText("%");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPercent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRemaining)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAdd))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(lblName)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdAdd)
                    .addComponent(cmdEdit)
                    .addComponent(cmdDelete)
                    .addComponent(jLabel2)
                    .addComponent(lblPercent)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(lblRemaining)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        try {
            if (DB.isCollege(classid))
                new AddCriteria(id, caps, classid, this).setVisible(true);
            else
                new AddCriteriaSHS(id, caps, classid, this).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Criterias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdAddActionPerformed

    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        int crid;
        crid = tblCriterias.getSelectedRow();
        if (crid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a criteria to delete first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        crid = (int)tblCriterias.getValueAt(crid, 0);
        try {
            if (DB.isCollege(classid))
            {
                DB.deleteCriteria(crid);
                refresh();
            }
            else
            {
                DB.deleteCriteriaSHS(crid);
                refresh();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Criterias.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_cmdDeleteActionPerformed

    private void cmdEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditActionPerformed
        int crid;
        crid = tblCriterias.getSelectedRow();
        if (crid < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a criteria to edit first.", "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        crid = (int)tblCriterias.getValueAt(crid, 0);
        try {
            if (DB.isCollege(classid))
                new EditCriteria(id, caps, crid, this).setVisible(true);
            else
                new EditCriteriaSHS(id, caps, crid, this).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Criterias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdEditActionPerformed

    public void refresh()
    {
        try {
            if (DB.isCollege(classid))
            {
                tblCriterias.setModel(DB.getCriteriaTable(classid));
                int p = DB.totalCriteriaPercent(classid);
                lblPercent.setText(Integer.toString(p));
                lblRemaining.setText(Integer.toString(100 - p));
            }
            else
            {
                tblCriterias.setModel(DB.getCriteriaTableSHS(classid));
                int p = DB.totalCriteriaPercentSHS(classid);
                lblPercent.setText(Integer.toString(p));
                lblRemaining.setText(Integer.toString(100 - p));
            }
            lblName.setText(DB.getClassName(classid));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "An internal error has occurred:\n" + ex.getMessage(), "Internal error", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Criterias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdDelete;
    private javax.swing.JButton cmdEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPercent;
    private javax.swing.JLabel lblRemaining;
    private javax.swing.JTable tblCriterias;
    // End of variables declaration//GEN-END:variables
}
