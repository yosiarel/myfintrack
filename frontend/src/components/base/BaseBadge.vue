<template>
  <span :class="badgeClasses">
    <slot />
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'income' | 'expense' | 'safe' | 'warning' | 'danger' | 'info'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'info',
})

const badgeClasses = computed(() => {
  const variants = {
    income: 'badge-income',
    expense: 'badge-expense',
    safe: 'badge-safe',
    warning: 'badge-warning',
    danger: 'badge-danger',
    info: 'px-3 py-1 rounded-full bg-blue-500/20 text-blue-400 text-sm font-medium',
  }
  
  return variants[props.variant]
})
</script>
