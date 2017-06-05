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

import info.gianlucacosta.easypmd.system.SystemPropertiesService;
import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.pmdscanner.pmdcatalogs.LanguageVersionParser;
import info.gianlucacosta.easypmd.pmdscanner.pmdcatalogs.StandardRuleSetsCatalog;
import info.gianlucacosta.helios.regex.OsSpecificPathCompositeRegex;
import net.sourceforge.pmd.RulePriority;
import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of OptionsFactory
 */
@ServiceProvider(service = OptionsFactory.class)
public class DefaultOptionsFactory implements OptionsFactory {

    private static final Logger logger = Logger.getLogger(DefaultOptionsFactory.class.getName());
    private static final String defaultJavaLanguageVersion = "1.8";

    private final StandardRuleSetsCatalog standardRulesetsCatalog;
    private final SystemPropertiesService systemPropertiesService;
    private final LanguageVersionParser languageVersionParser;

    public DefaultOptionsFactory() {
        standardRulesetsCatalog = Injector.lookup(StandardRuleSetsCatalog.class);
        systemPropertiesService = Injector.lookup(SystemPropertiesService.class);
        languageVersionParser = Injector.lookup(LanguageVersionParser.class);
    }

    @Override
    public Options createDefaultOptions() {
        DefaultOptions result = new DefaultOptions();

        String retrievedJavaVersion = systemPropertiesService.getJavaVersion();
        String javaVersion = retrievedJavaVersion != null ? retrievedJavaVersion : defaultJavaLanguageVersion;

        try {
            languageVersionParser.parse(javaVersion);
        } catch (IllegalArgumentException ex) {
            javaVersion = defaultJavaLanguageVersion;
        }

        result.setTargetJavaVersion(javaVersion);
        result.setSourceFileEncoding("utf-8");
        result.setSuppressMarker("NOPMD");
        result.setMinimumPriority(RulePriority.MEDIUM);

        result.setAdditionalClassPathUrls(new ArrayList<>());

        final String[] suggestedDefaultRuleSetFileNames = new String[]{
            "rulesets/java/basic.xml",
            "rulesets/java/imports.xml",
            "rulesets/java/unusedcode.xml"
        };

        List<String> defaultRuleSets = new ArrayList<>();

        Arrays
                .stream(suggestedDefaultRuleSetFileNames)
                .forEach(ruleSetFileName -> {
                    if (standardRulesetsCatalog.containsFileName(ruleSetFileName)) {
                        defaultRuleSets.add(ruleSetFileName);
                    } else {
                        logger.warning(() -> String.format(
                                "The standard ruleset '%s' was not found",
                                ruleSetFileName
                        ));
                    }
                });

        result.setRuleSets(defaultRuleSets);

        result.setUseScanMessagesCache(true);

        result.setShowRulePriorityInTasks(true);
        result.setShowAnnotationsInEditor(true);
        result.setShowDescriptionInTasks(true);
        result.setShowRuleInTasks(false);
        result.setShowRuleSetInTasks(false);
        result.setShowAllMessagesInGuardedSections(false);

        result.setPathFilteringOptions(
                new PathFilteringOptions(
                        new OsSpecificPathCompositeRegex("^.*\\.java$"),
                        new OsSpecificPathCompositeRegex()));

        return result;
    }
}
