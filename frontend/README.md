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

## Quick Launch

**Frontend**
1. In the IntelliJ Project panel on the left, navigate to the "frontend" directory, beneath "docs", and expand it.
2. Navigate to it's "src" directory and expand it.
3. Double click "App.vue".
4. A prompt to download Node.js will appear in the top right of IntelliJ. Click "Download Node.js".
5. A popup will appear, click "Download". If you already have Node installed, you'll need to copy the path from the<br>
download panel, close the panel, click "Configure" in the top right of IntelliJ instead of "Download" and paste the<br>
file path in the corresponding sections.
6. In the bottom left corner of IntelliJ, click the terminal icon, arranged vertically among several others.
7. In the new terminal window, type ```cd ./frontend```, or navigate the terminal to the frontend directory another way.
8. Type ```npm install```; now you will be able to locally host an instance of the frontend.
9. Type ```npm run dev```; you will be provided with a local host link.
10. On the left side of IntelliJ in the navigation panel, locate the "SecurityConfig.java" file in the "config" directory:<br>
```...\backend\src\main\java\org.cs250.nan.backend\config\SecurityConfig.java```
11. Open "SecurityConfig.java" and move to line 48; confirm that the local host address matches the address generated<br>
by Node.js after running ```npm run dev```. If not, change line 48 to match the corect port.
12. Using IntelliJ's built-in run capabilities, click the start button at the top right of the IntelliJ window, right of<br>
    "Interactive Default".
13. Once the backend has finished loading, return to your terminal and click the local host link to open the front end.
14. You will be prompted to enter user and password credentials, if not, click on any of the buttons to cause the prompt<br>
to appear. Enter "admin" for the username and "nimda" for the password.
15. Credential are only necessary the first time running the frontend or after the browser's cache and save information<br>
is cleared.
16. The front end is now up and running. If your device is connected to the internet and has Wi-Fi capabilities, you may<br>
conduct a scan, the results of which are automatically uploaded to the database. You may search the database by using<br>
either the local data (San Diego, CA) in the format of YYMMDD (all integers) or you may type in a string to search for<br>
SSIDs. Use the filter box at the top to further refine your search.

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
