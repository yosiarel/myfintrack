<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
        <div>
          <h1 class="text-4xl font-bold text-white mb-2">Dashboard</h1>
          <p class="text-white/70">Ringkasan keuangan Anda bulan ini</p>
        </div>
        <!-- Month/Year Filter -->
        <div class="flex gap-3">
          <select v-model="selectedMonth" @change="fetchDashboard" class="glass-select text-sm">
            <option v-for="m in months" :key="m.value" :value="m.value" class="bg-gray-900 text-white">{{ m.label }}</option>
          </select>
          <select v-model="selectedYear" @change="fetchDashboard" class="glass-select text-sm">
            <option v-for="y in years" :key="y" :value="y" class="bg-gray-900 text-white">{{ y }}</option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-24">
        <BaseLoading size="lg" />
      </div>

      <template v-else>
        <!-- ROW 1: Summary Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <!-- Total Income -->
          <div class="glass-card p-6">
             <div class="flex items-center justify-between mb-2">
                <span class="text-white/60 text-sm">Pemasukan</span>
                <div class="w-8 h-8 rounded-lg bg-income-500/20 flex items-center justify-center text-income-400">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12" /></svg>
                </div>
             </div>
             <p class="text-2xl lg:text-3xl font-bold text-white mb-1">Rp {{ formatCurrency(summary?.totalIncome ?? 0) }}</p>
             <p class="text-xs text-white/40">Bulan ini</p>
          </div>

          <!-- Total Expense -->
          <div class="glass-card p-6">
             <div class="flex items-center justify-between mb-2">
                <span class="text-white/60 text-sm">Pengeluaran</span>
                <div class="w-8 h-8 rounded-lg bg-expense-500/20 flex items-center justify-center text-expense-400">
                   <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6" /></svg>
                </div>
             </div>
             <p class="text-2xl lg:text-3xl font-bold text-white mb-1">Rp {{ formatCurrency(summary?.totalExpense ?? 0) }}</p>
             <p class="text-xs text-white/40">Bulan ini</p>
          </div>

          <!-- Balance -->
          <div class="glass-card p-6">
             <div class="flex items-center justify-between mb-2">
                <span class="text-white/60 text-sm">Sisa (Net)</span>
                <div class="w-8 h-8 rounded-lg bg-primary-500/20 flex items-center justify-center text-primary-400">
                   <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
             </div>
             <p class="text-2xl lg:text-3xl font-bold mb-1" :class="(summary?.netSavings ?? 0) >= 0 ? 'text-income-400' : 'text-expense-400'">
               {{ (summary?.netSavings ?? 0) >= 0 ? '+' : '' }}Rp {{ formatCurrency(summary?.netSavings ?? 0) }}
             </p>
             <p class="text-xs text-white/40">Bulan ini</p>
          </div>
        </div>

        <!-- ROW 2: Charts Area -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
           <!-- Line Chart (Trend) -->
           <div class="glass-card p-6 lg:col-span-2 flex flex-col">
              <h3 class="text-lg font-bold text-white mb-6">Trend Keuangan (6 Bulan)</h3>
              <div class="flex-1 min-h-[300px]">
                 <BaseChart type="line" :data="trendChartData" :options="lineChartOptions" />
              </div>
           </div>

           <!-- Doughnut Chart (Categories) -->
           <div class="glass-card p-6 flex flex-col">
              <h3 class="text-lg font-bold text-white mb-6">Pengeluaran Kategori</h3>
              <div class="flex-1 min-h-[300px] flex items-center justify-center relative">
                 <div v-if="!summary?.expenseByCategory.length" class="absolute inset-0 flex items-center justify-center text-white/30 text-sm">
                   Belum ada pengeluaran
                 </div>
                 <BaseChart v-else type="doughnut" :data="categoryChartData" :options="doughnutChartOptions" />
              </div>
           </div>
        </div>

        <!-- ROW 3: Budget & Recent Activity -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
           <!-- Budget Overview -->
            <div class="glass-card p-6">
              <div class="flex items-center justify-between mb-6">
                <h3 class="font-bold text-white text-lg">Progress Budget</h3>
                <RouterLink to="/budgets" class="text-primary-400 text-sm hover:underline">Kelola</RouterLink>
              </div>
              
              <div v-if="!summary?.budgetProgress.length" class="text-center py-8 text-white/40 text-sm">
                Belum ada budget yang dibuat
              </div>
              <div v-else class="space-y-4">
                <div v-for="budget in summary.budgetProgress" :key="budget.id">
                   <div class="flex justify-between mb-1.5 text-sm">
                      <span class="text-white font-medium">{{ budget.categoryName }}</span>
                      <span :class="getBudgetTextColor(budget.status)">{{ Math.min(100, budget.usagePercentage).toFixed(0) }}%</span>
                   </div>
                   <div class="w-full bg-white/10 rounded-full h-2">
                      <div class="h-full rounded-full transition-all duration-500"
                           :class="getBudgetBarColor(budget.status)"
                           :style="{ width: Math.min(100, budget.usagePercentage) + '%' }"></div>
                   </div>
                   <div class="flex justify-between mt-1 text-xs text-white/40">
                      <span>Rp {{ formatCurrency(budget.spent) }}</span>
                      <span>Target: Rp {{ formatCurrency(budget.monthlyLimit) }}</span>
                   </div>
                </div>
              </div>
            </div>

            <!-- Recent Transactions -->
            <div class="glass-card p-6">
               <div class="flex items-center justify-between mb-6">
                 <h3 class="font-bold text-white text-lg">Transaksi Terbaru</h3>
                 <RouterLink to="/transactions" class="text-primary-400 text-sm hover:underline">Lihat Semua</RouterLink>
               </div>

               <div v-if="!summary?.recentTransactions.length" class="text-center py-8 text-white/40 text-sm">
                  Belum ada transaksi
               </div>
               <div v-else class="space-y-3">
                  <div v-for="tx in summary.recentTransactions" :key="tx.id" class="flex items-center justify-between p-3 rounded-xl bg-white/5 hover:bg-white/10 transition-colors">
                     <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-lg flex items-center justify-center text-lg" :class="tx.type === 'INCOME' ? 'bg-income-500/20' : 'bg-expense-500/20'">
                           {{ tx.type === 'INCOME' ? '💰' : '💸' }}
                        </div>
                        <div>
                           <p class="text-white font-medium text-sm line-clamp-1">{{ tx.description || tx.categoryName }}</p>
                           <p class="text-xs text-white/50">{{ formatDate(tx.transactionDate) }}</p>
                        </div>
                     </div>
                     <span class="font-bold text-sm whitespace-nowrap" :class="tx.type === 'INCOME' ? 'text-income-400' : 'text-expense-400'">
                        {{ tx.type === 'INCOME' ? '+' : '-' }}Rp {{ formatCurrency(tx.amount) }}
                     </span>
                  </div>
               </div>
            </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dashboardApi, type DashboardSummary } from '@/api/dashboard.api'
