import "dotenv/config";
import express from "express";
import cors from "cors";
import { registerRoutes } from "./routes.js";

// Convert camelCase keys to snake_case recursively
function toSnakeCase(str: string): string {
  return str.replace(/([A-Z])/g, "_$1").toLowerCase();
}

function toCamelCase(str: string): string {
  return str.replace(/_([a-z])/g, (_, c: string) => c.toUpperCase());
}

function convertKeysToSnakeCase(obj: any): any {
  if (Array.isArray(obj)) return obj.map(convertKeysToSnakeCase);
  if (obj !== null && typeof obj === "object" && !(obj instanceof Date)) {
    return Object.fromEntries(
      Object.entries(obj).map(([key, val]) => [toSnakeCase(key), convertKeysToSnakeCase(val)])
    );
  }
  return obj;
}

function convertKeysToCamelCase(obj: any): any {
  if (Array.isArray(obj)) return obj.map(convertKeysToCamelCase);
  if (obj !== null && typeof obj === "object" && !(obj instanceof Date)) {
    return Object.fromEntries(
      Object.entries(obj).map(([key, val]) => [toCamelCase(key), convertKeysToCamelCase(val)])
    );
  }
  return obj;
}

const app = express();
const PORT = parseInt(process.env.PORT || "3000", 10);

app.use(cors());
app.use(express.json({ limit: "25mb" }));
app.use(express.urlencoded({ extended: false }));

app.use((req, _res, next) => {
  if (req.body && typeof req.body === "object") {
    req.body = convertKeysToCamelCase(req.body);
  }
  next();
});

// Override res.json to convert all response keys to snake_case
app.use((_req, res, next) => {
  const originalJson = res.json.bind(res);
  res.json = (body: any) => originalJson(convertKeysToSnakeCase(body));
  next();
});

// Request logging
app.use((req, res, next) => {
  const start = Date.now();
  res.on("finish", () => {
    if (req.path.startsWith("/api")) {
      const duration = Date.now() - start;
      console.log(`${req.method} ${req.path} ${res.statusCode} in ${duration}ms`);
    }
  });
  next();
});

registerRoutes(app);

app.listen(PORT, "0.0.0.0", () => {
  console.log(`\n🏥 Golazo Medical API (PostgreSQL) running on http://localhost:${PORT}\n`);
  console.log("📋 Demo accounts:");
  console.log("   Player: player@uefa.com / password123");
  console.log("   Player: marco@uefa.com  / password123");
  console.log("   Doctor: doctor@uefa.com / password123");
  console.log("   PIN for all: 1234\n");
});
