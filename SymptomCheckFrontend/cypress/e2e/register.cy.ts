describe('Register Page - E2E', () => {
  // Intercepter les requêtes API
  const interceptApiRequests = () => {
    // Intercepter la récupération des cliniques
    cy.intercept('GET', '**/api/v1/medical/clinic**', {
      statusCode: 200,
      body: [
        { id: 1, name: 'Clinique Central', address: '123 Main Street, City' },
        { id: 2, name: 'Hôpital General', address: '456 Oak Avenue, Town' },
        { id: 3, name: 'Centre Médical Nord', address: '789 Pine Road, Village' }
      ]
    }).as('getClinics');

    // Intercepter l'inscription patient
    cy.intercept('POST', '**/api/v1/users/register', (req) => {
      const body = req.body;
      
      // Simuler une réponse réussie
      req.reply({
        statusCode: 201,
        body: {
          message: 'User registered successfully',
          userId: Math.floor(Math.random() * 1000),
          username: body.username
        },
        delay: 1000
      });
    }).as('registerUser');

    // Intercepter l'upload de fichier
    cy.intercept('POST', '**/api/v1/upload**', {
      statusCode: 200,
      body: {
        filename: 'profile.jpg',
        url: 'https://example.com/uploads/profile.jpg'
      }
    }).as('uploadFile');
  };

  beforeEach(() => {
    // Nettoyage avant chaque test
    cy.clearLocalStorage();
    cy.clearCookies();
    
    // Configurer les intercepteurs
    interceptApiRequests();

    // Visiter la page d'inscription
    cy.visit('localhost:4200/register');
  });

 it('should display register form with all fields', () => {
  // Vérifier le titre
  cy.get('h2').should('contain.text', 'Create Your Account');

  // Vérifier les boutons de rôle
  cy.contains('button', 'Patient').should('be.visible');
  cy.contains('button', 'Doctor').should('be.visible');

  // Vérifier que Patient est sélectionné par défaut
  cy.contains('button', 'Patient').should('have.class', 'btn-primary');
  cy.contains('button', 'Doctor').should('have.class', 'btn-outline-primary');

  // Vérifier les champs communs
  cy.get('input[formControlName="firstName"]').should('be.visible');
  cy.get('input[formControlName="lastName"]').should('be.visible');
  cy.get('input[formControlName="email"]').should('be.visible');
  cy.get('input[formControlName="username"]').should('be.visible');
  cy.get('input[formControlName="password"]').should('be.visible');
  cy.get('input[formControlName="confirmPassword"]').should('be.visible');
  cy.get('input[formControlName="phoneNumber"]').should('be.visible');
  cy.get('input[id="profilePhoto"]').should('be.visible');

  // Vérifier que les champs doctor ne sont PAS dans le DOM initialement
  // (puisqu'ils sont conditionnés par *ngIf)
  cy.get('body').then(($body) => {
    // Vérifier que les champs doctor n'existent pas dans le DOM
    const specialityExists = $body.find('input[formControlName="speciality"]').length > 0;
    const descriptionExists = $body.find('textarea[formControlName="description"]').length > 0;
    const diplomaExists = $body.find('input[formControlName="diploma"]').length > 0;
    const clinicExists = $body.find('select[formControlName="clinicId"]').length > 0;

    expect(specialityExists).to.be.false;
    expect(descriptionExists).to.be.false;
    expect(diplomaExists).to.be.false;
    expect(clinicExists).to.be.false;
  });

  // Vérifier que la section doctor-fields n'existe pas
  cy.get('.doctor-fields').should('not.exist');

  // Vérifier le bouton de soumission
  cy.get('button[type="submit"]')
    .should('be.visible')
    .and('contain.text', 'Register as Patient')
    .and('be.disabled');

  // Vérifier le lien de connexion
  cy.contains('Already have an account?').should('be.visible');
  cy.contains('a', 'Login').should('have.attr', 'href', '/login');
});

  it('should switch between patient and doctor roles', () => {
    // Vérifier l'état initial (Patient)
    cy.contains('button', 'Patient').should('have.class', 'btn-primary');
    cy.get('button[type="submit"]').should('contain.text', 'Register as Patient');

    // Passer à Doctor
    cy.contains('button', 'Doctor').click();

    // Vérifier le changement
    cy.contains('button', 'Patient').should('have.class', 'btn-outline-primary');
    cy.contains('button', 'Doctor').should('have.class', 'btn-primary');
    cy.get('button[type="submit"]').should('contain.text', 'Register as Doctor');

    // Vérifier que les champs doctor sont maintenant visibles
    cy.get('input[formControlName="speciality"]').should('be.visible');
    cy.get('textarea[formControlName="description"]').should('be.visible');
    cy.get('input[formControlName="diploma"]').should('be.visible');
    cy.get('select[formControlName="clinicId"]').should('be.visible');

    // Revenir à Patient
    cy.contains('button', 'Patient').click();

    // Vérifier le retour à l'état initial
    cy.contains('button', 'Patient').should('have.class', 'btn-primary');
    cy.contains('button', 'Doctor').should('have.class', 'btn-outline-primary');
    cy.get('button[type="submit"]').should('contain.text', 'Register as Patient');
  });

  it('should validate form fields for patient registration', () => {
    // Remplir le formulaire avec des données valides
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('john.doe@example.com');
    cy.get('input[formControlName="username"]').type('johndoe');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('input[formControlName="confirmPassword"]').type('password123');
    cy.get('input[formControlName="phoneNumber"]').type('123-456-7890');

    // Vérifier que le bouton est activé
    cy.get('button[type="submit"]').should('not.be.disabled');

    // Tester la validation des champs requis
    cy.get('input[formControlName="firstName"]').clear();
    cy.get('input[formControlName="firstName"]').blur();
    cy.get('input[formControlName="firstName"]').should('have.class', 'is-invalid');
    cy.contains('First name is required').should('be.visible');

    // Corriger l'erreur
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="firstName"]').should('not.have.class', 'is-invalid');

    // Tester la validation d'email
    cy.get('input[formControlName="email"]').clear().type('invalid-email');
    cy.get('input[formControlName="email"]').blur();
    cy.get('input[formControlName="email"]').should('have.class', 'is-invalid');
    cy.contains('Please enter a valid email address').should('be.visible');

    // Tester la validation de mot de passe
    cy.get('input[formControlName="password"]').clear().type('123');
    cy.get('input[formControlName="password"]').blur();
    cy.get('input[formControlName="password"]').should('have.class', 'is-invalid');
    cy.contains('Password must be at least 6 characters').should('be.visible');

    // Tester la non-correspondance des mots de passe
    cy.get('input[formControlName="password"]').clear().type('password123');
    cy.get('input[formControlName="confirmPassword"]').clear().type('different');
    cy.get('input[formControlName="confirmPassword"]').blur();
    cy.get('input[formControlName="confirmPassword"]').should('have.class', 'is-invalid');
    cy.contains('Passwords do not match').should('be.visible');
  });

  it('should validate doctor-specific fields when doctor role is selected', () => {
    // Sélectionner le rôle Doctor
    cy.contains('button', 'Doctor').click();

    // Remplir les champs communs
    cy.get('input[formControlName="firstName"]').type('Jane');
    cy.get('input[formControlName="lastName"]').type('Smith');
    cy.get('input[formControlName="email"]').type('jane.smith@example.com');
    cy.get('input[formControlName="username"]').type('janesmith');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('input[formControlName="confirmPassword"]').type('password123');
    cy.get('input[formControlName="phoneNumber"]').type('123-456-7890');

    // Vérifier que le bouton est toujours désactivé (champs doctor manquants)
    cy.get('button[type="submit"]').should('be.disabled');

    // Remplir les champs doctor
    cy.get('input[formControlName="speciality"]').type('Cardiology');
    cy.get('textarea[formControlName="description"]').type('Experienced cardiologist with 10 years of practice.');
    cy.get('input[formControlName="diploma"]').type('MD in Cardiology');

    // Attendre que les cliniques soient chargées
    cy.wait('@getClinics');

    // Sélectionner une clinique
    cy.get('select[formControlName="clinicId"]').select('1');

    // Vérifier que le bouton est maintenant activé
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

 it('should successfully register a patient', () => {
  // Remplir le formulaire patient
  cy.get('input[formControlName="firstName"]').type('Alice');
  cy.get('input[formControlName="lastName"]').type('Johnson');
  cy.get('input[formControlName="email"]').type('alice.johnson@example.com');
  cy.get('input[formControlName="username"]').type('alicej');
  cy.get('input[formControlName="password"]').type('securepass123');
  cy.get('input[formControlName="confirmPassword"]').type('securepass123');
  cy.get('input[formControlName="phoneNumber"]').type('5551234567');

  // Soumettre le formulaire
  cy.get('button[type="submit"]').click();

  // Vérifier l'indicateur de chargement
  cy.get('.spinner-border').should('be.visible');

  // Vérifier la requête API avec FormData
  cy.wait('@registerUser').then((interception) => {
    // Extraire le JSON du FormData
    const formDataBody = interception.request.body;
    
    // Le JSON est encapsulé dans le FormData, on peut le vérifier en cherchant les chaînes
    expect(formDataBody).to.include('"firstName":"Alice"');
    expect(formDataBody).to.include('"lastName":"Johnson"');
    expect(formDataBody).to.include('"email":"alice.johnson@example.com"');
    expect(formDataBody).to.include('"username":"alicej"');
    expect(formDataBody).to.include('"role":"PATIENT"');
    expect(formDataBody).to.include('"password":"securepass123"');
    expect(formDataBody).to.include('"phoneNumber":"5551234567"');
  });

  // Vérifier la redirection
  cy.url().should('include', '/login');
});
  it('should successfully register a doctor with clinic selection', () => {
  // Sélectionner Doctor
  cy.contains('button', 'Doctor').click();

  // Remplir les champs communs
  cy.get('input[formControlName="firstName"]').type('Robert');
  cy.get('input[formControlName="lastName"]').type('Brown');
  cy.get('input[formControlName="email"]').type('robert.brown@example.com');
  cy.get('input[formControlName="username"]').type('drbrown');
  cy.get('input[formControlName="password"]').type('doctorpass123');
  cy.get('input[formControlName="confirmPassword"]').type('doctorpass123');
  cy.get('input[formControlName="phoneNumber"]').type('555-987-6543');

  // Remplir les champs doctor
  cy.get('input[formControlName="speciality"]').type('Neurology');
  cy.get('textarea[formControlName="description"]').type('Board certified neurologist specializing in brain disorders.');
  cy.get('input[formControlName="diploma"]').type('MD, Neurology Board Certification');

  // Attendre le chargement des cliniques
  cy.wait('@getClinics');

  // Sélectionner une clinique
  cy.get('select[formControlName="clinicId"]').select('2');

  // Soumettre le formulaire
  cy.get('button[type="submit"]').click();

  // Vérifier la requête API avec FormData
  cy.wait('@registerUser').then((interception) => {
    const formDataBody = interception.request.body;
    
    // Vérifier les données dans le FormData
    expect(formDataBody).to.include('"firstName":"Robert"');
    expect(formDataBody).to.include('"lastName":"Brown"');
    expect(formDataBody).to.include('"email":"robert.brown@example.com"');
    expect(formDataBody).to.include('"username":"drbrown"');
    expect(formDataBody).to.include('"role":"DOCTOR"');
    expect(formDataBody).to.include('"speciality":"Neurology"');
    expect(formDataBody).to.include('"clinicId":"2"'); // Note: clinicId est string
    expect(formDataBody).to.include('"description":"Board certified neurologist specializing in brain disorders."');
    expect(formDataBody).to.include('"diploma":"MD, Neurology Board Certification"');
  });

  // Vérifier la redirection
  cy.url().should('include', '/login');
});
 it('should handle file upload for profile photo', () => {
  // Remplir le formulaire de base
  cy.get('input[formControlName="firstName"]').type('Emma');
  cy.get('input[formControlName="lastName"]').type('Wilson');
  cy.get('input[formControlName="email"]').type('emma.wilson@example.com');
  cy.get('input[formControlName="username"]').type('emmaw');
  cy.get('input[formControlName="password"]').type('emmapass123');
  cy.get('input[formControlName="confirmPassword"]').type('emmapass123');
  cy.get('input[formControlName="phoneNumber"]').type('555-111-2222');

  // Uploader un fichier
  cy.get('input[id="profilePhoto"]').selectFile({
    contents: Cypress.Buffer.from('file contents'),
    fileName: 'profile.jpg',
    mimeType: 'image/jpeg',
    lastModified: Date.now(),
  });

  // Vérifier la prévisualisation
  cy.get('img[alt="Preview"]').should('be.visible');

  // Soumettre le formulaire
  cy.get('button[type="submit"]').click();

  // Vérifier que la requête d'inscription est envoyée avec le fichier
  cy.wait('@registerUser').then((interception) => {
    const formDataBody = interception.request.body;
    
    // Vérifier que le FormData contient les données utilisateur
    expect(formDataBody).to.include('"firstName":"Emma"');
    expect(formDataBody).to.include('"lastName":"Wilson"');
    expect(formDataBody).to.include('"username":"emmaw"');
    
    // Vérifier que le FormData contient des informations de fichier
    // (le fichier lui-même peut être dans une partie séparée du FormData)
    expect(formDataBody).to.include('profile.jpg');
    expect(formDataBody).to.include('image/jpeg');
  });

  // Vérifier la redirection
  cy.url().should('include', '/login');
});

 it('should display error message on registration failure', () => {
  // Intercepter et simuler une erreur d'inscription
  cy.intercept('POST', '**/api/v1/users/register**', {
    statusCode: 400,
    body: {
      message: 'Username already exists'
    },
    delay: 500
  }).as('registerError');

  // Remplir le formulaire
  cy.get('input[formControlName="firstName"]').type('Test');
  cy.get('input[formControlName="lastName"]').type('User');
  cy.get('input[formControlName="email"]').type('test@example.com');
  cy.get('input[formControlName="username"]').type('existinguser');
  cy.get('input[formControlName="password"]').type('password123');
  cy.get('input[formControlName="confirmPassword"]').type('password123');
  cy.get('input[formControlName="phoneNumber"]').type('123-456-7890');

  // Soumettre
  cy.get('button[type="submit"]').click();

  // Attendre la réponse d'erreur
  cy.wait('@registerError');

  // Vérifier que nous sommes toujours sur la page d'inscription (pas de redirection)
  cy.url().should('include', '/register');

  // Vérifier le message d'erreur générique
  cy.get('.alert-danger')
    .should('be.visible')
    .and('contain.text', 'Username already exists');

  // Vérifier que le spinner a disparu et le bouton est réactivé
  cy.get('.spinner-border').should('not.exist');
  cy.get('button[type="submit"]').should('not.be.disabled');
});
  it('should load and display clinics dropdown', () => {
  // Sélectionner Doctor pour afficher le dropdown des cliniques
  cy.contains('button', 'Doctor').click();

  // Attendre le chargement des cliniques
  cy.wait('@getClinics');

  // Vérifier le dropdown
  cy.get('select[formControlName="clinicId"]').should('be.visible');
  
  // Vérifier le nombre d'options (option vide + 3 cliniques)
  cy.get('select[formControlName="clinicId"] option').should('have.length', 4);

  // Debug: Afficher le contenu réel des options pour voir le format
  cy.get('select[formControlName="clinicId"] option').each(($option, index) => {
    cy.log(`Option ${index}: ${$option.text()}`);
  });

  // Vérifier les options avec une approche plus flexible
  cy.get('select[formControlName="clinicId"]')
    .find('option')
    .then($options => {
      const texts = Array.from($options).map(option => option.textContent?.trim());
      
      // Vérifier avec des includes partiels au lieu de matches exacts
      expect(texts.some(text => text?.includes('Clinique Central'))).to.be.true;
      expect(texts.some(text => text?.includes('Hôpital General'))).to.be.true;
      expect(texts.some(text => text?.includes('Centre Médical Nord'))).to.be.true;
    });
});

  it('should handle clinic loading error', () => {
    // Intercepter et simuler une erreur de chargement des cliniques
    cy.intercept('GET', '**/api/v1/medical-clinics**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('getClinicsError');

    // Sélectionner Doctor pour déclencher le chargement
    cy.contains('button', 'Doctor').click();

    // Vérifier que le message de chargement disparaît
    cy.contains('Loading clinics...').should('not.exist');

    // Le dropdown devrait toujours être présent mais vide
    cy.get('select[formControlName="clinicId"]').should('be.visible');
  });
});