import { useNotification } from '@/composables/useNotification'
import { BaseLoading } from '@/components/base'
import { defineAsyncComponent } from 'vue'

const BaseChart = defineAsyncComponent(() => import('@/components/base/BaseChart.vue'))

const authStore = useAuthStore()
const notification = useNotification()

const loading = ref(false)
const summary = ref<DashboardSummary | null>(null)

const now = new Date()
const selectedMonth = ref(now.getMonth() + 1)
const selectedYear = ref(now.getFullYear())

const months = [
  { value: 1, label: 'Januari' }, { value: 2, label: 'Februari' }, { value: 3, label: 'Maret' },
  { value: 4, label: 'April' }, { value: 5, label: 'Mei' }, { value: 6, label: 'Juni' },
  { value: 7, label: 'Juli' }, { value: 8, label: 'Agustus' }, { value: 9, label: 'September' },
  { value: 10, label: 'Oktober' }, { value: 11, label: 'November' }, { value: 12, label: 'Desember' },
]
const years = Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)

// Chart Options
const lineChartOptions = {
  plugins: {
    legend: { labels: { color: '#ffffff', font: { family: 'Outfit' } } }
  },
  scales: {
    y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: 'rgba(255,255,255,0.6)' } },
    x: { grid: { display: false }, ticks: { color: 'rgba(255,255,255,0.6)' } }
  }
}

const doughnutChartOptions = {
  cutout: '70%',
  plugins: {
    legend: { display: false } // Hide legend for cleaner look, use tooltips
  },
  elements: {
    arc: { borderWidth: 0 }
  }
}

// Chart Data Computed
const trendChartData = computed(() => {
  if (!summary.value) return { labels: [], datasets: [] }
  const trends = summary.value.monthlyTrend || []
  
  return {
    labels: trends.map(t => {
      const date = new Date(t.year, t.month - 1)
      return date.toLocaleDateString('id-ID', { month: 'short' })
    }),
    datasets: [
      {
        label: 'Pemasukan',
        data: trends.map(t => t.income),
        borderColor: '#10b981', // emerald-500
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        tension: 0.4,
        fill: true
      },
      {
        label: 'Pengeluaran',
        data: trends.map(t => t.expense),
        borderColor: '#ef4444', // red-500
        backgroundColor: 'rgba(239, 68, 68, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  }
})

const categoryChartData = computed(() => {
  if (!summary.value) return { labels: [], datasets: [] }
  const cats = summary.value.expenseByCategory || []
  
  return {
    labels: cats.map(c => c.categoryName),
    datasets: [{
      data: cats.map(c => c.total),
      backgroundColor: cats.map(c => c.categoryColor || '#6366f1'),
      hoverOffset: 4
    }]
  }
})
const formatCurrency = (value: number) => new Intl.NumberFormat('id-ID').format(Math.abs(value))
const formatDate = (dateStr: string) => new Date(dateStr).toLocaleDateString('id-ID', { day: 'numeric', month: 'short' })

const getBudgetBarColor = (status: string) => {
  if (status === 'OVER_BUDGET') return 'bg-expense-500'
  if (status === 'WARNING') return 'bg-yellow-500'
  return 'bg-income-500'
}

const getBudgetTextColor = (status: string) => {
  if (status === 'OVER_BUDGET') return 'text-expense-400'
  if (status === 'WARNING') return 'text-yellow-400'
  return 'text-income-400'
}

const fetchDashboard = async () => {
  loading.value = true
  try {
    const res = await dashboardApi.getSummary(selectedMonth.value, selectedYear.value)
    summary.value = res.data
  } catch (e: any) {
    notification.showError('Gagal memuat dashboard')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>
