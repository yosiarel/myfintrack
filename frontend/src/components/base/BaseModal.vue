<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="modelValue"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm"
        @click.self="handleClose"
      >
        <div class="modal-glass rounded-3xl p-8 max-w-md w-full mx-4 relative">
          <!-- Close button -->
          <button
            v-if="!hideClose"
            class="absolute top-4 right-4 text-white/60 hover:text-white transition-colors"
            @click="handleClose"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>

          <!-- Title -->
          <h2 v-if="title" class="text-2xl font-bold text-white mb-4 pr-8">
            {{ title }}
          </h2>

          <!-- Content -->
          <div class="text-white/90">
            <slot />
          </div>

          <!-- Footer -->
          <div v-if="$slots.footer" class="mt-6 flex gap-3 justify-end">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
interface Props {
  modelValue: boolean
  title?: string
  hideClose?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  hideClose: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const handleClose = () => {
  emit('update:modelValue', false)
  emit('close')
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .glass,
.modal-leave-active .glass {
  transition: transform 0.3s ease;
}

.modal-enter-from .glass,
.modal-leave-to .glass {
  transform: scale(0.9);
}
</style>
