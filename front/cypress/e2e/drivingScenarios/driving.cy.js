describe('Admin Login Flow', () => {
    it('Sets up test data and logs in as admin', () => {
        // Step 1: Call the /test/setup API to insert test data
        cy.request('POST', 'http://localhost:8080/test/setup') // Replace with the correct API endpoint
            .then((response) => {
                // Assert that the setup was successful
                expect(response.body).to.include('Test data inserted ✅');
            });

        // Step 2: Navigate to the login page
        cy.visit('http://localhost:5173/login');

        // Step 3: Fill in the login form with admin credentials
        cy.get('input#phoneNumber')
            .type('0531223423') // Admin phone number from the test data
            .should('have.value', '0531223423');

        cy.get('input#password')
            .type('adminPassword123') // Admin password (ensure this matches the setup)
            .should('have.value', 'adminPassword123');

        // Step 4: Submit the login form
        cy.get('button[type="submit"]').click();

        // Step 5: Assert that the user is redirected to the home page after successful login
        cy.url().should('include', '/'); // Adjust based on your app's home route
        cy.get('h2').should('contain.text', 'Welcome'); // Adjust based on your app's actual welcome text
    });
});
