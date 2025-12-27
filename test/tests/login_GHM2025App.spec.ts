import { test, expect } from '@playwright/test';

test('Bejelentkezés tesztelése localhost:8080-on', async ({ page }) => {
  // Login oldal megnyitása
  await page.goto('http://localhost:8080/login');

  // Felhasználónév kitöltése
  await page.fill('input[name="username"]', 'test.elek');

  // Jelszó kitöltése
  await page.fill('input[name="password"]', 'test01');

  // Bejelentkezés gomb megnyomása
  await page.click('button[type="submit"]');

  // Várjuk az átirányítást
  await page.waitForURL('**/localhost:8080/');

  // Ellenőrzés: megjelent-e valami, ami csak login után látható
  await expect(page.locator('h1')).toHaveText('Welcome, test.elek!');
});

test('Bejelentkezés hiba tesztelése localhost:8080-on', async ({ page }) => {
  // Login oldal megnyitása
  await page.goto('http://localhost:8080/login');

  // Felhasználónév kitöltése
  await page.fill('input[name="username"]', 'xyz');

  // Jelszó kitöltése
  await page.fill('input[name="password"]', 'xyz01');

  // Bejelentkezés gomb megnyomása
  await page.click('button[type="submit"]');

  // Várjuk az átirányítást
  await page.waitForURL('**/localhost:8080/login?error');

  // Ellenőrzés: megjelent-e valami, ami csak login után látható
  await expect(page.locator('div.alert.alert-danger')).toHaveText('Invalid username or password.');
});
