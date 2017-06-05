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

import info.gianlucacosta.helios.collections.general.CollectionItems;
import net.sourceforge.pmd.RulePriority;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;

/**
 * The plugin's options
 */
public interface Options {

    static OptionsChanges computeChanges(Options oldOptions, Options newOptions) {
        if (oldOptions == null) {
            return OptionsChanges.ENGINE;
        }

        boolean noEngineChanges
                = Objects.equals(oldOptions.getTargetJavaVersion(), newOptions.getTargetJavaVersion())
                && Objects.equals(oldOptions.getSourceFileEncoding(), newOptions.getSourceFileEncoding())
                && Objects.equals(oldOptions.getSuppressMarker(), newOptions.getSuppressMarker())
                && CollectionItems.equals(oldOptions.getAdditionalClassPathUrls(), newOptions.getAdditionalClassPathUrls())
                && CollectionItems.equals(oldOptions.getRuleSets(), newOptions.getRuleSets())
                && (oldOptions.isShowAllMessagesInGuardedSections() == newOptions.isShowAllMessagesInGuardedSections())
                && Objects.equals(oldOptions.getPathFilteringOptions(), newOptions.getPathFilteringOptions())
                && (oldOptions.getMinimumPriority() == newOptions.getMinimumPriority())
                && Objects.equals(oldOptions.getAuxiliaryClassPath(), newOptions.getAuxiliaryClassPath())
                && (oldOptions.isUseScanMessagesCache() == newOptions.isUseScanMessagesCache());

        if (noEngineChanges) {
            boolean noChanges
                    = (oldOptions.isShowRulePriorityInTasks() == newOptions.isShowRulePriorityInTasks())
                    && (oldOptions.isShowDescriptionInTasks() == newOptions.isShowDescriptionInTasks())
                    && (oldOptions.isShowRuleInTasks() == newOptions.isShowRuleInTasks())
                    && (oldOptions.isShowRuleSetInTasks() == newOptions.isShowRuleSetInTasks())
                    && (oldOptions.isShowAnnotationsInEditor() == newOptions.isShowAnnotationsInEditor());

            if (noChanges) {
                return OptionsChanges.NONE;
            } else {
                return OptionsChanges.VIEW_ONLY;
            }
        } else {
            return OptionsChanges.ENGINE;
        }
    }

    String getTargetJavaVersion();

    String getSourceFileEncoding();

    String getSuppressMarker();

    Collection<URL> getAdditionalClassPathUrls();

    Collection<String> getRuleSets();

    boolean isUseScanMessagesCache();

    boolean isShowRulePriorityInTasks();

    boolean isShowDescriptionInTasks();

    boolean isShowRuleInTasks();

    boolean isShowRuleSetInTasks();

    boolean isShowAnnotationsInEditor();

    boolean isShowAllMessagesInGuardedSections();

    PathFilteringOptions getPathFilteringOptions();

    RulePriority getMinimumPriority();

    String getAuxiliaryClassPath();

    Options clone();
}
