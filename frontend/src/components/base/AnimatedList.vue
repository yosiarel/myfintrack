
<template>
  <div ref="containerRef" :class="`relative w-full ${className}`.trim()">
    <div
      ref="listRef"
      :class="`overflow-y-auto ${
        displayScrollbar
          ? '[&::-webkit-scrollbar]:w-[8px] [&::-webkit-scrollbar-track]:bg-transparent [&::-webkit-scrollbar-thumb]:bg-white/10 [&::-webkit-scrollbar-thumb]:rounded-[4px]'
          : 'scrollbar-hide'
      }`"
      :style="{
        scrollbarWidth: displayScrollbar ? 'thin' : 'none',
        scrollbarColor: 'rgba(255,255,255,0.1) transparent'
      }"
      @scroll="handleScroll"
    >
      <Motion
        v-for="(item, index) in items"
        :key="index"
        tag="div"
        :data-index="index"
        class="mb-3 cursor-pointer"
        :initial="{ scale: 0.9, opacity: 0, y: 20 }"
        :animate="getItemInView(index) ? { scale: 1, opacity: 1, y: 0 } : { scale: 0.9, opacity: 0, y: 20 }"
        :transition="{ duration: 0.3, delay: index * 0.05 }"
        @mouseenter="() => setSelectedIndex(index)"
        @click="
          () => {
            setSelectedIndex(index);
            emit('itemSelected', item, index);
          }
        "
      >
        <slot name="item" :item="item" :index="index" :selected="selectedIndex === index">
            <div :class="`p-4 bg-white/5 rounded-lg ${selectedIndex === index ? 'bg-white/10' : ''} ${itemClassName}`">
             {{ item }}
            </div>
        </slot>
      </Motion>
    </div>
    
    <!-- Gradients (optional, customized for glass theme) -->
    <div
      v-if="showGradients"
      class="absolute top-0 left-0 right-0 h-8 bg-gradient-to-b from-gray-900/50 to-transparent pointer-events-none transition-opacity duration-300 ease"
      :style="{ opacity: topGradientOpacity }"
    />
    <div
      v-if="showGradients"
      class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-gray-900/50 to-transparent pointer-events-none transition-opacity duration-300 ease"
      :style="{ opacity: bottomGradientOpacity }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, useTemplateRef } from 'vue';
import { Motion } from 'motion-v';

interface AnimatedListProps {
  items?: any[];
  showGradients?: boolean;
  enableArrowNavigation?: boolean;
  className?: string;
  itemClassName?: string;
  displayScrollbar?: boolean;
  initialSelectedIndex?: number;
}

const props = withDefaults(defineProps<AnimatedListProps>(), {
  items: () => [],
  showGradients: true,
  enableArrowNavigation: true,
  className: '',
  itemClassName: '',
  displayScrollbar: true,
  initialSelectedIndex: -1
});

const emit = defineEmits<{
  itemSelected: [item: any, index: number];
}>();

const containerRef = useTemplateRef<HTMLDivElement>('containerRef');
const listRef = useTemplateRef<HTMLDivElement>('listRef');
const selectedIndex = ref(props.initialSelectedIndex);
const keyboardNav = ref(false);
const topGradientOpacity = ref(0);
const bottomGradientOpacity = ref(1);
const itemsInView = ref<boolean[]>([]);

const setSelectedIndex = (index: number) => {
  selectedIndex.value = index;
};

const getItemInView = (index: number) => {
  return itemsInView.value[index] ?? true; // Default to true to prevent flashing hidden
};

const handleScroll = (e: Event) => {
  const target = e.target as HTMLDivElement;
  const { scrollTop, scrollHeight, clientHeight } = target;
  topGradientOpacity.value = Math.min(scrollTop / 50, 1);
  const bottomDistance = scrollHeight - (scrollTop + clientHeight);
  bottomGradientOpacity.value = scrollHeight <= clientHeight ? 0 : Math.min(bottomDistance / 50, 1);

  updateItemsInView();
};

const updateItemsInView = () => {
  if (!listRef.value) return;

  const container = listRef.value;
  const containerRect = container.getBoundingClientRect();

  itemsInView.value = props.items.map((_, index) => {
    const item = container.querySelector(`[data-index="${index}"]`) as HTMLElement;
    if (!item) return false;

    const itemRect = item.getBoundingClientRect();
    const viewHeight = containerRect.height;
    
    // Simplification: just check if it's somewhat in view or close to it
    const itemTop = itemRect.top - containerRect.top;
    return itemTop < viewHeight + 100 && itemTop > -100;
  });
};

const handleKeyDown = (e: KeyboardEvent) => {
  if (e.key === 'ArrowDown' || (e.key === 'Tab' && !e.shiftKey)) {
    // e.preventDefault(); // Don't prevent default tab behavior in app
    if(e.key === 'ArrowDown') e.preventDefault();
    
    keyboardNav.value = true;
    setSelectedIndex(Math.min(selectedIndex.value + 1, props.items.length - 1));
  } else if (e.key === 'ArrowUp' || (e.key === 'Tab' && e.shiftKey)) {
    if(e.key === 'ArrowUp') e.preventDefault();
    keyboardNav.value = true;
    setSelectedIndex(Math.max(selectedIndex.value - 1, 0));
  } else if (e.key === 'Enter') {
    if (selectedIndex.value >= 0 && selectedIndex.value < props.items.length) {
      e.preventDefault();
      emit('itemSelected', props.items[selectedIndex.value], selectedIndex.value);
    }
  }
};

onMounted(() => {
  if (props.enableArrowNavigation) {
    window.addEventListener('keydown', handleKeyDown);
  }

  itemsInView.value = new Array(props.items.length).fill(true);
  setTimeout(updateItemsInView, 100);
});

onUnmounted(() => {
  if (props.enableArrowNavigation) {
    window.removeEventListener('keydown', handleKeyDown);
  }
});
</script>
