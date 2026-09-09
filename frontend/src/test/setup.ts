import "@testing-library/jest-dom/vitest";
import { cleanup } from "@testing-library/react";
import { afterEach } from "vitest";
import { initializeI18n } from "../i18n";

await initializeI18n("en");

afterEach(cleanup);
