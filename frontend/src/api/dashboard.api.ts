import axios from './axios'
import type { ApiResponse } from './category.api'
import type { Transaction } from './transaction.api'
import type { Budget } from './budget.api'

export interface ExpenseByCategory {
  categoryId: number
  categoryName: string
  categoryColor: string
  total: number
  transactionCount: number
  percentage: number
}

export interface MonthlyTrend {
  year: number
  month: number
  income: number
  expense: number
  netSavings: number
}

export interface DashboardSummary {
  totalIncome: number
  totalExpense: number
  currentBalance: number
  netSavings: number
  expenseByCategory: ExpenseByCategory[]
  budgetProgress: Budget[]
  recentTransactions: Transaction[]
  monthlyTrend: MonthlyTrend[]
}

export const dashboardApi = {
  getSummary(month?: number, year?: number): Promise<ApiResponse<DashboardSummary>> {
    const params = { month, year }
    return axios.get('/api/dashboard/summary', { params }).then((res) => res.data)
  },
}
