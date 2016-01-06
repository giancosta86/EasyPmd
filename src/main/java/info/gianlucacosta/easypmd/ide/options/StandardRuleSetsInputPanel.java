/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2016 Gianluca Costa
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

import info.gianlucacosta.easypmd.pmdscanner.RuleSetWrapper;
import info.gianlucacosta.easypmd.pmdscanner.StandardRuleSetsCatalog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.sourceforge.pmd.RuleSet;

/**
 * Panel showing a list of available standard rule sets and allowing multiple selection
 */
public final class StandardRuleSetsInputPanel extends JPanel {
    private JList<RuleSetWrapper> inputList;
    
    public StandardRuleSetsInputPanel(StandardRuleSetsCatalog standardRuleSetsCatalog) {
        Collection<RuleSetWrapper> standardRuleSetWrappers = standardRuleSetsCatalog.getRuleSetWrappers();
        
        setLayout(new BorderLayout());
        
        JLabel inputLabel = new JLabel("Choose a standard rule set:");
        inputLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
        add(inputLabel, BorderLayout.NORTH);
        
        DefaultListModel<RuleSetWrapper> inputModel = new DefaultListModel<>();
        standardRuleSetWrappers.forEach(ruleSetWrapper -> inputModel.addElement(ruleSetWrapper));
                
        inputList = new JList<>(inputModel);        
        
        JScrollPane inputScroll = new JScrollPane(inputList);        
        add(inputScroll, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(450, 200));
    }
    
    public Stream<RuleSet> getSelectedRuleSets() {
        return inputList.getSelectedValuesList().stream()
                .map(ruleSetWrapper -> ruleSetWrapper.getRuleSet());                
    }    
}
