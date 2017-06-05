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
package info.gianlucacosta.easypmd.ide.options.regexes;

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.helios.product.ProductInfoService;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;
import java.util.Collection;

/**
 * Default implementation of RegexTemplateSelectionDialog
 */
@ServiceProvider(service = RegexTemplateSelectionDialog.class)
public class DefaultRegexTemplateSelectionDialog implements RegexTemplateSelectionDialog {

    private static final RegexTemplate[] regexTemplates;

    static {
        Collection<? extends RegexTemplate> foundRegexTemplates = Injector.lookupAll(RegexTemplate.class);

        regexTemplates = foundRegexTemplates
                .stream()
                .sorted()
                .toArray(RegexTemplate[]::new);
    }

    private final ProductInfoService pluginInfoService;

    public DefaultRegexTemplateSelectionDialog() {
        pluginInfoService = Injector.lookup(ProductInfoService.class);
    }

    @Override
    public RegexTemplate askForRegexTemplate() {
        RegexTemplate chosenTemplate = (RegexTemplate) JOptionPane.showInputDialog(
                null,
                "Please, choose a predefined regular expression:",
                pluginInfoService.getName(),
                JOptionPane.OK_CANCEL_OPTION | JOptionPane.PLAIN_MESSAGE,
                null,
                regexTemplates,
                null);
        return chosenTemplate;
    }
}
