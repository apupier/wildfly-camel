/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.camel.test.config;

import static org.wildfly.extension.camel.config.WildFlyCamelConfigEditor.NS_DOMAIN30;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.extension.camel.config.ConfigEditor;
import org.wildfly.extension.camel.config.ConfigSupport;
import org.wildfly.extension.camel.config.WildFlyCamelConfigEditor;

public class DomainConfigTest {

    @Test
    public void testDomainConfig() throws Exception {

        URL resurl = DomainConfigTest.class.getResource("/domain.xml");
        SAXBuilder jdom = new SAXBuilder();
        Document doc = jdom.build(resurl);

        ConfigEditor editor = new WildFlyCamelConfigEditor();
        editor.applyDomainConfigChange(true, doc);

        Element element = doc.getRootElement().getChild("server-groups", NS_DOMAIN30);
        Assert.assertNotNull("server-groups not null", element);
        element = ConfigSupport.findElementWithAttributeValue(element, "server-group", NS_DOMAIN30, "name", "camel-server-group");
        Assert.assertNotNull("camel-server-group not null", element);

        XMLOutputter output = new XMLOutputter();
        output.setFormat(Format.getRawFormat());
        String newXML = output.outputString(doc);
        System.out.println(newXML);
    }
}
