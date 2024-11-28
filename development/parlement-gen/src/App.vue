<template>
  <header>
    <RouterLink to="/">National</RouterLink><br/>
    <RouterLink to="/saphirie">Saphirie</RouterLink><br/>
    <RouterLink to="/sanslogne">Sanslogne</RouterLink>
  </header>
  <main>
    <RouterView />
  </main>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style scoped>
header {
  position: fixed;
  margin: 0;
  padding: 0;
  border: none;
  height: 10vh;
  width: 100vw;
  background-color: #dddddd;
}

main {
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>
