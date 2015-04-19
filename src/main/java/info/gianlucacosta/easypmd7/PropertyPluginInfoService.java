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
package info.gianlucacosta.easypmd7;

import info.gianlucacosta.helios.product.ProductInfoService;
import info.gianlucacosta.helios.product.PropertyProductInfoService;
import info.gianlucacosta.helios.properties.XmlProperties;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;

/**
 * Reads the plugin information from a property file
 */
@ServiceProvider(service = ProductInfoService.class)
public class PropertyPluginInfoService extends PropertyProductInfoService {

    public PropertyPluginInfoService() throws IOException {
        super(new XmlProperties("/info/gianlucacosta/easypmd7/Plugin.properties.xml"));
    }

}
