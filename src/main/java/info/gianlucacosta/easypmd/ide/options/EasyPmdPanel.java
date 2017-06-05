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

import info.gianlucacosta.easypmd.ide.DialogService;
import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.profiles.DefaultProfile;
import info.gianlucacosta.easypmd.ide.options.profiles.DefaultProfileContext;
import info.gianlucacosta.easypmd.ide.options.profiles.Profile;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileException;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileMap;
import info.gianlucacosta.easypmd.pmdscanner.messages.cache.ScanMessagesCache;
import info.gianlucacosta.helios.application.io.CommonQuestionOutcome;
import info.gianlucacosta.helios.product.ProductInfoService;
import info.gianlucacosta.helios.regex.OsSpecificPathCompositeRegex;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RulePriority;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileContext;

/**
 * The plugin's panel shown in the Options dialog
 */
class EasyPmdPanel extends JPanel {

    private final ProductInfoService pluginInfoService;
    private final DialogService dialogService;
    private final OptionsFactory optionsFactory;
    private final OptionsService optionsService;
    private final ScanMessagesCache scanMessagesCache;
    private ProfileMap profiles;
    private String activeProfileName;
    private final ActionListener profileComboActionListener;
    private boolean refillingProfileCombo;

    EasyPmdPanel() {
        pluginInfoService = Injector.lookup(ProductInfoService.class);
        optionsService = Injector.lookup(OptionsService.class);
        scanMessagesCache = Injector.lookup(ScanMessagesCache.class);

        initComponents();

        pathFilteringScrollPane.getVerticalScrollBar().setUnitIncrement(300);

        dialogService = Injector.lookup(DialogService.class);
        optionsFactory = Injector.lookup(OptionsFactory.class);

        minimumPriorityCombo.setModel(new RulePriorityComboBoxModel());

        profileComboActionListener = (ActionEvent e) -> {
            if (refillingProfileCombo) {
                return;
            }

            String selectedProfileName = (String) profileCombo.getSelectedItem();

            String oldProfileName = EasyPmdPanel.this.activeProfileName;
            EasyPmdPanel.this.activeProfileName = selectedProfileName;

            updateOptionsControls(oldProfileName);
        };

        ImageIcon pluginIcon = new ImageIcon(getClass().getResource("/info/gianlucacosta/easypmd/mainIcon128.png"));

        pluginIconPicture.setIcon(pluginIcon);
        pluginIconPicture.setText("");

        pluginTitleLabel.setText(
                String.format("%s %s",
                        pluginInfoService.getName(),
                        pluginInfoService.getVersion()
                )
        );

        pmdVersionLabel.setText(
                String.format("PMD version %s", PMD.VERSION)
        );
    }

    private Options getOptions() {
        DefaultOptions result = new DefaultOptions();

        result.setTargetJavaVersion(targetJavaVersionField.getText().trim());
        result.setSourceFileEncoding(sourceFileEncodingField.getText().trim());
        result.setSuppressMarker(suppressMarkerField.getText().trim());
        result.setMinimumPriority((RulePriority) minimumPriorityCombo.getSelectedItem());

        result.setAdditionalClassPathUrls(additionalClasspathPanel.getAdditionalClassPathUrls());
        result.setRuleSets(ruleSetsPanel.getRuleSets());

        result.setUseScanMessagesCache(useScanMessagesCacheCheckBox.isSelected());

        result.setShowRulePriorityInTasks(showRulePriorityInTasksCheckBox.isSelected());
        result.setShowDescriptionInTasks(showDescriptionInTasksCheckBox.isSelected());
        result.setShowRuleInTasks(showRuleInTasksCheckBox.isSelected());
        result.setShowRuleSetInTasks(showRuleSetInTasksCheckBox.isSelected());
        result.setShowAnnotationsInEditor(showAnnotationsInEditorCheckBox.isSelected());
        result.setShowAllMessagesInGuardedSections(showAllMessagesInGuardedSectionsCheckBox.isSelected());

        result.setPathFilteringOptions(
                new PathFilteringOptions(
                        new OsSpecificPathCompositeRegex(pathFilteringPanel.getIncludedPathRegexes()),
                        new OsSpecificPathCompositeRegex(pathFilteringPanel.getExcludedPathRegexes())));

        result.setAuxiliaryClassPath(auxiliaryPathField.getText().trim());

        return result;
    }

    synchronized ProfileContextDTO getProfileContextDTO() {
        updateOptionsInActiveProfile();

        ProfileContextDTO result = new ProfileContextDTO();

        result.setProfileContext(new DefaultProfileContext(profiles, activeProfileName)
        );

        return result;
    }

