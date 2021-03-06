package org.javaee7.jaspictest.customprincipal;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

/**
 * This tests that we can login from a public page (a page for which no security constraints have been set)
 * and that for this type of page the custom principal correctly arrives in a Servlet.
 * 
 * @author Arjan Tijms
 * 
 */
@RunWith(Arquillian.class)
public class CustomPrincipalPublicTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testPublicPageLoggedin() throws IOException, SAXException {

        // JASPIC has to be able to authenticate a user when accessing a public (non-protected) resource.

        String response = getFromServerPath("public/servlet?doLogin");

        // Has to be logged-in with the right principal
        assertTrue(
            "Username is not the expected one 'test'",
            response.contains("web username: test")
        );
        assertTrue(
            "Username is correct, but the expected role 'architect' is not present.",
            response.contains("web user has role \"architect\": true"));
        
        assertTrue(
            "Username and roles are correct, but principal type is not the expected custom type.",
            response.contains("isCustomPrincipal: true")
        );
    }

    @Test
    public void testPublicPageNotRememberLogin() throws IOException, SAXException {

        // -------------------- Request 1 ---------------------------

        String response = getFromServerPath("public/servlet");

        // Not logged-in
        assertTrue(response.contains("web username: null"));
        assertTrue(response.contains("web user has role \"architect\": false"));

        // -------------------- Request 2 ---------------------------

        response = getFromServerPath("public/servlet?doLogin");

        // Now has to be logged-in
        assertTrue(
            "Username is not the expected one 'test'",
            response.contains("web username: test")
        );
        assertTrue(
            "Username is correct, but the expected role 'architect' is not present.",
            response.contains("web user has role \"architect\": true")
        );

        // -------------------- Request 3 ---------------------------

        response = getFromServerPath("public/servlet");

        // Not logged-in
        assertTrue(
            "Should not be authenticated, but username was not null. Did the container remember it from previous request?",
            response.contains("web username: null")
        );
        assertTrue(
            "Request was not authenticated (username correctly null), but unauthenticated user incorrectly has role 'architect'",
            response.contains("web user has role \"architect\": false")
        );
    }

}