import { defineConfig } from 'vite'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const raiz = fileURLToPath(new URL('.', import.meta.url))

// Proyecto de varias paginas (no SPA):
// index.html = Login, registro.html, dashboard.html
export default defineConfig({
  server: {
    port: 5173,        // el backend ya permite este origen (CORS)
    strictPort: true
  },
  build: {
    rollupOptions: {
      input: {
        login: resolve(raiz, 'index.html'),
        registro: resolve(raiz, 'registro.html'),
        dashboard: resolve(raiz, 'dashboard.html')
      }
    }
  }
})
