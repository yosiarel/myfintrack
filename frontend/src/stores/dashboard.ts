import { defineStore } from 'pinia'
import { ref } from 'vue'
import { dashboardApi, type DashboardSummary } from '@/api/dashboard.api'

export const useDashboardStore = defineStore('dashboard', () => {
  // State
  const summary = ref<DashboardSummary | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const currentMonth = ref(new Date().getMonth() + 1)
  const currentYear = ref(new Date().getFullYear())

  // Actions
  async function fetchSummary(month?: number, year?: number) {
    loading.value = true
    error.value = null

    const targetMonth = month || currentMonth.value
    const targetYear = year || currentYear.value

    try {
      const response = await dashboardApi.getSummary(targetMonth, targetYear)
      if (response.success) {
        summary.value = response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch dashboard summary'
      throw err
    } finally {
      loading.value = false
    }
  }

  function setMonth(month: number, year: number) {
    currentMonth.value = month
    currentYear.value = year
  }

  return {
    // State
    summary,
    loading,
    error,
    currentMonth,
    currentYear,
    // Actions
    fetchSummary,
    setMonth,
  }
})
