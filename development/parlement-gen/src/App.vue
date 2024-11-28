<template>
  <header>
    <RouterLink to="/"><img src="https://github.com/SaphirDeFeu/Saphirie-Sanslogne/blob/main/Union.png?raw=true"></RouterLink>
    <RouterLink to="/saphirie"><img src="https://github.com/SaphirDeFeu/Saphirie-Sanslogne/blob/main/Saphirie.png?raw=true"></RouterLink>
    <RouterLink to="/sanslogne"><img src="https://github.com/SaphirDeFeu/Saphirie-Sanslogne/blob/main/Sanslogne.png?raw=true"></RouterLink>
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
  display: fixed;
  flex-direction: row;
  background-color: #dddddd;
}

header img {
  margin-right: 5vw;
  height: 10vh;
  aspect-ratio: inherit;
  border: 1px solid red;
  border-radius: 4px;
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
