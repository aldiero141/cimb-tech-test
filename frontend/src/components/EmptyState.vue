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
    <i class="pi pi-inbox empty-icon" />
    <p class="empty-message">{{ message }}</p>
  </div>
</template>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  color: #6b7280;
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #cbd5e1;
}

.empty-message {
  margin: 0;
}
</style>