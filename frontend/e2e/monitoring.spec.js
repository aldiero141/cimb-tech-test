import { test, expect } from '@playwright/test'

const CALLS_API = '/api/calls'

function callParams(url) {
  return new URL(url).searchParams
}

async function login(page) {
  await page.goto('/login')
  await page.locator('#username').fill('admin')
  await page.locator('#password').fill('admin123')
  await page.getByRole('button', { name: 'Sign in' }).click()
  await page.waitForURL('**/monitoring')
}

async function waitForCallsWith(page, predicate, options) {
  return page.waitForResponse(
    (response) =>
      response.url().includes(CALLS_API) && predicate(callParams(response.url())),
    options
  )
}

test('full monitoring journey: login, table, filter, sort, paginate', async ({ page }) => {
  await login(page)

  await expect(page.getByTestId('search-input')).toBeVisible()
  const rows = page.locator('tbody tr')
  await expect(rows).not.toHaveCount(0)

  await expect(page.locator('.p-paginator .p-paginator-current')).toHaveText('Page 1 of 20')

  await page.locator('button.p-paginator-next').click()
  await waitForCallsWith(page, (params) => params.get('page') === '1')
  await expect(page.locator('.p-paginator .p-paginator-current')).toHaveText('Page 2 of 20')
  await expect(rows.first().locator('td').first()).toHaveText('6')

  await page.getByRole('columnheader', { name: 'Sentiment Score' }).click()
  await waitForCallsWith(
    page,
    (params) => params.get('sort') === 'sentimentScore' && params.get('order') === 'asc'
  )

  await page.getByTestId('search-input').fill('siti')
  await waitForCallsWith(page, (params) => params.get('q') === 'siti')

  const picker = page.getByTestId('period-picker')
  await picker.click()
  const grid = page.locator('table[role="grid"]')
  await grid.locator('td', { hasText: /^1$/ }).first().click()
  await grid.locator('td[data-p-today="true"]').click()
  await page.keyboard.press('Escape')
  await waitForCallsWith(
    page,
    (params) => Boolean(params.get('startDate')) && Boolean(params.get('endDate'))
  )

  await page.getByTestId('search-input').clear()
  await waitForCallsWith(page, (params) => !params.get('q'))

  await page.getByTestId('sentiment-select').click()
  await page.locator('[role="option"]').filter({ hasText: '70% or above' }).click()
  await waitForCallsWith(page, (params) => params.get('sentiment') === 'above70')

  await page.waitForTimeout(300)
  const scorePills = page.locator('.sentiment-pill')
  if (await scorePills.count() > 0) {
    const scores = await scorePills.allTextContents()
    for (const score of scores) {
      expect(parseInt(score, 10)).toBeGreaterThanOrEqual(70)
    }
  } else {
    await expect(page.locator('.empty-state')).toBeVisible()
  }
})