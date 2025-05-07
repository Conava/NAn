# Vue 3 + TypeScript + Vite

## Dependencies:

### Runtime
- **vue** `^3.5.13`
- **axios** `^1.8.4`

### Dev
- **vite** `^6.0.5`
- **@vitejs/plugin-vue** `^5.2.1`
- **typescript** `~5.6.2`
- **vue-tsconfig** `^0.7.0`
- **vue-tsc** `^2.2.0`

### Styling
- **tailwindcss**
- **postcss**
- **autoprefixer**

## Development:
1. Install dependencies (once, after dependency changes):
    ```sh
    npm install
    ```
2. Run the development server:
    ```sh
    npm run dev
    ```
3. IMPORTANT:
   ```sh
    To run the development server, the link to the locally hosted server must match up to the link
    in line 48 of org/cs250/nan/backend/config/SecurityConfig.java. This is due a failure to get around
    browser security issues during development, so the link had to be hardcoded in order to work. 
   ```

## Build and Deployment:
1. Build the project for production:
    ```sh
    npm run build
    ```
2. Preview the built project:
    ```sh
    npm run preview
    ```
3. Deploy the contents of the `dist` directory to your web server or hosting service.
