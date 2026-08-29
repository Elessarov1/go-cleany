import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { App } from "./app/App";
import { bootstrap } from "./app/bootstrap";
import { initializeTheme } from "./theme/theme";
import "./styles/variables.css";
import "./styles/global.css";
import "./styles/theme-stella.css";
import "./styles/ui-polish.css";
import "./styles/ui-motion.css";
import "./styles/theme.css";

initializeTheme();

async function start() {
  const rootElement = document.getElementById("root");
  if (!rootElement) {
    throw new Error("Root element was not found");
  }

  const services = await bootstrap();
  createRoot(rootElement).render(
    <StrictMode>
      <App {...services} />
    </StrictMode>,
  );
}

void start();
