<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useNotification } from '@/composables/useNotification'

const notification = useNotification()

interface Props {
  month: number   // 1-12
  year: number
  label?: string
  placeholder?: string
  required?: boolean
  error?: string
  maxDate?: Date
}

const props = withDefaults(defineProps<Props>(), {
  required: false,
  maxDate: undefined,
})

const emit = defineEmits<{
  'update:month': [value: number]
  'update:year': [value: number]
  'update:date': [value: string | null]
}>()

const isOpen = ref(false)
const containerRef = ref<HTMLElement | null>(null)
const viewMode = ref<'days' | 'months'>('days')

const now = new Date()

const viewMonth = ref(props.month - 1)
const viewYear = ref(props.year)

const selectedDay = ref({
  day: 0,
  month: props.month - 1,
  year: props.year,
})

const monthNames = [
  'Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni',
  'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember',
]

const dayHeaders = ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab']

const daysInMonth = computed(() => {
  return new Date(viewYear.value, viewMonth.value + 1, 0).getDate()
})

const firstDayOfMonth = computed(() => {
  return new Date(viewYear.value, viewMonth.value, 1).getDay()
})

const selectedDisplay = computed(() => {
  if (!props.month || !props.year) return ''
  const { day, month, year } = selectedDay.value
  if (day > 0) {
    return `${day} ${monthNames[month]} ${year}`
  }
  return `${monthNames[month]} ${year}`
})

const todayDay = now.getDate()
const todayMonth = now.getMonth()
const todayYear = now.getFullYear()

const getDayClass = (day: number) => {
  const isToday = day === todayDay && viewMonth.value === todayMonth && viewYear.value === todayYear
  const isSelected =
    selectedDay.value.day === day &&
    selectedDay.value.month === viewMonth.value &&
    selectedDay.value.year === viewYear.value

  const date = new Date(viewYear.value, viewMonth.value, day)
  const isFuture = props.maxDate && date > props.maxDate

  if (isFuture) {
    return 'text-white/20 cursor-not-allowed'
  }

  if (isSelected) {
    return 'bg-primary-500 text-white shadow-lg shadow-primary-500/30'
  }
  if (isToday) {
    return 'bg-white/15 text-white ring-1 ring-white/40 font-bold'
  }
  return 'text-white/70 hover:bg-white/10 hover:text-white'
}

const prevMonth = () => {
  if (viewMonth.value === 0) {
    viewMonth.value = 11
    viewYear.value--
  } else {
    viewMonth.value--
  }
}

const nextMonth = () => {
  if (viewMonth.value === 11) {
    viewMonth.value = 0
    viewYear.value++
  } else {
    viewMonth.value++
  }
}

