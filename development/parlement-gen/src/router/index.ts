import { createRouter, createWebHistory } from 'vue-router'
import National from '../views/National.vue'
import Saphirie from '../views/Saphirie.vue'
import Sanslogne from '@/views/Sanslogne.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: National,
    },
    {
      path: '/saphirie',
      name: 'saphirie',
      component: Saphirie,
    },
    {
      path: '/sanslogne',
      name: 'sanslogne',
      component: Sanslogne,
    },
  ],
})

export default router
