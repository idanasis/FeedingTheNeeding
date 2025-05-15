describe('Homepage loads', () => {
    it('should display the homepage with expected content', () => {
        cy.visit('/');
        cy.contains('להשביע את הלב'); // Replace with actual visible text on your homepage
    });
});
