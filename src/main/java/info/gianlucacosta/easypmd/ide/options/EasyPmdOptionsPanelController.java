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

import info.gianlucacosta.easypmd.ide.Injector;
import info.gianlucacosta.easypmd.pmdscanner.PmdScanner;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileContext;
import info.gianlucacosta.easypmd.ide.options.profiles.ProfileContextRepository;
import java.util.logging.Logger;

/**
 * Controller underlying the plugin's options panel
 */
@OptionsPanelController.TopLevelRegistration(
        id = "info.gianlucacosta.easypmd.ide.options.EasyPmdOptionsPanelController",
        categoryName = "#Option_DisplayName_EasyPmd",
        keywords = "#Option_Keywords_EasyPmd",
        keywordsCategory = "#Option_KeywordsCategory_EasyPmd",
        iconBase = "info/gianlucacosta/easypmd/mainIcon32.png"
)
public class EasyPmdOptionsPanelController extends OptionsPanelController {

    private static final Logger logger = Logger.getLogger(EasyPmdOptionsPanelController.class.getName());
    private static final String EASY_PMD_OPTIONS_NAME_IN_EVENT = "EASYPMD_OPTIONS";
    private final EasyPmdPanel easyPmdPanel = new EasyPmdPanel();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ProfileContextRepository profileContextRepository;
    private final OptionsService optionsService;
    private boolean optionsChanged;

    private boolean valid;

    public EasyPmdOptionsPanelController() {
        profileContextRepository = Injector.lookup(ProfileContextRepository.class);
        optionsService = Injector.lookup(OptionsService.class);

        optionsService.addOptionsSetListener((options, optionsChanges) -> {
            optionsChanged = (optionsChanges != OptionsChanges.NONE);

            if (optionsChanged) {
                pcs.firePropertyChange(EASY_PMD_OPTIONS_NAME_IN_EVENT, null, options);
            }
        });
    }

    private void internalValidate() {
        logger.info(() -> "Internally validating the options, for the options controller...");
        try {
            Options activeOptions = easyPmdPanel.getProfileContextDTO().getProfileContext().getActiveOptions();
            optionsService.validateOptions(activeOptions);
            valid = true;
            logger.info("Options valid");
        } catch (InvalidOptionsException ex) {
            valid = false;
            logger.info("Options NOT valid");
        }
    }

    @Override
    public void update() {
        ProfileContext profileContext = profileContextRepository.getProfileContext();

        easyPmdPanel.setProfileContext(profileContext);

        internalValidate();
    }

    @Override
    public void applyChanges() {
        internalValidate();

        ProfileContextDTO profileContextDTO = easyPmdPanel.getProfileContextDTO();
        ProfileContext profileContext = profileContextDTO.getProfileContext();

        profileContextRepository.saveProfileContext(profileContext);

        Options newOptions = profileContext.getActiveOptions();

        optionsService.setOptions(newOptions);
    }

    @Override
    public void cancel() {
        //Need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isChanged() {
        return optionsChanged;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("info.gianlucacosta.easypmd.options");
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return easyPmdPanel;
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
