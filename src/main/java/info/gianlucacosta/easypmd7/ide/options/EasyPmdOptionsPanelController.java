/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2015 Gianluca Costa
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
package info.gianlucacosta.easypmd7.ide.options;

import info.gianlucacosta.easypmd7.ide.Injector;
import info.gianlucacosta.easypmd7.ide.options.profiles.ProfileConfiguration;
import info.gianlucacosta.easypmd7.ide.options.profiles.ProfileConfigurationRepository;
import info.gianlucacosta.easypmd7.pmdscanner.PmdScanner;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Controller underlying the plugin's options panel
 */
@OptionsPanelController.TopLevelRegistration(
        id = "info.gianlucacosta.easypmd7.ide.options.EasyPmdOptionsPanelController",
        categoryName = "#Option_DisplayName_EasyPmd",
        keywords = "#Option_Keywords_EasyPmd",
        keywordsCategory = "#Option_KeywordsCategory_EasyPmd",
        iconBase = "info/gianlucacosta/easypmd7/mainIcon32.png"
)
public class EasyPmdOptionsPanelController extends OptionsPanelController {

    private static final String EASY_PMD_OPTIONS_NAME_IN_EVENT = "EASYPMD_OPTIONS";
    private final EasyPmdPanel panel = new EasyPmdPanel();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ProfileConfigurationRepository profileConfigurationRepository;
    private final OptionsService optionsService;
    private boolean optionsChanged;

    public EasyPmdOptionsPanelController() {
        profileConfigurationRepository = Injector.lookup(ProfileConfigurationRepository.class);
        optionsService = Injector.lookup(OptionsService.class);

        optionsService.addOptionsVerifier((Options options) -> {
            try {
                new PmdScanner(options);
            } catch (RuntimeException ex) {
                throw new InvalidOptionsException(ex);
            }
        });
    }

    @Override
    public void update() {
        ProfileConfiguration profileConfiguration = profileConfigurationRepository.getProfileConfiguration();

        panel.setProfileConfiguration(profileConfiguration);
    }

    @Override
    public void applyChanges() {
        ProfileConfiguration profileConfiguration = panel.getProfileConfiguration();
        profileConfigurationRepository.saveProfileConfiguration(profileConfiguration);

        Options oldOptions = optionsService.getOptions();
        Options newOptions = profileConfiguration.getActiveOptions();

        optionsService.setOptions(newOptions);

        if (!newOptions.equals(oldOptions)) {
            pcs.firePropertyChange(EASY_PMD_OPTIONS_NAME_IN_EVENT, oldOptions, newOptions);
        }
    }

    @Override
    public void cancel() {
        //Need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        try {
            Options activeOptions = panel.getProfileConfiguration().getActiveOptions();
            optionsService.verifyOptions(activeOptions);
            return true;
        } catch (InvalidOptionsException ex) {
            return false;
        }
    }

    @Override
    public boolean isChanged() {
        return optionsChanged;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("info.gianlucacosta.easypmd7.options");
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return panel;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}
