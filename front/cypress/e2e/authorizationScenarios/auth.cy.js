describe('Needy User Authorization Tests', () => {
    before(() => {
        // Reset and set up test data once before all tests
        cy.request('POST', 'http://localhost:8080/test/setup')
            .then((response) => {
                expect(response.body).to.include('Test data inserted ✅');
            });
    });

    beforeEach(() => {
        // Set viewport to a larger size to make desktop elements visible
        cy.viewport(1200, 800);
    });

    it('should login as donor and be redirected from unauthorized pages to homepage', () => {
        // Step 1: Navigate to the login page
        cy.visit('http://localhost:5173/login');

        // Step 2: Fill in the login form with donor credentials
        cy.get('input#phoneNumber')
            .type('0531223421')
            .should('have.value', '0531223421');

        cy.get('input#password')
            .type('donorPassword123')
            .should('have.value', 'donorPassword123');

        // Step 3: Submit the login form
        cy.get('button[type="submit"]').click();

        cy.wait(5000); // Wait for the login to process

        // Step 4: Assert that the user is redirected to the home page after successful login
        cy.url().should('include', '/');

        // Define routes that needy user should NOT be able to access
        const unauthorizedRoutes = [
            '/social',           // STAFF, ADMIN only
            '/neederPending',    // STAFF, ADMIN only
            '/driving',          // STAFF, ADMIN only
            '/cookManager',      // STAFF, ADMIN only
            '/donorPending',     // STAFF, ADMIN only
            '/donors',           // STAFF, ADMIN only
            '/needers'           // STAFF, ADMIN only
        ];

        // Test each unauthorized route
        unauthorizedRoutes.forEach((route) => {
            cy.log(`Testing unauthorized access to: ${route}`);

            // Attempt to visit the protected route
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(2000); // Wait for the page to load
            // Assert that the user is redirected to homepage
            cy.url().should('eq', 'http://localhost:5173/');

            cy.wait(500);
        });
    });

    it('should redirect unauthenticated users from protected routes to login page', () => {
        // Clear any existing session/cookies to ensure user is not logged in
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.window().then((win) => {
            win.sessionStorage.clear();
        });

        // Define all protected routes
        const protectedRoutes = [
            '/social',           // STAFF, ADMIN only
            '/neederPending',    // STAFF, ADMIN only
            '/driving',          // STAFF, ADMIN only
            '/cookConstraints',  // STAFF, ADMIN, DONOR only
            '/driversConstraints', // STAFF, ADMIN, DONOR only
            '/donorConstraints', // STAFF, ADMIN, DONOR only
            '/cookManager',      // STAFF, ADMIN only
            '/donorPending',     // STAFF, ADMIN only
            '/donors',           // STAFF, ADMIN only
            '/needers'           // STAFF, ADMIN only
        ];

        // Test each protected route
        protectedRoutes.forEach((route) => {
            cy.log(`Testing unauthenticated access to: ${route}`);

            // Attempt to visit the protected route without being logged in
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(2000); // Wait for the page to load

            // Assert that the user is redirected to login page
            cy.url().should('include', '/login');

            cy.wait(500);
        });
    });


    it('should allow admin to access all protected routes', () => {
        // Login as admin
        cy.visit('http://localhost:5173/login');

        cy.get('input#phoneNumber')
            .type('0531223423') // Replace with actual admin credentials
            .should('have.value', '0531223423');

        cy.get('input#password')
            .type('adminPassword123') // Replace with actual admin password
            .should('have.value', 'adminPassword123');

        cy.get('button[type="submit"]').click();
        cy.wait(5000); // Wait for the page to load
        cy.url().should('include', '/');

        // Define all protected routes that admin should be able to access
        const adminAccessibleRoutes = [
            '/social',           // STAFF, ADMIN
            '/neederPending',    // STAFF, ADMIN
            '/driving',          // STAFF, ADMIN
            '/cookConstraints',  // STAFF, ADMIN, DONOR
            '/driversConstraints', // STAFF, ADMIN, DONOR
            '/donorConstraints', // STAFF, ADMIN, DONOR
            '/cookManager',      // STAFF, ADMIN
            '/donorPending',     // STAFF, ADMIN
            '/donors',           // STAFF, ADMIN
            '/needers',          // STAFF, ADMIN
            '/go',               // STAFF, ADMIN, DONOR
            '/editDonorDetails'  // STAFF, ADMIN, DONOR
        ];

        // Test each route that admin should be able to access
        adminAccessibleRoutes.forEach((route) => {
            cy.log(`Testing admin access to: ${route}`);

            // Visit the protected route
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(2000); // Wait for the page to load

            // Assert that admin can access the route (not redirected to homepage)
            cy.url().should('include', route);

            cy.wait(500);
        });

        // Also test public routes
        const publicRoutes = ['/', '/donation', '/pictures'];
        publicRoutes.forEach((route) => {
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(1000);
            cy.url().should('include', route);
        });
    });


    it('should allow donor to access his certain routes', () => {
        // Login as donor
        cy.visit('http://localhost:5173/login');

        cy.get('input#phoneNumber')
            .type('0531223421') // Replace with actual donor credentials
            .should('have.value', '0531223421');

        cy.get('input#password')
            .type('donorPassword123') // Replace with actual donor password
            .should('have.value', 'donorPassword123');

        cy.get('button[type="submit"]').click();
        cy.wait(5000); // Wait for the page to load
        cy.url().should('include', '/');

        // Define all protected routes that admin should be able to access
        const adminAccessibleRoutes = [
            '/cookConstraints',  // STAFF, ADMIN, DONOR
            '/driversConstraints', // STAFF, ADMIN, DONOR
            '/donorConstraints', // STAFF, ADMIN, DONOR
            '/go',               // STAFF, ADMIN, DONOR
            '/editDonorDetails'  // STAFF, ADMIN, DONOR
        ];

        // Test each route that donor should be able to access
        adminAccessibleRoutes.forEach((route) => {
            cy.log(`Testing donor access to: ${route}`);

            // Visit the protected route
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(2000); // Wait for the page to load

            // Assert that admin can access the route (not redirected to homepage)
            cy.url().should('include', route);

            cy.wait(500);
        });

        // Also test public routes
        const publicRoutes = ['/', '/donation', '/pictures'];
        publicRoutes.forEach((route) => {
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(1000);
            cy.url().should('include', route);
        });
    });


    it('should allow guest to certain routes', () => {
        // Clear any existing session/cookies to ensure user is not logged in
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.window().then((win) => {
            win.sessionStorage.clear();
        });

        cy.visit('http://localhost:5173');
        cy.wait(1000); // Wait for the page to load

        // test public routes
        const publicRoutes = ['/', '/donation', '/pictures', '/login', '/donorRegister'];
        publicRoutes.forEach((route) => {
            cy.visit(`http://localhost:5173${route}`);
            cy.wait(1000);
            cy.url().should('include', route);
        });
    });

});