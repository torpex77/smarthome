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
package org.eclipse.smarthome.io.rest.sitemap.internal;

/**
 * This is a data transfer object that is used to serialize sitemaps.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 * @author Chris Jackson
 *
 */
public class SitemapDTO {

    public String name;
    public String icon;
    public String label;

    public String link;

    public PageDTO homepage;

}
