<template>
  <div class="glass-card" :class="{ 'glass-hover': hoverable, 'cursor-pointer': clickable }">
    <slot />
  </div>
</template>

<script setup lang="ts">
interface Props {
  hoverable?: boolean
  clickable?: boolean
}

withDefaults(defineProps<Props>(), {
  hoverable: false,
  clickable: false,
})
</script>
