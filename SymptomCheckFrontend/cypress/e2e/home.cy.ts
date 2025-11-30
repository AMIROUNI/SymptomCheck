describe('Home Page - E2E', () => {
  // Intercepter les requêtes API
  const interceptApiRequests = () => {
    // Intercepter la récupération des docteurs
    cy.intercept('GET', 'localhost:8087/api/v1/doctors**', {
      statusCode: 200,
      body: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          speciality: 'Cardiology',
          profilePhotoUrl: 'https://example.com/doctor1.jpg',
          rating: 4.5
        },
        {
          id: 2,
          firstName: 'Jane',
          lastName: 'Smith',
          speciality: 'Neurology',
          profilePhotoUrl: 'https://example.com/doctor2.jpg',
          rating: 4.8
        },
        {
          id: 3,
          firstName: 'Mike',
          lastName: 'Johnson',
          speciality: 'Pediatrics',
          profilePhotoUrl: 'https://example.com/doctor3.jpg',
          rating: 4.3
        },
        {
          id: 4,
          firstName: 'Sarah',
          lastName: 'Wilson',
          speciality: 'Dermatology',
          profilePhotoUrl: 'https://example.com/doctor4.jpg',
          rating: 4.7
        }
      ]
    }).as('getDoctors');
  };

  beforeEach(() => {
    // Configurer les intercepteurs
    interceptApiRequests();

    // Visiter la page d'accueil
    cy.visit('localhost:4200');
  });

  it('should display hero section with all elements', () => {
    // Vérifier le titre hero
    cy.get('.hero-title')
      .should('be.visible')
      .and('contain.text', 'Your Health, Our Priority');

    // Vérifier le sous-titre
    cy.get('.hero-subtitle')
      .should('be.visible')
      .and('contain.text', 'Connect with trusted healthcare professionals');

    // Vérifier les boutons
    cy.contains('button', 'Find a Doctor')
      .should('be.visible')
      .and('have.class', 'btn-primary');

    cy.contains('button', 'Book Appointment')
      .should('be.visible')
      .and('have.class', 'btn-outline-primary');

    // Vérifier l'image hero (sur desktop)
    cy.get('.hero-image')
      .should('be.visible')
      .and('have.attr', 'src', 'background.jpg')
      .and('have.attr', 'alt', 'Healthcare professionals');
  });

  it('should display features section with all feature cards', () => {
    // Vérifier les 3 cartes de features
    cy.get('.feature-card').should('have.length', 3);

    // Vérifier la première carte
    cy.get('.feature-card').eq(0).within(() => {
      cy.get('.feature-icon i').should('have.class', 'fa-user-md');
      cy.get('h3').should('contain.text', 'Book Trusted Doctors');
      cy.get('p').should('contain.text', 'verified healthcare professionals');
    });

    // Vérifier la deuxième carte
    cy.get('.feature-card').eq(1).within(() => {
      cy.get('.feature-icon i').should('have.class', 'fa-dollar-sign');
      cy.get('h3').should('contain.text', 'Affordable Services');
      cy.get('p').should('contain.text', 'competitive and transparent prices');
    });

    // Vérifier la troisième carte
    cy.get('.feature-card').eq(2).within(() => {
      cy.get('.feature-icon i').should('have.class', 'fa-headset');
      cy.get('h3').should('contain.text', '24/7 Support');
      cy.get('p').should('contain.text', 'always available to assist you');
    });
  });

  it('should display featured doctors section with loading state and doctors', () => {
  cy.visit('localhost:4200');

  // Vérifier que la section des docteurs existe
  cy.get('.doctors-section').should('exist');

  // Vérifier le titre et la description
  cy.contains('h2', 'Meet Our Specialists').should('be.visible');
  cy.contains('p', 'Experienced doctors across various specialties').should('be.visible');

  // Attendre que les cartes de docteurs soient chargées
  cy.get('.doctor-card', { timeout: 15000 })
    .should('exist')
    .and('have.length.at.least', 1)
    .and('be.visible');

  // Vérifier que chaque carte a le contenu attendu
  cy.get('.doctor-card').each(($card) => {
    cy.wrap($card).within(() => {
      // Vérifier le nom du docteur (commence par Dr.)
      cy.get('h3').invoke('text').should('match', /Dr\./);
      
      // Vérifier la spécialité
      cy.get('.doctor-specialty').should('not.be.empty');
      
      // Vérifier l'image
      cy.get('img').should('be.visible');
      
      // Vérifier le bouton
      cy.contains('button', 'View Profile').should('be.visible');
    });
  });

  // Vérifier le bouton "View All Doctors"
  cy.contains('button', 'View All Doctors')
    .should('be.visible')
    .and('have.class', 'btn-primary');
});

  it('should display stats section with correct numbers', () => {
    // Vérifier les 4 statistiques
    cy.get('.stat-item').should('have.length', 4);

    // Vérifier chaque stat
    cy.get('.stat-item').eq(0).within(() => {
      cy.get('h3').should('contain.text', '5,000+');
      cy.get('p').should('contain.text', 'Happy Patients');
    });

    cy.get('.stat-item').eq(1).within(() => {
      cy.get('h3').should('contain.text', '100+');
      cy.get('p').should('contain.text', 'Expert Doctors');
    });

    cy.get('.stat-item').eq(2).within(() => {
      cy.get('h3').should('contain.text', '50+');
      cy.get('p').should('contain.text', 'Medical Services');
    });

    cy.get('.stat-item').eq(3).within(() => {
      cy.get('h3').should('contain.text', '10+');
      cy.get('p').should('contain.text', 'Years Experience');
    });
  });

  it('should display CTA section', () => {
    cy.get('.cta-section h2')
      .should('be.visible')
      .and('contain.text', 'Ready to prioritize your health?');

    cy.get('.cta-section p')
      .should('be.visible')
      .and('contain.text', 'Book an appointment with one of our healthcare professionals today.');

    cy.contains('button', 'Book Now')
      .should('be.visible')
      .and('have.class', 'btn-light');
  });

  it('should navigate to doctors page when clicking "Find a Doctor" in hero section', () => {
    cy.contains('button', 'Find a Doctor').click();
    cy.url().should('include', '/doctors');
  });

  it('should navigate to appointment page when clicking "Book Appointment" in hero section', () => {
    cy.contains('button', 'Book Appointment').click();
    cy.url().should('include', '/appointment');
  });

  it('should navigate to doctors page when clicking "View All Doctors"', () => {
    // Attendre le chargement des docteurs
    cy.wait('@getDoctors');
    
    cy.contains('button', 'View All Doctors').click();
    cy.url().should('include', '/doctors');
  });

  it('should navigate to appointment page when clicking "Book Now" in CTA section', () => {
    cy.contains('button', 'Book Now').click();
    cy.url().should('include', '/appointment');
  });

  it('should navigate to doctor profile when clicking "View Profile" on doctor card', () => {
    // Attendre le chargement des docteurs
    cy.wait('@getDoctors');
    
    // Cliquer sur le premier "View Profile"
    cy.get('.doctor-card').eq(0).within(() => {
      cy.contains('button', 'View Profile').click();
    });

    // Vérifier la navigation vers la page du docteur
    cy.url().should('include', '/doctors/1');
  });

  it('should handle doctors loading error gracefully', () => {
    // Intercepter avec une erreur
    cy.intercept('GET', '**/api/v1/doctors**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('getDoctorsError');

    // Recharger la page pour déclencher le nouvel intercepteur
    cy.reload();

    // Vérifier que le spinner disparaît
    cy.get('.spinner-border').should('not.exist');

    // Vérifier qu'aucun docteur n'est affiché
    cy.get('.doctor-card').should('not.exist');

    // Vérifier que le bouton "View All Doctors" est toujours présent
    cy.contains('button', 'View All Doctors').should('be.visible');
  });

  it('should display testimonials section', () => {
    // Vérifier que le composant testimonials est présent
    cy.get('app-testimonials').should('exist');
  });

  it('should be responsive on mobile devices', () => {
    // Tester en mode mobile
    cy.viewport('iphone-6');

    // Vérifier que l'image hero est cachée sur mobile
    cy.get('.hero-image').should('not.be.visible');

    // Vérifier que les boutons hero s'empilent correctement
    cy.get('.hero-buttons').within(() => {
      cy.contains('button', 'Find a Doctor').should('be.visible');
      cy.contains('button', 'Book Appointment').should('be.visible');
    });

    // Vérifier que les statistiques s'adaptent
    cy.get('.stats-section .col-6').should('have.length', 4);
  });
});