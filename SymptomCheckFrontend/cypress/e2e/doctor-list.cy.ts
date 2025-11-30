describe('Doctor List Component', () => {

  beforeEach(() => {
    // Va dans la page qui contient <app-doctor-list>
    cy.visit('http://localhost:4200/doctors'); 
  });

  it('should display the title "Find a Doctor"', () => {
    cy.contains('Find a Doctor').should('exist');
  });

  it('should load doctor cards', () => {
    cy.get('app-doctor-card').should('exist');
  });

  it('should filter doctors by search term', () => {
    cy.get('input[placeholder="Search by name or specialty..."]')
      .type('ali');

    cy.get('app-doctor-card').each(card => {
      cy.wrap(card).should('contain.text', 'ali');
    });
  });

  it('should filter by specialty', () => {
    cy.get('select.form-select').select(1); // sélectionne la 1ère spécialité

    cy.get('app-doctor-card').should('exist');

    // Vérifie qu'il y a des résultats
    cy.get('.no-results').should('not.exist');
  });

});
