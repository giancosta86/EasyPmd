/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2017 Gianluca Costa
 * ===========================================================================%%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ==========================================================================%##
 */
package info.gianlucacosta.easypmd.ide.options;

import javax.swing.*;
import java.util.Collection;

/**
 * Panel dedicated to edit path filtering options
 */
public class PathFilteringPanel extends JPanel {

    /**
     * Creates new form PathFilteringPanel
     */
    public PathFilteringPanel() {
        initComponents();
    }

    public Collection<String> getIncludedPathRegexes() {
        return includedPathRegexesPanel.getRegexes();
    }

    public void setIncludedPathRegexes(Collection<String> includedPathRegexes) {
        includedPathRegexesPanel.setRegexes(includedPathRegexes);
    }

    public Collection<String> getExcludedPathRegexes() {
        return excludedPathRegexesPanel.getRegexes();
    }

    public void setExcludedPathRegexes(Collection<String> excludedPathRegexes) {
        excludedPathRegexesPanel.setRegexes(excludedPathRegexes);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        includedPathsPanelContainer = new javax.swing.JPanel();
        includedPathRegexesPanel = new info.gianlucacosta.easypmd.ide.options.regexes.RegexesPanel();
        excludedPathsPanelContainer = new javax.swing.JPanel();
        excludedPathRegexesPanel = new info.gianlucacosta.easypmd.ide.options.regexes.RegexesPanel();

        setLayout(new java.awt.GridLayout(2, 1));

        includedPathsPanelContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PathFilteringPanel.class, "PathFilteringPanel.includedPathsPanelContainer.border.title"))); // NOI18N
        includedPathsPanelContainer.setLayout(new java.awt.BorderLayout());

        includedPathRegexesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        includedPathsPanelContainer.add(includedPathRegexesPanel, java.awt.BorderLayout.CENTER);

        add(includedPathsPanelContainer);

        excludedPathsPanelContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PathFilteringPanel.class, "PathFilteringPanel.excludedPathsPanelContainer.border.title"))); // NOI18N
        excludedPathsPanelContainer.setLayout(new java.awt.BorderLayout());

        excludedPathRegexesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        excludedPathsPanelContainer.add(excludedPathRegexesPanel, java.awt.BorderLayout.CENTER);

        add(excludedPathsPanelContainer);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private info.gianlucacosta.easypmd.ide.options.regexes.RegexesPanel excludedPathRegexesPanel;
    private javax.swing.JPanel excludedPathsPanelContainer;
    private info.gianlucacosta.easypmd.ide.options.regexes.RegexesPanel includedPathRegexesPanel;
    private javax.swing.JPanel includedPathsPanelContainer;
    // End of variables declaration//GEN-END:variables
}
