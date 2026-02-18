<template>
  <div class="relative" ref="containerRef">
    <label v-if="label" class="block text-sm font-medium text-white/80 mb-2">
      {{ label }}
      <span v-if="required" class="text-red-400">*</span>
    </label>

    <!-- Trigger Button -->
    <button
      type="button"
      :disabled="disabled"
      class="glass-input w-full flex items-center justify-between cursor-pointer text-left"
      :class="{ 'border-red-500': error, 'opacity-50 cursor-not-allowed': disabled }"
      @click="toggleOpen"
    >
      <span :class="selectedLabel ? 'text-white' : 'text-white/50'">
        {{ selectedLabel || placeholder || 'Pilih...' }}
      </span>
      <svg
        class="w-4 h-4 text-white/60 transition-transform duration-200 flex-shrink-0 ml-2"
        :class="{ 'rotate-180': isOpen }"
        fill="none" stroke="currentColor" viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    <!-- Dropdown List -->
    <Transition name="dropdown">
      <div
        v-if="isOpen"
        class="absolute z-50 w-full mt-2 rounded-2xl overflow-hidden"
        style="
          background: linear-gradient(135deg, rgba(30, 30, 60, 0.95) 0%, rgba(20, 20, 50, 0.98) 100%);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(255, 255, 255, 0.15);
          box-shadow: 0 20px 60px rgba(0,0,0,0.5), inset 0 1px 0 rgba(255,255,255,0.1);
        "
      >
        <div class="max-h-56 overflow-y-auto custom-scrollbar py-1">
          <button
            v-for="option in options"
            :key="option.value"
            type="button"
            class="w-full text-left px-4 py-2.5 text-sm transition-colors duration-150 flex items-center gap-2"
            :class="[
              String(option.value) === String(modelValue)
                ? 'bg-primary-500/30 text-primary-300 font-medium'
                : 'text-white/90 hover:bg-white/10 hover:text-white'
            ]"
            @click="selectOption(option)"
          >
            <svg
              v-if="String(option.value) === String(modelValue)"
              class="w-4 h-4 text-primary-400 flex-shrink-0"
              fill="none" stroke="currentColor" viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <span v-else class="w-4 flex-shrink-0"></span>
            {{ option.label }}
          </button>
          <div v-if="options.length === 0" class="px-4 py-3 text-white/40 text-sm text-center">
            Tidak ada pilihan
          </div>
        </div>
      </div>
    </Transition>

    <p v-if="error" class="mt-1 text-sm text-red-400">{{ error }}</p>
    <p v-else-if="hint" class="mt-1 text-sm text-white/50">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

interface SelectOption {
  value: string | number
  label: string
}

interface Props {
  modelValue: string | number
  options: SelectOption[]
  label?: string
  placeholder?: string
  disabled?: boolean
  required?: boolean
  error?: string
  hint?: string
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  required: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const isOpen = ref(false)
const containerRef = ref<HTMLElement | null>(null)

const selectedLabel = computed(() => {
  const found = props.options.find((o) => String(o.value) === String(props.modelValue))
  return found?.label ?? ''
})

const toggleOpen = () => {
  if (!props.disabled) isOpen.value = !isOpen.value
}

const selectOption = (option: SelectOption) => {
  emit('update:modelValue', option.value)
  isOpen.value = false
}

const handleClickOutside = (e: MouseEvent) => {
  if (containerRef.value && !containerRef.value.contains(e.target as Node)) {
    isOpen.value = false
  }
}

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

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}
</style>
