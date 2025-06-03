// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

Cypress.Commands.add('visitWithRetry', (url, options = {}) => {
    const maxRetries = 3;
    let retries = 0;

    function attempt() {
        cy.visit(url, { failOnStatusCode: false, ...options })
            .then((resp) => {
                if (resp.status >= 400 && retries < maxRetries) {
                    retries++;
                    cy.wait(1000); // Wait 1 second before retry
                    return attempt();
                }
            });
    }

    attempt();
});


// Add the visitWithRetry command to handle potential page load issues
Cypress.Commands.add('visitWithRetry', (url, options) => {
    const maxAttempts = 3;
    let attempts = 0;

    function attemptVisit() {
        attempts++;
        cy.visit(url, options).then(() => {
            // If the visit succeeds, we're done
        }).catch((error) => {
            if (attempts < maxAttempts) {
                // If the visit fails and we haven't reached max attempts, try again
                cy.wait(1000); // Wait before retrying
                attemptVisit();
            } else {
                // If we've reached max attempts, fail the test
                throw error;
            }
        });
    }

    attemptVisit();
});

