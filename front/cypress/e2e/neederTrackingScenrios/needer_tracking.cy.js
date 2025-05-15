  const PAGE_LOAD_WAIT = 2000;
describe('Needer Tracking Page', () => {
  before(() => {
    cy.request('POST', 'http://localhost:8080/test/setup') // Replace with the correct API endpoint
            .then((response) => {
                // Assert that the setup was successful
                expect(response.body).to.include('Test data inserted ✅');
            });
   

  });
  beforeEach(() => {
     cy.visit('/login');
    cy.get('input#phoneNumber')
            .type('0531223423') // Admin phone number from the test data
            .should('have.value', '0531223423');

        cy.get('input#password')
            .type('adminPassword123') // Admin password (ensure this matches the setup)
            .should('have.value', 'adminPassword123');

        // Step 4: Submit the login form
    cy.get('button[type="submit"]').click();
    cy.wait(PAGE_LOAD_WAIT);
    cy.visit('/social');
    
    cy.wait(PAGE_LOAD_WAIT);
    cy.visit('/social');
    
    cy.wait(PAGE_LOAD_WAIT);

  });
  it('loads the table and shows rows', () => {
    cy.get('table').should('exist');
    cy.get('table tbody tr').should('have.length.greaterThan', 1);
  });

it('edits food preference and notes and saves', () => {
  // First, find a row that is NOT "זמין" and change it
  cy.get('table tbody tr')
    .contains('לא זמין')
    .parents('tr')
    .first()
    .within(() => {
      cy.get('select').select('זמין');
    });

  // Now wait for status to change in UI — could be via text change or just brief wait
  cy.wait(1000); // Optional, replace with a better condition if available

  // Now find the updated row and expand it
  cy.get('table tbody tr')
    .contains('זמין')
    .parents('tr')
    .first()
    .within(() => {
      cy.get('button[aria-label="expand row"]').click();
    });

  // Fill in the fields
  cy.get('input#foodPreference').clear().type('צמחוני');
  cy.get('input#notes').clear().type('בלי פלפל');

  cy.contains('שמור').click();

  // Confirm alert
  cy.on('window:alert', (str) => {
    expect(str).to.include('הפרטים נשמרו');
  });
});

  it('changes availability dropdown', () => {
    cy.get('table tbody tr')
      .first()
      .within(() => {
        cy.get('select').select('לא זמין');
      });

    // Add validation based on how your app reflects that change (e.g., updated label, alert)
  });
});
