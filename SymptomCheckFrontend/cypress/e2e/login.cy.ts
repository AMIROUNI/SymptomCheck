// cypress/e2e/login.cy.ts

describe('Login Page - E2E (avec données statiques)', () => {
  // Store the mock service so it persists across reloads
  let mockAuthServiceInstance: any;

  // On mocke AuthService directement dans le navigateur
  const mockAuthService = () => {
    cy.window().then((win: any) => {
      // Reuse existing mock or create new one
      if (!mockAuthServiceInstance) {
        mockAuthServiceInstance = {
          isAuthenticated: () => {
            return !!localStorage.getItem('access_token');
          },
          loginWithCredentials: (username: string, _password: string) => {
            return new Promise<void>((resolve, reject) => {
              setTimeout(() => {
                // Données statiques selon l'utilisateur
                const users: Record<string, { roles: string[], redirect: string }> = {
                  'yasser': { roles: ['PATIENT'], redirect: '/' },
                  'patient1': { roles: ['PATIENT'], redirect: '/' },
                  'patient2': { roles: ['PATIENT'], redirect: '/' },
                  'dassmello': { roles: ['DOCTOR'], redirect: '/doctor-dashboard' },
                  'doctor1': { roles: ['DOCTOR'], redirect: '/doctor-dashboard' },
                  'doctor2': { roles: ['DOCTOR'], redirect: '/doctor-dashboard' },
                  'admin': { roles: ['ADMIN'], redirect: '/admoun' },
                  'admin1': { roles: ['ADMIN', 'DOCTOR'], redirect: '/admoun' },
                };

                const userData = users[username];
                
                if (!userData) {
                  reject(new Error('Invalid credentials'));
                  return;
                }

                // On simule le stockage du token + rôles
                localStorage.setItem('access_token', 'fake-jwt-token');
                localStorage.setItem('user_roles', JSON.stringify(userData.roles));
                localStorage.setItem('username', username);
                localStorage.setItem('redirect_path', userData.redirect);

                resolve();
              }, 800);
            });
          },
          getUserRoles: () => {
            const roles = localStorage.getItem('user_roles');
            return roles ? JSON.parse(roles) : [];
          },
          logout: () => {
            localStorage.clear();
          },
        };
      }

      // On remplace le vrai service par notre mock
      win['__mockAuthService'] = mockAuthServiceInstance;
      Object.defineProperty(win, 'authService', {
        get: () => win['__mockAuthService'],
        configurable: true,
      });
    });
  };

  beforeEach(() => {
    // Nettoyage complet avant chaque test
    cy.clearLocalStorage();
    cy.clearCookies();
    
    // Reset the mock instance for each test
    mockAuthServiceInstance = null;

    // Mock du service AVANT de charger la page
    mockAuthService();

    cy.visit('localhost:4200/login');
  });

  it('should display login form', () => {
    cy.get('h2').should('contain.text', 'Welcome Back');
    cy.contains('patient1').should('be.visible');
    cy.contains('doctor1').should('be.visible');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should login as patient1 → redirect to home /', () => {
    cy.get('input[formControlName="username"]').type('yasser');
    cy.get('input[formControlName="password"]').type('123456789');
    cy.get('button[type="submit"]').click();

    cy.get('.spinner-border').should('be.visible');
    
    // Wait for navigation to complete
    cy.url({ timeout: 10000 }).should('eq', 'http://localhost:4200/');
  });

  it('should login as doctor1 → redirect to /doctor-dashboard', () => {
    cy.get('input[formControlName="username"]').type('dassmello');
    cy.get('input[formControlName="password"]').type('123456');
    cy.get('button[type="submit"]').click();

    cy.url({ timeout: 10000 }).should('include', '/doctor-dashboard');
  });

  it('should login as admin → redirect to /admoun', () => {
    cy.get('input[formControlName="username"]').type('admin');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();

    cy.url({ timeout: 10000 }).should('include', '/admoun');
  });

  it('should show error with unknown user', () => {
    cy.get('input[formControlName="username"]').type('unknownuser');
    cy.get('input[formControlName="password"]').type('wrong');
    cy.get('button[type="submit"]').click();

    cy.get('.alert-danger')
      .should('be.visible')
      .and('contain.text', 'Invalid credentials');
  });

  
});