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

package org.wildfly.extras.jolokia.deployment;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.metadata.web.jboss.JBoss80WebMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.WebMetaData;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JolokiaServletProcessor extends JolokiaDeploymentUnitProcessor {
    public JolokiaServletProcessor(String warName) {
        super(warName);
    }

    protected void deploy(DeploymentUnit unit) throws DeploymentUnitProcessingException {
        WarMetaData warMetaData = unit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        if (warMetaData == null)
            return;

        JBossWebMetaData jBossWebMetaData = warMetaData.getJBossWebMetaData();
        if (jBossWebMetaData == null) {
            jBossWebMetaData = new JBoss80WebMetaData();
            warMetaData.setJBossWebMetaData(jBossWebMetaData);
        }
        jBossWebMetaData.setContextRoot("");
        jBossWebMetaData.setServerInstanceName("jolokia");
    }
}
