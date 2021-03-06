/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2017 RedHat
 * %%
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
 * #L%
 */
package org.wildfly.camel.test.msv;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
@RunWith(Arquillian.class)
public class MsvIntegrationTest {

    private static final String ORDER_XML_VALID = "<order xmlns='http://org.wildfly.camel.test.jing'><sku>12345</sku><quantity>2</quantity></order>";
    private static final String ORDER_XML_INVALID = "<order xmlns='http://org.wildfly.camel.test.jing'><sku>12345</sku></order>";

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "camel-msv-tests.jar")
            .addAsResource("msv/schema.rng", "schema.rng");
    }

    @Test
    public void testMsvSchemaValidationSuccess() throws Exception {
        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                .doTry()
                    .to("msv:/schema.rng")
                    .to("mock:valid")
                .doCatch(ValidationException.class)
                    .to("mock:invalid");
            }
        });

        MockEndpoint mockEndpointValid = camelctx.getEndpoint("mock:valid", MockEndpoint.class);
        mockEndpointValid.expectedMessageCount(1);

        MockEndpoint mockEndpointInvalid = camelctx.getEndpoint("mock:invalid", MockEndpoint.class);
        mockEndpointInvalid.expectedMessageCount(0);

        camelctx.start();
        try {
            ProducerTemplate template = camelctx.createProducerTemplate();
            template.sendBody("direct:start", ORDER_XML_VALID);

            mockEndpointValid.assertIsSatisfied();
            mockEndpointInvalid.assertIsSatisfied();
        } finally {
            camelctx.stop();
        }
    }

    @Test
    public void testMsvSchemaValidationFailure() throws Exception {
        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                .doTry()
                    .to("msv:/schema.rng")
                    .to("mock:valid")
                .doCatch(ValidationException.class)
                    .to("mock:invalid");
            }
        });

        MockEndpoint mockEndpointValid = camelctx.getEndpoint("mock:valid", MockEndpoint.class);
        mockEndpointValid.expectedMessageCount(0);

        MockEndpoint mockEndpointInvalid = camelctx.getEndpoint("mock:invalid", MockEndpoint.class);
        mockEndpointInvalid.expectedMessageCount(1);

        camelctx.start();
        try {
            ProducerTemplate template = camelctx.createProducerTemplate();
            template.sendBody("direct:start", ORDER_XML_INVALID);

            mockEndpointValid.assertIsSatisfied();
            mockEndpointInvalid.assertIsSatisfied();
        } finally {
            camelctx.stop();
        }
    }

}