const selectDay = (day: number) => {
  const date = new Date(viewYear.value, viewMonth.value, day)
  if (props.maxDate && date > props.maxDate) {
    notification.showError('Tidak bisa memilih tanggal melewati hari ini')
    return
  }

  selectedDay.value = { day, month: viewMonth.value, year: viewYear.value }
  const isoDate = `${viewYear.value}-${String(viewMonth.value + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  emit('update:month', viewMonth.value + 1)
  emit('update:year', viewYear.value)
  emit('update:date', isoDate)
  isOpen.value = false
}

const selectMonth = (monthIdx: number) => {
  viewMonth.value = monthIdx
  selectedDay.value = { day: 0, month: monthIdx, year: viewYear.value }
  emit('update:month', monthIdx + 1)
  emit('update:year', viewYear.value)
  emit('update:date', null)
  viewMode.value = 'days'
  isOpen.value = false
}

const toggleOpen = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) viewMode.value = 'days'
}

const handleClickOutside = (e: MouseEvent) => {
  if (containerRef.value && !containerRef.value.contains(e.target as Node)) {
    isOpen.value = false
  }
}

watch(() => props.month, (v) => {
  viewMonth.value = v - 1
  selectedDay.value.month = v - 1
})
watch(() => props.year, (v) => {
  viewYear.value = v
  selectedDay.value.year = v
})

onMounted(() => document.addEventListener('mousedown', handleClickOutside))
onUnmounted(() => document.removeEventListener('mousedown', handleClickOutside))
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
</style>

<template>
  <div class="relative" ref="containerRef">
    <label v-if="label" class="block text-sm font-medium text-white/80 mb-2">
      {{ label }}
      <span v-if="required" class="text-red-400">*</span>
    </label>

    <button
      type="button"
      class="glass-input w-full flex items-center justify-between cursor-pointer text-left"
      :class="{ 'border-red-500': error }"
      @click="toggleOpen"
    >
      <div class="flex items-center gap-2">
        <svg class="w-4 h-4 text-white/60 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <span :class="selectedDisplay ? 'text-white' : 'text-white/50'">
          {{ selectedDisplay || placeholder || 'Pilih periode...' }}
        </span>
      </div>
      <svg
        class="w-4 h-4 text-white/60 transition-transform duration-200 flex-shrink-0"
        :class="{ 'rotate-180': isOpen }"
        fill="none" stroke="currentColor" viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    <Transition name="dropdown">
      <div
        v-if="isOpen"
        class="absolute z-50 w-full mt-2 rounded-2xl overflow-hidden"
        style="
          background: linear-gradient(135deg, rgba(20, 20, 50, 0.97) 0%, rgba(15, 15, 40, 0.99) 100%);
          backdrop-filter: blur(24px);
          -webkit-backdrop-filter: blur(24px);
          border: 1px solid rgba(255, 255, 255, 0.15);
          box-shadow: 0 24px 60px rgba(0,0,0,0.6), inset 0 1px 0 rgba(255,255,255,0.1);
          min-width: 300px;
        "
      >
        <div class="flex items-center justify-between px-4 py-3 border-b border-white/10">
          <button type="button" @click="prevMonth" class="p-1.5 rounded-lg hover:bg-white/10 text-white/70 hover:text-white transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>

          <button
            type="button"
            @click="viewMode = viewMode === 'days' ? 'months' : 'days'"
            class="text-white font-semibold hover:text-primary-300 transition-colors text-sm px-2 py-1 rounded-lg hover:bg-white/10"
          >
            {{ monthNames[viewMonth] }} {{ viewYear }}
          </button>

          <button type="button" @click="nextMonth" class="p-1.5 rounded-lg hover:bg-white/10 text-white/70 hover:text-white transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>

        <div v-if="viewMode === 'days'" class="p-3">
          <div class="grid grid-cols-7 mb-1">
            <div v-for="day in dayHeaders" :key="day" class="text-center text-xs text-white/40 font-medium py-1">
              {{ day }}
            </div>
          </div>

          <div class="grid grid-cols-7 gap-0.5">
            <div v-for="n in firstDayOfMonth" :key="'empty-' + n"></div>

            <button
              v-for="day in daysInMonth"
              :key="day"
              type="button"
              @click="selectDay(day)"
              class="aspect-square flex items-center justify-center text-sm rounded-lg transition-all duration-150 font-medium"
              :class="getDayClass(day)"
            >
              {{ day }}
            </button>
          </div>

          <div class="mt-3 pt-3 border-t border-white/10 text-center">
            <p class="text-xs text-white/40">
              Budget berlaku untuk seluruh bulan
              <span class="text-primary-300 font-medium">{{ monthNames[viewMonth] }} {{ viewYear }}</span>
            </p>
          </div>
        </div>

        <div v-else class="p-3">
          <div class="flex items-center justify-between mb-3">
            <button type="button" @click="viewYear--" class="p-1.5 rounded-lg hover:bg-white/10 text-white/70 hover:text-white transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <span class="text-white font-semibold">{{ viewYear }}</span>
            <button type="button" @click="viewYear++" class="p-1.5 rounded-lg hover:bg-white/10 text-white/70 hover:text-white transition-colors">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>

          <div class="grid grid-cols-3 gap-2">
            <button
              v-for="(name, idx) in monthNames"
              :key="idx"
              type="button"
              @click="selectMonth(idx)"
              class="py-2 px-1 rounded-xl text-sm font-medium transition-all duration-150"
              :class="[
                idx === selectedDay.month && viewYear === selectedDay.year
                  ? 'bg-primary-500/40 text-primary-300 border border-primary-400/50'
                  : 'text-white/70 hover:bg-white/10 hover:text-white'
              ]"
            >
              {{ name.slice(0, 3) }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <p v-if="error" class="mt-1 text-sm text-red-400">{{ error }}</p>
  </div>
</template>


