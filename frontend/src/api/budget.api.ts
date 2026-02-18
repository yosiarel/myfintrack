import axios from './axios'
import type { ApiResponse } from './category.api'

export interface Budget {
  id: number
  categoryId: number
  categoryName: string
  categoryColor: string
  monthlyLimit: number
  spent: number
  remaining: number
  usagePercentage: number
  status: 'SAFE' | 'WARNING' | 'OVER_BUDGET'
  startDate: string | null   // ISO date string e.g. '2026-02-18'
  month: number
  year: number
}

export interface BudgetSummary {
  totalIncome: number
  totalAllocated: number
  totalUnbudgetedSpent: number
  totalSpent: number
  netBalance: number
  month: number
  year: number
}

export interface BudgetRequest {
  categoryId: number
  monthlyLimit: number
  startDate?: string | null  // ISO date string e.g. '2026-02-18'
  month: number
  year: number
}

export const budgetApi = {
  /**
   * Get all budgets for a specific month/year
   */
  getAll(month?: number, year?: number): Promise<ApiResponse<Budget[]>> {
    const params = { month, year }
    return axios.get('/api/budgets', { params }).then((res) => res.data)
  },

  /**
   * Get envelope summary (Income, Allocated, Unbudgeted, Net)
   */
  getSummary(month?: number, year?: number): Promise<ApiResponse<BudgetSummary>> {
    const params = { month, year }
    return axios.get('/api/budgets/summary', { params }).then((res) => res.data)
  },

  /**
   * Get budget by ID
   */
  getById(id: number): Promise<ApiResponse<Budget>> {
    return axios.get(`/api/budgets/${id}`).then((res) => res.data)
  },

  /**
   * Create new budget
   */
  create(data: BudgetRequest): Promise<ApiResponse<Budget>> {
    return axios.post('/api/budgets', data).then((res) => res.data)
  },

  /**
   * Update budget
   */
  update(id: number, data: BudgetRequest): Promise<ApiResponse<Budget>> {
    return axios.put(`/api/budgets/${id}`, data).then((res) => res.data)
  },

  /**
   * Delete budget
   */
  delete(id: number): Promise<void> {
    return axios.delete(`/api/budgets/${id}`).then((res) => res.data)
  },
}
