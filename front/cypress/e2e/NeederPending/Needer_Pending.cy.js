const PAGE_LOAD_WAIT = 2000;

describe('Needy Approval Page', () => {
  beforeEach(() => {
    cy.request('POST', 'http://localhost:8080/test/setup'); // Add a pending needy in setup

    cy.visit('/login');
    cy.get('input#phoneNumber').type('0531223423');
    cy.get('input#password').type('adminPassword123');
    cy.get('button[type="submit"]').click();

    cy.wait(PAGE_LOAD_WAIT);
    cy.visit('/neederPending'); // Adjust to your actual route
    cy.wait(PAGE_LOAD_WAIT);
  });

  it('approves a pending needy', () => {
    // Wait for the table to load at least one needy
    cy.get('table tbody tr').should('have.length.greaterThan', 0);

    // Store count before approval
    cy.get('table tbody tr').then((rowsBefore) => {
      const countBefore = rowsBefore.length;

      // Click "אשר" (Approve) on the first row
      cy.get('table tbody tr').first().within(() => {
        cy.contains('אשר').click();
      });

      // Wait for approval to complete and fetchNeeders() to refresh list
      cy.wait(1000);

      // Assert that one less row appears
      cy.get('table tbody tr').should('have.length.lessThan', countBefore);
    });
  });
});
