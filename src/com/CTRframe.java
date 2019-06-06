/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import ilog.cp.IloCP;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author WaMa
 */
public class CTRframe extends javax.swing.JFrame {

    private IloCP cp;

    public void setCp(IloCP cp) {
        this.cp = cp;
    }
    private long st;

    public void setSt(long st) {
        this.st = st;
    }
    /**
     * Creates new form CTRframe
     */
    public CTRframe() {
        initComponents();
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                long currtim = (System.currentTimeMillis() - st) / 1000;
                int min = (int) currtim / 60;
                int secs = (int) currtim % 60;
                String stringtim = String.format("%d:%02ds", min, secs);
                jlTimer.setText(stringtim);
            }
        });
        timer.start();
    }

    public void dispMsg(String msg) {
        this.jtaMsg.append(msg);
    }

    public void setEtap(String msg) {
        jlEtap.setText(msg);
    }
    
    public void buttonEnable(boolean b) {
        jbCancel.setEnabled(b);
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
        jbCancel = new javax.swing.JButton();
        jlTimer = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaMsg = new javax.swing.JTextArea();
        jlEtap = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Planowanie Obron");
        setLocationByPlatform(true);
        setResizable(false);

        jbCancel.setText("Przerwij");
        jbCancel.setEnabled(false);
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jlTimer.setText("0:00s");

        jtaMsg.setColumns(20);
        jtaMsg.setRows(5);
        jScrollPane1.setViewportView(jtaMsg);

        jlEtap.setText("Etap 1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbCancel)
                        .addGap(111, 111, 111)
                        .addComponent(jlEtap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                        .addComponent(jlTimer)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jlTimer)
                    .addComponent(jlEtap))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        cp.abortSearch();
    }//GEN-LAST:event_jbCancelActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JLabel jlEtap;
    private javax.swing.JLabel jlTimer;
    private javax.swing.JTextArea jtaMsg;
    // End of variables declaration//GEN-END:variables
}
