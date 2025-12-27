import { test, expect } from '@playwright/test';

 // Egyedi adatok (ütközés elkerülése regisztrációnál)
  const timestamp = Date.now();
  const username = `testuser_${timestamp}`;
  const email = `test_${timestamp}@example.com`;
  const password = 'Test1234!';

test('Felhasználó regisztráció + bejelentkezés', async ({ page }) => {
  // Regisztrációs oldal
  await page.goto('http://localhost:8080/register');

  // Űrlap kitöltése
  await page.fill('input[name="name"]', 'Teszt Elek');
  await page.fill('input[name="emailAddress"]', email);
  await page.fill('input[name="userName"]', username);
  await page.fill('input[name="password"]', password);

  // Regisztráció elküldése
  await page.click('button[type="submit"]');

  // Sikeres regisztráció → login oldal
  await page.waitForURL('**/login?registration_successful');

  // Bejelentkezés az új felhasználóval
  await page.fill('input[name="username"]', username);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');

  // Sikeres login ellenőrzése
  await page.waitForURL('**/localhost:8080/');
 await expect(page.locator('h4')).toHaveText('Available Raffles');

});
test('Felhasználó létező -hibás regisztráció ', async ({ page }) => {
  // Regisztrációs oldal
  await page.goto('http://localhost:8080/register');


  // Űrlap kitöltése
  await page.fill('input[name="name"]', 'Teszt Elek');
  await page.fill('input[name="emailAddress"]', email);
  await page.fill('input[name="userName"]', 'test.elek');
  await page.fill('input[name="password"]', password);

  // Regisztráció elküldése
  await page.click('button[type="submit"]');

    // Hiba üzenet  ellenőrzése
 await expect(page.locator('div.alert.alert-danger')).toHaveText('Username or email already exists. Please choose another.');

});
