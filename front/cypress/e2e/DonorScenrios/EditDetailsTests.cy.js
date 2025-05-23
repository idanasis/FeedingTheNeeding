const PAGE_LOAD_WAIT = 2000;
describe('Donor Edit Page', () => {
    before(() => {
    cy.request('POST', 'http://localhost:8080/test/setup') // Replace with the correct API endpoint
            .then((response) => {
                // Assert that the setup was successful
                expect(response.body).to.include('Test data inserted ✅');
            });
   

  });  
  const donor = {
    email: 'admin@example.com',
    firstName: 'admin',
    lastName: 'Admin',
    phoneNumber: '0531223423',
    address: 'admin Address',
  };
  const newDonor = {
    email: 'new@example.com',
    firstName: 'אורי',
    lastName: 'לוי',
    phoneNumber: '0509876543',
    address: 'ירושלים',
    };
  beforeEach(() => {
    // Stub authenticated donor response
    // cy.request('POST', 'http://localhost:8080/test/setup'); // Add a pending needy in setup
    cy.wait(PAGE_LOAD_WAIT);
    cy.visit('/login');
    cy.get('input#phoneNumber').type('0531223423');
    cy.get('input#password').type('adminPassword123');
    cy.get('button[type="submit"]').click();

    cy.wait(PAGE_LOAD_WAIT);
    cy.visit('/editDonorDetails'); // Adjust to your actual route
    cy.wait(PAGE_LOAD_WAIT);
  });

  it('should render form with donor details', () => {
    cy.get('input#email').should('have.value', donor.email);
    cy.get('input#firstName').should('have.value', donor.firstName);
    cy.get('input#lastName').should('have.value', donor.lastName);
    cy.get('input#phoneNumber').should('have.value', donor.phoneNumber);
    cy.get('input#address').should('have.value', donor.address);
  });

  it('should show validation error on invalid email', () => {
    cy.get('input#email').clear().type('invalid-email');
    cy.get('form').submit();
    cy.contains('כתובת האימייל אינה תקינה').should('be.visible');
  });

  it('should show validation error on invalid phone number', () => {
    cy.get('input#phoneNumber').clear().type('12345');
    cy.get('form').submit();
    cy.contains('מספר הטלפון אינו תקין').should('be.visible');
  });

  it('should submit valid form and show success message', () => {
    cy.get('input#email').clear().type(newDonor.email);
    cy.get('input#firstName').clear().type(newDonor.firstName);
    cy.get('input#lastName').clear().type(newDonor.lastName);
    cy.get('input#phoneNumber').clear().type(newDonor.phoneNumber);
    cy.get('input#address').clear().type(newDonor.address);

    cy.get('form').submit();

    cy.wait(PAGE_LOAD_WAIT);
    cy.contains('הנתונים עודכנו בהצלחה!').should('be.visible');
    cy.get('.success-popup').should('be.visible');
    cy.get('.success-popup button').click();
    cy.get('.success-popup').should('not.exist');
  });
});
