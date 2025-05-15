describe('Needer Tracking Page', () => {
  beforeEach(() => {
    cy.visit('/needer-tracking');
    cy.request('POST', 'http://localhost:8080/test/setup') // Replace with the correct API endpoint
            .then((response) => {
                // Assert that the setup was successful
                expect(response.body).to.include('Test data inserted ✅');
            });

  });

  it('loads the table and shows rows', () => {
    cy.get('table').should('exist');
    cy.get('table tbody tr').should('have.length.greaterThan', 1);
  });

  it('opens the collapsible details for a "זמין" needer', () => {
    cy.get('table tbody tr')
      .contains('זמין')
      .parents('tr')
      .first()
      .within(() => {
        cy.get('button[aria-label="expand row"]').click();
      });

    cy.get('input#foodPreference').should('exist');
    cy.get('input#notes').should('exist');
  });

  it('edits food preference and notes and saves', () => {
    cy.get('table tbody tr')
      .contains('זמין')
      .parents('tr')
      .first()
      .within(() => {
        cy.get('button[aria-label="expand row"]').click();
      });

    cy.get('input#foodPreference').clear().type('צמחוני');
    cy.get('input#notes').clear().type('בלי פלפל');

    cy.contains('שמור').click();

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
