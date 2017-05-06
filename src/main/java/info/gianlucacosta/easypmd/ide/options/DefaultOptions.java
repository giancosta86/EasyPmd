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

import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Modifiable implementation of Options
 */
class DefaultOptions implements Options, Serializable, Cloneable {

    private static final long serialVersionUID = 7;

    private String targetJavaVersion;
    private String sourceFileEncoding;
    private String suppressMarker;
    private List<URL> additionalClassPathUrls;
    private List<String> ruleSets;
    private boolean useScanMessagesCache;
    private boolean showRulePriorityInTasks;
    private boolean showDescriptionInTasks;
    private boolean showRuleInTasks;
    private boolean showRuleSetInTasks;
    private boolean showAnnotationsInEditor;
    private boolean showAllMessagesInGuardedSections;
    private PathFilteringOptions pathFilteringOptions;
    private RulePriority minimumPriority;
    private String auxiliaryClassPath;

    public DefaultOptions() {
        //Just do nothing
    }

    @Override
    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String targetJavaVersion) {
        this.targetJavaVersion = targetJavaVersion;
    }

    @Override
    public String getSourceFileEncoding() {
        return sourceFileEncoding;
    }

    public void setSourceFileEncoding(String sourceFileEncoding) {
        this.sourceFileEncoding = sourceFileEncoding;
    }

    @Override
    public String getSuppressMarker() {
        return suppressMarker;
    }

    public void setSuppressMarker(String suppressMarker) {
        this.suppressMarker = suppressMarker;
    }

    @Override
    public Collection<URL> getAdditionalClassPathUrls() {
        return Collections.unmodifiableCollection(additionalClassPathUrls);
    }

    public void setAdditionalClassPathUrls(Collection<URL> additionalClassPathUrls) {
        this.additionalClassPathUrls = new ArrayList<>(additionalClassPathUrls);
    }

    @Override
    public Collection<String> getRuleSets() {
        return Collections.unmodifiableCollection(ruleSets);
    }

    public void setRuleSets(Collection<String> ruleSets) {
        this.ruleSets = new ArrayList<>(ruleSets);
    }

    @Override
    public boolean isUseScanMessagesCache() {
        return useScanMessagesCache;
    }

    public void setUseScanMessagesCache(boolean useScanMessagesCache) {
        this.useScanMessagesCache = useScanMessagesCache;
    }

    @Override
    public boolean isShowRulePriorityInTasks() {
        return showRulePriorityInTasks;
    }

    public void setShowRulePriorityInTasks(boolean showRulePriorityInTasks) {
        this.showRulePriorityInTasks = showRulePriorityInTasks;
    }

    @Override
    public boolean isShowDescriptionInTasks() {
        return showDescriptionInTasks;
    }

    public void setShowDescriptionInTasks(boolean showDescriptionInTasks) {
        this.showDescriptionInTasks = showDescriptionInTasks;
    }

    @Override
    public boolean isShowRuleInTasks() {
        return showRuleInTasks;
    }

    public void setShowRuleInTasks(boolean showRuleInTasks) {
        this.showRuleInTasks = showRuleInTasks;
    }

    @Override
    public boolean isShowRuleSetInTasks() {
        return showRuleSetInTasks;
    }

    public void setShowRuleSetInTasks(boolean showRuleSetInTasks) {
        this.showRuleSetInTasks = showRuleSetInTasks;
    }

    @Override
    public boolean isShowAnnotationsInEditor() {
        return showAnnotationsInEditor;
    }

    public void setShowAnnotationsInEditor(boolean showAnnotationsInEditor) {
        this.showAnnotationsInEditor = showAnnotationsInEditor;
    }

    @Override
    public boolean isShowAllMessagesInGuardedSections() {
        return showAllMessagesInGuardedSections;
    }

    public void setShowAllMessagesInGuardedSections(boolean showAllMessagesInGuardedSections) {
        this.showAllMessagesInGuardedSections = showAllMessagesInGuardedSections;
    }

    @Override
    public PathFilteringOptions getPathFilteringOptions() {
        return pathFilteringOptions;
    }

    public void setPathFilteringOptions(PathFilteringOptions pathFilteringOptions) {
        this.pathFilteringOptions = pathFilteringOptions;
    }

    @Override
    public RulePriority getMinimumPriority() {
        return minimumPriority;
    }

    public void setMinimumPriority(RulePriority minimumPriority) {
        this.minimumPriority = minimumPriority;
    }

    @Override
    public String getAuxiliaryClassPath() {
        return auxiliaryClassPath;
    }

    public void setAuxiliaryClassPath(String auxiliaryClassPath) {
        this.auxiliaryClassPath = auxiliaryClassPath;
    }

    @Override
    public Options clone() {
        DefaultOptions result;
        try {
            result = (DefaultOptions) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }

        //Manually cloning collections...
        result.additionalClassPathUrls = new ArrayList<>(additionalClassPathUrls);
        result.ruleSets = new ArrayList<>(ruleSets);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Options)) {
            return false;
        }

        Options other = (Options) obj;

        return Objects.equals(getTargetJavaVersion(), other.getTargetJavaVersion())
                && Objects.equals(getSourceFileEncoding(), other.getSourceFileEncoding())
                && Objects.equals(getSuppressMarker(), other.getSuppressMarker())
                && CollectionItems.equals(getAdditionalClassPathUrls(), other.getAdditionalClassPathUrls())
                && CollectionItems.equals(getRuleSets(), other.getRuleSets())
                && isUseScanMessagesCache() == other.isUseScanMessagesCache()
                && isShowRulePriorityInTasks() == other.isShowRulePriorityInTasks()
                && isShowDescriptionInTasks() == other.isShowDescriptionInTasks()
                && isShowRuleInTasks() == other.isShowRuleInTasks()
                && isShowRuleSetInTasks() == other.isShowRuleSetInTasks()
                && isShowAnnotationsInEditor() == other.isShowAnnotationsInEditor()
                && isShowAllMessagesInGuardedSections() == other.isShowAllMessagesInGuardedSections()
                && Objects.equals(getPathFilteringOptions(), other.getPathFilteringOptions())
                && Objects.equals(getMinimumPriority(), other.getMinimumPriority())
                && Objects.equals(getAuxiliaryClassPath(), other.getAuxiliaryClassPath());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
