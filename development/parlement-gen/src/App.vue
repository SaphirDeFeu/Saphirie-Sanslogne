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
* {
  font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
}

header {
  z-index: 100;
  top: 0;
  left: 0;
  margin: 1vh;
  border: none;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

header a {
  margin-top: 1vw;
  z-index: 101;
  top: 0;
  left: 0;
  padding: 10px;
  width: 15vw;
  border-radius: 4px;
}

header img {
  width: 12vw;
  padding: 2px;
  aspect-ratio: inherit;
  border-radius: 4px;

  animation-name: select-route-inverse;
  animation-duration: 0.3s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: forwards;
}

header img:hover {
  animation-name: select-route;
  animation-duration: 0.3s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: forwards;
}

main {
  z-index: 0;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 8vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

@keyframes select-route {
  0% {
    width: 12vw;
    padding: 2px;
    background: blue;
  }

  100% {
    width: calc(12vw * 1.1);
    padding: calc(2px * 1.1);
    background: red;
  }
}

@keyframes select-route-inverse {
  0% {
    width: calc(12vw * 1.1);
    padding: calc(2px * 1.1);
    background: red;
  }

  100% {
    width: 12vw;
    padding: 2px;
    background: blue;
  }
}
</style>
