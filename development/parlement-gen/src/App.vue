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
  width: 100vw;
  display: flex;
}

header a {
  margin-left: 1vw;
  z-index: 101;
  padding-bottom: 11px;
  height: 6vh;
  border-radius: 4px;
}

header img {
  height: 6vh;
  padding: 1px;
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
    height: 6vh;
    padding: 2px;
    background: blue;
  }

  100% {
    height: calc(6vh * 1.1);
    padding: calc(2px * 1.1);
    background: red;
  }
}

@keyframes select-route-inverse {
  0% {
    height: calc(6vh * 1.1);
    padding: calc(2px * 1.1);
    background: red;
  }

  100% {
    height: 6vh;
    padding: 2px;
    background: blue;
  }
}
</style>
