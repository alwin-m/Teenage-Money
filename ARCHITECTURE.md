# Architecture

Teenage Money is currently a small Vite + React prototype. `src/main.jsx` owns the screen composition and local state. `src/styles.css` contains the visual system and responsive layouts. `src/finance.js` contains pure validation and balance helpers intended to become the shared domain layer as the project moves to native Android.

The current flow is:

`Home → quick log / add money → local state → localStorage → Activity / receipt`

There is no server, authentication, analytics SDK, bank connection, or network financial service.

