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
package info.gianlucacosta.easypmd.pmdscanner;

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.Options;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.dfa.report.ReportTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Scans a file using PMD
 */
class LinkedPmdScanningStrategy implements PmdScannerStrategy {

    private final LanguageVersionParser languageVersionParser;
    private final PMD pmd;
    private final RuleSets ruleSets;
    private final String sourceFileEncoding;

    public LinkedPmdScanningStrategy(Options options) {
        languageVersionParser = Injector.lookup(LanguageVersionParser.class);

        ClassLoader pmdBasedClassLoader = PmdBasedClassLoader.create(options.getAdditionalClassPathUrls());

        RuleSetFactory ruleSetFactory = new RuleSetFactory();

        String ruleSetsString = buildRuleSetsString(options.getRuleSets());

        try {
            ruleSetFactory.setClassLoader(pmdBasedClassLoader);
            ruleSetFactory.setMinimumPriority(options.getMinimumPriority());
            ruleSets = ruleSetFactory.createRuleSets(ruleSetsString);
        } catch (RuleSetNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        LanguageVersion languageVersion = languageVersionParser.parse(options.getTargetJavaVersion());
        sourceFileEncoding = options.getSourceFileEncoding();

        pmd = new PMD();
        PMDConfiguration pmdConfiguration = pmd.getConfiguration();
        pmdConfiguration.setDefaultLanguageVersion(languageVersion);
        pmdConfiguration.setSuppressMarker(options.getSuppressMarker());
        pmdConfiguration.setClassLoader(pmdBasedClassLoader);
        pmdConfiguration.setMinimumPriority(options.getMinimumPriority());

        String auxiliaryClassPath = options.getAuxiliaryClassPath();

        if (auxiliaryClassPath != null && !auxiliaryClassPath.isEmpty()) {
            try {
                pmdConfiguration.prependClasspath(auxiliaryClassPath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private String buildRuleSetsString(Collection<String> ruleSets) {
        StringBuilder result = new StringBuilder();

        Iterator<String> iterator = ruleSets.iterator();

        while (iterator.hasNext()) {
            String ruleSet = iterator.next();
            result.append(ruleSet);

            if (iterator.hasNext()) {
                result.append(",");
            }
        }

        return result.toString();
    }

    @Override
    public ScanMessageList scanFile(File file) {
        String filePath = file.getAbsolutePath();

        Report report = new Report();

        RuleContext ruleContext = new RuleContext();
        ruleContext.setReport(report);
        ruleContext.setSourceCodeFilename(filePath);

        ScanMessageList scanMessages = new ScanMessageList();

        RuleSets applicableRuleSets = new RuleSets();
        Iterator<RuleSet> ruleSetsIterator = ruleSets.getRuleSetsIterator();
        while (ruleSetsIterator.hasNext()) {
            RuleSet currentRuleSet = ruleSetsIterator.next();
            if (currentRuleSet.applies(file)) {
                applicableRuleSets.addRuleSet(currentRuleSet);
            }
        }

        try {
            try (Reader reader = new InputStreamReader(new FileInputStream(filePath), sourceFileEncoding)) {
                pmd.getSourceCodeProcessor().processSourceCode(reader, applicableRuleSets, ruleContext);
            }

            ReportTree violationTree = report.getViolationTree();

            Iterator<RuleViolation> violationsIterator = violationTree.iterator();
            while (violationsIterator.hasNext()) {
                RuleViolation violation = violationsIterator.next();
                scanMessages.add(new ScanViolation(violation));
            }

        } catch (IOException | PMDException ex) {
            scanMessages.add(new ScanError(ex));
        }

        return scanMessages;
    }
}
