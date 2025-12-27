import { test, expect } from '@playwright/test';

test('admin login', async ({ page }) => {
  // Login oldal megnyitása
  await page.goto('http://localhost:8080/login');

  // Felhasználónév kitöltése
  await page.fill('input[name="username"]', 'admin');

  // Jelszó kitöltése
  await page.fill('input[name="password"]', 'admin');

  // Bejelentkezés gomb megnyomása
  await page.click('button[type="submit"]');

  // Várjuk az átirányítást
  await page.waitForURL('**/localhost:8080/');

  // Ellenőrzés: admin bejelentkezés, create raffle gomb megjelenítése
  await expect(page.locator('a#create-raffle-button.btn.btn-info')).toHaveText('Create Raffle');

 // Create Raffle gomb megnyomása
  await page.click('#create-raffle-button');

  // Várjuk az átirányítást
  await page.waitForURL('**/localhost:8080/raffle');

 await expect(page.locator('h2')).toHaveText('Create new raffle');

});

