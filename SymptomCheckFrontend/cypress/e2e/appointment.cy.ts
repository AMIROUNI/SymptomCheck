describe('Appointment Booking Component - Static Test', () => {

  const patientId = 'b93b7591-8268-456c-9d3b-9f801b3b75f4';
  const doctorId = 'fc6274ff-730a-44f0-9245-17ada9054fe8';

 beforeEach(() => {
  cy.visit("http://localhost:4200/login");

  // Wait for the username field to appear (this proves the app is loaded)
  cy.get('input[name=username]', { timeout: 10000 }).should('be.visible');

  // Now safely login
  cy.get('input[name=username]').type("yasser");
  cy.get('input[name=password]').type("123456789");
  cy.get("button[type=submit]").click();

  // Wait for navigation after login
  cy.url({ timeout: 10000 }).should("include", "/appointment");
});

  it("should load and show the form", () => {
    cy.get(".loading-state").should("exist");

    cy.get(".appointment-form", { timeout: 10000 })
      .should("exist");
  });

  it("should select doctor and service correctly", () => {
    // STEP 1
    cy.get("#doctorId").should("exist").select(doctorId);

    cy.get("#serviceId", { timeout: 5000 })
      .find("option")
      .should("have.length.greaterThan", 1);

    cy.get("#serviceId").select(1);

    cy.contains("Next: Date & Time").should("be.enabled").click();
  });

  it("should select date and time slot", () => {
    // Repeat Step 1 (mandatory for navigation)
    cy.get("#doctorId").select(doctorId);
    cy.get("#serviceId").select(1);
    cy.contains("Next: Date & Time").click();

    // STEP 2
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateStr = tomorrow.toISOString().split("T")[0];

    cy.get("#date").type(dateStr);

    cy.get(".time-slot", { timeout: 10000 })
      .first()
      .click();

    cy.contains("Next: Details").click();
  });

  it("should fill details and submit successfully", () => {
    // Step 1
    cy.get("#doctorId").select(doctorId);
    cy.get("#serviceId").select(1);
    cy.contains("Next: Date & Time").click();

    // Step 2
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateStr = tomorrow.toISOString().split("T")[0];

    cy.get("#date").type(dateStr);
    cy.get(".time-slot").first().click();
    cy.contains("Next: Details").click();

    // Step 3
    cy.get("#description").type("Static description for Cypress Test.");

    cy.contains("Confirm Appointment")
      .should("be.enabled")
      .click();

    cy.get(".success-message", { timeout: 8000 })
      .should("exist");
  });

});
