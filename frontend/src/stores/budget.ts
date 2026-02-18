import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  budgetApi,
  type Budget,
  type BudgetRequest,
} from '@/api/budget.api'

export const useBudgetStore = defineStore('budget', () => {
  // State
  const budgets = ref<Budget[]>([])
  const currentMonth = ref(new Date().getMonth() + 1)
  const currentYear = ref(new Date().getFullYear())
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Actions
  async function fetchBudgets(month?: number, year?: number) {
    loading.value = true
    error.value = null

    const targetMonth = month || currentMonth.value
    const targetYear = year || currentYear.value

    try {
      const response = await budgetApi.getAll(targetMonth, targetYear)
      if (response.success) {
        budgets.value = response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch budgets'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createBudget(data: BudgetRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await budgetApi.create(data)
      if (response.success) {
        // Refresh budgets
        await fetchBudgets(data.month, data.year)
        return response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create budget'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateBudget(id: number, data: BudgetRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await budgetApi.update(id, data)
      if (response.success) {
        // Refresh budgets
        await fetchBudgets(data.month, data.year)
        return response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update budget'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteBudget(id: number) {
    loading.value = true
    error.value = null

    try {
      await budgetApi.delete(id)
      // Refresh budgets
      await fetchBudgets()
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete budget'
      throw err
    } finally {
      loading.value = false
    }
  }

  function setMonth(month: number, year: number) {
    currentMonth.value = month
    currentYear.value = year
  }

  function getBudgetByCategory(categoryId: number): Budget | undefined {
    return budgets.value.find((b) => b.categoryId === categoryId)
  }

  return {
    // State
    budgets,
    currentMonth,
    currentYear,
    loading,
    error,
    // Actions
    fetchBudgets,
    createBudget,
    updateBudget,
    deleteBudget,
    setMonth,
    getBudgetByCategory,
  }
})
