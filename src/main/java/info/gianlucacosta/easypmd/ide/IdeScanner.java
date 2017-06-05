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
package info.gianlucacosta.easypmd.ide;

import info.gianlucacosta.easypmd.ide.annotations.AnnotationService;
import info.gianlucacosta.easypmd.ide.annotations.GuardedSectionsAnalyzer;
import info.gianlucacosta.easypmd.ide.options.Options;
import info.gianlucacosta.easypmd.ide.options.OptionsChanges;
import info.gianlucacosta.easypmd.ide.options.OptionsService;
import info.gianlucacosta.easypmd.pmdscanner.PmdScanner;
import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import info.gianlucacosta.easypmd.pmdscanner.messages.cache.ScanMessagesCache;
import info.gianlucacosta.easypmd.util.Throwables;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The link between the IDE and the plugin's scan system
 */
public class IdeScanner extends FileTaskScanner {

    private static final Logger logger = Logger.getLogger(IdeScanner.class.getName());
    private static final String OPTIONS_PATH = "Advanced/info.gianlucacosta.easypmd";
    private final AnnotationService annotationService;
    private final DialogService dialogService;
    private final OptionsService optionsService;
    private final ScanMessagesCache scanMessagesCache;
    private Callback callback;
    private PmdScanner pmdScanner;
    private Options options;
    private final Lock readOptionsLock;
    private final Lock writeOptionsLock;

    public IdeScanner(String displayName, String description, String optionsPath) {
        super(displayName, description, optionsPath);

        dialogService = Injector.lookup(DialogService.class);
        annotationService = Injector.lookup(AnnotationService.class);
        optionsService = Injector.lookup(OptionsService.class);
        scanMessagesCache = Injector.lookup(ScanMessagesCache.class);

        ReadWriteLock optionsLock = new ReentrantReadWriteLock();
        readOptionsLock = optionsLock.readLock();
        writeOptionsLock = optionsLock.writeLock();

        options = optionsService.getOptions();

        try {
            pmdScanner = new PmdScanner(options);
        } catch (RuntimeException ex) {
            showScannerConfigurationException(ex);
        }

        optionsService.addOptionsSetListener((newOptions, optionsChanges) -> {
            switch (optionsChanges) {
                case NONE:
                    //Just do nothing
                    logger.info(() -> "Options were not changed");
                    break;

                case VIEW_ONLY:
                case ENGINE:
                    writeOptionsLock.lock();

                    try {
                        options = newOptions;

                        if (!scanMessagesCache.clear()) {
                            logger.warning(() -> "Could not clear the cache before replacing the PMD scanner");
                        }

                        if (optionsChanges == OptionsChanges.ENGINE) {
                            logger.info(() -> "Engine options changed");

                            try {
                                pmdScanner = new PmdScanner(options);
                            } catch (RuntimeException ex) {
                                pmdScanner = null;
                                showScannerConfigurationException(ex);
                            }
                        } else {
                            logger.info(() -> "Only view options were changed");
                        }

                        annotationService.detachAllAnnotations();

                        if (callback != null) {
                            callback.refreshAll();
                        }
                    } finally {
                        writeOptionsLock.unlock();
                    }

                    break;

                default:
                    throw new IllegalArgumentException();
            }
        });
    }

    private void showScannerConfigurationException(Exception ex) {
        logger.warning(
                () -> String.format(
                        "Configuration exception for EasyPmd: %s",
                        Throwables.toStringWithStackTrace(ex)
                )
        );

        dialogService.showWarning(String.format("Could not run EasyPmd because of configuration errors:\n\t%s (%s)", ex.getMessage(), ex.getClass().getSimpleName()));
    }

    @Override
    public List<? extends Task> scan(FileObject fileObject) {
        if (pmdScanner == null) {
            return Collections.emptyList();
        }

        readOptionsLock.lock();
        try {
            File file = FileUtil.toFile(fileObject);

            if (file == null || !file.isFile()) {
                return Collections.emptyList();
            }

            annotationService.detachAnnotationsFrom(fileObject);

            String filePath = file.getPath();

            if (!options.getPathFilteringOptions().isPathValid(filePath)) {
                return Collections.emptyList();
            }

            DataObject dataObject;
            try {
                dataObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            Set<ScanMessage> scanMessages = pmdScanner.scan(file.toPath());

            if (!options.isShowAllMessagesInGuardedSections()) {
                GuardedSectionsAnalyzer guardedSectionsAnalyzer = new GuardedSectionsAnalyzer(dataObject);

                Set<Integer> guardedPmdLineNumbers = guardedSectionsAnalyzer.getGuardedLineNumbers();

                if (!guardedPmdLineNumbers.isEmpty()) {
                    scanMessages = scanMessages
                            .stream()
                            .parallel()
                            .filter(scanMessage -> {
                                return scanMessage.isShowableInGuardedSections()
                                        || !guardedPmdLineNumbers.contains(scanMessage.getLineNumber());

                            })
                            .collect(Collectors.toSet());
                }
            }

            List<Task> tasks = scanMessages
                    .stream()
                    .parallel()
                    .map(scanMessage -> scanMessage.createTask(options, fileObject))
                    .collect(Collectors.toList());

            if (options.isShowAnnotationsInEditor()) {
                annotationService.attachAnnotationsTo(options,
                        dataObject,
                        scanMessages
                );
            }

            return tasks;
        } finally {
            readOptionsLock.unlock();
        }

    }

    @Override
    public void attach(Callback callback) {

        logger.info(
                () -> String.format(
                        "Attaching callback: %s",
                        callback
                )
        );

        this.callback = callback;

        if (callback == null) {
            annotationService.detachAllAnnotations();
        } else {
            callback.refreshAll();
        }
    }

    public static IdeScanner create() throws ParserConfigurationException {
        ResourceBundle bundle = NbBundle.getBundle(IdeScanner.class);

        return new IdeScanner(
                bundle.getString("Filter_DisplayName"),
                bundle.getString("Filter_Description"),
                OPTIONS_PATH);
    }
}
