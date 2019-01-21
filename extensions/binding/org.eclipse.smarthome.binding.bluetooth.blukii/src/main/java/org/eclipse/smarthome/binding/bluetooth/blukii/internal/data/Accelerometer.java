/**
 * Copyright (c) 2014,2019 Contributors to the Eclipse Foundation
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
package org.eclipse.smarthome.binding.bluetooth.blukii.internal.data;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Blukii accelerometer data.
 *
 * @author Markus Rathgeb - Initial contribution (migrated from handler)
 */
@NonNullByDefault
public class Accelerometer {
    public final double tiltX;
    public final double tiltY;
    public final double tiltZ;

    public Accelerometer(final double tiltX, final double tiltY, final double tiltZ) {
        this.tiltX = tiltX;
        this.tiltY = tiltY;
        this.tiltZ = tiltZ;
    }

    @Override
    public String toString() {
        return "Accelerometer [tiltX=" + tiltX + ", tiltY=" + tiltY + ", tiltZ=" + tiltZ + "]";
    }

}
