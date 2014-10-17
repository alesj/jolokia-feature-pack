/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extras.jolokia.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.BYTES;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.dmr.ModelNode;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class WarUtils {
    static ModelNode getDummyWar() {
        ModelNode contentItem = new ModelNode();
        byte[] war = getDummyWarBytes();
        contentItem.get(BYTES).set(war);
        return contentItem;
    }

    private static byte[] getDummyWarBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(baos);
            ZipEntry webXml = new ZipEntry("WEB-INF/web.xml");
            zip.putNextEntry(webXml);
            zip.write(getWebXml());
            zip.closeEntry();
            zip.finish();
            zip.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] getWebXml() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream webXml = WarUtils.class.getClassLoader().getResourceAsStream("web.xml")) {
            copyStream(webXml, baos);
        }
        return baos.toByteArray();
    }

    private static void copyStream(final InputStream in, final OutputStream out) throws IOException {
        final byte[] bytes = new byte[8192];
        int cnt;
        while ((cnt = in.read(bytes)) != -1) {
            out.write(bytes, 0, cnt);
        }
    }
}