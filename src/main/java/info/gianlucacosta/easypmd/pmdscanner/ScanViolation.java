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

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.ide.options.OptionsService;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

class ScanViolation implements ScanMessage {

    private static final String ANNOTATION_TOKEN_SEPARATOR = "\n";
    private static final String TASK_TOKEN_SEPARATOR = " ";
    private final int lineNumber;
    private final String description;
    private final String ruleName;
    private final String ruleSetName;
    private final RulePriority priority;

    private transient String taskText;
    private transient String annotationText;

    public ScanViolation(RuleViolation ruleViolation) {
        lineNumber = ruleViolation.getBeginLine();
        description = ruleViolation.getDescription();
        ruleName = ruleViolation.getRule().getName();
        ruleSetName = ruleViolation.getRule().getRuleSetName();
        priority = ruleViolation.getRule().getPriority();
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    private void verifyTransientFields() {
        if (taskText != null && annotationText != null) {
            return;
        }

        OptionsService optionsService = Injector.lookup(OptionsService.class);
        Options options = optionsService.getOptions();

        taskText = formatViolationComponents(TASK_TOKEN_SEPARATOR, options.isShowRulePriorityInTasks(), options.isShowDescriptionInTasks(), options.isShowRuleInTasks(), options.isShowRuleSetInTasks());
        annotationText = formatViolationComponents(ANNOTATION_TOKEN_SEPARATOR, false, true, true, true);
    }

    @Override
    public String getTaskText() {
        verifyTransientFields();

        return taskText;
    }

    @Override
    public String getTaskType() {
        switch (priority) {
            case HIGH:
                return "info.gianlucacosta.easypmd.ide.tasklist.High";
            case MEDIUM_HIGH:
                return "info.gianlucacosta.easypmd.ide.tasklist.MediumHigh";
            case MEDIUM:
                return "info.gianlucacosta.easypmd.ide.tasklist.Medium";
            case MEDIUM_LOW:
                return "info.gianlucacosta.easypmd.ide.tasklist.MediumLow";
            case LOW:
                return "info.gianlucacosta.easypmd.ide.tasklist.Low";
            default:
                throw new RuntimeException(String.format("Unexpected priority value: '%s'", priority));
        }
    }

    @Override
    public String getAnnotationText() {
        verifyTransientFields();

        return annotationText;
    }

    @Override
    public String getAnnotationType() {
        switch (priority) {
            case HIGH:
                return "info.gianlucacosta.easypmd.ide.annotations.High";
            case MEDIUM_HIGH:
                return "info.gianlucacosta.easypmd.ide.annotations.MediumHigh";
            case MEDIUM:
                return "info.gianlucacosta.easypmd.ide.annotations.Medium";
            case MEDIUM_LOW:
                return "info.gianlucacosta.easypmd.ide.annotations.MediumLow";
            case LOW:
                return "info.gianlucacosta.easypmd.ide.annotations.Low";
            default:
                throw new RuntimeException(String.format("Unexpected priority value: '%s'", priority));
        }
    }

    private String formatViolationComponents(String separator, boolean showPriority, boolean showDescription, boolean showRuleName, boolean showRuleSetName) {
        StringBuilder resultBuilder = new StringBuilder();

        if (showPriority) {
            resultBuilder.append(priority.getPriority());
            resultBuilder.append(" - ");
        }

        if (showDescription) {
            resultBuilder.append(description);
        }

        if (showRuleName) {
            if (resultBuilder.length() > 0) {
                resultBuilder.append(separator);
            }
            resultBuilder.append("Rule: ");
            resultBuilder.append(ruleName);
        }

        if (showRuleSetName) {
            if (resultBuilder.length() > 0) {
                resultBuilder.append(separator);
            }
            resultBuilder.append("Rule set: ");
            resultBuilder.append(ruleSetName);
        }

        return resultBuilder.toString();
    }

    @Override
    public boolean isShowableInGuardedSections() {
        return false;
    }
}
