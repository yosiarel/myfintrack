import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  transactionApi,
  type Transaction,
  type TransactionRequest,
  type TransactionFilters,
} from '@/api/transaction.api'

export const useTransactionStore = defineStore('transaction', () => {
  // State
  const transactions = ref<Transaction[]>([])
  const currentPage = ref(0)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const pageSize = ref(10)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Filters
  const filters = ref<TransactionFilters>({
    page: 0,
    size: 10,
  })

  // Getters
  const incomeTransactions = computed(() =>
    transactions.value.filter((t) => t.type === 'INCOME')
  )

  const expenseTransactions = computed(() =>
    transactions.value.filter((t) => t.type === 'EXPENSE')
  )

  const totalIncome = computed(() =>
    incomeTransactions.value.reduce((sum, t) => sum + t.amount, 0)
  )

  const totalExpense = computed(() =>
    expenseTransactions.value.reduce((sum, t) => sum + t.amount, 0)
  )

  const balance = computed(() => totalIncome.value - totalExpense.value)

  // Actions
  async function fetchTransactions(newFilters?: TransactionFilters) {
    loading.value = true
    error.value = null

    if (newFilters) {
      filters.value = { ...filters.value, ...newFilters }
    }

    try {
      const response = await transactionApi.getAll(filters.value)
      if (response.success) {
        transactions.value = response.data.content
        currentPage.value = response.data.number
        totalPages.value = response.data.totalPages
        totalElements.value = response.data.totalElements
        pageSize.value = response.data.size
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch transactions'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createTransaction(data: TransactionRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await transactionApi.create(data)
      if (response.success) {
        // Refresh transactions
        await fetchTransactions()
        return response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create transaction'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateTransaction(id: number, data: TransactionRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await transactionApi.update(id, data)
      if (response.success) {
        // Refresh transactions
        await fetchTransactions()
        return response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update transaction'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteTransaction(id: number) {
    loading.value = true
    error.value = null

    try {
      await transactionApi.delete(id)
      // Refresh transactions
      await fetchTransactions()
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete transaction'
      throw err
    } finally {
      loading.value = false
    }
  }

  function setFilters(newFilters: TransactionFilters) {
    filters.value = { ...filters.value, ...newFilters }
  }

  function clearFilters() {
    filters.value = {
      page: 0,
      size: 10,
    }
  }

  return {
    // State
    transactions,
    currentPage,
    totalPages,
    totalElements,
    pageSize,
    loading,
    error,
    filters,
    // Getters
    incomeTransactions,
    expenseTransactions,
    totalIncome,
    totalExpense,
    balance,
    // Actions
    fetchTransactions,
    createTransaction,
    updateTransaction,
    deleteTransaction,
    setFilters,
    clearFilters,
  }
})
