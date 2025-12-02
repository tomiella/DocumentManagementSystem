// placement: C - Top Right (column 3, row 1)
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import loggedout from "../assets/loggedout_iconT.png";
import loggedin from "../assets/loggedIn_iconT.png";

//INFO: dev-only mock authentication
const DEV_DEMO = {
  username: "demo",
  password: "demo12345",
  name: "Demo User",
} as const;
const USE_MOCK_AUTH =
  (import.meta as any)?.env?.VITE_USE_MOCK_AUTH === "true" ||
  !(import.meta as any)?.env?.VITE_API_BASE_URL;

export default function UserMenu() {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState<null | { name: string }>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const stored = sessionStorage.getItem("userName");
    if (stored) setUser({ name: stored });
  }, []);

  async function handleLoginSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    const fd = new FormData(e.currentTarget);
    const username = String(fd.get("username") || "").trim();
    const password = String(fd.get("password") || "");
    if (!username) {
      setError("Username is required.");
      return;
    }
    if (password.length < 4) {
      setError("Password must be at least 8 characters.");
      return;
    }

    // INFO:Dev-only mock authentication for local testing

    // if (USE_MOCK_AUTH) {
    //     if (username === DEV_DEMO.username && password === DEV_DEMO.password) {
    //         setUser({ name: DEV_DEMO.name });
    //         sessionStorage.setItem('userName', DEV_DEMO.name);
    //         window.dispatchEvent(new Event('auth:changed'));
    //         setOpen(false);
    //     } else {
    //         setError('Invalid credentials (try demo / demo12345)');
    //     }
    //     return; // skip real network call in mock mode
    // }
    setSubmitting(true);
    try {
      const API_BASE = "http://localhost:8080";
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
        credentials: "include",
      });
      if (!res.ok) throw new Error("Login failed");
      // const userDto = await res.json();
      const { token } = await res.json();
      console.log("Received token:", token);
      setUser({ name: username });
      // sessionStorage.setItem('user', JSON.stringify({ name: username }));
      sessionStorage.setItem("userName", username);
      sessionStorage.setItem("isAuthenticated", "true");
      sessionStorage.setItem("token", token);
      window.dispatchEvent(new Event("auth:changed"));
      setOpen(false);
    } catch {
      setError("Login failed. Please try again.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="relative flex items-center gap-2">
      <img
        src={user ? loggedin : loggedout}
        alt={user ? "logged-in" : "unlogged"}
        className="h-8 w-8"
      />
      <button
        className="border rounded px-3 py-2 text-left"
        onClick={() => setOpen((o) => !o)}
      >
        {user ? user.name : "Sign in"}
      </button>

      {open && (
        <div className="absolute right-0 top-full mt-2 w-64 bg-black border rounded shadow p-3 z-50">
          {!user ? (
            <form className="space-y-3" onSubmit={handleLoginSubmit} noValidate>
              <div className="flex flex-col gap-1">
                <label htmlFor="um-username" className="text-sm">
                  User Name
                </label>
                <input
                  id="um-username"
                  name="username"
                  className="border rounded px-2 py-1 w-full"
                  placeholder="User Name"
                  autoComplete="username"
                  required
                />
              </div>
              <div className="flex flex-col gap-1">
                <label htmlFor="um-password" className="text-sm">
                  Password
                </label>
                <input
                  id="um-password"
                  name="password"
                  type="password"
                  className="border rounded px-2 py-1 w-full"
                  placeholder="Password"
                  autoComplete="current-password"
                  minLength={8}
                  required
                />
              </div>
              {USE_MOCK_AUTH && (
                <div className="text-xs text-gray-500">
                  Demo creds: <code>admin</code> / <code>admin</code>
                </div>
              )}
              {error && (
                <div
                  className="text-red-700 text-sm"
                  role="alert"
                  aria-live="assertive"
                >
                  {error}
                </div>
              )}
              <div className="flex justify-between items-center">
                <button
                  className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-emerald-200 border-green-900"
                  type="submit"
                  disabled={submitting}
                >
                  {submitting ? "Logging in…" : "Login"}
                </button>
                <button
                  type="button"
                  className="text-sm text-text hover:underline"
                  onClick={() => {
                    setOpen(false);
                    navigate("/signup");
                  }}
                >
                  Go to Sign-up Page
                </button>
              </div>
            </form>
          ) : (
            <div className="flex flex-col gap-2">
              <button
                className="px-3 py-1 border rounded text-left"
                onClick={() => {
                  setOpen(false);
                  navigate("/profile");
                }}
              >
                Profile
              </button>
              <button
                className="px-3 py-1 border rounded text-left"
                onClick={() => {
                  setUser(null);
                  sessionStorage.removeItem("userName");
                  window.dispatchEvent(new Event("auth:changed"));
                  navigate("/documents");
                }}
              >
                Log out
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

