/**
 * Copyright (c) 2014,2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.designer.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.smarthome.config.core.ConfigConstants;
import org.eclipse.smarthome.designer.core.CoreActivator;
import org.eclipse.smarthome.designer.core.DesignerCoreConstants;

/**
 * This class initializes the preference setting for the configuration folder.
 * If no other preference has been set yet, the default defined in the config.core bundle
 * will be used.
 *
 * @author Kai Kreuzer - Initial contribution and API
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public PreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        IScopeContext context = DefaultScope.INSTANCE;
        IEclipsePreferences node = context.getNode(CoreActivator.getDefault().getBundle().getSymbolicName());
        String folderPath = ConfigConstants.DEFAULT_CONFIG_FOLDER;
        node.put(DesignerCoreConstants.CONFIG_FOLDER_PREFERENCE, folderPath);
    }

}
