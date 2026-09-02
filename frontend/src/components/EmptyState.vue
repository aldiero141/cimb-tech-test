<script setup>
defineProps({
  message: {
    type: String,
    default: 'No records found matching your criteria'
  }
})
</script>

<template>
  <div class="empty-state">
    <div class="empty-icon-circle">
      <span class="material-symbols-outlined empty-icon">inbox</span>
    </div>
    <p class="empty-message">{{ message }}</p>
  </div>
</template>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 1.5rem;
  color: var(--text-muted, #64748B);
  gap: 0.75rem;
}

.empty-icon-circle {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 9999px;
  background-color: var(--surface-header, #F1F5F9);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--surface-border, #E2E8F0);
}

.empty-icon {
  font-size: 1.75rem !important;
  color: var(--text-muted, #64748B);
}

.empty-message {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-muted, #64748B);
}
</style>