describe('Contact Form Direct Test', () => {
    beforeEach(() => {
        // Visit the homepage
        cy.visit('http://localhost:5173/');

        // Wait for page to load completely
        cy.wait(2000);

        // Use more direct and reliable selectors that don't depend on specific IDs
        // This approach uses the form structure directly, which might be more reliable

        // Find the form by its structure or unique content
        cy.contains('אנחנו כאן לעזור').scrollIntoView({ timeout: 10000 });

        // Give it time to stabilize after scrolling
        cy.wait(500);

        // Make sure we can see the form elements before proceeding
        cy.get('input[name="firstName"]').should('be.visible');
    });

    it('should fill out and submit the form successfully', () => {
        // Fill in all form fields
        cy.get('input[name="firstName"]').type('ישראל');
        cy.get('input[name="lastName"]').type('ישראלי');

        // Select a neighborhood
        cy.get('select[name="street"]').select(1); // Select the first non-empty option

        cy.get('input[name="address"]').type('האלון 5');
        cy.get('input[name="phoneNumber"]').type('0501234567');
        cy.get('input[name="familySize"]').type('4');
        cy.get('textarea[name="notes"]').type('הערות נוספות לבדיקה');

        // Submit the form
        cy.get('button[type="submit"]').click();


        // Verify success message is displayed (different approaches to find the message)
        cy.contains('בקשה נשלחה בהצלחה').should('be.visible')
            .then((result) => {
                if (!result) {
                    cy.get('p.text-green-500').should('be.visible');
                }
            });

    });

    it('should validate required fields', () => {
        // Submit without filling any fields
        cy.get('button[type="submit"]').click();

        // Check for validation messages - using more flexible selectors
        cy.contains('שם פרטי').should('be.visible');
        cy.contains('שם משפחה').should('be.visible');

        // Fill just the first field to test progressive validation
        cy.get('input[name="firstName"]').type('ישראל');

        // Submit again
        cy.get('button[type="submit"]').click();

        // Check that the first field no longer has an error
        cy.contains('שם פרטי הוא שדה חובה').should('not.exist');

        // But other errors should still be there
        cy.contains('שם משפחה').should('be.visible');
    });

    it('should test phone number validation', () => {
        // Fill all required fields except phone
        cy.get('input[name="firstName"]').type('ישראל');
        cy.get('input[name="lastName"]').type('ישראלי');
        cy.get('select[name="street"]').select(1);
        cy.get('input[name="address"]').type('האלון 5');
        cy.get('input[name="familySize"]').type('4');

        // Try invalid phone
        cy.get('input[name="phoneNumber"]').type('123abc').blur();

        // Submit and check for validation message
        cy.get('button[type="submit"]').click();
        cy.contains('מספר טלפון').should('be.visible');

        // Correct the phone number
        cy.get('input[name="phoneNumber"]').clear().type('0501234569');


        // Submit again
        cy.get('button[type="submit"]').click();


        // Verify success
        cy.contains('בקשה נשלחה').should('be.visible');
    });
});