    private void updateOptionsInActiveProfile() {
        Options activeOptions = getOptions();
        Profile activeProfile = new DefaultProfile(activeOptions);

        try {
            profiles.setProfile(activeProfileName, activeProfile);
        } catch (ProfileException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void refillProfileCombo() {
        refillingProfileCombo = true;

        try {
            profileCombo.removeAllItems();
            profiles.getProfileNames().forEach(profileName -> {
                profileCombo.addItem(profileName);
            });
        } finally {
            refillingProfileCombo = false;
        }
    }

    private void updateProfileButtons() {
        removeProfileButton.setEnabled(profiles.getProfileNames().size() > 1);
    }

    private void updateOptionsControls(String oldProfileName) {
        if (activeProfileName.equals(oldProfileName)) {
            return;
        }

        if (profiles.profileNameExists(oldProfileName)) {
            Options oldProfileOptions = getOptions();

            Profile oldProfile = new DefaultProfile(oldProfileOptions);
            try {
                profiles.setProfile(oldProfileName, oldProfile);
            } catch (ProfileException ex) {
                throw new RuntimeException(ex);
            }
        }

        Profile activeProfile = profiles.getProfile(activeProfileName);
        setOptions(activeProfile.getOptions());
    }

    synchronized void setProfileContext(ProfileContext profileContext) {
        this.activeProfileName = profileContext.getActiveProfileName();

        profiles = profileContext.getProfiles();

        profiles.addProfileNamesChangedListener(() -> {
            refillProfileCombo();
            updateProfileButtons();
        });

        profileCombo.removeActionListener(profileComboActionListener);
        refillProfileCombo();
        updateProfileButtons();

        profileCombo.addActionListener(profileComboActionListener);
        profileCombo.setSelectedItem(activeProfileName);

        this.activeProfileName = profileContext.getActiveProfileName();
        Options initialOptions = profileContext.getActiveOptions();
        setOptions(initialOptions);
    }

    private void setOptions(Options options) {
        targetJavaVersionField.setText(options.getTargetJavaVersion());
        sourceFileEncodingField.setText(options.getSourceFileEncoding());
        suppressMarkerField.setText(options.getSuppressMarker());
        minimumPriorityCombo.setSelectedItem(options.getMinimumPriority());

        additionalClasspathPanel.setAdditionalClasspathUrls(options.getAdditionalClassPathUrls());
        ruleSetsPanel.setRuleSets(options.getRuleSets());

        useScanMessagesCacheCheckBox.setSelected(options.isUseScanMessagesCache());

        showRulePriorityInTasksCheckBox.setSelected(options.isShowRulePriorityInTasks());
        showDescriptionInTasksCheckBox.setSelected(options.isShowDescriptionInTasks());
        showRuleInTasksCheckBox.setSelected(options.isShowRuleInTasks());
        showRuleSetInTasksCheckBox.setSelected(options.isShowRuleSetInTasks());
        showAnnotationsInEditorCheckBox.setSelected(options.isShowAnnotationsInEditor());
        showAllMessagesInGuardedSectionsCheckBox.setSelected(options.isShowAllMessagesInGuardedSections());

        pathFilteringPanel.setIncludedPathRegexes(options.getPathFilteringOptions().getIncludedPathCompositeRegex().getSubRegexes());
        pathFilteringPanel.setExcludedPathRegexes(options.getPathFilteringOptions().getExcludedPathCompositeRegex().getSubRegexes());

        auxiliaryPathField.setText(options.getAuxiliaryClassPath());
    }

    private boolean clearScanMessagesScache() {
        return scanMessagesCache.clear();
    }

    private void verifyOptions() throws InvalidOptionsException {
        optionsService.verifyOptions(getOptions());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        profilePanel = new javax.swing.JPanel();
        profileLabel = new javax.swing.JLabel();
        profileCombo = new javax.swing.JComboBox();
        duplicateProfileButton = new javax.swing.JButton();
        renameProfileButton = new javax.swing.JButton();
        removeProfileButton = new javax.swing.JButton();
        optionsTabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        targetJavaVersionLabel = new javax.swing.JLabel();
        targetJavaVersionField = new javax.swing.JTextField();
        sourceFileEncodingLabel = new javax.swing.JLabel();
        sourceFileEncodingField = new javax.swing.JTextField();
        suppressMarkerLabel = new javax.swing.JLabel();
        suppressMarkerField = new javax.swing.JTextField();
        minimumPriorityLabel = new javax.swing.JLabel();
        minimumPriorityCombo = new javax.swing.JComboBox();
        mainAdditionalClasspathPanel = new javax.swing.JPanel();
        additionalClasspathPanel = new info.gianlucacosta.easypmd.ide.options.AdditionalClasspathPanel();
        mainRuleSetsPanel = new javax.swing.JPanel();
        ruleSetsPanel = new info.gianlucacosta.easypmd.ide.options.RuleSetsPanel();
        cachePanel = new javax.swing.JPanel();
        useScanMessagesCacheCheckBox = new javax.swing.JCheckBox();
        clearScanMessagesCacheButton = new javax.swing.JButton();
        reportingPanel = new javax.swing.JPanel();
        showDescriptionInTasksCheckBox = new javax.swing.JCheckBox();
        showRuleInTasksCheckBox = new javax.swing.JCheckBox();
        showRuleSetInTasksCheckBox = new javax.swing.JCheckBox();
        showAnnotationsInEditorCheckBox = new javax.swing.JCheckBox();
        showAllMessagesInGuardedSectionsCheckBox = new javax.swing.JCheckBox();
        showRulePriorityInTasksCheckBox = new javax.swing.JCheckBox();
        mainPathFilteringPanel = new javax.swing.JPanel();
        pathFilteringScrollPane = new javax.swing.JScrollPane();
        pathFilteringPanel = new info.gianlucacosta.easypmd.ide.options.PathFilteringPanel();
        miscPanel = new javax.swing.JPanel();
        auxiliaryClassPathLabel = new javax.swing.JLabel();
        auxiliaryPathField = new javax.swing.JTextField();
        infoPanel = new javax.swing.JPanel();
        pluginIconPicture = new javax.swing.JLabel();
        pluginTitleLabel = new javax.swing.JLabel();
        pmdVersionLabel = new javax.swing.JLabel();
        showHomePageButton = new javax.swing.JButton();
        showFacebookPageButton = new javax.swing.JButton();
        optionButtonsPanel = new javax.swing.JPanel();
        resetOptionsButton = new javax.swing.JButton();
        verifyOptionsButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        profilePanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(profileLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.profileLabel.text")); // NOI18N
        profilePanel.add(profileLabel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        profilePanel.add(profileCombo, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(duplicateProfileButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.duplicateProfileButton.text")); // NOI18N
        duplicateProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicateProfileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        profilePanel.add(duplicateProfileButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(renameProfileButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.renameProfileButton.text")); // NOI18N
        renameProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameProfileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        profilePanel.add(renameProfileButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeProfileButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.removeProfileButton.text")); // NOI18N
        removeProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeProfileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        profilePanel.add(removeProfileButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(profilePanel, gridBagConstraints);

        optionsTabbedPane.setPreferredSize(new java.awt.Dimension(600, 300));

        generalPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(targetJavaVersionLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.targetJavaVersionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(targetJavaVersionLabel, gridBagConstraints);

        targetJavaVersionField.setText(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.sourceFileEncodingField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(targetJavaVersionField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(sourceFileEncodingLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.sourceFileEncodingLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(sourceFileEncodingLabel, gridBagConstraints);

        sourceFileEncodingField.setText(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.sourceFileEncodingField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(sourceFileEncodingField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(suppressMarkerLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.suppressMarkerLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(suppressMarkerLabel, gridBagConstraints);

        suppressMarkerField.setText(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.sourceFileEncodingField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(suppressMarkerField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(minimumPriorityLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.minimumPriorityLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(minimumPriorityLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        generalPanel.add(minimumPriorityCombo, gridBagConstraints);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.generalPanel.TabConstraints.tabTitle"), generalPanel); // NOI18N

        mainAdditionalClasspathPanel.setLayout(new java.awt.BorderLayout());

        additionalClasspathPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        mainAdditionalClasspathPanel.add(additionalClasspathPanel, java.awt.BorderLayout.CENTER);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.mainAdditionalClasspathPanel.TabConstraints.tabTitle"), mainAdditionalClasspathPanel); // NOI18N

        mainRuleSetsPanel.setLayout(new java.awt.BorderLayout());

        ruleSetsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        mainRuleSetsPanel.add(ruleSetsPanel, java.awt.BorderLayout.CENTER);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.mainRuleSetsPanel.TabConstraints.tabTitle"), mainRuleSetsPanel); // NOI18N

        cachePanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(useScanMessagesCacheCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.useScanMessagesCacheCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        cachePanel.add(useScanMessagesCacheCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(clearScanMessagesCacheButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.clearScanMessagesCacheButton.text")); // NOI18N
        clearScanMessagesCacheButton.setPreferredSize(new java.awt.Dimension(107, 30));
        clearScanMessagesCacheButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearScanMessagesCacheButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        cachePanel.add(clearScanMessagesCacheButton, gridBagConstraints);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.cachePanel.TabConstraints.tabTitle"), cachePanel); // NOI18N

        reportingPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(showDescriptionInTasksCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showDescriptionInTasksCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showDescriptionInTasksCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(showRuleInTasksCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showRuleInTasksCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showRuleInTasksCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(showRuleSetInTasksCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showRuleSetInTasksCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showRuleSetInTasksCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(showAnnotationsInEditorCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showAnnotationsInEditorCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showAnnotationsInEditorCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(showAllMessagesInGuardedSectionsCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showAllMessagesInGuardedSectionsCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showAllMessagesInGuardedSectionsCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(showRulePriorityInTasksCheckBox, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showRulePriorityInTasksCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        reportingPanel.add(showRulePriorityInTasksCheckBox, gridBagConstraints);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.reportingPanel.TabConstraints.tabTitle"), reportingPanel); // NOI18N

        mainPathFilteringPanel.setLayout(new java.awt.BorderLayout());

        pathFilteringScrollPane.setHorizontalScrollBar(null);

        pathFilteringPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        pathFilteringScrollPane.setViewportView(pathFilteringPanel);

        mainPathFilteringPanel.add(pathFilteringScrollPane, java.awt.BorderLayout.CENTER);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.mainPathFilteringPanel.TabConstraints.tabTitle"), mainPathFilteringPanel); // NOI18N

        miscPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(auxiliaryClassPathLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.auxiliaryClassPathLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        miscPanel.add(auxiliaryClassPathLabel, gridBagConstraints);

        auxiliaryPathField.setText(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.auxiliaryPathField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        miscPanel.add(auxiliaryPathField, gridBagConstraints);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.miscPanel.TabConstraints.tabTitle"), miscPanel); // NOI18N

        infoPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(pluginIconPicture, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.pluginIconPicture.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 4, 16);
        infoPanel.add(pluginIconPicture, gridBagConstraints);

        pluginTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pluginTitleLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.pluginTitleLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 16, 8);
        infoPanel.add(pluginTitleLabel, gridBagConstraints);

        pmdVersionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pmdVersionLabel, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.pmdVersionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 24, 16);
        infoPanel.add(pmdVersionLabel, gridBagConstraints);

        showHomePageButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showHomePageButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showHomePageButton.text")); // NOI18N
        showHomePageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHomePageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        infoPanel.add(showHomePageButton, gridBagConstraints);

        showFacebookPageButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showFacebookPageButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.showFacebookPageButton.text")); // NOI18N
        showFacebookPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFacebookPageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        infoPanel.add(showFacebookPageButton, gridBagConstraints);

        optionsTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.infoPanel.TabConstraints.tabTitle"), infoPanel); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(optionsTabbedPane, gridBagConstraints);

        optionButtonsPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(resetOptionsButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.resetOptionsButton.text")); // NOI18N
        resetOptionsButton.setPreferredSize(new java.awt.Dimension(101, 30));
        resetOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetOptionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        optionButtonsPanel.add(resetOptionsButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(verifyOptionsButton, org.openide.util.NbBundle.getMessage(EasyPmdPanel.class, "EasyPmdPanel.verifyOptionsButton.text")); // NOI18N
        verifyOptionsButton.setPreferredSize(new java.awt.Dimension(101, 30));
        verifyOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyOptionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        optionButtonsPanel.add(verifyOptionsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(optionButtonsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void clearScanMessagesCacheButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearScanMessagesCacheButtonActionPerformed
        if (clearScanMessagesScache()) {
            dialogService.showInfo("The cache has been correctly cleared");
        } else {
            dialogService.showWarning("The cache might have been only partially cleared");
        }
    }//GEN-LAST:event_clearScanMessagesCacheButtonActionPerformed

    private void verifyOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyOptionsButtonActionPerformed
        try {
            verifyOptions();
            dialogService.showInfo("Your EasyPmd options seem to be correct.");
        } catch (InvalidOptionsException ex) {
            dialogService.showWarning(String.format("The current EasyPmd options appear to be incorrect.\n%s", ex.getMessage()));
        }
    }//GEN-LAST:event_verifyOptionsButtonActionPerformed

    private void resetOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetOptionsButtonActionPerformed
        Options defaultOptions = optionsFactory.createDefaultOptions();

        setOptions(defaultOptions);

        dialogService.showInfo("The default options have been restored in the dialog controls.\nTo save them, please confirm the options dialog.");
    }//GEN-LAST:event_resetOptionsButtonActionPerformed

    private void renameProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameProfileButtonActionPerformed
        String currentName = activeProfileName;

        String newName = dialogService.askForString("New profile name:", currentName);
        if (newName == null) {
            return;
        }

        try {
            updateOptionsInActiveProfile();
            profiles.renameProfile(currentName, newName);
        } catch (ProfileException ex) {
            dialogService.showWarning(ex.getMessage());
            return;
        }

        profileCombo.setSelectedItem(newName);
    }//GEN-LAST:event_renameProfileButtonActionPerformed

    private void duplicateProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateProfileButtonActionPerformed
        String newName = dialogService.askForString("New profile name:");
        if (newName == null) {
            return;
        }

        String sourceName = activeProfileName;

        try {
            updateOptionsInActiveProfile();
            profiles.duplicateProfile(sourceName, newName);
        } catch (ProfileException ex) {
            dialogService.showWarning(ex.getMessage());
            return;
        }

        profileCombo.setSelectedItem(newName);
    }//GEN-LAST:event_duplicateProfileButtonActionPerformed

    private void removeProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeProfileButtonActionPerformed
        if (dialogService.askYesNoQuestion("Do you really wish to delete the selected profile?") != CommonQuestionOutcome.YES) {
            return;
        }

        try {
            profiles.removeProfile(activeProfileName);
        } catch (ProfileException ex) {
            dialogService.showWarning(ex.getMessage());
            return;
        }

        profileCombo.setSelectedIndex(0);
    }//GEN-LAST:event_removeProfileButtonActionPerformed

    private void showHomePageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHomePageButtonActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(pluginInfoService.getWebsite()));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }//GEN-LAST:event_showHomePageButtonActionPerformed

    private void showFacebookPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showFacebookPageButtonActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(pluginInfoService.getFacebookPage()));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }//GEN-LAST:event_showFacebookPageButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private info.gianlucacosta.easypmd.ide.options.AdditionalClasspathPanel additionalClasspathPanel;
    private javax.swing.JLabel auxiliaryClassPathLabel;
    private javax.swing.JTextField auxiliaryPathField;
    private javax.swing.JPanel cachePanel;
    private javax.swing.JButton clearScanMessagesCacheButton;
    private javax.swing.JButton duplicateProfileButton;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel mainAdditionalClasspathPanel;
    private javax.swing.JPanel mainPathFilteringPanel;
    private javax.swing.JPanel mainRuleSetsPanel;
    private javax.swing.JComboBox minimumPriorityCombo;
    private javax.swing.JLabel minimumPriorityLabel;
    private javax.swing.JPanel miscPanel;
    private javax.swing.JPanel optionButtonsPanel;
    private javax.swing.JTabbedPane optionsTabbedPane;
    private info.gianlucacosta.easypmd.ide.options.PathFilteringPanel pathFilteringPanel;
    private javax.swing.JScrollPane pathFilteringScrollPane;
    private javax.swing.JLabel pluginIconPicture;
    private javax.swing.JLabel pluginTitleLabel;
    private javax.swing.JLabel pmdVersionLabel;
    private javax.swing.JComboBox profileCombo;
    private javax.swing.JLabel profileLabel;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JButton removeProfileButton;
    private javax.swing.JButton renameProfileButton;
    private javax.swing.JPanel reportingPanel;
    private javax.swing.JButton resetOptionsButton;
    private info.gianlucacosta.easypmd.ide.options.RuleSetsPanel ruleSetsPanel;
    private javax.swing.JCheckBox showAllMessagesInGuardedSectionsCheckBox;
    private javax.swing.JCheckBox showAnnotationsInEditorCheckBox;
    private javax.swing.JCheckBox showDescriptionInTasksCheckBox;
    private javax.swing.JButton showFacebookPageButton;
    private javax.swing.JButton showHomePageButton;
    private javax.swing.JCheckBox showRuleInTasksCheckBox;
    private javax.swing.JCheckBox showRulePriorityInTasksCheckBox;
    private javax.swing.JCheckBox showRuleSetInTasksCheckBox;
    private javax.swing.JTextField sourceFileEncodingField;
    private javax.swing.JLabel sourceFileEncodingLabel;
    private javax.swing.JTextField suppressMarkerField;
    private javax.swing.JLabel suppressMarkerLabel;
    private javax.swing.JTextField targetJavaVersionField;
    private javax.swing.JLabel targetJavaVersionLabel;
    private javax.swing.JCheckBox useScanMessagesCacheCheckBox;
    private javax.swing.JButton verifyOptionsButton;
    // End of variables declaration//GEN-END:variables
}
