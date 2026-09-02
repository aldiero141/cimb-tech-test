import { test, expect } from '@playwright/test'

async function login(page) {
  await page.goto('/login')
  await page.locator('#username').fill('admin')
  await page.locator('#password').fill('admin123')
  await page.getByRole('button', { name: 'Sign in' }).click()
  await page.waitForURL('**/monitoring')
}

test('sign-out journey: login, click sign out, redirect to login with notification, block protected access', async ({ page }) => {
  await login(page)

  await expect(page.locator('.sign-out-btn')).toBeVisible()
  await page.locator('.sign-out-btn').click()

  await page.waitForURL('**/login?signedOut=true')
  await expect(page.locator('.signed-out-message')).toBeVisible()
  await expect(page.locator('.signed-out-message')).toContainText('You have been successfully signed out.')

  await page.goto('/monitoring')
  await page.waitForURL('**/login')
  await expect(page.url()).toContain('/login')
})
