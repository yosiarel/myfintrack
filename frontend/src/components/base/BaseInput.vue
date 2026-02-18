<template>
  <div class="relative">
    <label v-if="label" :for="inputId" class="block text-sm font-medium text-white/80 mb-2">
      {{ label }}
      <span v-if="required" class="text-red-400">*</span>
    </label>
    
    <input
      :id="inputId"
      ref="inputRef"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
      :min="min"
      :max="max"
      :step="step"
      class="glass-input w-full"
      :class="{ 'border-red-500': error }"
      @input="handleInput"
      @blur="emit('blur', $event)"
      @focus="emit('focus', $event)"
    />
    
    <p v-if="error" class="mt-1 text-sm text-red-400">{{ error }}</p>
    <p v-else-if="hint" class="mt-1 text-sm text-white/50">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  modelValue: string | number
  type?: 'text' | 'email' | 'password' | 'number' | 'date' | 'tel'
  label?: string
  placeholder?: string
  disabled?: boolean
  required?: boolean
  error?: string
  hint?: string
  min?: number
  max?: number
  step?: number
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  required: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  blur: [event: FocusEvent]
  focus: [event: FocusEvent]
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const inputId = computed(() => `input-${Math.random().toString(36).substr(2, 9)}`)

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  const value = props.type === 'number' ? parseFloat(target.value) : target.value
  emit('update:modelValue', value)
}

defineExpose({
  focus: () => inputRef.value?.focus(),
  blur: () => inputRef.value?.blur(),
})
</script>
