describe('Contact Form Direct Test', () => {
    // Constants
    const PAGE_LOAD_WAIT = 2000;
    const POST_SCROLL_WAIT = 500;
    const STREET_INDEX_FIRST_OPTION = 1;

    const TEST_FIRST_NAME = 'דני';
    const TEST_LAST_NAME = 'כהן';
    const TEST_ADDRESS = 'רחוב התמר 12';
    const TEST_PHONE_FORM_SUCCESS = '0501234567';
    const TEST_PHONE_VALIDATION_SUCCESS = '0501234569';
    const TEST_PHONE_INVALID = '123abc';
    const TEST_FAMILY_SIZE = '4';
    const TEST_NOTES = 'בדיקת מערכת - אנא לא לטפל';

    beforeEach(() => {
        cy.visit('http://localhost:5173/');
        cy.wait(PAGE_LOAD_WAIT);

        cy.contains('אנחנו כאן לעזור').scrollIntoView({ timeout: 10000 });
        cy.wait(POST_SCROLL_WAIT);

        cy.get('input[name="firstName"]').should('be.visible');
    });

    it('should fill out and submit the form successfully', () => {
        cy.get('input[name="firstName"]').type(TEST_FIRST_NAME);
        cy.get('input[name="lastName"]').type(TEST_LAST_NAME);
        cy.get('select[name="street"]').select(STREET_INDEX_FIRST_OPTION);
        cy.get('input[name="address"]').type(TEST_ADDRESS);
        cy.get('input[name="phoneNumber"]').type(TEST_PHONE_FORM_SUCCESS);
        cy.get('input[name="familySize"]').type(TEST_FAMILY_SIZE);
        cy.get('textarea[name="notes"]').type(TEST_NOTES);

        cy.get('button[type="submit"]').click();

        cy.contains('בקשה נשלחה בהצלחה').should('be.visible')
            .then((result) => {
                if (!result) {
                    cy.get('p.text-green-500').should('be.visible');
                }
            });
    });

    it('should validate required fields', () => {
        cy.get('button[type="submit"]').click();

        cy.contains('שם פרטי').should('be.visible');
        cy.contains('שם משפחה').should('be.visible');

        cy.get('input[name="firstName"]').type(TEST_FIRST_NAME);
        cy.get('button[type="submit"]').click();

        cy.contains('שם פרטי הוא שדה חובה').should('not.exist');
        cy.contains('שם משפחה').should('be.visible');
    });

    it('should test phone number validation', () => {
        cy.get('input[name="firstName"]').type(TEST_FIRST_NAME);
        cy.get('input[name="lastName"]').type(TEST_LAST_NAME);
        cy.get('select[name="street"]').select(STREET_INDEX_FIRST_OPTION);
        cy.get('input[name="address"]').type(TEST_ADDRESS);
        cy.get('input[name="familySize"]').type(TEST_FAMILY_SIZE);

        cy.get('input[name="phoneNumber"]').type(TEST_PHONE_INVALID).blur();
        cy.get('button[type="submit"]').click();
        cy.contains('מספר טלפון').should('be.visible');

        cy.get('input[name="phoneNumber"]').clear().type(TEST_PHONE_VALIDATION_SUCCESS);
        cy.get('button[type="submit"]').click();

        cy.contains('בקשה נשלחה').should('be.visible');
    });
});
