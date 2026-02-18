<template>
  <div class="relative w-full h-full">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, type PropType } from 'vue'
import Chart, { type ChartConfiguration, type ChartType } from 'chart.js/auto'

const props = defineProps({
  type: {
    type: String as PropType<ChartType>,
    required: true
  },
  data: {
    type: Object,
    required: true
  },
  options: {
    type: Object,
    default: () => ({})
  }
})

const canvas = ref<HTMLCanvasElement | null>(null)
let chartInstance: Chart | null = null

const renderChart = () => {
  if (!canvas.value) return
  
  if (chartInstance) {
    chartInstance.destroy()
  }

  const config: ChartConfiguration = {
    type: props.type,
    data: JSON.parse(JSON.stringify(props.data)), // Deep copy to avoid reactivity issues
    options: {
      responsive: true,
      maintainAspectRatio: false,
      ...props.options
    }
  }

  chartInstance = new Chart(canvas.value, config)
}

watch(() => props.data, () => {
  renderChart()
}, { deep: true })

onMounted(() => {
  renderChart()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.destroy()
  }
})
</script>
