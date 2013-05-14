package org.camelcookbook.splitjoin.xml;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Demonstrates the splitting of Xml files through XPath expression using Namespaces.
 *
 * This test is intended to be run out of Maven, as it references the target directory.
 */
public class XmlNamespacesSplitTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            Namespaces namespaces = new Namespaces("c", "http://camelcookbook.org/schema/books")
                .add("se", "http://camelcookbook.org/schema/somethingElse");

            from("direct:in")
                .split(namespaces.xpath("//c:book[@category='Tech']/c:authors/c:author/text()"))
                .to("mock:out");
            }
        };
    }

    @Test
    public void testSplitArray() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(2);
        mockOut.expectedBodiesReceived("Scott Cranton", "Jakub Korab");


        String filename = "target/classes/xml/books-ns.xml";
        assertFileExists(filename);
        InputStream booksStream = new FileInputStream(filename);

        template.sendBody("direct:in", booksStream);

        assertMockEndpointsSatisfied();
    }
}
