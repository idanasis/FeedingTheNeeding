describe('Admin Login and Driving Management Flow', () => {
    beforeEach(() => {
        // Reset and set up test data before each test
        cy.request('POST', 'http://localhost:8080/test/setup')
            .then((response) => {
                expect(response.body).to.include('Test data inserted ✅');
            });

        // Set viewport to a larger size to make desktop elements visible
        cy.viewport(1200, 800);
    });

    it('should login as admin and perform driving management tasks', () => {
        // Step 1: Navigate to the login page
        cy.visit('http://localhost:5173/login');

        // Step 2: Fill in the login form with admin credentials
        cy.get('input#phoneNumber')
            .type('0531223423')
            .should('have.value', '0531223423');

        cy.get('input#password')
            .type('adminPassword123')
            .should('have.value', 'adminPassword123');

        // Step 3: Submit the login form
        cy.get('button[type="submit"]').click();

        // Step 4: Assert that the user is redirected to the home page after successful login
        cy.url().should('include', '/');

        // Step 5: Click on לפעולות button (in desktop view now)
        cy.contains('לפעולות').click();

        // Step 7: Navigate to driving management
        cy.contains('ניהול נסיעות').click();

        // Wait for the driving management page to load
        cy.contains('מסלולים').should('be.visible');

        cy.wait(2000);

        // Step 7: Add a new route
        cy.get('button').contains('הוסף מסלול').click();

        // Step 8: Expand the route (assuming the latest route was added at the end)
        cy.get('svg[data-testid="ExpandMoreIcon"]')
            .last()
            .parents('button')
            .click({ force: true });
        cy.wait(2000);

        // Step 9: Select a driver for the route
        cy.get('div.MuiSelect-select').contains('לא נבחר').click({ force: true });
        cy.contains('TestDonor User').click();

        // Wait for the route to update with the selected driver
        cy.contains('TestDonor User').should('be.visible');

        // Step 10: Add chef to route via modal
        cy.contains('הוסף טבח').click();
        // Wait for the chef selection modal to appear

        // Ensure the chef selection modal is visible and the title is correct
        cy.contains('בחר טבח להוספה').should('be.visible').then(($modalTitle) => {
            // Find the closest modal/container to the title
            cy.wrap($modalTitle)
                .closest('div[role="dialog"], .MuiDialog-root, .MuiPaper-root')
                .within(() => {
                    // Now click only the TestDonor2 User2 under this modal
                    cy.contains('h6', 'TestDonor2 User2')
                        .should('be.visible')
                        .click({ force: true });
                });
        });



        // Step 11: Add recipient to route via modal
        cy.contains('הוסף נזקק').click();

        // Ensure the chef selection modal is visible and the title is correct
        cy.contains('בחר נזקק להוספה').should('be.visible').then(($modalTitle) => {
            // Find the closest modal/container to the title
            cy.wrap($modalTitle)
                .closest('div[role="dialog"], .MuiDialog-root, .MuiPaper-root')
                .within(() => {
                    // Now click only the TestDonor2 User2 under this modal
                    cy.contains('h6', 'TestNeedy User')
                        .should('be.visible')
                        .click({ force: true });
                });
        });

        // Step 12: Publish the route
        cy.contains('פרסם').click();

        // Verify the route was published
        cy.contains('פורסם').should('be.visible');
    });
});