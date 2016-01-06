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
package info.gianlucacosta.easypmd.pmdscanner;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import org.openide.util.lookup.ServiceProvider;

import java.util.*;

/**
 * Default implementation of StandardRuleSetsCatalog
 */
@ServiceProvider(service = StandardRuleSetsCatalog.class)
public class DefaultStandardRuleSetsCatalog implements StandardRuleSetsCatalog {

    private final List<RuleSetWrapper> wrappers = new ArrayList<>();

    public DefaultStandardRuleSetsCatalog() {
        RuleSetFactory ruleSetFactory = new RuleSetFactory();

        try {
            Iterator<RuleSet> ruleSetsIterator = ruleSetFactory.getRegisteredRuleSets();

            while (ruleSetsIterator.hasNext()) {
                RuleSet ruleSet = ruleSetsIterator.next();
                wrappers.add(new RuleSetWrapper(ruleSet));
            }
        } catch (RuleSetNotFoundException ex) {
            throw new RuntimeException("Error while initializing the list of PMD's standard rule sets", ex);
        }
    }

    @Override
    public Collection<RuleSetWrapper> getRuleSetWrappers() {
        return Collections.unmodifiableCollection(wrappers);
    }

    @Override
    public boolean containsFileName(String ruleSetFileName) {
        for (RuleSetWrapper wrapper : wrappers) {
            RuleSet ruleSet = wrapper.getRuleSet();

            if (ruleSet.getFileName().equals(ruleSetFileName)) {
                return true;
            }
        }

        return false;
    }
}
